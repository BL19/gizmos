package net.bl19.gizmos.api.data;

import net.bl19.gizmos.api.utils.VectorUtil;
import org.joml.Matrix3f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class RotatedArea {
    
    private Quaternionf rotation;
    private Vector3f extent;
    private Vector3f center;
    
    private Vector3f axisX;
    private Vector3f axisY;
    private Vector3f axisZ;
    
    private Vector3f scaledAxisX;
    private Vector3f scaledAxisY;
    private Vector3f scaledAxisZ;

    public RotatedArea(Vector3f center, Vector3f size, Quaternionf rotation) {
        this.rotation = rotation;
        this.extent = new Vector3f(size).div(2);
        this.center = center;
        
        Matrix3f rotationMatrix = new Matrix3f();
        rotation.get(rotationMatrix);
        
        // Maybe work?
        this.axisZ = rotationMatrix.getColumn(2, new Vector3f()).normalize();
        this.axisY = rotationMatrix.getColumn(1, new Vector3f()).normalize();
        this.axisX = rotationMatrix.getColumn(0, new Vector3f()).normalize();

        scale();
    }

    public RotatedArea(Vector3f size, Vector3f center, float yaw, float pitch) {
        this.rotation = new Quaternionf().rotationZ((float) Math.toRadians(pitch)).rotationY((float) Math.toRadians(yaw));
        this.extent = new Vector3f(size).div(2);
        this.center = center;

        this.axisZ = VectorUtil.vectorFromPolar(pitch, yaw).normalize();
        this.axisY = VectorUtil.vectorFromPolar(pitch + 90, yaw).mul(-1).normalize();
        this.axisX = new Vector3f(axisZ).cross(axisY);
        
        scale();
    }
    
    private void scale() {
        scaledAxisX = new Vector3f(axisX).mul(extent.x);
        scaledAxisY = new Vector3f(axisY).mul(extent.y);
        scaledAxisZ = new Vector3f(axisZ).mul(extent.z);
    }

    public RotatedArea offsetX(float offset) {
        this.center.add(new Vector3f(axisX).mul(offset));
        return this;
    }
    
    public RotatedArea offsetY(float offset) {
        this.center.add(new Vector3f(axisY).mul(offset));
        return this;
    }
    
    public RotatedArea offsetZ(float offset) {
        this.center.add(new Vector3f(axisZ).mul(offset));
        return this;
    }

    public RotatedArea offset(Vector3f offset) {
        offsetX(offset.x);
        offsetY(offset.y);
        offsetZ(offset.z);
        return this;
    }

    public RotatedArea scale(float scale) {
        this.extent.mul(scale);
        scale();
        return this;
    }
    
    public boolean contains(Vector3f point) {
        var distance = new Vector3f(point).sub(center);
        distance.rotate(rotation.invert());
        return Math.abs(distance.x) < extent.x
                && Math.abs(distance.y) < extent.y
                && Math.abs(distance.z) < extent.z;
    }
    
    public Vector3f[] constructVertices() {

        var vertex1 = new Vector3f(center).sub(scaledAxisZ).sub(scaledAxisX).sub(scaledAxisY);
        var vertex2 = new Vector3f(center).sub(scaledAxisZ).add(scaledAxisX).sub(scaledAxisY);
        var vertex3 = new Vector3f(center).sub(scaledAxisZ).add(scaledAxisX).add(scaledAxisY);
        var vertex4 = new Vector3f(center).sub(scaledAxisZ).sub(scaledAxisX).add(scaledAxisY);

        var vertex5 = new Vector3f(center).add(scaledAxisZ).sub(scaledAxisX).sub(scaledAxisY);
        var vertex6 = new Vector3f(center).add(scaledAxisZ).add(scaledAxisX).sub(scaledAxisY);
        var vertex7 = new Vector3f(center).add(scaledAxisZ).add(scaledAxisX).add(scaledAxisY);
        var vertex8 = new Vector3f(center).add(scaledAxisZ).sub(scaledAxisX).add(scaledAxisY);

        return new Vector3f[] { vertex1, vertex2, vertex3, vertex4, vertex5, vertex6, vertex7, vertex8 };
    }

    public Quaternionf getRotation() {
        return rotation;
    }

    public void setRotation(Quaternionf rotation) {
        this.rotation = rotation;
        Matrix3f rotationMatrix = new Matrix3f();
        rotation.get(rotationMatrix);

        // Maybe work?
        this.axisZ = rotationMatrix.getColumn(2, new Vector3f()).normalize();
        this.axisY = rotationMatrix.getColumn(1, new Vector3f()).normalize();
        this.axisX = rotationMatrix.getColumn(0, new Vector3f()).normalize();

        scale();
    }

    public Vector3f getSize() {
        return new Vector3f(extent).mul(2);
    }

    public void setSize(Vector3f size) {
        this.extent = new Vector3f(size).div(2);
    }

    public Vector3f getCenter() {
        return center;
    }

    public void setCenter(Vector3f center) {
        this.center = center;
    }
    
    public Vector3f getScaledAxisX() {
        return scaledAxisX;
    }

    public Vector3f getScaledAxisY() {
        return scaledAxisY;
    }

    public Vector3f getScaledAxisZ() {
        return scaledAxisZ;
    }

    public Vector3f getExtent() {
        return extent;
    }

    public Vector3f getAxisX() {
        return axisX;
    }

    public Vector3f getAxisY() {
        return axisY;
    }

    public Vector3f getAxisZ() {
        return axisZ;
    }
}
