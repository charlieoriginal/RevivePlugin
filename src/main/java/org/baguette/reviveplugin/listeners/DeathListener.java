package org.baguette.reviveplugin.listeners;

import org.baguette.reviveplugin.RevivePlugin;
import org.baguette.reviveplugin.profiles.DeathProfile;
import org.baguette.reviveplugin.profiles.DeathState;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;

public class DeathListener implements Listener {

    private RevivePlugin plugin;

    public DeathListener(RevivePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDeath(EntityDamageByEntityEvent e) {
        if (e.getEntity() != null) {
            if (!e.isCancelled() && e.getEntity() instanceof Player) {
                double playerHP = ((Player) e.getEntity()).getHealth();
                double damage = e.getFinalDamage();

                if (playerHP - damage <= 0) {
                    DeathProfile profile = plugin.getProfileManager().getProfile((Player) e.getEntity());
                    if (profile.getDeathState() != null) {
                        profile.setDead(true);
                    }

                    if (e.getDamager() instanceof Player) {
                        if (!profile.isDead() || profile.getDeathState() == null) {
                            profile.killByPlayer((Player) e.getEntity(), (Player) e.getDamager());
                            ((Player) e.getEntity()).setHealth(1);
                            profile.getDeathState().spawnHolo("&c&l*DEAD*");
                            e.setCancelled(true);

                            Bukkit.getLogger().info("(RevivePlugin): " + e.getEntity().getName() + " was killed by " + e.getDamager().getName());
                        } else {
                            DeathState deathState = profile.getDeathState();
                            profile.setDead(false);
                            if (deathState.getUpdateTimerTask() != null)
                                deathState.getUpdateTimerTask().cancel();

                            if (deathState.getStand() != null) {
                                deathState.getStand().removePassenger(e.getEntity());
                                deathState.getStand().remove();
                            }

                            if (deathState.getHologram() != null)
                                deathState.getHologram().remove();

                            if (deathState.getTimerHolo() != null)
                                deathState.getTimerHolo().remove();

                            profile.setDeathState(null);
                            ((Player) e.getEntity()).setHealth(0);

                            if (plugin.isDebug()) {
                                Bukkit.getLogger().info("(RevivePlugin): True death by " + e.getDamager());
                                Bukkit.getLogger().info("(RevivePlugin): " + profile.getDeathState());
                                Bukkit.getLogger().info("(RevivePlugin): " + profile.isDead());
                            }
                        }
                    } else {
                        if (!profile.isDead() || profile.getDeathState() == null) {
                            profile.killOther((Player) e.getEntity());
                            ((Player) e.getEntity()).setHealth(1);
                            profile.getDeathState().spawnHolo("&c&l*DEAD*");
                            e.setCancelled(true);

                            Bukkit.getLogger().info(("(RevivePlugin): " + e.getEntity().getName() + " was killed by " + e.getDamager()));
                        } else {
                            DeathState deathState = profile.getDeathState();
                            profile.setDead(false);
                            if (deathState.getUpdateTimerTask() != null)
                                deathState.getUpdateTimerTask().cancel();

                            if (deathState.getStand() != null) {
                                deathState.getStand().removePassenger(e.getEntity());
                                deathState.getStand().remove();
                            }

                            if (deathState.getHologram() != null)
                                deathState.getHologram().remove();

                            if (deathState.getTimerHolo() != null)
                                deathState.getTimerHolo().remove();

                            profile.setDeathState(null);
                            ((Player) e.getEntity()).setHealth(0);

                            if (plugin.isDebug()) {
                                Bukkit.getLogger().info("(RevivePlugin): True death by " + e.getDamager());
                                Bukkit.getLogger().info("(RevivePlugin): " + profile.getDeathState());
                                Bukkit.getLogger().info("(RevivePlugin): " + profile.isDead());
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDeath(EntityDamageEvent e) {
        if (e.getEntity() != null) {
            if (!e.isCancelled() && e.getEntity() instanceof Player) {
                double playerHP = ((Player) e.getEntity()).getHealth();
                double damage = e.getDamage();

                EntityDamageEvent.DamageCause cause = e.getCause();

                if (!cause.equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK) || !cause.equals(EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK)) {
                    if (playerHP - damage <= 0) {
                        DeathProfile profile = plugin.getProfileManager().getProfile((Player) e.getEntity());

                        if (profile.isDead()) {
                            profile.killOther((Player) e.getEntity());
                            ((Player) e.getEntity()).setHealth(1);
                            profile.getDeathState().spawnHolo("&c&l*DEAD*");
                            e.setCancelled(true);
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void regenHealthEvent(EntityRegainHealthEvent e) {
        if (!e.isCancelled() && e.getEntity() instanceof Player) {
            Player player = (Player) e.getEntity();
            DeathProfile profile = plugin.getProfileManager().getProfile(player);

            if (profile.isDead() || profile.getDeathState() != null) {
                e.setCancelled(true);
            }
        }
    }

}
