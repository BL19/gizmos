package net.bl19.gizmos.api.utils;

import org.joml.Vector3f;

public class VectorUtil {
    
    public static Vector3f vectorFromPolar(float pitch, float yaw) {
        double f = Math.cos(-yaw * ((float)Math.PI / 180) - (float)Math.PI);
        double g = Math.sin(-yaw * ((float)Math.PI / 180) - (float)Math.PI);
        double h = -Math.cos(-pitch * ((float)Math.PI / 180));
        double i = Math.sin(-pitch * ((float)Math.PI / 180));
        return new Vector3f((float) (g * h), (float) i, (float) (f * h));
    }
    
    public static Vector3f rotateAroundAxisX(Vector3f v, float angle) {
        float cos = (float) Math.cos(angle);
        float sin = (float) Math.sin(angle);
        float y = v.y * cos - v.z * sin;
        float z = v.y * sin + v.z * cos;
        return v.set(v.x, y, z);
    }
    
    public static Vector3f rotateAroundAxisY(Vector3f v, float angle) {
        float cos = (float) Math.cos(angle);
        float sin = (float) Math.sin(angle);
        float x = v.x * cos + v.z * sin;
        float z = v.x * -sin + v.z * cos;
        return v.set(x, v.y, z);
    }
    
    public static Vector3f rotateAroundAxisZ(Vector3f v, float angle) {
        float cos = (float) Math.cos(angle);
        float sin = (float) Math.sin(angle);
        float x = v.x * cos - v.y * sin;
        float y = v.x * sin + v.y * cos;
        return v.set(x, y, v.z);
    }
    
    public static Vector3f transform(org.bukkit.util.Vector vector) {
        return new Vector3f((float) vector.getX(), (float) vector.getY(), (float) vector.getZ());
    }
    
    public static org.bukkit.util.Vector transform(Vector3f vector) {
        return new org.bukkit.util.Vector(vector.x, vector.y, vector.z);
    }
    
}
