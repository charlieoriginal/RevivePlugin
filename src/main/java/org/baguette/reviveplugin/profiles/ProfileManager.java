package org.baguette.reviveplugin.profiles;

import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProfileManager {

    @Getter private List<DeathProfile> profiles = new ArrayList<>();

    public void addProfile(Player player) {
        UUID compare = player.getUniqueId();
        boolean hasProfile = false;
        for (DeathProfile profile : profiles) {
            if (profile.getUuid().equals(compare)) {
                hasProfile = true;
                break;
            }
        }
        if (!hasProfile) {
            profiles.add(new DeathProfile(player.getUniqueId()));
        }
    }

    public DeathProfile getProfile(UUID uuid) {
        for (DeathProfile profile : profiles) {
            if (profile.getUuid().equals(uuid)) {
                return profile;
            }
        }
        DeathProfile profile = new DeathProfile(uuid);
        profiles.add(profile);
        return profile;
    }

    public DeathProfile getProfile(Player player) {
        UUID uuid = player.getUniqueId();
        for (DeathProfile profile : profiles) {
            if (profile.getUuid().equals(uuid)) {
                return profile;
            }
        }
        DeathProfile profile = new DeathProfile(uuid);
        profiles.add(profile);
        return profile;
    }

}
