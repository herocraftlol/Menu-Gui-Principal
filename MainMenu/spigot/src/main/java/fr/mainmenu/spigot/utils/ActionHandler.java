package fr.mainmenu.spigot.utils;

import fr.mainmenu.spigot.MainMenuSpigot;
import fr.mainmenu.spigot.gui.MenuGui;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

/**
 * Exécute les actions configurables (CONNECT, COMMAND, URL, ...) que ce soit
 * depuis un clic dans le GUI ou depuis un item de la hotbar personnalisée.
 */
public final class ActionHandler {

    private ActionHandler() {
    }

    public static void execute(MainMenuSpigot plugin, MenuGui menuGui, Player player,
                                String actionType, String actionValue, boolean closeInventory) {
        switch (actionType.toUpperCase()) {
            case "CONNECT" -> {
                if (closeInventory) player.closeInventory();
                String msg = plugin.getConfigManager().getMessage("teleporting")
                        .replace("%server%", actionValue);
                player.sendMessage(msg);
                plugin.sendToServer(player, actionValue);
            }
            case "COMMAND" -> {
                if (closeInventory) player.closeInventory();
                player.performCommand(actionValue.replace("%player%", player.getName()));
            }
            case "CONSOLE_COMMAND" -> {
                if (closeInventory) player.closeInventory();
                String cmd = actionValue.replace("%player%", player.getName());
                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), cmd);
            }
            case "URL" -> {
                if (closeInventory) player.closeInventory();
                TextComponent message = new TextComponent(
                        plugin.getConfigManager().getMessage("click-link")
                                .replace("%url%", actionValue));
                message.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, actionValue));
                player.spigot().sendMessage(message);
            }
            case "MESSAGE" -> {
                if (closeInventory) player.closeInventory();
                player.sendMessage(actionValue.replace("&", "§")
                        .replace("%player%", player.getName()));
            }
            case "OPEN_MENU" -> {
                // Ouvre le GUI du menu principal (utilisé par la hotbar personnalisée)
                if (menuGui != null) menuGui.open(player);
            }
            case "CLOSE" -> player.closeInventory();
            case "NONE" -> { /* Rien */ }
            default -> plugin.getLogger().warning("Action inconnue: " + actionType);
        }
    }
}
