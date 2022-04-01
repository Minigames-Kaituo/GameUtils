package fun.kaituo.utils;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class ItemStackBuilder {
    ItemStack stack;
    ItemMeta meta;

    public ItemStackBuilder(Material material) {
        stack = new ItemStack(material);
        meta = stack.getItemMeta();
    }

    public ItemStackBuilder setAmount(int amount) {
        this.stack.setAmount(amount);
        return this;
    }

    public ItemStackBuilder setDisplayName(String name) {
        this.meta.setDisplayName(name);
        return this;
    }

    public ItemStackBuilder setLore(String... loreText) {
        this.meta.setLore(Arrays.stream(loreText).toList());
        return this;
    }

    public ItemStackBuilder addEnchantment(Enchantment enchantment) {
        return this.addEnchantment(enchantment, 1);
    }

    public ItemStackBuilder addEnchantment(Enchantment enchantment, int level) {
        return this.addEnchantment(enchantment, level, true);
    }

    public ItemStackBuilder addEnchantment(Enchantment enchantment, int level, boolean ignoreLevelRestriction) {
        this.meta.addEnchant(enchantment, level, ignoreLevelRestriction);
        return this;
    }

    public ItemStackBuilder removeEnchantment(Enchantment enchantment) {
        this.meta.removeEnchant(enchantment);
        return this;
    }

    public ItemStackBuilder addFlags(ItemFlag... itemFlags) {
        this.meta.addItemFlags(itemFlags);
        return this;
    }

    public ItemStackBuilder removeFlags(ItemFlag... itemFlags) {
        this.meta.removeItemFlags(itemFlags);
        return this;
    }

    public ItemStackBuilder setUnbreakable(boolean unbreakable) {
        this.meta.setUnbreakable(unbreakable);
        return this;
    }

    public ItemStack build() {
        stack.setItemMeta(meta);
        return stack;
    }
}
