package net.bl19.gizmos.api.objects.impl;

import net.bl19.gizmos.api.data.Area;
import net.bl19.gizmos.api.managers.Namespace;
import net.bl19.gizmos.api.objects.ColoredGizmo;
import net.bl19.gizmos.api.objects.GizmoType;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;
import org.joml.Vector2d;

import java.awt.*;
import java.util.List;

public class FlatPolygonGizmo extends ColoredGizmo {
    
    private List<Vector2d> vertices;
    private double maxY;
    private double minY;
    
    private double particleSpace = 2;
    
    private Area aabb;

    public FlatPolygonGizmo(Namespace namespace, String name, World world, List<Vector2d> vertices, double maxY, double minY) {
        this(namespace, name, world, vertices, maxY, minY, null);
    }

    public FlatPolygonGizmo(Namespace namespace, String name, World world, List<Vector2d> vertices, double maxY, double minY, Color color) {
        super(namespace, name, world, color);
        this.vertices = vertices;
        this.maxY = maxY;
        this.minY = minY;
        updateAABB();
    }

    @Override
    public GizmoType getType() {
        return GizmoType.FLAT_POLYGON;
    }

    public List<Vector2d> getVertices() {
        return vertices;
    }
    
    public FlatPolygonGizmo setVertices(List<Vector2d> vertices) {
        this.vertices = vertices;
        updateAABB();
        changed();
        return this;
    }
    
    public double getMaxY() {
        return maxY;
    }
    
    public FlatPolygonGizmo setMaxY(double maxY) {
        this.maxY = maxY;
        updateAABB();
        changed();
        return this;
    }
    
    public double getMinY() {
        return minY;
    }
    
    public FlatPolygonGizmo setMinY(double minY) {
        this.minY = minY;
        updateAABB();
        changed();
        return this;
    }
    
    public double getParticleSpace() {
        return particleSpace;
    }
    
    public FlatPolygonGizmo setParticleSpace(double particleSpace) {
        this.particleSpace = particleSpace;
        changed();
        return this;
    }
    
    public void updateAABB() {
        if(vertices == null || vertices.isEmpty()) return;
        if(isHoldingAABB) {
            wantsToProcessAABB = true;
            return;
        }
        double maxX = Double.MIN_VALUE;
        double minX = Double.MAX_VALUE;
        double maxZ = Double.MIN_VALUE;
        double minZ = Double.MAX_VALUE;
        for (Vector2d vertex : vertices) {
            if (vertex.x > maxX) {
                maxX = vertex.x;
            }
            if (vertex.x < minX) {
                minX = vertex.x;
            }
            if (vertex.y > maxZ) {
                maxZ = vertex.y;
            }
            if (vertex.y < minZ) {
                minZ = vertex.y;
            }
        }
        this.aabb = new Area(new Vector(minX, minY, minZ), new Vector(maxX, maxY, maxZ));
    }

    @Override
    public boolean isWithinRenderDistance(Location location) {
        if (location.getWorld() != null && !location.getWorld().equals(getWorld()))
            return false;
        return aabb.isWithinInflated(location, getRenderDistance());
    }
    
    boolean wantsToProcessAABB = false;
    boolean isHoldingAABB = false;

    public void holdAABB() {
        isHoldingAABB = true;
    }
    
    public void releaseAABB() {
        isHoldingAABB = false;
        if(wantsToProcessAABB) {
            updateAABB();
            wantsToProcessAABB = false;
        }
    }
}
