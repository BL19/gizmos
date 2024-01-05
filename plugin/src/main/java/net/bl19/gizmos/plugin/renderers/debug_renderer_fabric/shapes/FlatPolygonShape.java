package net.bl19.gizmos.plugin.renderers.debug_renderer_fabric.shapes;

import net.bl19.gizmos.api.objects.Gizmo;
import net.bl19.gizmos.api.objects.impl.FlatPolygonGizmo;
import net.bl19.gizmos.nms.NMSPacketSerializer;
import net.bl19.gizmos.plugin.renderers.debug_renderer_fabric.DebugRendererFabricRenderer;
import net.bl19.gizmos.plugin.renderers.debug_renderer_fabric.operations.DebugRendererFabricOperation;
import net.bl19.gizmos.plugin.renderers.debug_renderer_fabric.operations.RemoveOperation;
import net.bl19.gizmos.plugin.renderers.debug_renderer_fabric.operations.SetOperation;
import org.bukkit.util.Vector;
import org.joml.Vector2d;

import java.util.*;

public class FlatPolygonShape extends MultiShape {
    
    private Map<Integer, PolygonQuadShape> surfaces;
    
    public FlatPolygonShape(Gizmo gizmo) {
        super(gizmo);
        surfaces = createSurfaces();
    }

    private Map<Integer, PolygonQuadShape> createSurfaces() {
        FlatPolygonGizmo flatPolygonGizmo = (FlatPolygonGizmo) gizmo;
        var shapes = flatPolygonGizmo.getVertices().stream().map(x -> new PolygonQuadShape(DebugRendererFabricRenderer.nextId(), flatPolygonGizmo, flatPolygonGizmo.getVertices().indexOf(x))).toList();
        var map = new HashMap<Integer, PolygonQuadShape>();
        for (PolygonQuadShape shape : shapes) {
            map.put(shape.getIndex(), shape);
        }
        return map;
    }

    @Override
    public List<DebugRendererFabricOperation> update(Gizmo gizmo) {
        this.gizmo = gizmo;
        var operations = new ArrayList<DebugRendererFabricOperation>();
        operations.addAll(updateSurfaces());
        operations.addAll(surfaces.values().stream().map(x -> (DebugRendererFabricOperation) new SetOperation(x)).toList());
        return operations;
    }

    private List<DebugRendererFabricOperation> updateSurfaces() {
        if (gizmo instanceof FlatPolygonGizmo flatPolygonGizmo) {
            if (flatPolygonGizmo.getVertices().size() != surfaces.size()) {
                return updateSurfacesCountChange();
            }
        }
        return Collections.emptyList();
    }

    private List<DebugRendererFabricOperation> updateSurfacesCountChange() {
        if(gizmo instanceof FlatPolygonGizmo flatPolygonGizmo) {
            if(flatPolygonGizmo.getVertices().size() > surfaces.size()) {
                return updateSurfacesCountIncrease();
            } else {
                return updateSurfacesCountDecrease();
            }
        }
        return Collections.emptyList();
    }

    private List<DebugRendererFabricOperation> updateSurfacesCountDecrease() {
        if(gizmo instanceof FlatPolygonGizmo flatPolygonGizmo) {
            // Find removed indices
            var vertices = flatPolygonGizmo.getVertices();
            var removedIndices = new HashMap<Integer, PolygonQuadShape>();
            for (int i = 0; i < surfaces.size() - vertices.size(); i++) {
                var index = vertices.size() + i;
                removedIndices.put(index, surfaces.get(index));
            }
            // Remove removed indices
            surfaces.keySet().removeAll(removedIndices.keySet());
            return removedIndices.values().stream().map(x -> (DebugRendererFabricOperation) new RemoveOperation(gizmo, x.getShapeId())).toList();
        }
        return Collections.emptyList();
    }

    private List<DebugRendererFabricOperation> updateSurfacesCountIncrease() {
        if(gizmo instanceof FlatPolygonGizmo flatPolygonGizmo) {
            // Find new indices
            var vertices = flatPolygonGizmo.getVertices();
            var newCount = vertices.size() - surfaces.size();
            var newIndices = new HashMap<Integer, PolygonQuadShape>();
            for (int i = 0; i < newCount; i++) {
                var index = vertices.size() - 1 - i;
                newIndices.put(index, new PolygonQuadShape(DebugRendererFabricRenderer.nextId(), flatPolygonGizmo, index));
            }
            // Add new indices
            surfaces.putAll(newIndices);
            return newIndices.values().stream().map(x -> (DebugRendererFabricOperation) new SetOperation(x)).toList();
        }
        return Collections.emptyList();
    }

    @Override
    public List<Shape> getShapes() {
        return surfaces.values().stream().map(x -> (Shape) x).toList();
    }
    
    public class PolygonQuadShape extends QuadShape {

        private final FlatPolygonGizmo flatPolygonGizmo;
        private final int index;

        public PolygonQuadShape(int shapeId, FlatPolygonGizmo flatPolygonGizmo, int index) {
            super(shapeId, flatPolygonGizmo);
            this.flatPolygonGizmo = flatPolygonGizmo;
            this.index = index;
        }
        
        public int getIndex() {
            return index;
        }
        
        @Override
        public List<Vector> getPoints() {
            int oldIndex = (index + flatPolygonGizmo.getVertices().size() - 1) % flatPolygonGizmo.getVertices().size();
            int newIndex = index;
            Vector2d oldVertex = flatPolygonGizmo.getVertices().get(oldIndex);
            Vector2d newVertex = flatPolygonGizmo.getVertices().get(newIndex);
            Vector oldTop = new Vector(oldVertex.x, flatPolygonGizmo.getMinY(), oldVertex.y);
            Vector oldBottom = new Vector(oldVertex.x, flatPolygonGizmo.getMaxY(), oldVertex.y);
            Vector newTop = new Vector(newVertex.x, flatPolygonGizmo.getMinY(), newVertex.y);
            Vector newBottom = new Vector(newVertex.x, flatPolygonGizmo.getMaxY(), newVertex.y);
            return List.of(oldTop, oldBottom, newBottom, newTop);
        }
        
    }
}
