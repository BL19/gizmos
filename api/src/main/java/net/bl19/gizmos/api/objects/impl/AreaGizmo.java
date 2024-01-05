package net.bl19.gizmos.api.objects.impl;

import net.bl19.gizmos.api.data.Area;
import net.bl19.gizmos.api.managers.Namespace;
import net.bl19.gizmos.api.objects.ColoredGizmo;
import net.bl19.gizmos.api.objects.GizmoType;
import org.bukkit.Location;
import org.bukkit.World;

import java.awt.*;

public class AreaGizmo extends ColoredGizmo {


    private World world;
    private Area area;
    private double particleSpace = 2;
    
    public AreaGizmo(Namespace namespace, String name, World world, Area area) {
        this(namespace, name, world, area, null);
    }

    protected AreaGizmo(Namespace namespace, String name, World world, Area area, Color color) {
        super(namespace, name, world, color);
        this.world = world;
        this.area = area;
    }

    @Override
    public GizmoType getType() {
        return GizmoType.AREA;
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
        changed();
    }

    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
        changed();
    }

    @Override
    public boolean isWithinRenderDistance(Location location) {
        return location.getWorld().equals(world) && area.isWithinInflated(location, getRenderDistance());
    }

    public double getParticleSpace() {
        return particleSpace;
    }
    
    public void setParticleSpace(double particleSpace) {
        this.particleSpace = particleSpace;
        changed();
    }
}
