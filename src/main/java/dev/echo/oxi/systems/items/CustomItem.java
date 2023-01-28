package dev.echo.oxi.systems.items;

import dev.echo.oxi.OxiPlugin;
import dev.echo.oxi.files.InfoFile;
import dev.echo.utils.general.Color;
import dev.echo.utils.general.PluginInstance;
import dev.echo.utils.general.text.SpecialText;
import dev.echo.utils.spigot.api.gui.ItemBuilder;
import lombok.AccessLevel;
import lombok.Getter;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.regex.Pattern;

public class CustomItem {

    private final String id;
    private final InfoFile file = ((OxiPlugin) PluginInstance.getSpigotClass()).getInfoFile();

    @Getter
    private final Attributes attributes;
    @Getter(AccessLevel.PRIVATE)
    private ColorAttribute colorAttributes;

    public CustomItem(String id) {
        this.id = id;
        this.attributes = new Attributes();
        read();
    }

    void read() {
        try {
            attributes.name = file.getT(String.format("items.%s.name", id));
            attributes.material = Material.getMaterial(file.getT(String.format("items.%s.material", id)).toString().toUpperCase());
            attributes.glint = file.getTOrDefault(String.format("items.%s.resources.glint", id), false);
            attributes.customModelData = file.getTOrDefault(String.format("items.%s.resources.model-data", id), 0);
            //TODO Add minecraft attributes

            for (String lines : attributes.lore) {
                if (lines.matches(Pattern.compile("^(&\\w+)").pattern())) continue;

                attributes.lore.remove(lines);
                attributes.lore.add("&7" + lines);
            }
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage(Color.c("&cSomething went wrong when trying to read the file!"));
        }
    }

    ColorAttribute readColorAttributes() {
        ColorAttribute attribute = new ColorAttribute();
        if (file.contains(String.format("items.%s.resources.color", id))) {
            attribute.red = file.getTOrDefault(String.format("items.%s.resources.color.red", id), 0);
            attribute.blue = file.getTOrDefault(String.format("items.%s.resources.color.blue", id), 0);
            attribute.green = file.getTOrDefault(String.format("items.%s.resources.color.green", id), 0);
            if (file.contains(String.format("items.%s.resources.color.hex", id)))
                attribute.hex = file.getTOrDefault(String.format("items.%s.resources.color.hex", id), "#000000");
        } else {
            return null;
        }
        return attribute;
    }

    public ItemStack getItemStack() {
        Material material = this.attributes.material;
        ColorAttribute colorAttributes = readColorAttributes();
        ItemStack stack = ItemBuilder.build(material, builder -> {
            builder.setDisplayName(Color.c(SpecialText.toSmallCapFont(attributes.name)));
            builder.setCustomModelData(attributes.customModelData);
            builder.addItemFlags(ItemFlag.values());
            builder.setGlow(attributes.glint);
            if (isDyeable(material)) {
                if (colorAttributes != null)
                    if (colorAttributes.hex == null)
                        builder.setColor(org.bukkit.Color.fromRGB(colorAttributes.red, colorAttributes.green, colorAttributes.blue));
                    else {
                        if (!colorAttributes.hex.isEmpty() && SpecialText.isValidHexCode(colorAttributes.hex))
                            builder.setColor(Color.hexToRGB(colorAttributes.hex));
                    }

            }
        });

        ItemMeta meta = stack.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();

        container.set(new NamespacedKey("","customid"), PersistentDataType.STRING,id);
        stack.setItemMeta(meta);
        return stack;
    }

    boolean isDyeable(Material material) {
        if (material == null || material.isAir()) return false;
        return material.name().startsWith("LEATHER");
    }

    static class Attributes {

        Material material;
        int customModelData;
        String name;
        List<String> lore;
        boolean glint;

        //(\<\w+\>(.*)\<\\\w+\>)

    }

    static class ColorAttribute {
        int red;
        int green;
        int blue;
        String hex;
    }
}
