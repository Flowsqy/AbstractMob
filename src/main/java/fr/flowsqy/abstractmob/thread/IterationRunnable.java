package fr.flowsqy.abstractmob.thread;

import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Consumer;

public abstract class IterationRunnable<E> extends BukkitRunnable {

    private final int iterations;
    private boolean dirty;
    private ArrayDeque<E> deque;

    public IterationRunnable(int iterations) {
        this.iterations = iterations;
        dirty = false;
        deque = new ArrayDeque<>();
    }

    @Override
    public void run() {
        for (int index = 0; index < iterations; index++) {
            final E element = deque.pollFirst();
            if (element == null) {
                if (dirty) {
                    deque = new ArrayDeque<>();
                    dirty = false;
                }
                return;
            } else {
                perform(element);
            }
        }
    }

    protected abstract void perform(E element);

    public void add(E e) {
        dirty = true;
        deque.addLast(e);
    }

    public void add(E[] e) {
        dirty = true;
        deque.addAll(new ReferenceCollections<>(e));
    }

    private static class ReferenceCollections<E> implements Collection<E> {

        private final E[] elements;

        public ReferenceCollections(E[] elements) {
            this.elements = elements;
        }

        // Needed to grow deque
        @Override
        public int size() {
            return elements.length;
        }

        // Needed to add every element in deque
        @Override
        public void forEach(Consumer<? super E> action) {
            for (E element : elements) {
                action.accept(element);
            }
        }

        // All function that need to be implemented
        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public boolean contains(Object o) {
            return false;
        }

        @Override
        public Iterator<E> iterator() {
            return null;
        }

        @Override
        public Object[] toArray() {
            return new Object[0];
        }

        @Override
        public <T> T[] toArray(T[] a) {
            return a;
        }

        @Override
        public boolean add(E e) {
            return false;
        }

        @Override
        public boolean remove(Object o) {
            return false;
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return false;
        }

        @Override
        public boolean addAll(Collection<? extends E> c) {
            return false;
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            return false;
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            return false;
        }

        @Override
        public void clear() {
        }
    }

}
