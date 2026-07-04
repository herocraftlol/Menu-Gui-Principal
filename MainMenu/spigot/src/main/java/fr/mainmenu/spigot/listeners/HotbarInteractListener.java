package fr.mainmenu.spigot.listeners;

import fr.mainmenu.spigot.MainMenuSpigot;
import fr.mainmenu.spigot.gui.MenuGui;
import fr.mainmenu.spigot.managers.ConfigManager;
import fr.mainmenu.spigot.utils.ActionHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.EquipmentSlot;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Gère les clics sur les items de la hotbar personnalisée (menu, boutique, /friend,
 * discord, site, voter) et empêche de les jeter ou de les déplacer tant que le
 * joueur est dans la zone de hub.
 */
public class HotbarInteractListener implements Listener {

    private final MainMenuSpigot plugin;
    private final MenuGui menuGui;
    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private static final long COOLDOWN_MS = 300L;

    public HotbarInteractListener(MainMenuSpigot plugin) {
        this.plugin = plugin;
        this.menuGui = new MenuGui(plugin);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;

        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK
                && action != Action.LEFT_CLICK_AIR && action != Action.LEFT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        if (!plugin.getHotbarManager().isInZone(player)) return;

        ItemStack item = event.getItem();
        String id = plugin.getHotbarManager().getHotbarItemId(item);
        if (id == null) return;

        event.setCancelled(true);

        // Anti double-déclenchement (RIGHT_CLICK_BLOCK + PHYSICAL, etc.)
        long now = System.currentTimeMillis();
        Long last = cooldowns.get(player.getUniqueId());
        if (last != null && now - last < COOLDOWN_MS) return;
        cooldowns.put(player.getUniqueId(), now);

        for (ConfigManager.MenuItem hotbarItem : plugin.getConfigManager().getHotbarItems()) {
            if (hotbarItem.getId().equals(id)) {
                ActionHandler.execute(plugin, menuGui, player,
                        hotbarItem.getActionType(), hotbarItem.getActionValue(), false);
                return;
            }
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        String id = plugin.getHotbarManager().getHotbarItemId(event.getItemDrop().getItemStack());
        if (id != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onSwapHands(PlayerSwapHandItemsEvent event) {
        if (plugin.getHotbarManager().isInZone(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!plugin.getHotbarManager().isInZone(player)) return;

        // Empêche de déplacer/jeter les items de la hotbar personnalisée depuis l'inventaire (touche E)
        String clickedId = plugin.getHotbarManager().getHotbarItemId(event.getCurrentItem());
        String cursorId = plugin.getHotbarManager().getHotbarItemId(event.getCursor());

        if (clickedId != null || cursorId != null) {
            event.setCancelled(true);
        }
    }
}
