package fr.mainmenu.spigot.gui;

import fr.mainmenu.spigot.MainMenuSpigot;
import fr.mainmenu.spigot.managers.ConfigManager;
import fr.mainmenu.spigot.managers.PlayerCountManager;
import fr.mainmenu.spigot.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class MenuGui {

    private final MainMenuSpigot plugin;

    public MenuGui(MainMenuSpigot plugin) {
        this.plugin = plugin;
    }

    /**
     * Ouvre le menu principal pour le joueur.
     */
    public void open(Player player) {
        ConfigManager cm = plugin.getConfigManager();
        PlayerCountManager pcm = plugin.getPlayerCountManager();

        // Demande la mise à jour des counts avant d'ouvrir
        plugin.requestPlayerCount(player);

        Inventory inv = Bukkit.createInventory(null, cm.getMenuSize(), cm.getMenuTitle());

        // Items configurables (infos joueur, friends, liens, etc.)
        for (ConfigManager.MenuItem item : cm.getMenuItems()) {
            ItemStack stack = buildMenuItem(item, player);
            if (item.getSlot() < inv.getSize()) {
                inv.setItem(item.getSlot(), stack);
            }
        }

        // Items des serveurs BungeeCord avec compteur de joueurs
        for (ConfigManager.ServerEntry server : cm.getServers()) {
            List<String> lore = new ArrayList<>(server.getLore());
            int count = pcm.getCount(server.getServerName());
            // Remplacement du placeholder %players%
            lore.replaceAll(line -> line
                    .replace("%players%", String.valueOf(count))
                    .replace("%server%", server.getDisplayName()));

            ItemStack stack = new ItemBuilder(server.getMaterial())
                    .setName(server.getDisplayName())
                    .setLore(lore)
                    .setCustomModelData(server.getCustomModelData())
                    .build();

            if (server.getSlot() < inv.getSize()) {
                inv.setItem(server.getSlot(), stack);
            }
        }

        player.openInventory(inv);
    }

    private ItemStack buildMenuItem(ConfigManager.MenuItem item, Player player) {
        List<String> lore = new ArrayList<>(item.getLore());

        // Remplacement des placeholders joueur
        lore.replaceAll(line -> replacePlaceholders(line, player));
        String name = replacePlaceholders(item.getName(), player);

        ItemBuilder builder = new ItemBuilder(item.getMaterial())
                .setName(name)
                .setLore(lore)
                .setCustomModelData(item.getCustomModelData());

        if (item.isGlowing()) builder.setGlowing();

        return builder.build();
    }

    private String replacePlaceholders(String s, Player player) {
        return s
                .replace("%player%", player.getName())
                .replace("%displayname%", player.getDisplayName())
                .replace("%health%", String.format("%.1f", player.getHealth()))
                .replace("%level%", String.valueOf(player.getLevel()))
                .replace("%world%", player.getWorld().getName())
                .replace("%ping%", String.valueOf(player.getPing()))
                .replace("%gamemode%", player.getGameMode().name())
                .replace("%food%", String.valueOf(player.getFoodLevel()));
    }
}
