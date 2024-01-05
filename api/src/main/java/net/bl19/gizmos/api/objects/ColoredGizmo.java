package net.bl19.gizmos.api.objects;

import net.bl19.gizmos.api.managers.Namespace;
import org.bukkit.Location;
import org.bukkit.World;

import java.awt.*;

public abstract class ColoredGizmo extends Gizmo{
    private Color color;
    
    protected ColoredGizmo(Namespace namespace, String name, World world) {
        this(namespace, name, world,null);
    }

    protected ColoredGizmo(Namespace namespace, String name, World world, Color color) {
        super(namespace, name, world);
        this.color = color;
    }
    
    public Color getColor() {
        if(color == null) {
            return getNamespace().getColor();
        }
        return color;
    }
    
    public void setColor(Color color) {
        this.color = color;
        changed();
    }
    
    public void resetColor() {
        this.color = null;
        changed();
    }
}
