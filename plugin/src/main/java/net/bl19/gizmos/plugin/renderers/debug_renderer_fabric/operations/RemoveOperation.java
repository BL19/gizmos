package net.bl19.gizmos.plugin.renderers.debug_renderer_fabric.operations;

import net.bl19.gizmos.api.objects.Gizmo;
import net.bl19.gizmos.nms.NMSPacketSerializer;

public class RemoveOperation implements DebugRendererFabricOperation {

    private final Gizmo gizmo;
    private final int shapeId;

    public RemoveOperation(Gizmo gizmo, int shapeId) {
        this.gizmo = gizmo;
        this.shapeId = shapeId;
    }

    @Override
    public int getOperationId() {
        return 1;
    }

    @Override
    public void write(NMSPacketSerializer packetSerializer) {
        writeGizmoId(packetSerializer, gizmo, shapeId);
    }
}
