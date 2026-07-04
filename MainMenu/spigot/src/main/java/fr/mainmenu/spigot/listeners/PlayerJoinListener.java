package fr.mainmenu.spigot.listeners;

import fr.mainmenu.spigot.MainMenuSpigot;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private final MainMenuSpigot plugin;

    public PlayerJoinListener(MainMenuSpigot plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Demande les counts à BungeeCord à chaque connexion
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (event.getPlayer().isOnline()) {
                plugin.requestPlayerCount(event.getPlayer());
            }
        }, 20L); // 1 seconde de délai pour que BungeeCord soit prêt
    }
}
