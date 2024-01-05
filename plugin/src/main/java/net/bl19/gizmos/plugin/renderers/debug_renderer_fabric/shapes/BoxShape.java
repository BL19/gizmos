package net.bl19.gizmos.plugin.renderers.debug_renderer_fabric.shapes;

import net.bl19.gizmos.api.objects.Gizmo;
import net.bl19.gizmos.api.objects.impl.AreaGizmo;
import net.bl19.gizmos.nms.NMSPacketSerializer;
import net.bl19.gizmos.plugin.renderers.debug_renderer_fabric.DebugRendererFabricLayer;

public class BoxShape extends Shape {
    
    public BoxShape(int shapeId, Gizmo gizmo) {
        super(shapeId, gizmo);
    }

    @Override
    public ShapeTypes getShapeType() {
        return ShapeTypes.BOX;
    }

    @Override
    public void write(NMSPacketSerializer packetSerializer) {
        if(gizmo instanceof AreaGizmo areaGizmo) {
            var area = areaGizmo.getArea();
            var start = area.getStartVector();
            var end = area.getEndVector();
            // Positions
            packetSerializer.writeDouble(start.getX());
            packetSerializer.writeDouble(start.getY());
            packetSerializer.writeDouble(start.getZ());
            packetSerializer.writeDouble(end.getX());
            packetSerializer.writeDouble(end.getY());
            packetSerializer.writeDouble(end.getZ());
            
            // Face Color
            packetSerializer.writeInt(areaGizmo.getColor().getRGB());
            // Face Layer
            packetSerializer.writeEnum(DebugRendererFabricLayer.INLINE);
            
            // Edge Color
            packetSerializer.writeInt(areaGizmo.getColor().getRGB());
            // Edge Layer
            packetSerializer.writeEnum(DebugRendererFabricLayer.INLINE);
            
            // Edge Thickness
            packetSerializer.writeFloat(4.0f);
        }
    }
    
}
