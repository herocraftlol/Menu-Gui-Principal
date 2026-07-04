package fr.mainmenu.bungee;

import fr.mainmenu.bungee.listeners.PlayerCountListener;
import fr.mainmenu.bungee.messaging.PluginMessageHandler;
import net.md_5.bungee.api.plugin.Plugin;

public class MainMenuBungee extends Plugin {

    private static MainMenuBungee instance;

    @Override
    public void onEnable() {
        instance = this;
        getLogger().info("MainMenu BungeeCord Plugin activé !");

        // Enregistrement du canal de messaging
        getProxy().registerChannel("mainmenu:data");

        // Listeners
        getProxy().getPluginManager().registerListener(this, new PlayerCountListener(this));

        // Handler pour les messages des serveurs Spigot
        getProxy().getPluginManager().registerListener(this, new PluginMessageHandler(this));
    }

    @Override
    public void onDisable() {
        getProxy().unregisterChannel("mainmenu:data");
        getLogger().info("MainMenu BungeeCord Plugin désactivé !");
    }

    public static MainMenuBungee getInstance() {
        return instance;
    }
}
