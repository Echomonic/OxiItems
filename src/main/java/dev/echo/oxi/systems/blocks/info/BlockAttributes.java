package dev.echo.oxi.systems.blocks.info;

import dev.echo.oxi.enums.MushroomType;
import lombok.Data;

@Data
public class BlockAttributes {



    public static class AirAttributes{

    }

    @Data
    public static class NoteBlockAttributes {
        int note;
        String instrument;
        boolean powered;
    }
    @Data
    public static class TripWireAttributes {

        boolean powered, disarmed, attached;

        boolean[] directions;


    }
    @Data
    public static class MushroomBlockAttributes {
        MushroomType type;

        boolean[] directions;

    }

}
