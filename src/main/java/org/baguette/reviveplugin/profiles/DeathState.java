package org.baguette.reviveplugin.profiles;

import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.UUID;

public class DeathState {

    @Getter private ArmorStand stand;
    @Getter private ArmorStand hologram;
    @Getter private ArmorStand timerHolo;
    @Getter private Player player;

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

        String translated = ChatColor.translateAlternateColorCodes('&', text);
        Location location = this.stand.getLocation().clone().add(0, 2.3f, 0);
        this.hologram = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        this.hologram.setCollidable(false);
        this.hologram.setGravity(false);
        this.hologram.setSmall(true);
        this.hologram.setInvulnerable(true);
        this.hologram.setInvisible(true);
        this.hologram.setCustomName(translated);
        this.hologram.setCustomNameVisible(true);
    }

    public boolean byStand(ArmorStand stand) {
        UUID standUUID = stand.getUniqueId();
        UUID compare = this.stand.getUniqueId();
        return standUUID.equals(compare);
    }

}
