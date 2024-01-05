package net.bl19.gizmos.plugin.renderers.debug_renderer_fabric.shapes;

// https://github.com/mworzala/mc_debug_renderer/blob/master/src/main/java/com/mattworzala/debug/client/shape/Shape.java
public enum ShapeTypes {

    LINE(0),
    SPLINE(1),
    QUAD(2),
    BOX(3);
    
    private final int id;
    
    ShapeTypes(int id) {
        this.id = id;
    }
    
    public int getId() {
        return id;
    }
    
    
}
