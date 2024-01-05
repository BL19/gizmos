package net.bl19.gizmos.plugin.renderers.debug_renderer_fabric.operations;

import net.bl19.gizmos.api.objects.Gizmo;
import net.bl19.gizmos.nms.NMSPacketSerializer;

public interface DebugRendererFabricOperation {
    
    int getOperationId();
    void write(NMSPacketSerializer packetSerializer);
    
    default void writeGizmoId(NMSPacketSerializer packetSerializer, Gizmo gizmo, int shapeId) {
        var ns = gizmo.getNamespace().getName() + ":" + gizmo.getName() + "_" + shapeId;
        ns = ns.replace("/", "_");
        packetSerializer.writeString(ns);
    }
    
}
