package org.baguette.reviveplugin.listeners;

import org.baguette.reviveplugin.RevivePlugin;
import org.baguette.reviveplugin.profiles.DeathProfile;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;

public class ReviveListener implements Listener {

    private RevivePlugin plugin;

    public ReviveListener(RevivePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRevive(PlayerInteractAtEntityEvent e) {
        Material item = Material.valueOf(plugin.getConfig().getString("revival-item"));
        Entity inter = e.getRightClicked();
        if (inter instanceof Player) {
            Player player = e.getPlayer();
            Inventory pInv = player.getInventory();
            if (((PlayerInventory) pInv).getItem(e.getHand()).getType().equals(item)) {
                Player target = (Player) inter;
                decrementItem(e.getHand(), e.getPlayer());
                Sound reviveSound = Sound.valueOf(plugin.getConfig().getString("revive-sound"));
                player.playSound(player, reviveSound, 1f, 1f);
                String revivalMessage = plugin.getConfig().getString("messages.revived-player");
                String revivalMessageTarget = plugin.getConfig().getString("messages.revived-target");
                revivalMessage = revivalMessage.replace("%player%", target.getName());
                revivalMessageTarget = revivalMessageTarget.replace("%player%", player.getName());
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', revivalMessage));
                target.sendMessage(ChatColor.translateAlternateColorCodes('&', revivalMessageTarget));
                DeathProfile profile = plugin.getProfileManager().getProfile(target);
                if (profile.isDead() && profile.getDeathState() != null) {
                    profile.revive(target);
                }
            }
        }
    }

    public void decrementItem(EquipmentSlot slot, Player player) {
        PlayerInventory inv = player.getInventory();
        if (inv.getItem(slot).getAmount() > 1) {
            inv.getItem(slot).setAmount(inv.getItem(slot).getAmount() - 1);
        } else {
            inv.setItem(slot, null);
        }
    }

}
