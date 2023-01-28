package dev.echo.oxi.systems.blocks;


import com.google.common.collect.Maps;
import dev.echo.oxi.OxiPlugin;
import dev.echo.oxi.enums.MushroomType;
import dev.echo.oxi.files.InfoFile;
import dev.echo.oxi.systems.blocks.info.BlockAttributes;
import dev.echo.utils.general.PluginInstance;
import lombok.*;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.MultipleFacing;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.block.data.type.Tripwire;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Data
public class CustomBlockData {

    @Getter(AccessLevel.PRIVATE)
    private final HashMap<String, ?> dataMap;

    public CustomBlockData(HashMap<String, Object> dataMap) {
        this.dataMap = dataMap;
        index();
    }

    @Setter(AccessLevel.NONE)
    private Material material;
    @Setter(AccessLevel.NONE)
    private Location location;
    @Setter(AccessLevel.NONE)
    private CustomBlock.BlockType blockType;
    @Setter(AccessLevel.NONE)
    private final HashMap<String, Object> statesData = Maps.newHashMap();

    private void index() {
        material = Material.getMaterial(dataMap.get("material").toString());
        location = (Location) dataMap.get("location");
        blockType = CustomBlock.getBlockType(material);

        if (blockType == CustomBlock.BlockType.NOTE_BLOCK) {
            statesData.put("note_block.note", dataMap.get("note_block.note"));
            statesData.put("note_block.instrument", dataMap.get("note_block.instrument"));
        }
        if (blockType == CustomBlock.BlockType.WIRE) {
            statesData.put("tripwire.powered", dataMap.get("tripwire.powered"));
            statesData.put("tripwire.disarmed", dataMap.get("tripwire.disarmed"));
            statesData.put("tripwire.attached", dataMap.get("tripwire.attached"));
            String[] directions = new String[]{"north", "south", "east", "west"};
            for (String direction : directions)
                statesData.put(String.format("tripwire.directions.%s", direction), dataMap.get(String.format("tripwire.directions.%s", direction)));
        }
        if (blockType == CustomBlock.BlockType.MUSHROOM_BLOCK) {
            String[] directions = new String[]{"up", "down", "north", "south", "east", "west"};
            for (String direction : directions)
                statesData.put(String.format("mushroom.directions.%s", direction), dataMap.get(String.format("mushroom.directions.%s", direction)));
        }
    }

    @SneakyThrows
    public void writeToFile(File file) {
        YamlConfiguration tempConfig = YamlConfiguration.loadConfiguration(file);

        String locationString = location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ();


        tempConfig.set(String.format("%s.material", locationString), material.name());
        tempConfig.set(String.format("%s.block-type", locationString), blockType.name());


        if (blockType == CustomBlock.BlockType.NOTE_BLOCK) {
            String[] noteBlockStates = new String[]{"note_block.note", "note_block.instrument"};
            for (String noteBlockState : noteBlockStates) {
                tempConfig.set(String.format("%s.%s", locationString, noteBlockState), statesData.get(noteBlockState));
            }
        } else if (blockType == CustomBlock.BlockType.WIRE) {
            String[] wireStates = new String[]{"tripwire.powered",
                    "tripwire.disarmed",
                    "tripwire.attached",
                    "tripwire.directions.north",
                    "tripwire.directions.south",
                    "tripwire.directions.east",
                    "tripwire.directions.west"
            };
            for (String wireState : wireStates) {
                tempConfig.set(String.format("%s.%s", locationString, wireState), statesData.get(wireState));
            }
        } else if (blockType == CustomBlock.BlockType.MUSHROOM_BLOCK) {
            String[] mushroomStates = new String[]{
                    "mushroom.directions.up",
                    "mushroom.directions.down",
                    "mushroom.directions.north",
                    "mushroom.directions.south",
                    "mushroom.directions.east",
                    "mushroom.directions.west"
            };
            for (String mushroomState : mushroomStates) {
                tempConfig.set(String.format("%s.%s", locationString, mushroomState), statesData.get(mushroomState));
            }
        }
        tempConfig.save(file);
    }

    public static class Utility {
        public static String convertLocation(@NotNull Location location) {
            int x, y, z;
            x = location.getBlockX();
            y = location.getBlockY();
            z = location.getBlockZ();

            return String.format("%s,%s,%s", x, y, z);
        }

