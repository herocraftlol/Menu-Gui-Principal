package fr.mainmenu.spigot.listeners;

import fr.mainmenu.spigot.MainMenuSpigot;
import fr.mainmenu.spigot.gui.MenuGui;
import fr.mainmenu.spigot.managers.ConfigManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class GuiClickListener implements Listener {

    private final MainMenuSpigot plugin;
    private final MenuGui menuGui;

    public GuiClickListener(MainMenuSpigot plugin) {
        this.plugin = plugin;
        this.menuGui = new MenuGui(plugin);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getCurrentItem() == null) return;

        String title = event.getView().getTitle();
        String menuTitle = plugin.getConfigManager().getMenuTitle();

        if (!title.equals(menuTitle)) return;

        event.setCancelled(true);

        int slot = event.getRawSlot();

        // Vérifie les items configurables
        for (ConfigManager.MenuItem item : plugin.getConfigManager().getMenuItems()) {
            if (item.getSlot() == slot) {
                handleAction(player, item.getActionType(), item.getActionValue());
                return;
            }
        }

        // Vérifie les serveurs
        for (ConfigManager.ServerEntry server : plugin.getConfigManager().getServers()) {
            if (server.getSlot() == slot) {
                player.closeInventory();
                String msg = plugin.getConfigManager().getMessage("teleporting")
                        .replace("%server%", server.getDisplayName());
                player.sendMessage(msg);
                plugin.sendToServer(player, server.getServerName());
                return;
            }
        }
    }

    private void handleAction(Player player, String actionType, String actionValue) {
        switch (actionType.toUpperCase()) {
            case "CONNECT" -> {
                // Connexion à un serveur BungeeCord
                player.closeInventory();
                String msg = plugin.getConfigManager().getMessage("teleporting")
                        .replace("%server%", actionValue);
                player.sendMessage(msg);
                plugin.sendToServer(player, actionValue);
            }
            case "COMMAND" -> {
                // Commande exécutée par le joueur
                player.closeInventory();
                player.performCommand(actionValue.replace("%player%", player.getName()));
            }
            case "CONSOLE_COMMAND" -> {
                // Commande exécutée par la console
                player.closeInventory();
                String cmd = actionValue.replace("%player%", player.getName());
                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), cmd);
            }
            case "URL" -> {
                // Ouvre un lien dans le chat (cliquable)
                player.closeInventory();
                TextComponent message = new TextComponent(
                        plugin.getConfigManager().getMessage("click-link")
                                .replace("%url%", actionValue));
                message.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, actionValue));
                player.spigot().sendMessage(message);
            }
            case "MESSAGE" -> {
                // Envoie un message au joueur
                player.closeInventory();
                player.sendMessage(actionValue.replace("&", "§")
                        .replace("%player%", player.getName()));
            }
            case "CLOSE" -> player.closeInventory();
            case "NONE" -> { /* Rien */ }
            default -> plugin.getLogger().warning("Action inconnue: " + actionType);
        }
    }
}
