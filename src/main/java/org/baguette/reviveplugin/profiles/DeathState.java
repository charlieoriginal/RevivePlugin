package org.baguette.reviveplugin.profiles;

import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ChatColor;
import org.baguette.reviveplugin.RevivePlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;

public class DeathState {

    @Getter private ArmorStand stand;
    @Getter private ArmorStand hologram;
    @Getter private ArmorStand timerHolo;
    @Getter @Setter private long deathTime;
    @Getter private Player player;
    @Getter private BukkitTask updateTimerTask;

    public DeathState(Player player) {
        Location location = player.getLocation().subtract(0, 1.6f, 0);

        //Spawn the armor stand
        this.stand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        this.stand.setCollidable(false);
        this.stand.setGravity(false);
        this.stand.setInvulnerable(true);
        this.stand.setInvisible(true);

        //Seat the player in the armor stand.
        this.stand.addPassenger(player);
    }

    public void spawnHolo(String text) {
        if (this.hologram != null) {
            this.hologram.remove();
        }

        RevivePlugin plugin = JavaPlugin.getPlugin(RevivePlugin.class);

        text = plugin.getConfig().getString("hologram-top");
        String translated = ChatColor.translateAlternateColorCodes('&', text);
        Location location = this.stand.getLocation().clone().add(0, 2.6f, 0);
        this.hologram = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        this.hologram.setCollidable(false);
        this.hologram.setGravity(false);
        this.hologram.setSmall(true);
        this.hologram.setInvulnerable(true);
        this.hologram.setInvisible(true);
        this.hologram.setCustomName(translated);
        this.hologram.setCustomNameVisible(true);

        String timerText = plugin.getConfig().getString("hologram-timer");
        //The amount of time in seconds until the current value of the timer.
        long timerLength = plugin.getConfig().getInt("timer-length")*1000L;
        long elapsedSeconds = (System.currentTimeMillis() - this.deathTime) / 1000L;
        long difference = timerLength - elapsedSeconds;
        String timerTextWithValue = timerText.replace("%timer%", difference + "s");
        String translatedTimer = ChatColor.translateAlternateColorCodes('&', timerTextWithValue);

        Location timerLoc = this.stand.getLocation().clone().add(0, 2.3f, 0);
        this.timerHolo = (ArmorStand) timerLoc.getWorld().spawnEntity(timerLoc, EntityType.ARMOR_STAND);
        this.timerHolo.setCollidable(false);
        this.timerHolo.setGravity(false);
        this.timerHolo.setSmall(true);
        this.timerHolo.setInvulnerable(true);
        this.timerHolo.setInvisible(true);
        this.timerHolo.setCustomName(translatedTimer);
        this.timerHolo.setCustomNameVisible(true);

        this.deathTime = System.currentTimeMillis();

        updateTimerTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            long currentTime = System.currentTimeMillis();
            long timeLeft = (this.deathTime + (timerLength)) - (currentTime);
            long secondsLeft = timeLeft / 1000;
            String tt = plugin.getConfig().getString("hologram-timer");
            String timerTextNew = tt.replace("%timer%", String.valueOf(secondsLeft));
            String translatedTimerNew = ChatColor.translateAlternateColorCodes('&', timerTextNew + "s");
            this.timerHolo.setCustomName(translatedTimerNew);

            if (timeLeft <= 0) {
                this.updateTimerTask.cancel();
                this.updateTimerTask = null;
            }
        }, 0, 20);
    }

    public boolean byStand(ArmorStand stand) {
        UUID standUUID = stand.getUniqueId();
        UUID compare = this.stand.getUniqueId();
        return standUUID.equals(compare);
    }

}
