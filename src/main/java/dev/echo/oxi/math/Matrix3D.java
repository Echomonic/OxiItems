package dev.echo.oxi.math;

import com.google.common.annotations.Beta;
import lombok.RequiredArgsConstructor;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

@RequiredArgsConstructor
public class Matrix3D {

    final double x, y, z;

    public Matrix3D rotate(double angle) {

        double angleCos = Math.cos(angle);
        double angleSin = Math.sin(angle);

        double x = angleCos * this.x + angleSin * this.z;
        double y = this.y;
        double z = -angleSin * this.x + angleCos * this.z;

        return new Matrix3D(x, y, z);
    }

    /**
     * "Rotate a point around a pivot point by a given angle."
     *
     * The function takes in a pivot point and a rotation point. It then rotates the point around the pivot point by the given angle
     *
     * @param pivot The point to rotate around
     * @param rotations The rotations to apply to the point.
     * @return A new Matrix3D object with the new x, y, and z values.
     */
    public Matrix3D rotate(Matrix3D pivot, Matrix3D rotations) {
        double rotX, rotY, rotZ;

        rotX = Math.toRadians(rotations.x);
        rotY = Math.toRadians(rotations.y);
        rotZ = Math.toRadians(rotations.z);

        double x = Math.cos(rotX) * this.x + Math.sin(rotX) * this.z;
        double y = Math.sin(rotY) * this.z + Math.cos(rotY) * this.x;
        double z = -Math.sin(rotZ) * this.x + Math.cos(rotZ) * this.z;

        Matrix3D newRot = new Matrix3D(x + pivot.x, y + pivot.y, z + pivot.z);

        return newRot;
    }
}
