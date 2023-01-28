package dev.echo.oxi.entites;

import dev.echo.oxi.math.Matrix3D;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;

@RequiredArgsConstructor
@Data
public class Bone {

    private static final int WORLD_OFFSET = 0;

    private final Bone parent;
    private final Matrix3D rotation;
    private final Location pos;
    private final Location offset;



}
