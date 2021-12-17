package fr.flowsqy.abstractmob;

import fr.flowsqy.abstractmob.thread.IterationRunnable;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Consumer;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Random;

public class TestListener implements Listener {

    private final AbstractMobPlugin plugin;
    private final IterationRunnable<Entity> runnable;
    private final IterationRunnable<EntityDamageByEntityEvent> velocityRunnable;

    public TestListener(AbstractMobPlugin abstractMobPlugin, IterationRunnable<Entity> runnable, IterationRunnable<EntityDamageByEntityEvent> velocityRunnable) {
        this.plugin = abstractMobPlugin;
        this.runnable = runnable;
        this.velocityRunnable = velocityRunnable;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }


    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event){
        if(event.isNewChunk()){
            return;
        }
        runnable.add(event.getChunk().getEntities());
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event){
        final Player player = event.getPlayer();
        final Consumer<Zombie> entityConsumer = e -> {
            e.getPersistentDataContainer().set(
                    plugin.LIGHTING_ON_DEATH_KEY,
                    PersistentDataType.BYTE,
                    (byte) 1
            );
            e.setMetadata(
                    AbstractMobPlugin.LIGHTING_ON_DEATH,
                    new FixedMetadataValue(plugin, true)
            );
            e.setMetadata(
                    AbstractMobPlugin.KNOCKBACK,
                    new FixedMetadataValue(plugin, 2.0)
            );
            final AttributeInstance instance = e.getAttribute(Attribute.GENERIC_ATTACK_KNOCKBACK);
            if(instance != null){
                instance.addModifier(new AttributeModifier("Knockback", 5, AttributeModifier.Operation.ADD_NUMBER));
            }
        };
        /*
        final Consumer<Zombie> entityConsumer = e -> e.setMetadata(
                AbstractMobPlugin.LIGHTING_ON_DEATH,
                new FixedMetadataValue(plugin, true)
        );*/
        switch (event.getMessage()){
            case "/spawne":
                spawnEntity(player.getWorld(), player.getLocation(), Zombie.class, entityConsumer);
                break;
            case "/spawne10":
                for (int index = 0; index < 10; index++) {
                    spawnEntity(player.getWorld(), player.getLocation(), Zombie.class, entityConsumer);
                }
                break;
            case "/spawne1000":
                new BukkitRunnable() {

                    private int wave = 0;

                    @Override
                    public void run() {
                        for (int index = 0; index < 10; index++) {
                            spawnEntity(player.getWorld(), player.getLocation(), Zombie.class, entityConsumer);
                        }
                        wave++;
                        if(wave >= 100){
                            cancel();
                        }
                    }
                }.runTaskTimer(plugin, 0L, 1L);

                break;
            case "/spawns1000":
                new BukkitRunnable() {

                    private int wave = 0;
                    private Random random = new Random();
                    private Consumer<Spider> spiderConsumer = new Consumer<Spider>() {
                        @Override
                        public void accept(Spider spider) {
                            Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
                                @Override
                                public void run() {
                                    if(!spider.isValid()){
                                        cancel();
                                    }

                                    if(random.nextInt(10) <= 1) {
                                        spider.getWorld().getBlockAt(spider.getLocation()).setType(Material.COBWEB);
                                    }
                                }
                            }, 0L, 1L);
                        }
                    };

                    @Override
                    public void run() {
                        for (int index = 0; index < 10; index++) {
                            // Spawn spider
                            spawnEntity(player.getWorld(), player.getLocation(), Spider.class, spiderConsumer);
                        }
                        wave++;
                        if(wave >= 100){
                            cancel();
                        }
                    }
                }.runTaskTimer(plugin, 0L, 1L);
        }
    }

    private <T extends Entity> void spawnEntity(World world, Location location, Class<T> entityClass, Consumer<T> consumer){
        world.spawn(
                location,
                entityClass,
                false,
                consumer
        );
    }

    @EventHandler
    public void onKnock(EntityDamageByEntityEvent event){
        Entity damagerEntity = event.getDamager();
        if (damagerEntity instanceof Projectile projectile && projectile.getShooter() instanceof Entity shooterEntity) {
            damagerEntity = shooterEntity;
        }
        final List<MetadataValue> values = damagerEntity.getMetadata(AbstractMobPlugin.KNOCKBACK);
        if (values.isEmpty()) {
            return;
        }
        double up = 0;
        for(MetadataValue value : values){
            up += value.asDouble();
        }
        event.getEntity().setVelocity(event.getEntity().getVelocity().setY(up / values.size()));
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event){
        final LivingEntity entity = event.getEntity();
        if(entity.hasMetadata(AbstractMobPlugin.LIGHTING_ON_DEATH)){
            entity.getWorld().strikeLightning(entity.getLocation());
            entity.getWorld().playSound(entity.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_IMPACT, SoundCategory.AMBIENT, 1.0f, 1.0f);
        }
    }

}
