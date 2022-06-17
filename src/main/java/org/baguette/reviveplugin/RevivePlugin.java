package org.baguette.reviveplugin;

import lombok.Getter;
import org.baguette.reviveplugin.listeners.DeathListener;
import org.baguette.reviveplugin.listeners.DismountListener;
import org.baguette.reviveplugin.listeners.JoinListener;
import org.baguette.reviveplugin.listeners.ReviveListener;
import org.baguette.reviveplugin.profiles.DeathProfile;
import org.baguette.reviveplugin.profiles.ProfileManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public final class RevivePlugin extends JavaPlugin {
    /*
    @class RevivalPlugin.java
    @description Makes a player sit down after they've been killed. Lets them be revived.
    @author wizard-chan#8732
    @version 1.0.0
     */

    @Getter private List<PotionEffectType> deathEffects;
    @Getter private ProfileManager profileManager;
    @Getter private DeathListener deathListener;
    @Getter private JoinListener joinListener;
    @Getter private DismountListener dismountListener;
    @Getter private ReviveListener reviveListener;
    @Getter private boolean debug = false;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.debug = getConfig().getBoolean("debug-mode");
        this.deathEffects = new ArrayList<>();
        for (String effect : getConfig().getStringList("death-effects")) {
            this.deathEffects.add(PotionEffectType.getByName(effect));
        }

        this.profileManager = new ProfileManager();
        this.joinListener = new JoinListener(this);
        this.deathListener = new DeathListener(this);
        this.dismountListener = new DismountListener(this);
        this.reviveListener = new ReviveListener(this);

        getServer().getPluginManager().registerEvents(this.joinListener, this);
        getServer().getPluginManager().registerEvents(this.deathListener, this);
        getServer().getPluginManager().registerEvents(this.dismountListener, this);
        getServer().getPluginManager().registerEvents(this.reviveListener, this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        for (DeathProfile profile : profileManager.getProfiles()) {
            if (profile.getDeathState() != null) {
                if (profile.getDeathState().getStand() != null)
                    profile.getDeathState().getStand().remove();
                if (profile.getDeathState().getHologram() != null)
                    profile.getDeathState().getHologram().remove();
                if (profile.getDeathState().getTimerHolo() != null)
                    profile.getDeathState().getTimerHolo().remove();
            }
        }
    }
}
