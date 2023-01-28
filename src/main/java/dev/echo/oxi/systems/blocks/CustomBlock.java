package dev.echo.oxi.systems.blocks;

import dev.echo.oxi.OxiPlugin;
import dev.echo.oxi.enums.MushroomType;
import dev.echo.oxi.files.InfoFile;
import dev.echo.oxi.systems.blocks.info.BlockAttributes;
import dev.echo.utils.general.PluginInstance;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.MultipleFacing;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.block.data.type.Tripwire;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import utils.files.Directory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CustomBlock {

    private final String id;

    private final InfoFile file = ((OxiPlugin) PluginInstance.getSpigotClass()).getInfoFile();

    @Getter
    private final Attributes attributes;
    private BlockType blockType;

    public CustomBlock(String id) {
        this.id = id;
        this.attributes = new Attributes();
        read();
    }

    void read() {
        attributes.material = Material.getMaterial(file.getT(String.format("blocks.%s.material", id)));
        attributes.name = file.getT(String.format("blocks.%s.name", id));
        this.blockType = getBlockType(attributes.material);
        readAttributes();
    }

    protected static BlockType getBlockType(Material material) {
        if (BlockType.valueOf(material.name()) != null) return BlockType.valueOf(material.name());
        else return BlockType.AIR;
    }


    void write(Location location) {

    }


    public void place(Location location) {
        Block baseBlock = location.getBlock();
        baseBlock.setType(attributes.material);
        if (blockType == BlockType.NOTE_BLOCK) {
            NoteBlock block = (NoteBlock) baseBlock.getBlockData();
            block.setInstrument(Instrument.valueOf(noteBlockAttributes.getInstrument().toUpperCase().replace(" ", "_")));
            block.setNote(new Note(noteBlockAttributes.getNote()));
            block.setPowered(noteBlockAttributes.isPowered());

            baseBlock.setBlockData(block, false);
        } else if (blockType == BlockType.WIRE) {
            Tripwire tripwire = (Tripwire) baseBlock.getBlockData();
            tripwire.setDisarmed(tripWireAttributes.isDisarmed());
            tripwire.setAttached(tripWireAttributes.isAttached());
            tripwire.setPowered(tripWireAttributes.isPowered());

            boolean[] directions = tripWireAttributes.getDirections();

            tripwire.setFace(BlockFace.NORTH, directions[0]);
            tripwire.setFace(BlockFace.SOUTH, directions[1]);
            tripwire.setFace(BlockFace.EAST, directions[2]);
            tripwire.setFace(BlockFace.WEST, directions[3]);
            baseBlock.setBlockData(tripwire, false);
        } else if (blockType == BlockType.MUSHROOM_BLOCK) {
            BlockData blockData = baseBlock.getBlockData();
            MultipleFacing facing = (MultipleFacing) blockData;

            boolean[] directions = mushroomBlockAttributes.getDirections();

            facing.setFace(BlockFace.UP, directions[0]);
            facing.setFace(BlockFace.DOWN, directions[1]);
            facing.setFace(BlockFace.NORTH, directions[2]);
            facing.setFace(BlockFace.SOUTH, directions[3]);
            facing.setFace(BlockFace.EAST, directions[4]);
            facing.setFace(BlockFace.WEST, directions[5]);

            baseBlock.setBlockData(facing, false);
        }
    }

    private BlockAttributes.TripWireAttributes tripWireAttributes;
    private BlockAttributes.MushroomBlockAttributes mushroomBlockAttributes;
    private BlockAttributes.NoteBlockAttributes noteBlockAttributes;

    private void readAttributes() {
        switch (blockType) {
            case WIRE:
                this.tripWireAttributes = new BlockAttributes.TripWireAttributes();
                tripWireAttributes.setAttached(file.getTOrDefault(String.format("blocks.%s.trip.attached", id), false));
                tripWireAttributes.setDisarmed(file.getTOrDefault(String.format("blocks.%s.trip.disarmed", id), false));
                tripWireAttributes.setPowered(file.getTOrDefault(String.format("blocks.%s.trip.powered", id), false));
                tripWireAttributes.setDirections(new boolean[]{
                        file.getT(String.format("blocks.%s.trip.states.north", id)),
                        file.getT(String.format("blocks.%s.trip.states.south", id)),
                        file.getT(String.format("blocks.%s.trip.states.east", id)),
                        file.getT(String.format("blocks.%s.trip.states.west", id))
                });

                break;
            case NOTE_BLOCK:
                this.noteBlockAttributes = new BlockAttributes.NoteBlockAttributes();
                noteBlockAttributes.setNote(file.getTOrDefault(String.format("blocks.%s.note_block.note", id), 0));
                noteBlockAttributes.setPowered(file.getTOrDefault(String.format("blocks.%s.note_block.powered", id), false));
                noteBlockAttributes.setInstrument(file.getTOrDefault(String.format("blocks.%s.note_block.instrument", id), "harp"));
                break;
            case MUSHROOM_BLOCK:
                this.mushroomBlockAttributes = new BlockAttributes.MushroomBlockAttributes();

                mushroomBlockAttributes.setType(MushroomType.valueOf(attributes.material.name().split("_")[0]));
                mushroomBlockAttributes.setDirections(new boolean[]{
                        file.getT(String.format("blocks.%s.mushroom.states.up", id)),
                        file.getT(String.format("blocks.%s.mushroom.states.down", id)),
                        file.getT(String.format("blocks.%s.mushroom.states.north", id)),
                        file.getT(String.format("blocks.%s.mushroom.states.south", id)),
                        file.getT(String.format("blocks.%s.mushroom.states.east", id)),
                        file.getT(String.format("blocks.%s.mushroom.states.west", id))
                });
                break;
            case AIR:
                return;

        }
    }

    static class Attributes {
        Material material;
        String name;
        List<ItemStack> drops;

    }

    public static class BlockFile {

        private final World world;
        @Getter
        private File file;
        private YamlConfiguration configuration;

        @SneakyThrows
        public BlockFile(World world) {
            this.world = world;

            Directory directory = new Directory(world.getWorldFolder());

            if (!directory.hasSpecificChild("blocks.yml")) {
                this.file = new File(world.getWorldFolder(), "blocks.yml");

                if (!file.exists())
                    file.createNewFile();

            }

            if (configuration == null)
                configuration = YamlConfiguration.loadConfiguration(file);

            cache();
        }

        public void write(HashMap<String, Object> objectHashMap) {
            CustomBlockData customBlockData = new CustomBlockData(objectHashMap);
            customBlockData.writeToFile(file);
            customBlockLocations.add(customBlockData);
        }

        public void write(CustomBlockData data) {
            data.writeToFile(file);
            customBlockLocations.add(data);
        }

        private final ArrayList<CustomBlockData> customBlockLocations = new ArrayList<>();

        /*
          x,y,z:
          material: $string$
          block-type: $string$
          states:
            mushroom:
              up: $bool$
              down: $bool$
              north: $bool$
              south: $bool$
              east: $bool$
              west: $bool$
            note_block:
              note: $int$
              instrument: $string$
            tripwire:
              powered: $bool$
              disarmed: $bool$
              attached: $bool$
              directions:
                north: $bool$
                south: $bool$
                east: $bool$
                west: $bool$

*/
        private void cache() {
            for (String key : configuration.getKeys(false)) {
                if (key == null || key.isEmpty()) continue;
                customBlockLocations.add(CustomBlockData.Utility.generate(CustomBlockData.Utility.convertLocString(key, world)));
            }
        }

        @SneakyThrows
        public void remove(CustomBlockData data) {
            if (!configuration.contains(CustomBlockData.Utility.convertLocation(data.getLocation()))) {
                System.out.println("Doesn't contain block");
                return;
            }



            configuration.set(CustomBlockData.Utility.convertLocation(data.getLocation()), null);
            configuration.save(file);

            if (customBlockLocations.contains(data))
                customBlockLocations.remove(data);
        }

        public CustomBlockData getBlockData(Location location) {
            for (CustomBlockData blockData : customBlockLocations) {
                if (blockData.getLocation() != location) continue;
                return blockData;
            }
            return null;
        }

        public boolean contains(Location location) {
            for (CustomBlockData blockData : customBlockLocations) {
                return blockData.getLocation() == location;
            }
            return false;
        }

        public boolean contains(CustomBlockData data) {
            return customBlockLocations.contains(data);
        }
    }

    public enum BlockType {
        MUSHROOM_BLOCK,
        NOTE_BLOCK,
        WIRE,
        AIR,
    }
}
