package net.bl19.gizmos.plugin.renderers.debug_renderer_fabric.shapes;

import net.bl19.gizmos.api.data.Area;
import net.bl19.gizmos.api.objects.ColoredGizmo;
import net.bl19.gizmos.api.objects.Gizmo;
import net.bl19.gizmos.api.objects.impl.AreaGizmo;
import net.bl19.gizmos.api.objects.impl.FlatPolygonGizmo;
import net.bl19.gizmos.api.objects.impl.LineGizmo;
import net.bl19.gizmos.api.objects.impl.RotatedAreaGizmo;
import net.bl19.gizmos.api.utils.VectorUtil;
import net.bl19.gizmos.nms.NMSPacketSerializer;
import net.bl19.gizmos.plugin.renderers.debug_renderer_fabric.DebugRendererFabricLayer;
import net.bl19.gizmos.plugin.renderers.debug_renderer_fabric.operations.DebugRendererFabricOperation;
import org.bukkit.util.Vector;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LineShape extends Shape {
    public LineShape(int shapeId, Gizmo gizmo) {
        super(shapeId, gizmo);
    }

    @Override
    public ShapeTypes getShapeType() {
        return ShapeTypes.LINE;
    }

    @Override
    public void write(NMSPacketSerializer packetSerializer) {
        List<Vector> positions;
        Type type;
        switch (gizmo.getType()) {
            case LINE -> {
                LineGizmo lineGizmo = (LineGizmo) gizmo;
                
                type = Type.STRIP;
                positions = lineGizmo.getPoints();
            }
            case AREA -> {
                AreaGizmo areaGizmo = (AreaGizmo) gizmo;
                Area area = areaGizmo.getArea();

                Vector corner1 = area.getStartVector();
                Vector corner2 = area.getEndVector();

                type = Type.STRIP;
                positions = Arrays.asList( // Order from an Eulerian path 
                        corner1,
                        new Vector(corner1.getX(), corner1.getY(), corner2.getZ()),
                        new Vector(corner2.getX(), corner1.getY(), corner2.getZ()),
                        new Vector(corner2.getX(), corner1.getY(), corner1.getZ()),
                        new Vector(corner2.getX(), corner2.getY(), corner1.getZ()),
                        new Vector(corner1.getX(), corner2.getY(), corner1.getZ()),
                        new Vector(corner1.getX(), corner2.getY(), corner2.getZ()),
                        corner2,
                        new Vector(corner1.getX(), corner2.getY(), corner2.getZ()),
                        new Vector(corner1.getX(), corner1.getY(), corner2.getZ()),
                        new Vector(corner1.getX(), corner1.getY(), corner1.getZ()),
                        new Vector(corner1.getX(), corner2.getY(), corner1.getZ()),
                        new Vector(corner2.getX(), corner2.getY(), corner1.getZ())
                );
            }
            case ROTATED_AREA -> {
                RotatedAreaGizmo areaGizmo = (RotatedAreaGizmo) gizmo;
                Vector3f[] vertices = areaGizmo.getArea().constructVertices();

                type = Type.STRIP;
                positions = Arrays.asList( // Order from an Eulerian path 
                        VectorUtil.transform(vertices[0]),
                        VectorUtil.transform(vertices[1]),
                        VectorUtil.transform(vertices[2]),
                        VectorUtil.transform(vertices[3]),
                        VectorUtil.transform(vertices[0]),
                        VectorUtil.transform(vertices[4]),
                        VectorUtil.transform(vertices[5]),
                        VectorUtil.transform(vertices[1]),
                        VectorUtil.transform(vertices[5]),
                        VectorUtil.transform(vertices[6]),
                        VectorUtil.transform(vertices[2]),
                        VectorUtil.transform(vertices[6]),
                        VectorUtil.transform(vertices[7]),
                        VectorUtil.transform(vertices[3]),
                        VectorUtil.transform(vertices[7]),
                        VectorUtil.transform(vertices[4])
                );
            }
            default -> {
                return;
            }
        }

        // Type id 
        packetSerializer.writeEnum(type);

        // Positions
        packetSerializer.writeCollection(positions, (serializer, position) -> {
            serializer.writeDouble(position.getX());
            serializer.writeDouble(position.getY());
            serializer.writeDouble(position.getZ());
        });

        // Color 
        if (gizmo instanceof ColoredGizmo coloredGizmo) {
            packetSerializer.writeInt(coloredGizmo.getColor().getRGB());
        } else {
            packetSerializer.writeInt(0xFFFFFFFF);
        }

        // Layer
        packetSerializer.writeEnum(DebugRendererFabricLayer.INLINE);

        // Line thickness
        packetSerializer.writeFloat(4.0f);
    }
    
    public enum Type {
        SINGLE(0),
        STRIP(1),
        LOOP(2);
        
        private final int id;
        
        Type(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }
}
