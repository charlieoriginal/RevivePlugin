package org.baguette.reviveplugin.listeners;

import org.baguette.reviveplugin.RevivePlugin;
import org.baguette.reviveplugin.events.RPDenyDismountEvent;
import org.baguette.reviveplugin.profiles.DeathProfile;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.spigotmc.event.entity.EntityDismountEvent;

public class DismountListener implements Listener {

    private RevivePlugin plugin;

    public DismountListener(RevivePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDismount(EntityDismountEvent e) {
        if (e.getEntity() instanceof Player && e.getDismounted() instanceof ArmorStand) {
            DeathProfile profile = plugin.getProfileManager().getProfile((Player) e.getEntity());
            if (profile.isDead() && profile.getDeathState() != null) {
                RPDenyDismountEvent event = new RPDenyDismountEvent((Player) e.getEntity());
                Bukkit.getPluginManager().callEvent(event);

                e.setCancelled(!event.isCancelled());
            }
        }
    }

}
