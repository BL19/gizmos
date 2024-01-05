package net.bl19.gizmos.api.objects.impl;

import net.bl19.gizmos.api.managers.Namespace;
import net.bl19.gizmos.api.objects.ColoredGizmo;
import net.bl19.gizmos.api.objects.GizmoType;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.awt.*;

public class SphereGizmo extends ColoredGizmo {
    
    private Vector center;
    private double radius;
    private double particleSpace = 1;
    
    public SphereGizmo(Namespace namespace, String name, Location location, double radius) {
        this(namespace, name, location.getWorld(), location.toVector(), radius, null);
    }

    public SphereGizmo(Namespace namespace, String name, World world, Vector center, double radius) {
        this(namespace, name, world, center, radius, null);
    }
    
    public SphereGizmo(Namespace namespace, String name, Location location, double radius, Color color) {
        this(namespace, name, location.getWorld(), location.toVector(), radius, color);
    }
    
    public SphereGizmo(Namespace namespace, String name, World world, Vector center, double radius, Color color) {
        super(namespace, name, world, color);
        this.center = center;
        this.radius = radius;
    }

    @Override
    public GizmoType getType() {
        return GizmoType.AREA;
    }
    
    public Vector getCenter() {
        return center;
    }

    public SphereGizmo setCenter(Vector center) {
        this.center = center;
        changed();
        return this;
    }

    public double getRadius() {
        return radius;
    }

    public SphereGizmo setRadius(double radius) {
        this.radius = radius;
        changed();
        return this;
    }
    
    public double getParticleSpace() {
        return particleSpace;
    }
    
    public SphereGizmo setParticleSpace(double particleSpace) {
        this.particleSpace = particleSpace;
        changed();
        return this;
    }

    @Override
    public boolean isWithinRenderDistance(Location location) {
        if(location.getWorld() != null && !location.getWorld().equals(getWorld())) return false;
        double renderDistance = getRenderDistance() + radius;
        double renderDistanceSquared = renderDistance * renderDistance;
        return location.toVector().distanceSquared(center) <= renderDistanceSquared;
    }
}
