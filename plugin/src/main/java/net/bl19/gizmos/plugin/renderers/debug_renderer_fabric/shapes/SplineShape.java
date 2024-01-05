package net.bl19.gizmos.plugin.renderers.debug_renderer_fabric.shapes;

import net.bl19.gizmos.api.objects.ColoredGizmo;
import net.bl19.gizmos.api.objects.Gizmo;
import net.bl19.gizmos.api.objects.impl.LineGizmo;
import net.bl19.gizmos.nms.NMSPacketSerializer;
import net.bl19.gizmos.plugin.renderers.debug_renderer_fabric.DebugRendererFabricLayer;

public class SplineShape extends Shape{
    public SplineShape(int shapeId, Gizmo gizmo) {
        super(shapeId, gizmo);
    }

    @Override
    public ShapeTypes getShapeType() {
        return ShapeTypes.SPLINE;
    }

    @Override
    public void write(NMSPacketSerializer packetSerializer) {
        if (gizmo instanceof LineGizmo lineGizmo) {
            
            switch (lineGizmo.getLineType()) {
                case SPLINE -> packetSerializer.writeEnum(Type.CATMULL_ROM);
                case BEZIER_CURVE -> packetSerializer.writeEnum(Type.BEZIER);
            }
            
            // Points
            packetSerializer.writeCollection(lineGizmo.getPoints(), (serializer, point) -> {
                serializer.writeDouble(point.getX());
                serializer.writeDouble(point.getY());
                serializer.writeDouble(point.getZ());
            });

            // Color
            if (gizmo instanceof ColoredGizmo coloredGizmo) {
                packetSerializer.writeInt(coloredGizmo.getColor().getRGB());
            } else {
                packetSerializer.writeInt(0xFFFFFFFF);
            }

            // Layer
            packetSerializer.writeEnum(DebugRendererFabricLayer.INLINE);
        }
    }

    public enum Type {
        CATMULL_ROM(0),
        BEZIER(1);
        
        private final int id;
        
        Type(int id) {
            this.id = id;
        }
        
        public int getId() {
            return id;
        }
        
    }
}
