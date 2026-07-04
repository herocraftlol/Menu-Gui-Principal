package fr.mainmenu.spigot.utils;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ItemBuilder {

    private final ItemStack item;
    private final ItemMeta meta;

    public ItemBuilder(String materialName) {
        Material material;
        try {
            material = Material.valueOf(materialName.toUpperCase());
        } catch (IllegalArgumentException e) {
            material = Material.STONE;
        }
        this.item = new ItemStack(material);
        this.meta = item.getItemMeta();
    }

    public ItemBuilder(Material material) {
        this.item = new ItemStack(material);
        this.meta = item.getItemMeta();
    }

    public ItemBuilder setName(String name) {
        if (meta != null) meta.setDisplayName(name);
        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        if (meta != null && lore != null && !lore.isEmpty()) meta.setLore(lore);
        return this;
    }

    public ItemBuilder setCustomModelData(int data) {
        if (meta != null && data > 0) meta.setCustomModelData(data);
        return this;
    }

    public ItemBuilder setGlowing() {
        if (meta != null) {
            meta.addEnchant(Enchantment.LUCK, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        return this;
    }

    public ItemBuilder hideFlags() {
        if (meta != null) meta.addItemFlags(ItemFlag.values());
        return this;
    }

    public ItemStack build() {
        item.setItemMeta(meta);
        return item;
    }
}
