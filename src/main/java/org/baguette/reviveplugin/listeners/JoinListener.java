package org.baguette.reviveplugin.listeners;

import org.baguette.reviveplugin.RevivePlugin;
import org.baguette.reviveplugin.profiles.DeathProfile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinListener implements Listener {

    private RevivePlugin plugin;

    public JoinListener(RevivePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        DeathProfile profile = plugin.getProfileManager().getProfile(player);

        Bukkit.getLogger().info("(RevivePlugin): " + profile.isDead() + " " + profile.getDeathState());
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (profile.getDeathState() == null && profile.isDead()) {
                player.teleport(player.getLocation().add(0, .4f, 0));
                profile.killOther(e.getPlayer());
                profile.getDeathState().spawnHolo("&c&l*DEAD*");
            }
        }, 5L);

        if (plugin.isDebug())
            Bukkit.getLogger().info("(RevivePlugin): Profile found/created for " + player.getName());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        DeathProfile profile = plugin.getProfileManager().getProfile(player);

        if (profile.getDeathState() != null) {
            if (profile.getDeathState().getStand() != null)
                profile.getDeathState().getStand().remove();
            if (profile.getDeathState().getHologram() != null)
                profile.getDeathState().getHologram().remove();
            if (profile.getDeathState().getTimerHolo() != null)
                profile.getDeathState().getTimerHolo().remove();
            profile.setDeathState(null);
        }

        if (plugin.isDebug())
            Bukkit.getLogger().info("(RevivePlugin): DeathState removed for " + player.getName());
    }

}
