package net.bl19.gizmos.plugin.managers;

import net.bl19.gizmos.api.managers.RenderManager;
import net.bl19.gizmos.api.objects.Gizmo;
import net.bl19.gizmos.api.rendering.GizmoRenderer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class RenderManagerImpl implements RenderManager {

    private final List<GizmoRenderer> renderers = new ArrayList<>();
    private final List<GizmoRenderer> sortedRenderers = new ArrayList<>();
    private final List<String> priorities = List.of("debug_renderer_fabric");
    
    @Override
    public boolean isSupported(Player player) {
        return false;
    }

    @Override
    public boolean isSupported(String version, Gizmo gizmo) {
        return false;
    }
    
    @Override
    public boolean isSupported() {
        return false;
    }

    @Override
    public void registerRenderer(GizmoRenderer renderer) {
        if(renderers.contains(renderer)) {
            return;
        }
        renderers.add(renderer);
        sortRenderers();
    }
    
    private void sortRenderers() {
        sortedRenderers.clear();
        var missingPriorities = new ArrayList<GizmoRenderer>();
        for (GizmoRenderer renderer : renderers) {
            if(!renderer.isSupported()) {
                continue;
            }
            int priority = priorities.indexOf(renderer.getName());
            if(priority == -1) {
                missingPriorities.add(renderer);
            } else {
                sortedRenderers.add(priority, renderer);
            }
        }
        sortedRenderers.addAll(missingPriorities);
        Logger.getLogger("Gizmos").info("Sorted renderers: " + String.join(", ", sortedRenderers.stream().map(GizmoRenderer::getName).toList()));
    }

    @Override
    public void unregisterRenderer(GizmoRenderer renderer) {
        renderers.remove(renderer);
        sortedRenderers.remove(renderer);
    }

    @Override
    public void render(Player player, Gizmo gizmo) {
        for (GizmoRenderer renderer : sortedRenderers) {
            if(renderer.isSupported(player) && renderer.isSupported(Bukkit.getBukkitVersion(), gizmo.getType())) {
                renderer.render(player, gizmo);
                return;
            }
        }
        Logger.getLogger("Gizmos").warning("No renderer found for player " + player.getName() + " and gizmo " + gizmo.getNamespace().getName() + ":" + gizmo.getName() + ", available renderers: " + String.join(", ", sortedRenderers.stream().map(GizmoRenderer::getName).toList()));
    }

    @Override
    public void stopRendering(Player player, Gizmo gizmo) {
        for (GizmoRenderer renderer : sortedRenderers) {
            if(renderer.isSupported(player) && renderer.isSupported(Bukkit.getBukkitVersion(), gizmo.getType())) {
                renderer.stopRendering(player, gizmo);
            }
        }
    }
}
