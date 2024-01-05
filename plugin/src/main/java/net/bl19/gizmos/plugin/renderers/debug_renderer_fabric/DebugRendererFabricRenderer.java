package net.bl19.gizmos.plugin.renderers.debug_renderer_fabric;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.MinecraftKey;
import net.bl19.gizmos.api.objects.Gizmo;
import net.bl19.gizmos.api.objects.GizmoType;
import net.bl19.gizmos.api.objects.impl.AreaGizmo;
import net.bl19.gizmos.api.objects.impl.FlatPolygonGizmo;
import net.bl19.gizmos.api.objects.impl.LineGizmo;
import net.bl19.gizmos.api.objects.impl.RotatedAreaGizmo;
import net.bl19.gizmos.api.rendering.GizmoRenderer;
import net.bl19.gizmos.nms.NMSProvider;
import net.bl19.gizmos.plugin.GizmosPlugin;
import net.bl19.gizmos.plugin.renderers.debug_renderer_fabric.operations.DebugRendererFabricOperation;
import net.bl19.gizmos.plugin.renderers.debug_renderer_fabric.operations.RemoveOperation;
import net.bl19.gizmos.plugin.renderers.debug_renderer_fabric.shapes.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class DebugRendererFabricRenderer implements GizmoRenderer {
    
    private static Map<Gizmo, BaseShape> shapeMap = new ConcurrentHashMap<>();
    private static Map<Player, List<Gizmo>> playerGizmoMap = new ConcurrentHashMap<>();
    private static int nextShapeId = 1;
    
    record QueuedOperation(Player player, DebugRendererFabricOperation operation) {}
    
    private static Deque<QueuedOperation> operationQueue = new ArrayDeque<>();
    
    public static List<UUID> playersWithMod = new ArrayList<>();
    
    @Override
    public String getName() {
        return "debug_renderer_fabric";
    }

    @Override
    public boolean isSupported(Player player) {
        return playersWithMod.contains(player.getUniqueId());
    }

    @Override
    public boolean isSupported(String version, GizmoType type) {
        if(type == GizmoType.AREA) {
            return true;
        }
        if(type == GizmoType.LINE) {
            return true;
        }
        if(type == GizmoType.FLAT_POLYGON) {
            return true;
        }
        if(type == GizmoType.ROTATED_AREA) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isSupported() {
        return true;
    }

    @Override
    public void renderGizmo(Player player, Gizmo gizmo) {
        var playerGizmos = playerGizmoMap.computeIfAbsent(player, x -> new ArrayList<>());
        if(!playerGizmos.contains(gizmo)) {
            playerGizmos.add(gizmo);
        }
        if(shapeMap.containsKey(gizmo)) {
            var shape = shapeMap.get(gizmo);
            var shapeUpdate = shape.update(gizmo);
            if(!shape.shouldBeVisible(player)) {
                queueOperations(player, shape.hide());
            } else {
                queueOperations(player, shapeUpdate);
            }
            return;
        }
        BaseShape shape = null;
        if(gizmo instanceof AreaGizmo areaGizmo) {
            shape = new BoxShape(nextId(), areaGizmo);
        } else if (gizmo instanceof LineGizmo lineGizmo) {
            switch (lineGizmo.getLineType()) {
                case STRAIGHT -> shape = new LineShape(nextId(), lineGizmo);
                case SPLINE, BEZIER_CURVE -> shape = new SplineShape(nextId(), lineGizmo);
            }
        } else if (gizmo instanceof FlatPolygonGizmo flatPolygonGizmo) {
            shape = new FlatPolygonShape(flatPolygonGizmo);
        } else if (gizmo instanceof RotatedAreaGizmo rotatedAreaGizmo) {
            shape = new LineShape(nextId(), rotatedAreaGizmo);
        } else {
            return;
        }
        if(shape != null) {
            shapeMap.put(gizmo, shape);
            var shapeUpdate = shape.update(gizmo);
            if(!shape.shouldBeVisible(player)) {
                queueOperations(player, shape.hide());
            } else {
                queueOperations(player, shapeUpdate);
            }
        }
    }
    
    public static int nextId() {
        return nextShapeId++;
    }

    private static void queueOperations(Player player, List<DebugRendererFabricOperation> update) {
        for (DebugRendererFabricOperation operation : update) {
            operationQueue.add(new QueuedOperation(player, operation));
        }
    }
    
    private static List<QueuedOperation> getOperations() {
        int processed = 0;
        var operations = new ArrayList<QueuedOperation>();
        while(!operationQueue.isEmpty() && processed++ < 100) {
            var queuedOperation = operationQueue.poll();
            if(queuedOperation == null) {
                break;
            }
            operations.add(queuedOperation);
        }
        return operations;
    }
    
    private static BukkitTask QUEUE_PROCESSOR = null;
    
    public static void startQueueProcessor() {
        QUEUE_PROCESSOR = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    processVisibility(onlinePlayer);
                }
                processOperationQueue();
            }
        }.runTaskTimerAsynchronously(GizmosPlugin.getInstance(), 5, 5);
    }
    
    public static void stopQueueProcessor() {
        if(QUEUE_PROCESSOR != null) {
            QUEUE_PROCESSOR.cancel();
        }
    }

    private static final Map<Player, Map<BaseShape, Boolean>> lastVisibility = new HashMap<>();
    
    private static void processVisibility(Player player) {
        var operations = new ArrayList<DebugRendererFabricOperation>();
        var playerGizmos = playerGizmoMap.get(player);
        if(playerGizmos == null) {
            return;
        }
        for (Gizmo gizmo : playerGizmos) {
            var shape = shapeMap.get(gizmo);
            if(gizmo == null) {
                continue;
            }
            var shouldBeVisible = shape.shouldBeVisible(player);
            if(!lastVisibility.containsKey(player)) {
                lastVisibility.put(player, new HashMap<>());
            }
            var lastVisibilityMap = lastVisibility.get(player);
            if(!lastVisibilityMap.containsKey(shape)) {
                lastVisibilityMap.put(shape, !shouldBeVisible);
            }
            var isVisible = lastVisibilityMap.get(shape).booleanValue();
            if(shouldBeVisible && !isVisible) {
                operations.addAll(shape.update(gizmo));
                lastVisibilityMap.put(shape, true);
                Logger.getLogger("Gizmos").info("Showing " + gizmo.getName() + " for " + player.getName());
            } else if(!shouldBeVisible && isVisible) {
                operations.addAll(shape.hide());
                lastVisibilityMap.put(shape, false);
                Logger.getLogger("Gizmos").info("Hiding " + gizmo.getName() + " for " + player.getName());
            }
        }
        if(!operations.isEmpty())
            Logger.getLogger("Gizmos").info(operations.size() + " visibility update operations for " + player.getName() + " (" + playerGizmos.size() + " gizmos)");
        if(player.isOnline())
            queueOperations(player, operations);
    }

    private static void processOperationQueue() {
        var operations = getOperations();
        if(operations.isEmpty()) {
            return;
        }
        
        // Group by player using a stream
        var byPlayer = operations.stream().collect(Collectors.groupingBy(QueuedOperation::player));
        
        for (Map.Entry<Player, List<QueuedOperation>> entry : byPlayer.entrySet()) {
            sendOperations(entry.getKey(), entry.getValue().stream().map(QueuedOperation::operation).toList());
        }
    }

    private static void sendOperations(Player player, List<DebugRendererFabricOperation> operations) {
        var packet = new PacketContainer(PacketType.Play.Server.CUSTOM_PAYLOAD);
        
        var packetSerializer = NMSProvider.getNMS().createPacketSerializer();
        packet.getMinecraftKeys().write(0, new MinecraftKey("debug","shapes"));
        System.out.println("Sending " + operations.size() + " operations to " + player.getName());
        packetSerializer.writeVarInt(operations.size());
        for (DebugRendererFabricOperation operation : operations) {
            packetSerializer.writeVarInt(operation.getOperationId());
            operation.write(packetSerializer);
        }
        
        packetSerializer.writeToPacket(packet, 0);

        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send debug_renderer_fabric operations packet", e);
        }
    }

    @Override
    public void stopRendering(Player player, Gizmo gizmo) {
        var shapeIds = new ArrayList<Integer>();
        var baseShape = shapeMap.get(gizmo);
        if (baseShape == null) return;
        
        if(baseShape instanceof MultiShape multiShape) {
            for (Shape shape : multiShape.getShapes()) {
                shapeIds.add(shape.getShapeId());
            }
        } else if (baseShape instanceof Shape shape) {
            shapeIds.add(shape.getShapeId());
        }
        
        var operations = new ArrayList<DebugRendererFabricOperation>();
        for (Integer shapeId : shapeIds) {
            operations.add(new RemoveOperation(gizmo, shapeId));
        }
        queueOperations(player, operations);
    }
}
