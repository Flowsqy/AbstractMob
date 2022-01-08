package fr.flowsqy.abstractmob.thread;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class IterationTask {

    private final BlockingQueue<Runnable> queue;
    private final String name;
    private volatile Thread thread;

    public IterationTask(String name) {
        queue = new LinkedBlockingQueue<>();
        this.name = name;
    }

    /**
     * Start task, except if it is already
     */
    public void start() {
        if (!isRunning()) {
            thread = new Thread(() -> {
                while (!Thread.interrupted()) {
                    try {
                        queue.take().run();
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }, name);
            thread.start();
        }
    }

    /**
     * Stop any running task then start it again
     */
    public void restart() {
        stop();
        start();
    }

    /**
     * Stop task properly
     */
    public void stop() {
        if (thread != null) {
            thread.interrupt();
            thread = null;
        }
    }

    /**
     * @return whether task is running or not
     */
    public boolean isRunning() {
        return thread != null;
    }

    /**
     * Register a task to run before next loop
     *
     * @param runnable task to run
     */
    public void queue(Runnable runnable) {
        queue.add(runnable);
    }

}
