package net.bl19.gizmos.api.managers;

import net.bl19.gizmos.api.objects.Gizmo;
import net.bl19.gizmos.api.rendering.GizmoRenderer;
import org.bukkit.entity.Player;

public interface RenderManager {
    
    
    public boolean isSupported(Player player);
    public boolean isSupported(String version, Gizmo gizmo);
    public boolean isSupported();
    
    public void registerRenderer(GizmoRenderer renderer);
    public void unregisterRenderer(GizmoRenderer renderer);
    
    public void render(Player player, Gizmo gizmo);
    public void stopRendering(Player player, Gizmo gizmo);
    
}
