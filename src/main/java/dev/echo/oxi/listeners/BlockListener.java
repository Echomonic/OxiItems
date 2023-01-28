package dev.echo.oxi.listeners;

import dev.echo.oxi.systems.blocks.CustomBlock;
import dev.echo.oxi.systems.blocks.CustomBlockData;
import dev.echo.oxi.systems.items.CustomItemManager;
import dev.echo.oxi.world.CustomBlockFile;
import dev.echo.utils.general.PluginInstance;
import dev.echo.utils.general.Region;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class BlockListener implements Listener {

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        ItemStack stack = event.getItemInHand();

        System.out.println("Is directory: " + player.getWorld().getWorldFolder().isDirectory());


        if (CustomItemManager.hasBlock(stack)) {
            event.setCancelled(true);
            Block placedBlock = event.getBlockPlaced();
            placedBlock.setType(Material.AIR,false);
            CustomBlock block = CustomItemManager.getCustomBlock(stack);
            block.place(event.getBlock().getLocation());
            CustomBlockFile.getCustomBlockFile(player.getWorld()).write(CustomBlockData.Utility.generate(event.getBlock().getLocation()));
            event.setCancelled(false);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        CustomBlock.BlockFile file = CustomBlockFile.getCustomBlockFile(event.getBlock().getWorld());
        Location location = event.getBlock().getLocation();

        if (!file.contains(CustomBlockData.Utility.generate(location))) return;
        CustomBlockData data = file.getBlockData(location);
        file.remove(data);
        event.getBlock().setType(Material.AIR);
    }

    @EventHandler(ignoreCancelled = true)
    public void onUpdate(BlockPhysicsEvent event) {
        Block block = event.getBlock();

        CustomBlock.BlockFile file = CustomBlockFile.getCustomBlockFile(event.getBlock().getWorld());
        Location location = block.getLocation();
        if(file.contains(location)){
            CustomBlockData customBlockData = file.getBlockData(location);
            block.setBlockData(CustomBlockData.Utility.buildBlockData(customBlockData,block),false);
            event.setCancelled(true);
        }
    }
}
