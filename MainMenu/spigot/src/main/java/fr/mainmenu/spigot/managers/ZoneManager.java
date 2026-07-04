package fr.mainmenu.spigot.managers;

import fr.mainmenu.spigot.MainMenuSpigot;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Gère les zones de "hub" définies par les administrateurs (via /hubzone pos1 et pos2).
 * Une zone est un cuboïde délimité par deux coordonnées, associée à un monde Spigot.
 * Quand un joueur se trouve dans cette zone, sa hotbar est remplacée par la hotbar
 * personnalisée (voir HotbarManager).
 */
public class ZoneManager {

    private final MainMenuSpigot plugin;
    private final File file;
    private final Map<String, Zone> zones = new HashMap<>(); // clé = nom du monde
    private final Map<UUID, Location> pendingPos1 = new HashMap<>(); // sélection temporaire pos1 par joueur

    public ZoneManager(MainMenuSpigot plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "zones.yml");
        load();
    }

    // === Sélection pos1 / pos2 ===

    public void setPos1(Player player) {
        pendingPos1.put(player.getUniqueId(), player.getLocation());
    }

    /**
     * Définit pos2 et sauvegarde la zone pour le monde du joueur.
     * @return true si la zone a bien été créée/mise à jour
     */
    public boolean setPos2(Player player) {
        Location pos1 = pendingPos1.get(player.getUniqueId());
        if (pos1 == null) return false;

        Location pos2 = player.getLocation();
        World world1 = pos1.getWorld();
        World world2 = pos2.getWorld();
        if (world1 == null || world2 == null || !world1.equals(world2)) {
            return false;
        }

        Zone zone = Zone.fromLocations(world1.getName(), pos1, pos2);
        zones.put(world1.getName(), zone);
        pendingPos1.remove(player.getUniqueId());
        save();
        return true;
    }

    public boolean hasPendingPos1(Player player) {
        return pendingPos1.containsKey(player.getUniqueId());
    }

    public boolean removeZone(String worldName) {
        return zones.remove(worldName) != null;
    }

    public Zone getZone(String worldName) {
        return zones.get(worldName);
    }

    public boolean isInZone(Location loc) {
        if (loc == null || loc.getWorld() == null) return false;
        Zone zone = zones.get(loc.getWorld().getName());
        if (zone == null) return false;
        return zone.contains(loc);
    }

    // === Persistance (zones.yml) ===

    private void load() {
        zones.clear();
        if (!file.exists()) return;

        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = cfg.getConfigurationSection("zones");
        if (section == null) return;

        for (String worldName : section.getKeys(false)) {
            ConfigurationSection z = section.getConfigurationSection(worldName);
            if (z == null) continue;

            Zone zone = new Zone(
                    worldName,
                    z.getInt("min-x"), z.getInt("min-y"), z.getInt("min-z"),
                    z.getInt("max-x"), z.getInt("max-y"), z.getInt("max-z")
            );
            zones.put(worldName, zone);
        }
    }

    private void save() {
        FileConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<String, Zone> entry : zones.entrySet()) {
            Zone zone = entry.getValue();
            String path = "zones." + entry.getKey();
            cfg.set(path + ".min-x", zone.minX);
            cfg.set(path + ".min-y", zone.minY);
            cfg.set(path + ".min-z", zone.minZ);
            cfg.set(path + ".max-x", zone.maxX);
            cfg.set(path + ".max-y", zone.maxY);
            cfg.set(path + ".max-z", zone.maxZ);
        }
        try {
            if (!plugin.getDataFolder().exists()) plugin.getDataFolder().mkdirs();
            cfg.save(file);
        } catch (IOException e) {
            plugin.getLogger().warning("Impossible de sauvegarder zones.yml : " + e.getMessage());
        }
    }

    /**
     * Cuboïde représentant une zone de hub sur un monde donné.
     */
    public static class Zone {
        public final String worldName;
        public final int minX, minY, minZ, maxX, maxY, maxZ;

        public Zone(String worldName, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
            this.worldName = worldName;
            this.minX = minX;
            this.minY = minY;
            this.minZ = minZ;
            this.maxX = maxX;
            this.maxY = maxY;
            this.maxZ = maxZ;
        }

        public static Zone fromLocations(String worldName, Location a, Location b) {
            int minX = Math.min(a.getBlockX(), b.getBlockX());
            int minY = Math.min(a.getBlockY(), b.getBlockY());
            int minZ = Math.min(a.getBlockZ(), b.getBlockZ());
            int maxX = Math.max(a.getBlockX(), b.getBlockX());
            int maxY = Math.max(a.getBlockY(), b.getBlockY());
            int maxZ = Math.max(a.getBlockZ(), b.getBlockZ());
            return new Zone(worldName, minX, minY, minZ, maxX, maxY, maxZ);
        }

        public boolean contains(Location loc) {
            double x = loc.getX();
            double y = loc.getY();
            double z = loc.getZ();
            return x >= minX && x <= maxX + 1
                    && y >= minY && y <= maxY + 1
                    && z >= minZ && z <= maxZ + 1;
        }
    }
}
