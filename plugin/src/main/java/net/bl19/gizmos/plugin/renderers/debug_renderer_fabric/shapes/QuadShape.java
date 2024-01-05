package net.bl19.gizmos.plugin.renderers.debug_renderer_fabric.shapes;

import net.bl19.gizmos.api.objects.ColoredGizmo;
import net.bl19.gizmos.api.objects.Gizmo;
import net.bl19.gizmos.api.objects.impl.FlatPolygonGizmo;
import net.bl19.gizmos.nms.NMSPacketSerializer;
import net.bl19.gizmos.plugin.renderers.debug_renderer_fabric.DebugRendererFabricLayer;
import org.bukkit.util.Vector;

import java.util.List;

public class QuadShape extends Shape {
    public QuadShape(int shapeId, Gizmo gizmo) {
        super(shapeId, gizmo);
    }

    @Override
    public ShapeTypes getShapeType() {
        return ShapeTypes.QUAD;
    }
    
    public List<Vector> getPoints() {
        return null;
    }

    @Override
    public void write(NMSPacketSerializer packetSerializer) {
        List<Vector> points = getPoints();
        if (points.size() != 4)
            return;

        // Points
        for (Vector point : points) {
            packetSerializer.writeDouble(point.getX());
            packetSerializer.writeDouble(point.getY());
            packetSerializer.writeDouble(point.getZ());
        }

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
