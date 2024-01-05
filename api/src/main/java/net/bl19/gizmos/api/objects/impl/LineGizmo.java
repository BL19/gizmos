package net.bl19.gizmos.api.objects.impl;

import net.bl19.gizmos.api.managers.Namespace;
import net.bl19.gizmos.api.objects.ColoredGizmo;
import net.bl19.gizmos.api.objects.Gizmo;
import net.bl19.gizmos.api.objects.GizmoType;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.awt.*;
import java.security.cert.X509Certificate;
import java.util.List;

public class LineGizmo extends ColoredGizmo {
    
    private List<Vector> controlPoints;
    private LineType lineType = LineType.STRAIGHT;
    private double particleSpace = 0.5;
    
    public LineGizmo(Namespace namespace, String name, World world, List<Vector> points) {
        this(namespace, name, world, points, null);
    }
    
    public LineGizmo(Namespace namespace, String name, List<Location> points) {
        this(namespace, name, points, null);
    }
    
    public LineGizmo(Namespace namespace, String name, World world, List<Vector> points, Color color) {
        super(namespace, name, world, color);
        this.controlPoints = points.stream().toList();
        if(points.size() < 2) {
            throw new IllegalArgumentException("Line must have at least 2 points!");
        }
    }

    public LineGizmo(Namespace namespace, String name, List<Location> points, Color color) {
        this(namespace, name, points.get(0).getWorld(), points.stream().map(x -> x.toVector()).toList(), color);
        // Check that all points are in the same world
        if(points.stream().anyMatch(x -> !x.getWorld().equals(getWorld()))) {
            throw new IllegalArgumentException("All points must be in the same world!");
        }
    }
    
    @Override
    public GizmoType getType() {
        return GizmoType.LINE;
    }
    
    public List<Vector> getPoints() {
        return controlPoints;
    }
    
    public LineType getLineType() {
        return lineType;
    }
    
    public LineGizmo setLineType(LineType lineType) {
        this.lineType = lineType;
        changed();
        return this;
    }
    
    public LineGizmo setPoints(List<Vector> points) {
        if (points.size() < 2) {
            throw new IllegalArgumentException("Line must have at least 2 points!");
        }
        this.controlPoints = points.stream().toList();
        changed();
        return this;
    }
    
    public double getParticleSpace() {
        if(getLineType() == LineType.BEZIER_CURVE) return 0.2;
        return particleSpace;
    }
    
    public LineGizmo setParticleSpace(double particleSpace) {
        this.particleSpace = particleSpace;
        changed();
        return this;
    }

    @Override
    public boolean isWithinRenderDistance(Location location) {
        if(location != null && !location.getWorld().equals(getWorld())) return false;
        var locationVector = location.toVector();
        var renderDistanceSq = getRenderDistance() * getRenderDistance();
        var distanceToClosestPoint = controlPoints.stream().mapToDouble(x -> x.distanceSquared(locationVector)).min().orElse(Double.MAX_VALUE);
        return distanceToClosestPoint <= renderDistanceSq;
    }
    
    public enum LineType {
        STRAIGHT,
        SPLINE,
        BEZIER_CURVE
    }
}
