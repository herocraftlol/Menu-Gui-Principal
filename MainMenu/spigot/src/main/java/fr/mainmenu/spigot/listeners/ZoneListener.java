package fr.mainmenu.spigot.listeners;

import fr.mainmenu.spigot.MainMenuSpigot;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 * Détecte les entrées/sorties de la zone de hub définie via /hubzone,
 * et bascule la hotbar personnalisée en conséquence (voir HotbarManager).
 */
public class ZoneListener implements Listener {

    private final MainMenuSpigot plugin;

    public ZoneListener(MainMenuSpigot plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.getTo() == null) return;
        // On évite de vérifier à chaque micro-mouvement de la caméra
        if (event.getFrom().getBlockX() == event.getTo().getBlockX()
                && event.getFrom().getBlockY() == event.getTo().getBlockY()
                && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }
        checkZone(event.getPlayer());
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        plugin.getServer().getScheduler().runTask(plugin, () -> checkZone(event.getPlayer()));
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        checkZone(event.getPlayer());
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        plugin.getServer().getScheduler().runTask(plugin, () -> checkZone(event.getPlayer()));
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (event.getPlayer().isOnline()) checkZone(event.getPlayer());
        }, 5L);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        plugin.getHotbarManager().handleQuit(event.getPlayer());
    }

    private void checkZone(Player player) {
        boolean nowIn = plugin.getZoneManager().isInZone(player.getLocation());
        boolean wasIn = plugin.getHotbarManager().isInZone(player);

        if (nowIn && !wasIn) {
            plugin.getHotbarManager().enterZone(player);
        } else if (!nowIn && wasIn) {
            plugin.getHotbarManager().exitZone(player);
        }
    }
}
