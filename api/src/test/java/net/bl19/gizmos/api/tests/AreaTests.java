package net.bl19.gizmos.api.tests;

import net.bl19.gizmos.api.data.Area;
import org.bukkit.util.Vector;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AreaTests {
    
    @Test
    public void canCheckInflatedArea() {
        Area area = new Area(new Vector(0, 0, 0), new Vector(10, 10, 10));
        Assertions.assertTrue(area.isWithinInflated(new Vector(5, 5, 5).toLocation(null), 1), "Location should be within inflated area");
        Assertions.assertFalse(area.isWithinInflated(new Vector(15, 15, 15).toLocation(null), 0), "Location should not be within inflated area");
        Assertions.assertFalse(area.isWithinInflated(new Vector(15, 15, 15).toLocation(null), 1), "Location should not be within inflated area");
        Assertions.assertTrue(area.isWithinInflated(new Vector(15, 15, 15).toLocation(null), 5), "Location should be within inflated area");
        Assertions.assertTrue(area.isWithinInflated(new Vector(15, 15, 15).toLocation(null), 10), "Location should be within inflated area");
    }
    
}
