package dev.echo.oxi.guis;

import dev.echo.oxi.OxiPlugin;
import dev.echo.oxi.systems.items.CustomItemManager;
import dev.echo.utils.general.PluginInstance;
import dev.echo.utils.spigot.api.gui.GlassType;
import dev.echo.utils.spigot.api.gui.PaginatedGui;
import dev.echo.utils.spigot.api.gui.Size;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class ItemGUI extends PaginatedGui {

    YamlConfiguration configuration = ((OxiPlugin) PluginInstance.getSpigotClass()).getInfoFile().config();

    public ItemGUI() {
        super("Items", Size.SIZE_36);

        setArrows(Size.SIZE_36, true);

        ringInventory(GlassType.BLACK_STAINED_GLASS_PANE);
        List<ItemStack> customItems = new ArrayList<>();
        for (String string : configuration.getConfigurationSection("items").getKeys(false)) {
            System.out.println(string);
            customItems.add(CustomItemManager.read(string).getItemStack());
        }
        setItems(customItems);
    }

    @Override
    public void click(InventoryClickEvent event) {
        event.setCancelled(true);
        super.click(event);
        if (isUtilitySlot(event.getSlot())) return;
        if (event.getCurrentItem() == null) return;
        event.getWhoClicked().getInventory().addItem(event.getCurrentItem());
        ItemStack stack = event.getCurrentItem();

        if (!stack.hasItemMeta()) return;
        ItemMeta meta = stack.getItemMeta();
        event.getWhoClicked().sendMessage(Boolean.toString(meta.getPersistentDataContainer().has(new NamespacedKey(PluginInstance.getSpigotClass(),
                "customid"), PersistentDataType.STRING)));
        if (meta.getPersistentDataContainer().has(new NamespacedKey(PluginInstance.getSpigotClass(), "customid"), PersistentDataType.STRING)) {
            String foundID = meta.getPersistentDataContainer().get(new NamespacedKey(PluginInstance.getSpigotClass(), "customid"),
                    PersistentDataType.STRING);
            event.getWhoClicked().sendMessage(foundID);
        }
    }
}