        @Contract("_, _ -> new")
        public static @NotNull Location convertLocString(@NotNull String location, World world) {
            int x, y, z;

            String[] coords = location.split(",");

            x = Integer.parseInt(coords[0]);
            y = Integer.parseInt(coords[1]);
            z = Integer.parseInt(coords[2]);

            return new Location(world, x, y, z);
        }

        @Contract("_, _ -> new")
        public static @NotNull CustomBlockData generate(String id, Location loc) {

            InfoFile file = ((OxiPlugin) PluginInstance.getSpigotClass()).getInfoFile();
            YamlConfiguration tempConfig = file.config();
            Material material = Material.getMaterial(tempConfig.getString(String.format("blocks.%s.material", id)));
            CustomBlock.BlockType blockType = CustomBlock.getBlockType(material);

            HashMap<String, Object> data = Maps.newHashMap();

            data.put("material", material);
            data.put("block-type", blockType);
            data.put("location", loc);

            if (blockType == CustomBlock.BlockType.MUSHROOM_BLOCK) {
                String[] directions = new String[]{"up", "down", "north", "south", "east", "west"};

                for (String direction : directions)
                    data.put(String.format("mushroom.directions.%s", direction),
                            tempConfig.getBoolean(String.format("blocks.%s.mushroom.directions.%s", id, direction)));

            } else if (blockType == CustomBlock.BlockType.NOTE_BLOCK) {
                data.put("note_block.note", tempConfig.getInt(String.format("blocks.%s.note_block.instrument", id)));
                data.put("note_block.instrument", tempConfig.getString(String.format("blocks.%s.note_block.instrument", id)));

            } else if (blockType == CustomBlock.BlockType.WIRE) {
                data.put("tripwire.powered", tempConfig.getBoolean(String.format("blocks.%s.tripwire.powered", id)));
                data.put("tripwire.disarmed", tempConfig.getBoolean(String.format("blocks.%s.tripwire.disarmed", id)));
                data.put("tripwire.attached", tempConfig.getBoolean(String.format("blocks.%s.tripwire.attached", id)));

                String[] directions = new String[]{"north", "south", "east", "west"};

                for (String direction : directions)
                    data.put(String.format("tripwire.directions.%s", direction),
                            tempConfig.getBoolean(String.format("blocks.%s.tripwire.directions.%s", id, direction)));
            }


            return new CustomBlockData(data);
        }

        public static CustomBlockData generate(File blocksFile, Location loc) {
            String stringLoc = convertLocation(loc);
            YamlConfiguration tempConfig = YamlConfiguration.loadConfiguration(blocksFile);

            System.out.println("Block Type: " + tempConfig.getString(String.format("%s.block-type", stringLoc)));

            Material material = Material.getMaterial(tempConfig.getString(String.format("%s.material", stringLoc)));
            CustomBlock.BlockType blockType = CustomBlock.getBlockType(material);

            HashMap<String, Object> data = Maps.newHashMap();

            data.put("material", material.name());
            data.put("block-type", blockType.name());
            data.put("location", loc);

            if (blockType == CustomBlock.BlockType.MUSHROOM_BLOCK) {
                String[] directions = new String[]{"up", "down", "north", "south", "east", "west"};

                for (String direction : directions)
                    data.put(String.format("mushroom.directions.%s", direction), tempConfig.getBoolean(String.format("mushroom.directions.%s", direction)));

            } else if (blockType == CustomBlock.BlockType.NOTE_BLOCK) {
                data.put("note_block.note", tempConfig.getInt("note_block.note"));
                data.put("note_block.instrument", tempConfig.getString("note_block.instrument"));

            } else if (blockType == CustomBlock.BlockType.WIRE) {
                data.put("tripwire.powered", tempConfig.getBoolean("tripwire.powered"));
                data.put("tripwire.disarmed", tempConfig.getBoolean("tripwire.disarmed"));
                data.put("tripwire.attached", tempConfig.getBoolean("tripwire.attached"));

                String[] directions = new String[]{"north", "south", "east", "west"};

                for (String direction : directions)
                    data.put(String.format("tripwire.directions.%s", direction), tempConfig.getBoolean(String.format("tripwire.directions.%s", direction)));
            }


            return new CustomBlockData(data);
        }

