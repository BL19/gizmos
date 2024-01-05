package net.bl19.gizmos.api.tests;

import net.bl19.gizmos.api.data.RotatedArea;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.joml.Vector3f;
import org.joml.Quaternionf;

public class RotatedAreaTests {

    // Helper method to create an instance of RotatedArea
    private RotatedArea createRotatedArea() {
        return new RotatedArea(new Vector3f(0, 0, 0), new Vector3f(10, 10, 10), new Quaternionf());
    }

    @Test
    public void offsetX_ShouldOffsetCenterX() {
        RotatedArea area = createRotatedArea();
        float offsetX = 5.0f;
        area.offsetX(offsetX);
        Assertions.assertEquals(5.0f, area.getCenter().x);
    }

    @Test
    public void offsetY_ShouldOffsetCenterY() {
        RotatedArea area = createRotatedArea();
        float offsetY = 5.0f;
        area.offsetY(offsetY);
        Assertions.assertEquals(5.0f, area.getCenter().y);
    }

    @Test
    public void offsetZ_ShouldOffsetCenterZ() {
        RotatedArea area = createRotatedArea();
        float offsetZ = 5.0f;
        area.offsetZ(offsetZ);
        Assertions.assertEquals(5.0f, area.getCenter().z);
    }

    @Test
    public void offset_ShouldOffsetCenter() {
        RotatedArea area = createRotatedArea();
        Vector3f offset = new Vector3f(3.0f, 4.0f, 5.0f);
        area.offset(offset);
        Assertions.assertEquals(new Vector3f(3.0f, 4.0f, 5.0f), area.getCenter());
    }

    @Test
    public void scale_ShouldScaleSize() {
        RotatedArea area = createRotatedArea();
        float scaleFactor = 2.0f;
        area.scale(scaleFactor);
        Vector3f expectedSize = new Vector3f(20.0f, 20.0f, 20.0f);
        Assertions.assertEquals(expectedSize, area.getSize());
    }

    @Test
    public void contains_ShouldDetectPointInside() {
        RotatedArea area = createRotatedArea();
        Vector3f pointInside = new Vector3f(1, 1, 1);
        Assertions.assertTrue(area.contains(pointInside));
    }

    @Test
    public void contains_ShouldDetectPointOutside() {
        RotatedArea area = createRotatedArea();
        Vector3f pointOutside = new Vector3f(11, 11, 11);
        Assertions.assertFalse(area.contains(pointOutside));
    }

    @Test
    public void constructVertices_ShouldConstructCorrectVertices() {
        RotatedArea area = createRotatedArea();
        Vector3f[] vertices = area.constructVertices();
        // Add specific vertices checking logic here based on the implementation details of constructVertices
    }

    @Test
    public void setAndGetRotation() {
        Quaternionf rotation = new Quaternionf();
        RotatedArea area = new RotatedArea(new Vector3f(), new Vector3f(), rotation);
        Quaternionf newRotation = new Quaternionf().rotateY((float) Math.PI / 2); // Arbitrary rotation for testing purposes
        area.setRotation(newRotation);
        Assertions.assertEquals(newRotation, area.getRotation());
    }

    @Test
    public void setAndGetSize() {
        RotatedArea area = createRotatedArea();
        Vector3f newSize = new Vector3f(20, 20, 20);
        area.setSize(newSize);
        Assertions.assertEquals(newSize, area.getSize());
    }

    @Test
    public void setAndGetCenter() {
        RotatedArea area = createRotatedArea();
        Vector3f newCenter = new Vector3f(5, 5, 5);
        area.setCenter(newCenter);
        Assertions.assertEquals(newCenter, area.getCenter());
    }

    private RotatedArea testArea;
    private final Vector3f initialSize = new Vector3f(10, 10, 10);
    private final Vector3f initialCenter = new Vector3f(0, 0, 0);
    private final Quaternionf initialRotation = new Quaternionf().identity();

    @BeforeEach
    public void setUp() {
        testArea = new RotatedArea(initialCenter, initialSize, initialRotation);
    }

    @Test
    public void testOffsetMethods() {
        Vector3f expectedCenter;

        // Test offsetX
        testArea.offsetX(5);
        expectedCenter = new Vector3f(5, 0, 0);
        Assertions.assertEquals(expectedCenter, testArea.getCenter());

        // Test offsetY
        testArea.offsetY(5);
        expectedCenter.add(0, 5, 0);
        Assertions.assertEquals(expectedCenter, testArea.getCenter());

        // Test offsetZ
        testArea.offsetZ(5);
        expectedCenter.add(0, 0, 5);
        Assertions.assertEquals(expectedCenter, testArea.getCenter());

        // Test offset with vector
        Vector3f offsetVector = new Vector3f(-5, -5, -5);
        testArea.offset(offsetVector);
        expectedCenter.add(offsetVector);
        Assertions.assertEquals(expectedCenter, testArea.getCenter());
    }

