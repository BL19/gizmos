package net.bl19.gizmos.api.objects.impl;

import net.bl19.gizmos.api.managers.Namespace;
import net.bl19.gizmos.api.objects.ColoredGizmo;
import net.bl19.gizmos.api.objects.GizmoType;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.awt.*;

public class CylinderGizmo extends ColoredGizmo {
    
    private Vector center;
    private double radius;
    private double height;
    
    private double particleSpace = 1;
    
    public CylinderGizmo(Namespace namespace, String name, World world, Vector center, double radius, double height) {
        this(namespace, name, world, center, radius, height, null);
    }
    
    public CylinderGizmo(Namespace namespace, String name, World world, Vector center, double radius, double height, Color color) {
        super(namespace, name, world, color);
        this.center = center;
        this.radius = radius;
        this.height = height;
    }
    
    @Override
    public GizmoType getType() {
        return GizmoType.CYLINDER;
    }
    
    public Vector getCenter() {
        return center;
    }
    
    public CylinderGizmo setCenter(Vector center) {
        this.center = center;
        changed();
        return this;
    }
    
    public double getRadius() {
        return radius;
    }
    
    public CylinderGizmo setRadius(double radius) {
        this.radius = radius;
        changed();
        return this;
    }
    
    public double getHeight() {
        return height;
    }
    
    public CylinderGizmo setHeight(double height) {
        this.height = height;
        changed();
        return this;
    }
    
    public double getParticleSpace() {
        return particleSpace;
    }
    
    public CylinderGizmo setParticleSpace(double particleSpace) {
        this.particleSpace = particleSpace;
        changed();
        return this;
    }

    @Override
    public boolean isWithinRenderDistance(Location location) {
        if(location.getWorld() != null && !location.getWorld().equals(getWorld())) return false;
        double renderDistance = getRenderDistance() + radius + height/2d;
        double renderDistanceSquared = renderDistance * renderDistance;
        return location.toVector().distanceSquared(center) <= renderDistanceSquared;
    }
}
