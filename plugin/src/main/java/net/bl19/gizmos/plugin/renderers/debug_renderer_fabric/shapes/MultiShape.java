package net.bl19.gizmos.plugin.renderers.debug_renderer_fabric.shapes;

import net.bl19.gizmos.api.objects.Gizmo;
import net.bl19.gizmos.nms.NMSPacketSerializer;
import net.bl19.gizmos.plugin.renderers.debug_renderer_fabric.operations.DebugRendererFabricOperation;
import net.bl19.gizmos.plugin.renderers.debug_renderer_fabric.operations.RemoveOperation;

import java.util.List;

public abstract class MultiShape extends BaseShape {
    
    protected MultiShape(Gizmo gizmo) {
        super(gizmo);
    }
    
    public abstract List<Shape> getShapes();

    @Override
    public List<DebugRendererFabricOperation> hide() {
        return getShapes().stream().map(x -> (DebugRendererFabricOperation) new RemoveOperation(gizmo, x.getShapeId())).toList();
    }

}
