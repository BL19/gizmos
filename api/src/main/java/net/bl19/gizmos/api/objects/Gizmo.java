package net.bl19.gizmos.api.objects;

import net.bl19.gizmos.api.managers.Namespace;
import org.bukkit.Location;
import org.bukkit.World;

public abstract class Gizmo {

    private World world;
    private final Namespace namespace;
    private final String name;
    private double renderDistance = 100d;
    private boolean enabled = true;
    
    protected Gizmo(Namespace namespace, String name, World world) {
        this.namespace = namespace;
        this.name = name;
        this.world = world;
    }
    
    public Namespace getNamespace() {
        return namespace;
    }
    
    public String getName() {
        return name;
    }
    
    protected void changed() {
        getNamespace().notifyChange(this);
    }

    public double getRenderDistance() {
        return renderDistance;
    }

    public void setRenderDistance(double renderDistance) {
        this.renderDistance = renderDistance;
    }

    public abstract GizmoType getType();
    
    public boolean isWithinRenderDistance(double distance) {
        return distance <= renderDistance;
    }
    
    public abstract boolean isWithinRenderDistance(Location location);

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        changed();
    }
    
    public World getWorld() {
        return world;
    }
    
    public void setWorld(World world) {
        this.world = world;
        changed();
    }
}