    @Test
    public void testScaleMethod() {
        testArea.scale(2f);
        Vector3f expectedSize = new Vector3f(initialSize).mul(2f);
        Assertions.assertEquals(expectedSize, testArea.getSize());

        // Check scaled axis lengths
        Assertions.assertEquals(expectedSize.x / 2, testArea.getScaledAxisX().length(), 0.001);
        Assertions.assertEquals(expectedSize.y / 2, testArea.getScaledAxisY().length(), 0.001);
        Assertions.assertEquals(expectedSize.z / 2, testArea.getScaledAxisZ().length(), 0.001);
    }

    @Test
    public void testContainsMethod() {
        Vector3f insidePoint = new Vector3f(2, 3, 1);
        Vector3f outsidePoint = new Vector3f(20, 10, 5);

        Assertions.assertTrue(testArea.contains(insidePoint));
        Assertions.assertFalse(testArea.contains(outsidePoint));
    }

    @Test
    public void testRotationMethods() {
        // Set new rotation and check updated axes
        Quaternionf newRotation = new Quaternionf().rotateY((float)Math.PI / 2);
        testArea.setRotation(newRotation);
        Quaternionf expectedRotation = testArea.getRotation();
        Assertions.assertEquals(expectedRotation, newRotation);

        // Check if axes are correctly updated
        Vector3f expectedAxisX = new Vector3f(0, 0, -1); // X becomes -Z after 90 degree rotation around Y
        Vector3f expectedAxisZ = new Vector3f(1, 0, 0);  // Z becomes X
        Assertions.assertEquals(expectedAxisX, testArea.getAxisX());
        Assertions.assertEquals(expectedAxisZ, testArea.getAxisZ());
    }

    @Test
    public void testSizeMethods() {
        Vector3f newSize = new Vector3f(20, 20, 20);
        testArea.setSize(newSize);
        Assertions.assertEquals(newSize, testArea.getSize());

        // Ensure that the extent is half of the new size
        Assertions.assertEquals(newSize.div(2), testArea.getExtent());
    }

    @Test
    public void testCenterMethods() {
        Vector3f newCenter = new Vector3f(5, 5, 5);
        testArea.setCenter(newCenter);
        Assertions.assertEquals(newCenter, testArea.getCenter());
    }

    // Helper method to create a rotated area with given parameters.
    private RotatedArea createRotatedArea(Vector3f center, Vector3f size, Quaternionf rotation) {
        return new RotatedArea(center, size, rotation);
    }

    @Test
    public void testNoRotationContainsMethod() {
        RotatedArea testArea = createRotatedArea(new Vector3f(0, 0, 0), new Vector3f(10, 10, 10), new Quaternionf(0, 0, 0, 1));

        // Test points inside
        Assertions.assertTrue(testArea.contains(new Vector3f(1.81f, -1.01f, 3.09f)));
        Assertions.assertTrue(testArea.contains(new Vector3f(0.60f, 0.72f, -1.79f)));
        // ... add more points to test as needed

        // Test a point outside
        Assertions.assertFalse(testArea.contains(new Vector3f(20, 10, 5)));
    }

    @Test
    public void testRotationAroundYContainsMethod() {
        RotatedArea testArea = createRotatedArea(new Vector3f(5, 5, 5), new Vector3f(20, 10, 5), new Quaternionf(0, (float)Math.sqrt(0.5), 0, (float)Math.sqrt(0.5)));

        // Test points inside
        Assertions.assertTrue(testArea.contains(new Vector3f(3.89f, 6.45f, -2.83f)));
        Assertions.assertTrue(testArea.contains(new Vector3f(4.04f, 6.60f, 14.80f)));
        // ... add more points to test as needed

        // Test a point outside
        Assertions.assertFalse(testArea.contains(new Vector3f(20, 10, 5)));
    }

    @Test
    public void testRotationAroundXContainsMethod() {
        RotatedArea testArea = createRotatedArea(new Vector3f(-5, 0, 5), new Vector3f(5, 20, 10), new Quaternionf((float)Math.sqrt(0.5), 0, (float)Math.sqrt(0.5), 0));

        // Test points inside
        Assertions.assertTrue(testArea.contains(new Vector3f(-5.52f, 0.44f, 3.70f)));
        Assertions.assertTrue(testArea.contains(new Vector3f(-2.64f, -3.20f, 3.65f)));
        // ... add more points to test as needed

        // Test a point outside
        Assertions.assertFalse(testArea.contains(new Vector3f(20, 10, 5)));
    }
}
    

