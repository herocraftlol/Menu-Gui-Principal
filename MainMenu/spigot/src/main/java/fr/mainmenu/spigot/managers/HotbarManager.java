package fr.mainmenu.spigot.managers;

import fr.mainmenu.spigot.MainMenuSpigot;
import fr.mainmenu.spigot.utils.ItemBuilder;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Gère la hotbar personnalisée qui s'affiche uniquement quand un joueur se trouve
 * dans une zone de hub (voir ZoneManager). Sauvegarde/restaure la hotbar d'origine
 * du joueur à l'entrée/sortie de la zone.
 */
public class HotbarManager {

    private final MainMenuSpigot plugin;
    private final NamespacedKey itemIdKey;

    private final Set<UUID> playersInZone = new HashSet<>();
    private final Map<UUID, ItemStack[]> savedHotbars = new HashMap<>();

    public HotbarManager(MainMenuSpigot plugin) {
        this.plugin = plugin;
        this.itemIdKey = new NamespacedKey(plugin, "hotbar_item_id");
    }

    public boolean isInZone(Player player) {
        return playersInZone.contains(player.getUniqueId());
    }

    /**
     * Fait entrer le joueur dans la zone : sauvegarde sa hotbar puis affiche la hotbar personnalisée.
     */
    public void enterZone(Player player) {
        UUID uuid = player.getUniqueId();
        if (playersInZone.contains(uuid)) return;
        if (!plugin.getConfigManager().isHotbarEnabled()) return;

        PlayerInventory inv = player.getInventory();
        ItemStack[] saved = new ItemStack[9];
        for (int i = 0; i < 9; i++) {
            ItemStack current = inv.getItem(i);
            saved[i] = current == null ? null : current.clone();
        }
        savedHotbars.put(uuid, saved);
        playersInZone.add(uuid);

        // Vide la hotbar puis place les items configurés
        for (int i = 0; i < 9; i++) inv.setItem(i, null);

        for (fr.mainmenu.spigot.managers.ConfigManager.MenuItem item : plugin.getConfigManager().getHotbarItems()) {
            if (item.getSlot() < 0 || item.getSlot() > 8) continue;
            inv.setItem(item.getSlot(), buildItem(item));
        }
    }

    /**
     * Fait sortir le joueur de la zone : restaure sa hotbar d'origine.
     */
    public void exitZone(Player player) {
        UUID uuid = player.getUniqueId();
        if (!playersInZone.contains(uuid)) return;

        ItemStack[] saved = savedHotbars.remove(uuid);
        playersInZone.remove(uuid);

        if (saved != null) {
            PlayerInventory inv = player.getInventory();
            for (int i = 0; i < 9; i++) {
                inv.setItem(i, saved[i]);
            }
        }
    }

    /**
     * Nettoyage à la déconnexion : restaure la hotbar sans dépendre du monde/de la zone.
     */
    public void handleQuit(Player player) {
        exitZone(player);
    }

    /**
     * Retourne l'identifiant (clé de config) de l'item de hotbar tenu, ou null si ce n'en est pas un.
     */
    public String getHotbarItemId(ItemStack stack) {
        if (stack == null || stack.getType().isAir()) return null;
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) return null;
        return meta.getPersistentDataContainer().get(itemIdKey, PersistentDataType.STRING);
    }

    private ItemStack buildItem(fr.mainmenu.spigot.managers.ConfigManager.MenuItem item) {
        ItemBuilder builder = new ItemBuilder(item.getMaterial())
                .setName(item.getName())
                .setLore(item.getLore())
                .setCustomModelData(item.getCustomModelData());
        if (item.isGlowing()) builder.setGlowing();

        ItemStack stack = builder.build();
        ItemMeta meta = stack.getItemMeta();
        if (meta != null) {
            meta.getPersistentDataContainer().set(itemIdKey, PersistentDataType.STRING, item.getId());
            stack.setItemMeta(meta);
        }
        return stack;
    }
}
