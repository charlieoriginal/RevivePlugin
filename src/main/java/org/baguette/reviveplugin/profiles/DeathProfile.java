package org.baguette.reviveplugin.profiles;

import lombok.Getter;
import lombok.Setter;
import org.baguette.reviveplugin.RevivePlugin;
import org.baguette.reviveplugin.events.RPPlayerKillEvent;
import org.baguette.reviveplugin.events.RPPlayerReviveEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;

public class DeathProfile {

    @Getter private UUID uuid;
    @Getter @Setter
    private boolean isDead;
    @Getter @Setter
    private DeathState deathState;
    @Getter
    private BukkitTask activeTask;

    public DeathProfile(UUID uuid) {
        this.uuid = uuid;
        this.isDead = false;
    }

    public boolean killByPlayer(Player target, Player killer) {
        RPPlayerKillEvent event = new RPPlayerKillEvent(killer);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return false;
        }

        this.isDead = true;
        this.deathState = new DeathState(target);

        RevivePlugin plugin = JavaPlugin.getPlugin(RevivePlugin.class);
        long timerDelay = plugin.getConfig().getInt("timer-length")*20L;
        activeTask = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (isDead()) {
                target.setHealth(0);
                setDead(false);
                if (deathState != null) {
                    if (deathState.getStand() != null) {
                        deathState.getStand().removePassenger(target);
                        deathState.getStand().remove();
                    }

                    if (deathState.getHologram() != null)
                        deathState.getHologram().remove();

                    if (deathState.getTimerHolo() != null)
                        deathState.getTimerHolo().remove();
                }
                setDeathState(null);

                if (plugin.isDebug())
                    Bukkit.getLogger().info("(RevivePlugin) " + target.getName() + " has been killed by the timer.");
            }
        }, timerDelay);

        if ((JavaPlugin.getPlugin(RevivePlugin.class)).isDebug())
            Bukkit.getLogger().info("(RevivePlugin) " + this.uuid.toString() + " has been killed by a player.");

        return true;
    }

    public boolean killOther(Player target) {
        RPPlayerKillEvent event = new RPPlayerKillEvent(null);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return false;
        }

        RevivePlugin plugin = JavaPlugin.getPlugin(RevivePlugin.class);
        for (PotionEffectType type : plugin.getDeathEffects()) {
            PotionEffect effect = new PotionEffect(type, 1000000, 0);
            target.addPotionEffect(effect);
        }

        this.isDead = true;
        this.deathState = new DeathState(target);

        long timerDelay = plugin.getConfig().getInt("timer-length")*20L;
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (isDead()) {
                target.setHealth(0);
                setDead(false);
                if (deathState != null) {
                    if (deathState.getStand() != null) {
                        deathState.getStand().removePassenger(target);
                        deathState.getStand().remove();
                    }

                    if (deathState.getHologram() != null)
                        deathState.getHologram().remove();

                    if (deathState.getTimerHolo() != null)
                        deathState.getTimerHolo().remove();

                    if (plugin.isDebug())
                        Bukkit.getLogger().info("(RevivePlugin) " + target.getName() + " has been killed by the timer.");
                }
                setDeathState(null);
            }
        }, timerDelay);

        if ((JavaPlugin.getPlugin(RevivePlugin.class)).isDebug())
            Bukkit.getLogger().info("(RevivePlugin) " + this.uuid.toString() + " has been killed by a non-player.");

        return true;
    }

    public void revive(Player reviver) {
        if (deathState != null) {
            RPPlayerReviveEvent event = new RPPlayerReviveEvent(reviver);
            Bukkit.getServer().getPluginManager().callEvent(event);

            if (!event.isCancelled()) {
                Player player = Bukkit.getPlayer(uuid);
                if (player != null) {
                    RPPlayerReviveEvent reviveEvent = new RPPlayerReviveEvent(player);
                    Bukkit.getServer().getPluginManager().callEvent(reviveEvent);

                    if (!reviveEvent.isCancelled()) {
                        for (PotionEffectType type : PotionEffectType.values()) {
                            player.removePotionEffect(type);
                        }

                        player.setHealth(20D);
                        player.setFireTicks(0);
                        player.setFallDistance(0);
                        player.teleport(player.getLocation().add(0, 1, 0));
                        if (getDeathState().getHologram() != null)
                            getDeathState().getHologram().remove();
                        if (getDeathState().getTimerHolo() != null)
                            getDeathState().getTimerHolo().remove();
                        if (getDeathState().getStand() != null) {
                            getDeathState().getStand().removePassenger(player);
                            getDeathState().getStand().remove();
                        }
                        if (getDeathState().getUpdateTimerTask() != null && !getDeathState().getUpdateTimerTask().isCancelled()) {
                            getDeathState().getUpdateTimerTask().cancel();
                        }
                        setDeathState(null);
                        setDead(false);
                        if (activeTask != null && !activeTask.isCancelled())
                            activeTask.cancel();

                        if ((JavaPlugin.getPlugin(RevivePlugin.class)).isDebug())
                            Bukkit.getLogger().info("(RevivePlugin) " + this.uuid.toString() + " has been successfully revived.");
                    }
                }
            }
        }
    }

}
