package fr.mainmenu.spigot.commands;

import fr.mainmenu.spigot.MainMenuSpigot;
import fr.mainmenu.spigot.managers.ZoneManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Commande /hubzone pos1|pos2|remove|info
 * Permet de définir la zone de hub (cuboïde) dans laquelle la hotbar personnalisée
 * est affichée, monde par monde.
 */
public class HubZoneCommand implements CommandExecutor {

    private final MainMenuSpigot plugin;

    public HubZoneCommand(MainMenuSpigot plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("mainmenu.admin")) {
            sender.sendMessage(plugin.getConfigManager().getMessage("no-permission"));
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cCette commande ne peut être utilisée que par un joueur.");
            return true;
        }

        if (args.length != 1) {
            player.sendMessage("§eUtilisation : §f/hubzone <pos1|pos2|remove|info>");
            return true;
        }

        ZoneManager zoneManager = plugin.getZoneManager();

        switch (args[0].toLowerCase()) {
            case "pos1" -> {
                zoneManager.setPos1(player);
                player.sendMessage(plugin.getConfigManager().getMessage("zone-pos1-set"));
            }
            case "pos2" -> {
                if (!zoneManager.hasPendingPos1(player)) {
                    player.sendMessage(plugin.getConfigManager().getMessage("zone-no-pos1"));
                    return true;
                }
                boolean ok = zoneManager.setPos2(player);
                if (ok) {
                    player.sendMessage(plugin.getConfigManager().getMessage("zone-saved")
                            .replace("%world%", player.getWorld().getName()));
                } else {
                    player.sendMessage(plugin.getConfigManager().getMessage("zone-different-world"));
                }
            }
            case "remove" -> {
                boolean removed = zoneManager.removeZone(player.getWorld().getName());
                player.sendMessage(removed
                        ? plugin.getConfigManager().getMessage("zone-removed")
                        : plugin.getConfigManager().getMessage("zone-none"));
            }
            case "info" -> {
                ZoneManager.Zone zone = zoneManager.getZone(player.getWorld().getName());
                if (zone == null) {
                    player.sendMessage(plugin.getConfigManager().getMessage("zone-none"));
                } else {
                    player.sendMessage("§b✦ Zone de hub sur §f" + zone.worldName + "§b :");
                    player.sendMessage("§7Min : §f" + zone.minX + ", " + zone.minY + ", " + zone.minZ);
                    player.sendMessage("§7Max : §f" + zone.maxX + ", " + zone.maxY + ", " + zone.maxZ);
                }
            }
            default -> player.sendMessage("§eUtilisation : §f/hubzone <pos1|pos2|remove|info>");
        }

        return true;
    }
}
