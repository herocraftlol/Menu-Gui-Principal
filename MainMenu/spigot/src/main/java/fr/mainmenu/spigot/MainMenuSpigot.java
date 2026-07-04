package fr.mainmenu.spigot;

import fr.mainmenu.spigot.commands.HubZoneCommand;
import fr.mainmenu.spigot.commands.MenuCommand;
import fr.mainmenu.spigot.listeners.GuiClickListener;
import fr.mainmenu.spigot.listeners.HotbarInteractListener;
import fr.mainmenu.spigot.listeners.PlayerJoinListener;
import fr.mainmenu.spigot.listeners.ZoneListener;
import fr.mainmenu.spigot.managers.ConfigManager;
import fr.mainmenu.spigot.managers.HotbarManager;
import fr.mainmenu.spigot.managers.PlayerCountManager;
import fr.mainmenu.spigot.managers.ZoneManager;
import fr.mainmenu.spigot.messaging.PluginMessageReceiver;
import org.bukkit.plugin.java.JavaPlugin;

public class MainMenuSpigot extends JavaPlugin {

    private static MainMenuSpigot instance;
    private ConfigManager configManager;
    private PlayerCountManager playerCountManager;
    private ZoneManager zoneManager;
    private HotbarManager hotbarManager;

    @Override
    public void onEnable() {
        instance = this;

        // Chargement de la config
        saveDefaultConfig();
        configManager = new ConfigManager(this);
        playerCountManager = new PlayerCountManager();
        zoneManager = new ZoneManager(this);
        hotbarManager = new HotbarManager(this);

        // Canaux de messaging BungeeCord
        getServer().getMessenger().registerOutgoingPluginChannel(this, "mainmenu:data");
        getServer().getMessenger().registerIncomingPluginChannel(this, "mainmenu:data",
                new PluginMessageReceiver(this));

        // Commandes
        getCommand("menu").setExecutor(new MenuCommand(this));
        getCommand("hubzone").setExecutor(new HubZoneCommand(this));
        getCommand("hub").setExecutor((sender, cmd, label, args) -> {
            if (sender instanceof org.bukkit.entity.Player player) {
                sendToServer(player, configManager.getHubServer());
            }
            return true;
        });
        getCommand("lobby").setExecutor((sender, cmd, label, args) -> {
            if (sender instanceof org.bukkit.entity.Player player) {
                sendToServer(player, configManager.getHubServer());
            }
            return true;
        });

        // Listeners
        getServer().getPluginManager().registerEvents(new GuiClickListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new ZoneListener(this), this);
        getServer().getPluginManager().registerEvents(new HotbarInteractListener(this), this);

        getLogger().info("MainMenu Spigot Plugin activé !");
    }

    @Override
    public void onDisable() {
        getServer().getMessenger().unregisterOutgoingPluginChannel(this);
        getServer().getMessenger().unregisterIncomingPluginChannel(this);
        getLogger().info("MainMenu Spigot Plugin désactivé !");
    }

    /**
     * Envoie un joueur vers un serveur BungeeCord.
     */
    public void sendToServer(org.bukkit.entity.Player player, String serverName) {
        try {
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            java.io.DataOutputStream out = new java.io.DataOutputStream(baos);
            out.writeUTF("ConnectTo");
            out.writeUTF(player.getName());
            out.writeUTF(serverName);
            player.sendPluginMessage(this, "mainmenu:data", baos.toByteArray());
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Demande une mise à jour du nombre de joueurs au BungeeCord.
     */
    public void requestPlayerCount(org.bukkit.entity.Player player) {
        try {
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            java.io.DataOutputStream out = new java.io.DataOutputStream(baos);
            out.writeUTF("RequestPlayerCount");
            player.sendPluginMessage(this, "mainmenu:data", baos.toByteArray());
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    public static MainMenuSpigot getInstance() { return instance; }
    public ConfigManager getConfigManager() { return configManager; }
    public PlayerCountManager getPlayerCountManager() { return playerCountManager; }
    public ZoneManager getZoneManager() { return zoneManager; }
    public HotbarManager getHotbarManager() { return hotbarManager; }
}
