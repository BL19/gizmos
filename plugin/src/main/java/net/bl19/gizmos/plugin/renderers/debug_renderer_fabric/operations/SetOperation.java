package net.bl19.gizmos.plugin.renderers.debug_renderer_fabric.operations;

import net.bl19.gizmos.nms.NMSPacketSerializer;
import net.bl19.gizmos.plugin.renderers.debug_renderer_fabric.shapes.BaseShape;
import net.bl19.gizmos.plugin.renderers.debug_renderer_fabric.shapes.Shape;

public class SetOperation implements DebugRendererFabricOperation {

    private final Shape shape;

    public SetOperation(Shape shape) {

        this.shape = shape;
    }
    
    @Override
    public int getOperationId() {
        return 0;
    }

    @Override
    public void write(NMSPacketSerializer packetSerializer) {
        writeGizmoId(packetSerializer, shape.getGizmo(), shape.getShapeId());
        packetSerializer.writeVarInt(shape.getShapeType().getId());
        shape.write(packetSerializer);
    }
}
