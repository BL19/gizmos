package net.bl19.gizmos.plugin.renderers.debug_renderer_fabric.shapes;

import net.bl19.gizmos.api.objects.Gizmo;
import net.bl19.gizmos.nms.NMSPacketSerializer;
import net.bl19.gizmos.plugin.renderers.debug_renderer_fabric.operations.DebugRendererFabricOperation;
import net.bl19.gizmos.plugin.renderers.debug_renderer_fabric.operations.RemoveOperation;
import net.bl19.gizmos.plugin.renderers.debug_renderer_fabric.operations.SetOperation;

import java.util.List;

public abstract class Shape extends BaseShape {
    
    private final int shapeId;
    
    protected Shape(int shapeId, Gizmo gizmo){
        super(gizmo);
        this.shapeId = shapeId;
    }
    
    public int getShapeId() {
        return shapeId;
    }


    public abstract ShapeTypes getShapeType();
    public abstract void write(NMSPacketSerializer packetSerializer);

    @Override
    public List<DebugRendererFabricOperation> update(Gizmo gizmo) {
        this.gizmo = gizmo;
        return List.of(new SetOperation(this));
    }
    
    @Override
    public List<DebugRendererFabricOperation> hide() {
        return List.of(new RemoveOperation(getGizmo(), getShapeId()));
    }
    
}
