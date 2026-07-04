package fr.mainmenu.spigot.listeners;

import fr.mainmenu.spigot.MainMenuSpigot;
import fr.mainmenu.spigot.gui.MenuGui;
import fr.mainmenu.spigot.managers.ConfigManager;
import fr.mainmenu.spigot.utils.ActionHandler;
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

        // Verifie les items configurables
        for (ConfigManager.MenuItem item : plugin.getConfigManager().getMenuItems()) {
            if (item.getSlot() == slot) {
                ActionHandler.execute(plugin, menuGui, player, item.getActionType(), item.getActionValue(), true);
                return;
            }
        }

        // Verifie les serveurs
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
}
