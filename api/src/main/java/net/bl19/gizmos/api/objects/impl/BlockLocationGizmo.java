package net.bl19.gizmos.api.objects.impl;

import net.bl19.gizmos.api.managers.Namespace;
import net.bl19.gizmos.api.objects.ColoredGizmo;
import net.bl19.gizmos.api.objects.Gizmo;
import net.bl19.gizmos.api.objects.GizmoType;
import org.bukkit.Location;
import org.bukkit.World;

import java.awt.*;

public class BlockLocationGizmo extends ColoredGizmo {
    
    private int x;
    private int y;
    private int z;
    
    private Color borderColor;
    private int borderSize;
    
    public BlockLocationGizmo(Namespace namespace, String name, Location location) {
        this(namespace, name, location, null);
    }
    
    public BlockLocationGizmo(Namespace namespace, String name, Location location, Color color) {
        super(namespace, name, location.getWorld(), color);
        this.x = location.getBlockX();
        this.y = location.getBlockY();
        this.z = location.getBlockZ();
        this.setRenderDistance(10000d);
    }
    
    public BlockLocationGizmo(Namespace namespace, String name, Location location, Color color, Color borderColor, int borderSize) {
        this(namespace, name, location, color);
        this.borderColor = borderColor;
        this.borderSize = borderSize;
    }

    @Override
    public GizmoType getType() {
        return GizmoType.POSITION;
    }

    public int getX() {
        return x;
    }

    public BlockLocationGizmo setX(int x) {
        this.x = x;
        changed();
        return this;
    }

    public int getY() {
        return y;
    }

    public BlockLocationGizmo setY(int y) {
        this.y = y;
        changed();
        return this;
    }

    public int getZ() {
        return z;
    }

    public BlockLocationGizmo setZ(int z) {
        this.z = z;
        changed();
        return this;
    }

    public Color getBorderColor() {
        if(borderColor == null) {
            return getColor();
        }
        return borderColor;
    }

    public BlockLocationGizmo setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
        changed();
        return this;
    }

    public int getBorderSize() {
        return borderSize;
    }

    public BlockLocationGizmo setBorderSize(int borderSize) {
        this.borderSize = borderSize;
        changed();
        return this;
    }
    
    public Location getLocation() {
        return new Location(getWorld(), x, y, z);
    }
    
    public BlockLocationGizmo setLocation(Location location) {
        setWorld(location.getWorld());
        this.x = location.getBlockX();
        this.y = location.getBlockY();
        this.z = location.getBlockZ();
        changed();
        return this;
    }
    
    @Override
    public boolean isWithinRenderDistance(Location location) {
        if(location.getWorld() != null && !location.getWorld().equals(getWorld())) return false;
        return location.distance(getLocation()) <= getRenderDistance();
    }
}