        public static CustomBlockData generate(Location loc) {
            Block block = loc.getBlock();

            Material material = block.getType();
            CustomBlock.BlockType blockType = CustomBlock.getBlockType(material);

            HashMap<String, Object> data = Maps.newHashMap();

            data.put("material", material.name());
            data.put("block-type", blockType.name());
            data.put("location", loc);

            if (blockType == CustomBlock.BlockType.MUSHROOM_BLOCK) {
                MultipleFacing facing = (MultipleFacing) block.getBlockData();
                String[] directions = new String[]{"north", "east", "south", "west", "up", "down"};
                int i = 0;
                for (String direction : directions) {
                    data.put(String.format("mushroom.directions.%s", direction), facing.getFaces().toArray()[i]);
                    i++;
                }
            } else if (blockType == CustomBlock.BlockType.NOTE_BLOCK) {
                NoteBlock noteBlock = (NoteBlock) block.getBlockData();
                data.put("note_block.note", noteBlock.getNote().getId());
                data.put("note_block.instrument", noteBlock.getInstrument().name());

            } else if (blockType == CustomBlock.BlockType.WIRE) {
                Tripwire tripwire = (Tripwire) block.getBlockData();
                data.put("tripwire.powered", tripwire.isPowered());
                data.put("tripwire.disarmed", tripwire.isDisarmed());
                data.put("tripwire.attached", tripwire.isAttached());

                String[] directions = new String[]{"north", "east", "south", "west"};
                int i = 0;
                for (String direction : directions) {
                    data.put(String.format("tripwire.directions.%s", direction), tripwire.getFaces().toArray()[i]);
                    i++;
                }
            }


            return new CustomBlockData(data);
        }

        public static BlockData buildBlockData(CustomBlockData data, Block baseBlock) {
            HashMap<String, Object> statesData = data.statesData;
            CustomBlock.BlockType blockType = data.getBlockType();

            if (blockType == CustomBlock.BlockType.NOTE_BLOCK) {
                NoteBlock block = (NoteBlock) baseBlock.getBlockData();
                block.setInstrument(Instrument.valueOf(statesData.get("note_block.instrument").toString().toUpperCase().replace(" ", "_")));
                block.setNote((Note) statesData.get("note_block.note"));
                return block;
            } else if (blockType == CustomBlock.BlockType.WIRE) {
                Tripwire tripwire = (Tripwire) baseBlock.getBlockData();
                tripwire.setDisarmed((Boolean) statesData.get("tripwire.disarmed"));
                tripwire.setAttached((Boolean) statesData.get("tripwire.attached"));
                tripwire.setPowered((Boolean) statesData.get("tripwire.powered"));

                String[] directionStrings = new String[]{"north", "south", "east", "west"};

                List<Boolean> directionsList = new ArrayList<>();

                for (String direction : directionStrings)
                    directionsList.add((Boolean) statesData.get(String.format("tripwire.directions.%s", direction)));
                Boolean[] directions = Arrays.copyOf(directionsList.toArray(), directionsList.toArray().length, Boolean[].class);
                tripwire.setFace(BlockFace.NORTH, directions[0]);
                tripwire.setFace(BlockFace.SOUTH, directions[1]);
                tripwire.setFace(BlockFace.EAST, directions[2]);
                tripwire.setFace(BlockFace.WEST, directions[3]);

                return tripwire;
            } else if (blockType == CustomBlock.BlockType.MUSHROOM_BLOCK) {
                BlockData blockData = baseBlock.getBlockData();
                MultipleFacing facing = (MultipleFacing) blockData;
                String[] directionStrings = new String[]{"up", "down", "north", "south", "east", "west"};

                List<Boolean> directionsList = new ArrayList<>();

                for (String direction : directionStrings)
                    directionsList.add((Boolean) statesData.get(String.format("mushroom.directions.%s", direction)));
                Boolean[] directions = Arrays.copyOf(directionsList.toArray(), directionsList.toArray().length, Boolean[].class);

                facing.setFace(BlockFace.UP, directions[0]);
                facing.setFace(BlockFace.DOWN, directions[1]);
                facing.setFace(BlockFace.NORTH, directions[2]);
                facing.setFace(BlockFace.SOUTH, directions[3]);
                facing.setFace(BlockFace.EAST, directions[4]);
                facing.setFace(BlockFace.WEST, directions[5]);
                return facing;
            }
            return null;
        }
    }
}
