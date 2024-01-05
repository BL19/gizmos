package net.bl19.gizmos.plugin.hooks;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.regions.GlobalProtectedRegion;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.bl19.gizmos.api.Gizmos;
import net.bl19.gizmos.api.data.Area;
import net.bl19.gizmos.api.managers.Namespace;
import net.bl19.gizmos.api.objects.Gizmo;
import net.bl19.gizmos.api.objects.impl.AreaGizmo;
import net.bl19.gizmos.api.objects.impl.FlatPolygonGizmo;
import net.bl19.gizmos.plugin.GizmosPlugin;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.flywaydb.core.internal.util.logging.Log;
import org.joml.Vector2d;
import org.joml.Vector3d;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.logging.Logger;

public class WorldGuardHook implements PluginHook {
    
    private static final Map<String, Gizmo> regionGizmos = new HashMap<>();
    private static Namespace regionsNamespace;
    private static BukkitTask task;
    
    @Override
    public void enable() {
        regionsNamespace = Gizmos.getNamespaceManager().getOrCreateNamespace("worldguard/regions", "gizmos.hooks.worldguard");
        // Set the alpha to 0.5
        var c = regionsNamespace.getColor();
        regionsNamespace.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 128));
        task = new BukkitRunnable() {
            @Override
            public void run() {
                updateRegions();
            }
        }.runTaskTimerAsynchronously(GizmosPlugin.getInstance(), 0, 20);
    }
    
    private void updateRegions() {
        var missingRegions = new HashSet<>(regionGizmos.keySet());
        for (World world : Bukkit.getWorlds()) {
            WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world)).getRegions().forEach((name, region) -> {
                updateRegion(world, name, region);
                missingRegions.remove(name);
            });
        }
        for (String missingRegion : missingRegions) {
            // Remove the gizmo from the namespace
            regionsNamespace.removeGizmo(regionGizmos.get(missingRegion));
        }
    }

    private void updateRegion(World world, String name, ProtectedRegion region) {
        boolean isNew = false;
        if(!regionGizmos.containsKey(name)) {
            if(region instanceof ProtectedCuboidRegion) {
                var area = new Area(new Vector(region.getMinimumPoint().getX(), region.getMinimumPoint().getY(), region.getMinimumPoint().getZ()), new Vector(region.getMaximumPoint().getX(), region.getMaximumPoint().getY(), region.getMaximumPoint().getZ()));
                regionGizmos.put(name, new AreaGizmo(regionsNamespace, name, world, area));
            } else if (region instanceof ProtectedPolygonalRegion) {
                regionGizmos.put(name, new FlatPolygonGizmo(regionsNamespace, name, world, new ArrayList<>(), 0, 0));
            } else if (region instanceof GlobalProtectedRegion){
                return;
            }
            isNew = true;
        }
        Gizmo gizmo = regionGizmos.get(name);
        if(region instanceof ProtectedCuboidRegion cuboidRegion) {
            updateCuboidGizmo(world, cuboidRegion, (AreaGizmo) gizmo);
        } else if (region instanceof ProtectedPolygonalRegion polygonalRegion) {
            updatePolygonalGizmo(world, polygonalRegion, (FlatPolygonGizmo) gizmo);
        }
        if(isNew) {
            Logger.getLogger("Gizmos WG").info("Adding new region gizmo for " + name);
            regionsNamespace.addGizmo(gizmo);
        }
    }

    private void updateCuboidGizmo(World world, ProtectedCuboidRegion region, AreaGizmo gizmo) {
        if(gizmo.getWorld() != world) {
            gizmo.setWorld(world);
        }
        var min = region.getMinimumPoint();
        var max = region.getMaximumPoint();
        if(gizmo.getArea().getStartVector().getX() != min.getX() || gizmo.getArea().getStartVector().getY() != min.getY() || gizmo.getArea().getStartVector().getZ() != min.getZ()) {
            gizmo.getArea().setStart(new Vector(min.getX(), min.getY(), min.getZ()));
        }
        if(gizmo.getArea().getEndVector().getX() != max.getX() || gizmo.getArea().getEndVector().getY() != max.getY() || gizmo.getArea().getEndVector().getZ() != max.getZ()) {
            gizmo.getArea().setEnd(new Vector(max.getX(), max.getY(), max.getZ()));
        }
    }

    private void updatePolygonalGizmo(World world, ProtectedPolygonalRegion region,  FlatPolygonGizmo gizmo) {
        if(gizmo.getWorld() != world) {
            gizmo.setWorld(world);
        }
        gizmo.holdAABB();
        var newVerts = region.getPoints().stream().map(p -> new Vector2d(p.getX(), p.getZ())).toList();
        if(!new HashSet<>(newVerts).containsAll(gizmo.getVertices()) || !new HashSet<>(gizmo.getVertices()).containsAll(newVerts)) {
            gizmo.setVertices(newVerts);
        }
        if(gizmo.getMaxY() != region.getMaximumPoint().getY()) {
            gizmo.setMaxY(region.getMaximumPoint().getY());
        }
        if(gizmo.getMinY() != region.getMinimumPoint().getY()) {
            gizmo.setMinY(region.getMinimumPoint().getY());
        }
        gizmo.releaseAABB();
    }

    @Override
    public void disable() {
        if(task != null)
            task.cancel();
        Gizmos.getNamespaceManager().removeNamespace(regionsNamespace);
        regionGizmos.clear();
    }
    
}
