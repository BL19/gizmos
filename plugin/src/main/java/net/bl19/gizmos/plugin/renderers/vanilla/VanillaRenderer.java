package net.bl19.gizmos.plugin.renderers.vanilla;

import net.bl19.gizmos.api.data.RotatedArea;
import net.bl19.gizmos.api.objects.Gizmo;
import net.bl19.gizmos.api.objects.GizmoType;
import net.bl19.gizmos.api.objects.impl.*;
import net.bl19.gizmos.api.rendering.GizmoRenderer;
import net.bl19.gizmos.plugin.GizmosPlugin;
import net.bl19.gizmos.plugin.renderers.vanilla.impl.DebugMarkerRenderer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class VanillaRenderer implements GizmoRenderer {
    
    private static final int PARTICLE_RENDER_RATE = 5;
    private final Map<Player, Map<Gizmo, BukkitTask>> renderingTasks = new ConcurrentHashMap<>();
    
    @Override
    public String getName() {
        return "vanilla";
    }

    @Override
    public boolean isSupported(Player player) {
        return true;
    }

    @Override
    public boolean isSupported(String version, GizmoType type) {
        return true; 
    }

    @Override
    public boolean isSupported() {
        return true;
    }

    @Override
    public void renderGizmo(Player player, Gizmo gizmo) {
        Logger.getLogger("Gizmos").info("Rendering gizmo " + gizmo.getName() + " for player " + player.getName());
        if(gizmo instanceof BlockLocationGizmo blockLocationGizmo) {
            DebugMarkerRenderer.renderDebugMarker(player, blockLocationGizmo);
        }
        setParticleRenderer(player, gizmo, () -> {
            if(gizmo.isWithinRenderDistance(player.getLocation())) {
                drawParticles(player, gizmo);
            }
        });
    }

    @Override
    public void stopRendering(Player player, Gizmo gizmo) {
        if(renderingTasks.containsKey(player) && renderingTasks.get(player).containsKey(gizmo)) {
            renderingTasks.get(player).get(gizmo).cancel();
            renderingTasks.get(player).remove(gizmo);
        }
        if(gizmo instanceof BlockLocationGizmo blockLocationGizmo) {
            DebugMarkerRenderer.removeDebugMarker(player, blockLocationGizmo);
        }
    }

    private void drawParticles(Player player, Gizmo gizmo) {
        if (gizmo instanceof AreaGizmo areaGizmo) {
            ParticleRenderer.drawAreaForPlayer(player, areaGizmo.getWorld(), areaGizmo.getArea(), areaGizmo.getParticleSpace(), convertColor(areaGizmo.getColor()), areaGizmo.getRenderDistance());
        }
        if (gizmo instanceof LineGizmo lineGizmo) {
            var points = lineGizmo.getPoints();
            switch (lineGizmo.getLineType()) {
                case STRAIGHT -> {
                    for (int i = 1; i < points.size(); i++) {
                        ParticleRenderer.drawLineForPlayer(player, points.get(i - 1), points.get(i), lineGizmo.getParticleSpace(), convertColor(lineGizmo.getColor()), lineGizmo.getRenderDistance());
                    }
                }
                case SPLINE -> {
                }
                case BEZIER_CURVE -> {
                    ParticleRenderer.drawBezierForPlayer(player, lineGizmo.getPoints(), 0.2, convertColor(lineGizmo.getColor()), lineGizmo.getRenderDistance());
                }
            }
        }
        if (gizmo instanceof RotatedAreaGizmo areaGizmo) {
            ParticleRenderer.drawRotatedAreaForPlayer(player, areaGizmo.getWorld(), areaGizmo.getArea(), areaGizmo.getParticleSpace(), convertColor(areaGizmo.getColor()), areaGizmo.getRenderDistance());
        }
        if (gizmo instanceof FlatPolygonGizmo polygonGizmo) {
            ParticleRenderer.drawFlatPolygonForPlayer(player, polygonGizmo.getWorld(), polygonGizmo, polygonGizmo.getParticleSpace(), convertColor(polygonGizmo.getColor()), polygonGizmo.getRenderDistance());
        }
        if (gizmo instanceof CylinderGizmo cylinderGizmo) {
            ParticleRenderer.drawCylinderForPlayer(player, cylinderGizmo.getWorld(), cylinderGizmo.getCenter(), cylinderGizmo.getRadius(), cylinderGizmo.getHeight(), cylinderGizmo.getParticleSpace(), convertColor(cylinderGizmo.getColor()), cylinderGizmo.getRenderDistance());
        }
        if(gizmo instanceof SphereGizmo sphereGizmo) {
            ParticleRenderer.drawSphereForPlayer(player, sphereGizmo.getWorld(), sphereGizmo.getCenter(), sphereGizmo.getRadius(), sphereGizmo.getParticleSpace(), convertColor(sphereGizmo.getColor()), sphereGizmo.getRenderDistance());
        }
    }
    
    private void setParticleRenderer(Player player, Gizmo gizmo, Runnable runnable) {
        // If we have a previous task, cancel it
        if(renderingTasks.containsKey(player) && renderingTasks.get(player).containsKey(gizmo)) {
            renderingTasks.get(player).get(gizmo).cancel();
            renderingTasks.get(player).remove(gizmo);
        }
        
        if(!gizmo.isEnabled()) return;
        
        // Create a new task
        var task = new BukkitRunnable() {
            @Override
            public void run() {
                if(!player.isOnline()) {
                    cancel();
                    renderingTasks.get(player).remove(gizmo);
                    if(renderingTasks.get(player).isEmpty())
                        renderingTasks.remove(player);
                    return;
                }
                runnable.run();
            }
        }.runTaskTimerAsynchronously(GizmosPlugin.getInstance(), 0, PARTICLE_RENDER_RATE);
        renderingTasks.computeIfAbsent(player, k -> new ConcurrentHashMap<>()).put(gizmo, task);
    }
    
    private org.bukkit.Color convertColor(java.awt.Color color) {
        return org.bukkit.Color.fromRGB(color.getRed(), color.getGreen(), color.getBlue());
    }
    
}
