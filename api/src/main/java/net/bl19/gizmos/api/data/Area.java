package net.bl19.gizmos.api.data;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class Area {
    private Vector start;
    private Vector end;

    public Area(Vector start, Vector end) {
        this.start = start;
        this.end = end;
    }

    public Vector getStartVector() {
        return start;
    }
    public Vector getEndVector() {
        return end;
    }
    
    public void setStart(Vector start) {
        this.start = start;
    }
    
    public void setEnd(Vector end) {
        this.end = end;
    }

    /**
     * Returns whether or not the given location is within the area if the area were to be expanded by "distance" in each direction.
     * @param location The location to check 
     * @param distance The distance to expand the area by
     * @return Whether or not the location is within the expanded area
     */
    public boolean isWithinInflated(Location location, double distance) {
        return location.toVector().isInAABB(start.clone().subtract(new Vector(distance, distance, distance)), end.clone().add(new Vector(distance, distance, distance)));
    }
}
