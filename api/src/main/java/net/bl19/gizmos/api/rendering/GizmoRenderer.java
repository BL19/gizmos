package net.bl19.gizmos.api.rendering;

import net.bl19.gizmos.api.objects.Gizmo;
import net.bl19.gizmos.api.objects.GizmoType;
import org.bukkit.entity.Player;

public interface GizmoRenderer {
    
    public String getName();
    
    public boolean isSupported(Player player);
    public boolean isSupported(String version, GizmoType type);
    public boolean isSupported();
    
    default void render(Player player, Gizmo gizmo) {
        if (gizmo.isEnabled()) {
            renderGizmo(player, gizmo);
        }
    }
    
    public void renderGizmo(Player player, Gizmo gizmo);

    void stopRendering(Player player, Gizmo gizmo);
}
