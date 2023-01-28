package dev.echo.oxi.systems.items;

import dev.echo.oxi.OxiPlugin;
import dev.echo.oxi.files.InfoFile;
import dev.echo.oxi.systems.blocks.CustomBlock;
import dev.echo.utils.general.PluginInstance;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class CustomItemManager {


    public static CustomItem read(String string) {
        return new CustomItem(string.toLowerCase());
    }

    public static boolean hasBlock(ItemStack stack) {
        if (stack == null || !stack.hasItemMeta()) return false;

        ItemMeta meta = stack.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();

        NamespacedKey id = new NamespacedKey("", "customid");

        InfoFile file = ((OxiPlugin) PluginInstance.getSpigotClass()).getInfoFile();

        if (!container.has(id, PersistentDataType.STRING)) return false;
        return file.contains("blocks." + container.get(id, PersistentDataType.STRING));
    }
    public static CustomBlock getCustomBlock(ItemStack stack) {
        if (stack == null || !stack.hasItemMeta()) return null;

        ItemMeta meta = stack.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();

        NamespacedKey id = new NamespacedKey("", "customid");
        if (!container.has(id, PersistentDataType.STRING)) return null;

        return new CustomBlock(container.get(id,PersistentDataType.STRING));
    }
}
