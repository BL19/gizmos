package net.bl19.gizmos.plugin.renderers.vanilla.impl;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.scheduler.Task;
import com.comphenix.protocol.wrappers.MinecraftKey;
import net.bl19.gizmos.api.objects.impl.BlockLocationGizmo;
import net.bl19.gizmos.nms.NMSPacketSerializer;
import net.bl19.gizmos.nms.NMSProvider;
import net.bl19.gizmos.plugin.GizmosPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.awt.*;
import java.util.*;

public class DebugMarkerRenderer {
    
    private static final long MAX_LIFETIME = 1000L * 60 * 60 * 24 * 7 * 4; // 4 weeks
    
    private static final Map<Player, Map<BlockLocationGizmo, DebugMarker>> debugMarkers = new HashMap<>();

    private static BukkitTask task;
    
    public static void stop() {
        Bukkit.getOnlinePlayers().forEach(DebugMarkerRenderer::removeDebugMarkers);
        if(task != null) {
            task.cancel();
        }
    }

    record DebugMarker(BlockLocationGizmo gizmo, Location location, Color color, long expiresAt, String name) {}
    
    public static void start() {
        task = Bukkit.getScheduler().runTaskTimerAsynchronously(GizmosPlugin.getInstance(), () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                renderDebugMarkers(player, 550 + player.getPing());
            }
        }, 0, 10);
    }
    
    public static void renderDebugMarkers(Player player, int forcedDuration) {
        // Check if we have any debug markers for this player
        if(!debugMarkers.containsKey(player)) {
            return;
        }
        
        // Get all debug markers for this player
        Collection<DebugMarker> markers = debugMarkers.get(player).values();
        var toRemove = new ArrayList<DebugMarker>();
        
        // Iterate over all debug markers
        for (DebugMarker marker : markers) {
            if (marker.expiresAt < System.currentTimeMillis() || !marker.gizmo().getLocation().getWorld().equals(player.getWorld()) || !marker.gizmo().isEnabled()) {
                toRemove.add(marker);                
                continue;
            }
            if(marker.gizmo.getWorld().equals(player.getWorld())) {
                if(marker.gizmo.isWithinRenderDistance(player.getLocation())) {
                    sendDebugMarker(player, marker, forcedDuration);
                }
            }
        }
        
        // Remove all debug markers that are expired
        markers.removeAll(toRemove);
        if (markers.isEmpty()) {
            debugMarkers.remove(player);
            removeDebugMarkers(player);
        }
    }

    private static void sendDebugMarker(Player player, DebugMarker marker, int forcedDuration) {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.CUSTOM_PAYLOAD);
        
        // Write data to packet
        NMSPacketSerializer data = NMSProvider.getNMS().createPacketSerializer();
        data.writeBlockLocation(marker.location); // location
        data.writeInt(marker.color.getRGB()); // color
        data.writeString(marker.name); // name
        if(forcedDuration > 0) {
            data.writeInt(forcedDuration); // lifetime of marker
        } else {
            data.writeInt((int) (System.currentTimeMillis() - marker.expiresAt)); // lifetime of marker
        }
        
        packet.getMinecraftKeys().write(0, new MinecraftKey("debug/game_test_add_marker"));
        data.writeToPacket(packet, 0);

        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send debug marker packet", e);
        }
    }
    
    private static void removeDebugMarkers(Player player) {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.CUSTOM_PAYLOAD);
        
        // Write data to packet
        packet.getMinecraftKeys().write(0, new MinecraftKey("debug/game_test_clear"));
        NMSProvider.getNMS().createPacketSerializer().writeToPacket(packet, 0);
        
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send debug marker clear packet", e);
        }
    }

    public static void renderDebugMarker(Player player, BlockLocationGizmo gizmo) {
        // Get map of debug markers for this player
        var map = debugMarkers.computeIfAbsent(player, (a) -> new HashMap<>());
        
        if(!gizmo.isEnabled()) {
            // Remove debug marker if it exists
            map.remove(gizmo);
            return;
        }
        
        // Create/Update debug marker
        map.put(gizmo, new DebugMarker(gizmo, gizmo.getLocation(), gizmo.getColor(), System.currentTimeMillis() + MAX_LIFETIME, gizmo.getName()));
    }
    
    public static void removeDebugMarker(Player player, BlockLocationGizmo gizmo) {
        // Get map of debug markers for this player
        var map = debugMarkers.get(player);
        if(map == null) {
            return;
        }
        
        // Remove debug marker if it exists
        map.remove(gizmo);
    }
    
}
