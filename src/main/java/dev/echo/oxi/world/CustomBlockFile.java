package dev.echo.oxi.world;

import dev.echo.oxi.systems.blocks.CustomBlock;
import org.bukkit.World;

public class CustomBlockFile {

    public static CustomBlock.BlockFile getCustomBlockFile(World world){


        return new CustomBlock.BlockFile(world);
    }

}
