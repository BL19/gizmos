package net.bl19.gizmos.api.objects.impl;

import net.bl19.gizmos.api.data.RotatedArea;
import net.bl19.gizmos.api.managers.Namespace;
import net.bl19.gizmos.api.objects.ColoredGizmo;
import net.bl19.gizmos.api.objects.GizmoType;
import net.bl19.gizmos.api.utils.VectorUtil;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;
import org.joml.Vector3f;

import java.awt.*;

public class RotatedAreaGizmo extends ColoredGizmo {

    private RotatedArea area;
    private double particleSpace = 2;

    public RotatedAreaGizmo(Namespace namespace, String name, World world, RotatedArea area) {
        this(namespace, name, world, area, null);
    }

    public RotatedAreaGizmo(Namespace namespace, String name, World world, RotatedArea area, Color color) {
        super(namespace, name, world, color);
        this.area = area;
    }

    @Override
    public GizmoType getType() {
        return GizmoType.ROTATED_AREA;
    }

    public RotatedArea getArea() {
        return area;
    }

    public RotatedAreaGizmo setArea(RotatedArea area) {
        this.area = area;
        changed();
        return this;
    }

    public double getParticleSpace() {
        return particleSpace;
    }

    public RotatedAreaGizmo setParticleSpace(double particleSpace) {
        this.particleSpace = particleSpace;
        changed();
        return this;
    }

    @Override
    public boolean isWithinRenderDistance(Location location) {
        if (location.getWorld() != null && !location.getWorld().equals(getWorld()))
            return false;

        var extent = area.getExtent().add((float) getRenderDistance(), (float) getRenderDistance(), (float) getRenderDistance());

        var distance = VectorUtil.transform(location.toVector()).sub(area.getCenter());
        distance.rotate(area.getRotation().invert());
        return Math.abs(distance.x) < extent.x
                && Math.abs(distance.y) < extent.y
                && Math.abs(distance.z) < extent.z;
    }
}
