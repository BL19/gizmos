package net.bl19.gizmos.api.objects;

public enum GizmoType {
    
    POSITION("Position"),
    CYLINDER("Cylinder"),
    AREA("Area"),
    ROTATED_AREA("Rotated Area"),
    LINE("Line"),
    FLAT_POLYGON("Flat Polygon");
    
    private String name;
    
    private GizmoType(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
}
