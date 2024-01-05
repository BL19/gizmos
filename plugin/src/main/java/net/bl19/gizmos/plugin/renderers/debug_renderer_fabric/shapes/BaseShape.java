package net.bl19.gizmos.plugin.renderers.debug_renderer_fabric.shapes;

import net.bl19.gizmos.api.objects.Gizmo;
import net.bl19.gizmos.plugin.renderers.debug_renderer_fabric.DebugRendererFabricRenderer;
import net.bl19.gizmos.plugin.renderers.debug_renderer_fabric.operations.DebugRendererFabricOperation;
import net.bl19.gizmos.plugin.renderers.debug_renderer_fabric.operations.SetOperation;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public abstract class BaseShape {
    protected Gizmo gizmo;
    
    protected BaseShape(Gizmo gizmo) {
        this.gizmo = gizmo;
    }
    
    public Gizmo getGizmo() {
        return gizmo;
    }
    
    private final Map<UUID, Boolean> visible = new ConcurrentHashMap<>();
    private boolean enabled = true;
    
    public boolean shouldBeVisible(Player player) {
        return player.isOnline() && gizmo.getWorld().equals(player.getWorld());
    }
    
    public abstract List<DebugRendererFabricOperation> update(Gizmo gizmo);
    public abstract List<DebugRendererFabricOperation> hide();
    
}
