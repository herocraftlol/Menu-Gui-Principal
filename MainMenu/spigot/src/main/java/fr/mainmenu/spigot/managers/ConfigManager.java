package fr.mainmenu.spigot.managers;

import fr.mainmenu.spigot.MainMenuSpigot;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigManager {

    private final MainMenuSpigot plugin;
    private FileConfiguration config;

    public ConfigManager(MainMenuSpigot plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    public void reload() {
        plugin.reloadConfig();
        this.config = plugin.getConfig();
    }

    // === GUI Principal ===
    public String getMenuTitle() {
        return colorize(config.getString("menu.title", "&8✦ &bMenu Principal &8✦"));
    }

    public int getMenuSize() {
        return config.getInt("menu.size", 54);
    }

    // === Serveur Hub ===
    public String getHubServer() {
        return config.getString("bungee.hub-server", "lobby");
    }

    // === Items du menu ===
    public List<MenuItem> getMenuItems() {
        return parseMenuItems(config.getConfigurationSection("menu.items"));
    }

    // === Items de la hotbar personnalisée (zone de hub) ===
    public boolean isHotbarEnabled() {
        return config.getBoolean("hotbar.enabled", true);
    }

    public List<MenuItem> getHotbarItems() {
        return parseMenuItems(config.getConfigurationSection("hotbar.items"));
    }

    private List<MenuItem> parseMenuItems(ConfigurationSection section) {
        List<MenuItem> items = new ArrayList<>();
        if (section == null) return items;

        for (String key : section.getKeys(false)) {
            ConfigurationSection itemSection = section.getConfigurationSection(key);
            if (itemSection == null) continue;

            MenuItem item = new MenuItem();
            item.setId(key);
            item.setSlot(itemSection.getInt("slot", 0));
            item.setMaterial(itemSection.getString("material", "STONE"));
            item.setName(colorize(itemSection.getString("name", key)));
            item.setLore(colorizeList(itemSection.getStringList("lore")));
            item.setCustomModelData(itemSection.getInt("custom-model-data", 0));
            item.setGlowing(itemSection.getBoolean("glowing", false));

            // Action
            String actionType = itemSection.getString("action.type", "NONE");
            item.setActionType(actionType);
            item.setActionValue(itemSection.getString("action.value", ""));

            items.add(item);
        }
        return items;
    }

    // === Serveurs BungeeCord ===
    public List<ServerEntry> getServers() {
        List<ServerEntry> servers = new ArrayList<>();
        ConfigurationSection section = config.getConfigurationSection("servers");
        if (section == null) return servers;

        for (String key : section.getKeys(false)) {
            ConfigurationSection srv = section.getConfigurationSection(key);
            if (srv == null) continue;

            ServerEntry entry = new ServerEntry();
            entry.setId(key);
            entry.setDisplayName(colorize(srv.getString("display-name", key)));
            entry.setServerName(srv.getString("server-name", key));
            entry.setSlot(srv.getInt("slot", 0));
            entry.setMaterial(srv.getString("material", "GRASS_BLOCK"));
            entry.setLore(colorizeList(srv.getStringList("lore")));
            entry.setCustomModelData(srv.getInt("custom-model-data", 0));

            servers.add(entry);
        }
        return servers;
    }

    // === Messages ===
    public String getMessage(String path) {
        return colorize(config.getString("messages." + path, "&cMessage introuvable: " + path));
    }

    // === Utilitaires ===
    private String colorize(String s) {
        if (s == null) return "";
        return s.replace("&", "§");
    }

    private List<String> colorizeList(List<String> list) {
        List<String> result = new ArrayList<>();
        if (list == null) return result;
        for (String s : list) result.add(colorize(s));
        return result;
    }

    // === Classes internes ===
    public static class MenuItem {
        private String id, material, name, actionType, actionValue;
        private List<String> lore = new ArrayList<>();
        private int slot, customModelData;
        private boolean glowing;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getMaterial() { return material; }
        public void setMaterial(String material) { this.material = material; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getActionType() { return actionType; }
        public void setActionType(String actionType) { this.actionType = actionType; }
        public String getActionValue() { return actionValue; }
        public void setActionValue(String actionValue) { this.actionValue = actionValue; }
        public List<String> getLore() { return lore; }
        public void setLore(List<String> lore) { this.lore = lore; }
        public int getSlot() { return slot; }
        public void setSlot(int slot) { this.slot = slot; }
        public int getCustomModelData() { return customModelData; }
        public void setCustomModelData(int customModelData) { this.customModelData = customModelData; }
        public boolean isGlowing() { return glowing; }
        public void setGlowing(boolean glowing) { this.glowing = glowing; }
    }

    public static class ServerEntry {
        private String id, displayName, serverName, material;
        private List<String> lore = new ArrayList<>();
        private int slot, customModelData;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getDisplayName() { return displayName; }
        public void setDisplayName(String displayName) { this.displayName = displayName; }
        public String getServerName() { return serverName; }
        public void setServerName(String serverName) { this.serverName = serverName; }
        public String getMaterial() { return material; }
        public void setMaterial(String material) { this.material = material; }
        public List<String> getLore() { return lore; }
        public void setLore(List<String> lore) { this.lore = lore; }
        public int getSlot() { return slot; }
        public void setSlot(int slot) { this.slot = slot; }
        public int getCustomModelData() { return customModelData; }
        public void setCustomModelData(int customModelData) { this.customModelData = customModelData; }
    }
}
