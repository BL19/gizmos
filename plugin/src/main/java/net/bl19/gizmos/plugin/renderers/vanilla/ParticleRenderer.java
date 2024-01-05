package net.bl19.gizmos.plugin.renderers.vanilla;

import net.bl19.gizmos.api.data.Area;
import net.bl19.gizmos.api.data.RotatedArea;
import net.bl19.gizmos.api.objects.impl.FlatPolygonGizmo;
import org.apache.commons.lang.Validate;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.joml.Vector2d;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

/**
 * Thanks bl19 from making this so I can steal it from smm :)
 */
public class ParticleRenderer {

    //#region Points

    public static void drawPoint(World world, Vector vector, Color color) {
        drawPoint(world, vector, color, 1);
    }

    public static void drawPoint(World world, Vector vector, Color color, int size) {
        drawPoint(world, vector, new Particle.DustOptions(color, size));
    }

    public static void drawPoint(World world, Vector vector, Particle.DustOptions dustOptions) {
        drawPoint(world, vector.getX(), vector.getY(), vector.getZ(), dustOptions);
    }

    public static void drawPoint(World world, double x, double y, double z, Color color) {
        drawPoint(world, x, y, z, color, 1);
    }

    public static void drawPoint(World world, double x, double y, double z, Color color, int size) {
        drawPoint(world, x, y, z, new Particle.DustOptions(color, size));
    }

    public static void drawPoint(World world, double x, double y, double z, Particle.DustOptions dustOptions) {
        drawPoints(world, x, y, z, dustOptions, 1);
    }

    public static void drawPoints(World world, Vector vector, Color color, int count) {
        drawPoints(world, vector, color, 1, count);
    }

    public static void drawPoints(World world, Vector vector, Color color, int size, int count) {
        drawPoints(world, vector, new Particle.DustOptions(color, size), count);
    }

    public static void drawPoints(World world, Vector vector, Particle.DustOptions dustOptions, int count) {
        drawPoints(world, vector.getX(), vector.getY(), vector.getZ(), dustOptions, count);
    }

    public static void drawPoints(World world, double x, double y, double z, Color color, int count) {
        drawPoints(world, x, y, z, color, 1, count);
    }

    public static void drawPoints(World world, double x, double y, double z, Color color, int size, int count) {
        drawPoints(world, x, y, z, new Particle.DustOptions(color, size), count);
    }

    public static void drawPoints(Location location, Particle.DustOptions dustOptions, int count) {
        drawPoints(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ(), dustOptions, count);
    }

    public static void drawPoints(Location location, Color color, int count) {
        drawPoints(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ(), color, 1, count);
    }

    public static void drawPoints(Location location, Color color, int size, int count) {
        drawPoints(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ(), new Particle.DustOptions(color, size), count);
    }

    public static void drawPoints(World world, double x, double y, double z, Particle.DustOptions dustOptions, int count) {
        world.spawnParticle(Particle.REDSTONE, x, y, z, count, dustOptions);
    }

    //#endregion

    //#region Lines
    public static void drawLine(World world, Vector point1, Vector point2, double space, Color color) {
        drawLine(point1.toLocation(world), point2.toLocation(world), space, color);
    }
    
    public static void drawLine(World world, Vector3f point1, Vector3f point2, double space, Color color) {
        var p1 = new Vector(point1.x, point1.y, point1.z);
        var p2 = new Vector(point2.x, point2.y, point2.z);
        drawLine(world, p1, p2, space, color);
    }

    public static void drawLine(Location point1, Location point2, double space, Color color) {
        World world = point1.getWorld();
        Validate.isTrue(point2.getWorld().equals(world), "Lines cannot be in different worlds!");
        double distance = point1.distance(point2);
        Vector p1 = point1.toVector();
        Vector p2 = point2.toVector();
        Vector vector = p2.clone().subtract(p1).normalize().multiply(space);
        double covered = 0;
        Particle.DustOptions dustOptions = new Particle.DustOptions(color, 1);

        for (; covered < distance; p1.add(vector)) {
            drawPoint(world, p1.getX(), p1.getY(), p1.getZ(), dustOptions);
            covered += space;
        }
    }

    public static void drawPointForPlayer(Player player, Vector point, Color color, double maxDistanceToDraw) {
        Particle.DustOptions dustOptions = new Particle.DustOptions(color, 1);
        if(maxDistanceToDraw == -1 || isWithinDistance(point, player.getLocation().toVector(), maxDistanceToDraw))
            player.spawnParticle(Particle.REDSTONE, point.getX(), point.getY(), point.getZ(), 1, dustOptions);
    }

    public static void drawLineForPlayer(Player player, Vector p1, Vector p2, double space, Color color, double maxDistanceToDraw) {
        double distance = p1.distanceSquared(p2);
        Vector vector = p2.clone().subtract(p1).normalize().multiply(space);
        double covered = 0;
        Particle.DustOptions dustOptions = new Particle.DustOptions(color, 1);
        Location playerVector = player.getLocation();
        for (; covered*covered < distance; p1.add(vector)) {
            if(maxDistanceToDraw == -1 || isWithinDistance(p1, playerVector.toVector(), maxDistanceToDraw))
                player.spawnParticle(Particle.REDSTONE, p1.getX(), p1.getY(), p1.getZ(), 1, dustOptions);
            covered += space;
        }
    }
    
    public static void drawLineForPlayer(Player player, World world, Vector3f point1, Vector3f point2, double space, Color color, double maxDistanceToDraw) {
        var p1 = new Vector(point1.x, point1.y, point1.z);
        var p2 = new Vector(point2.x, point2.y, point2.z);
        drawLineForPlayer(player, p1, p2, space, color, maxDistanceToDraw);
    }

    private static boolean isWithinDistance(Vector origin, Vector other, double max) {
        double squareDistance = max * max;
        double dx = Math.abs(other.getX() - origin.getX());
        double dy = Math.abs(other.getY() - origin.getY());
        double dz = Math.abs(other.getZ() - origin.getZ());
        return dx*dx + dy*dy + dz*dz <= squareDistance;
    }
    //#endregion

    //#region Areas
    public static void drawXZPlane(World world, Area area, double space, Color color) {
        Vector drawDiff = area.getEndVector().subtract(area.getStartVector()); // This will be the total "area" of the area
        var basePosition = area.getStartVector();
        double yCovered = 0;

        for (; yCovered < drawDiff.getY(); yCovered += space) {
            drawLine(world, basePosition.clone().add(new Vector(0, yCovered, 0)), basePosition.clone().add(new Vector(drawDiff.getX(), yCovered, drawDiff.getZ())), space, color);
        }
    }

    public static void drawYPlane(World world, Area area, double space, Color color) {
        Vector drawDiff = area.getEndVector().subtract(area.getStartVector()); // This will be the total "area" of the area
        var basePosition = area.getStartVector();
        double xCovered = 0;

        for (; xCovered < drawDiff.getX(); xCovered += space) {
            drawLine(world, basePosition.clone().add(new Vector(xCovered, 0, 0)), basePosition.clone().add(new Vector(xCovered, drawDiff.getY(), drawDiff.getZ())), space, color);
        }
    }

    public static void drawArea(World world, Area area, double space, Color color) {
        Area cArea = new Area(area.getStartVector(), area.getEndVector().add(new Vector(1, 1, 1))); // This is to cover the entire of the blocks inside of the area, otherwise it ends up on the border.

        // A "cube" consists of 2 - YPlanes and 4 - XZPlanes
        Area bottomY = new Area(cArea.getStartVector(), new Vector(cArea.getEndVector().getX(), cArea.getStartVector().getY(), cArea.getEndVector().getZ()));
        Area topY = new Area(new Vector(cArea.getStartVector().getX(), cArea.getEndVector().getY(), cArea.getStartVector().getZ()), cArea.getEndVector());

        // The close ones should begin at the start vector and end at the end vector with the same x/z value as the start vector
        // The far ones should begin at the start vector with the same x/z value as the end vector and end at the end vector
        Area closeX = new Area(cArea.getStartVector(), new Vector(cArea.getStartVector().getX(), cArea.getEndVector().getY(), cArea.getEndVector().getZ()));
        Area farX = new Area(new Vector(cArea.getEndVector().getX(), cArea.getStartVector().getY(), cArea.getStartVector().getZ()), new Vector(cArea.getEndVector().getX(), cArea.getEndVector().getY(), cArea.getEndVector().getZ()));

        Area closeZ = new Area(cArea.getStartVector(), new Vector(cArea.getEndVector().getX(), cArea.getEndVector().getY(), cArea.getStartVector().getZ()));
        Area farZ = new Area(new Vector(cArea.getStartVector().getX(), cArea.getStartVector().getY(), cArea.getEndVector().getZ()), new Vector(cArea.getEndVector().getX(), cArea.getEndVector().getY(), cArea.getEndVector().getZ()));


        // Draw all of the planes
        drawYPlane(world, bottomY, space, color);
        drawYPlane(world, topY, space, color);
        drawXZPlane(world, closeX, space, color);
        drawXZPlane(world, farX, space, color);
        drawXZPlane(world, closeZ, space, color);
        drawXZPlane(world, farZ, space, color);
    }

    public static void drawXZPlaneForPlayer(Player player, World world, Area area, double space, Color color, double maxDistanceToDraw) {
        Vector drawDiff = area.getEndVector().subtract(area.getStartVector()); // This will be the total "area" of the area
        var basePosition = area.getStartVector();
        double yCovered = 0;

        for (; yCovered < drawDiff.getY(); yCovered += space) {
            drawLineForPlayer(player,  
                    basePosition.clone().add(new Vector(0, yCovered, 0)),
                    basePosition.clone().add(new Vector(drawDiff.getX(), yCovered, drawDiff.getZ())), space, color, maxDistanceToDraw);
        }
    }

    public static void drawYPlaneForPlayer(Player player, World world, Area area, double space, Color color, double maxDistanceToDraw) {
        Vector drawDiff = area.getEndVector().subtract(area.getStartVector()); // This will be the total "area" of the area
        var basePosition = area.getStartVector();
        double xCovered = 0;

        for (; xCovered < drawDiff.getX(); xCovered += space) {
            drawLineForPlayer(player, basePosition.clone()
                    .add(new Vector(xCovered, 0, 0)), 
                    basePosition.clone().add(new Vector(xCovered, drawDiff.getY(), drawDiff.getZ())), space, color, maxDistanceToDraw);
        }
    }

    public static void drawAreaForPlayer(Player player, World world, Area area, double space, Color color, double maxDistanceToDraw) {
        Area cArea = new Area(area.getStartVector(), area.getEndVector().add(new Vector(1, 1, 1))); // This is to cover the entire of the blocks inside of the area, otherwise it ends up on the border.

        // A "cube" consists of 2 - YPlanes and 4 - XZPlanes
        Area bottomY = new Area(cArea.getStartVector(), new Vector(cArea.getEndVector().getX(), cArea.getStartVector().getY(), cArea.getEndVector().getZ()));
        Area topY = new Area(new Vector(cArea.getStartVector().getX(), cArea.getEndVector().getY(), cArea.getStartVector().getZ()), cArea.getEndVector());

        // The close ones should begin at the start vector and end at the end vector with the same x/z value as the start vector
        // The far ones should begin at the start vector with the same x/z value as the end vector and end at the end vector
        Area closeX = new Area(cArea.getStartVector(), new Vector(cArea.getStartVector().getX(), cArea.getEndVector().getY(), cArea.getEndVector().getZ()));
        Area farX = new Area(new Vector(cArea.getEndVector().getX(), cArea.getStartVector().getY(), cArea.getStartVector().getZ()), new Vector(cArea.getEndVector().getX(), cArea.getEndVector().getY(), cArea.getEndVector().getZ()));

        Area closeZ = new Area(cArea.getStartVector(), new Vector(cArea.getEndVector().getX(), cArea.getEndVector().getY(), cArea.getStartVector().getZ()));
        Area farZ = new Area(new Vector(cArea.getStartVector().getX(), cArea.getStartVector().getY(), cArea.getEndVector().getZ()), new Vector(cArea.getEndVector().getX(), cArea.getEndVector().getY(), cArea.getEndVector().getZ()));


        // Draw all of the planes
        drawYPlaneForPlayer(player, world, bottomY, space, color, maxDistanceToDraw);
        drawYPlaneForPlayer(player, world, topY, space, color, maxDistanceToDraw);
        drawXZPlaneForPlayer(player, world, closeX, space, color, maxDistanceToDraw);
        drawXZPlaneForPlayer(player, world, farX, space, color, maxDistanceToDraw);
        drawXZPlaneForPlayer(player, world, closeZ, space, color, maxDistanceToDraw);
        drawXZPlaneForPlayer(player, world, farZ, space, color, maxDistanceToDraw);
    }

    public static void drawRotatedAreaForPlayer(Player player, World world, RotatedArea area, double space, Color color, double maxDistanceToDraw) {
        var vertex = area.constructVertices();
        
        // Draw lines connecting vertices to form a three-dimensional structure in the world
        drawLineForPlayer(player, world, vertex[0], vertex[1], space, color, maxDistanceToDraw);
        drawLineForPlayer(player, world, vertex[2], vertex[3], space, color, maxDistanceToDraw);
        drawLineForPlayer(player, world, vertex[4], vertex[5], space, color, maxDistanceToDraw);
        drawLineForPlayer(player, world, vertex[6], vertex[7], space, color, maxDistanceToDraw);

        // Draw lines connecting the top vertices to form the top face
        drawLineForPlayer(player, world, vertex[0], vertex[4], space, color, maxDistanceToDraw);
        drawLineForPlayer(player, world, vertex[1], vertex[5], space, color, maxDistanceToDraw);
        drawLineForPlayer(player, world, vertex[3], vertex[7], space, color, maxDistanceToDraw);
        drawLineForPlayer(player, world, vertex[2], vertex[6], space, color, maxDistanceToDraw);

        // Draw lines connecting the bottom vertices to form the bottom face
        drawLineForPlayer(player, world, vertex[0], vertex[3], space, color, maxDistanceToDraw);
        drawLineForPlayer(player, world, vertex[1], vertex[2], space, color, maxDistanceToDraw);
        drawLineForPlayer(player, world, vertex[4], vertex[7], space, color, maxDistanceToDraw);
        drawLineForPlayer(player, world, vertex[5], vertex[6], space, color, maxDistanceToDraw);
    }
    
    public static void drawFlatPolygonForPlayer(Player player, World world, FlatPolygonGizmo polygon, double space, Color color, double maxDistanceToDraw) {
        if (polygon.getVertices().size() < 3) return;
        
        Vector2d lastVertex = polygon.getVertices().get(0);
        drawLineForPlayer(player, new Vector(lastVertex.x, polygon.getMaxY(), lastVertex.y), new Vector(lastVertex.x, polygon.getMinY(), lastVertex.y), space, color, maxDistanceToDraw);
        
        for (int i = 1; i < polygon.getVertices().size(); i++) {
            Vector2d vertex = polygon.getVertices().get(i);
            
            // Top line
            drawLineForPlayer(player, new Vector(lastVertex.x, polygon.getMaxY(), lastVertex.y), new Vector(vertex.x, polygon.getMaxY(), vertex.y), space, color, maxDistanceToDraw);
            
            // Bottom line
            drawLineForPlayer(player, new Vector(lastVertex.x, polygon.getMinY(), lastVertex.y), new Vector(vertex.x, polygon.getMinY(), vertex.y), space, color, maxDistanceToDraw);

            // Fill the sides
            for (double j = polygon.getMinY() + space; j < polygon.getMaxY(); j+=space) {
                drawLineForPlayer(player, new Vector(lastVertex.x, j, lastVertex.y), new Vector(vertex.x, j, vertex.y), space, color, maxDistanceToDraw);
            }
            
            // Vertical line
            drawLineForPlayer(player, new Vector(vertex.x, polygon.getMaxY(), vertex.y), new Vector(vertex.x, polygon.getMinY(), vertex.y), space, color, maxDistanceToDraw);
            lastVertex = vertex;
        }

        Vector2d vertex = polygon.getVertices().get(0);
        drawLineForPlayer(player, new Vector(lastVertex.x, polygon.getMaxY(), lastVertex.y), new Vector(vertex.x, polygon.getMaxY(), vertex.y), space, color, maxDistanceToDraw);
        drawLineForPlayer(player, new Vector(lastVertex.x, polygon.getMinY(), lastVertex.y), new Vector(vertex.x, polygon.getMinY(), vertex.y), space, color, maxDistanceToDraw);
    }
    
    public static void drawCircleForPlayer(Player player, World world, double radius, Vector location, double space, Color color, double maxDistanceToDraw) {float circumference = (float) (2 * Math.PI * radius);
        var playerVector = player.getLocation();
        int points = (int) (circumference / space);
        for (int i = 0; i < points; i++) {
            double angle = Math.toRadians(((double) i / points) * 360d);
            double x = Math.cos(angle) * radius;
            double z = Math.sin(angle) * radius;
            var point = location.clone().add(new Vector(x, 0, z));
            if(maxDistanceToDraw == -1 || isWithinDistance(point, playerVector.toVector(), maxDistanceToDraw))
                drawPoint(world, point, color);
        }
    }
    
    public static void drawSphereForPlayer(Player player, World world, Vector location, double radius, double space, Color color, double maxDistanceToDraw) {
        float circumference = (float) (2 * Math.PI * radius);
        int points = (int) (circumference / space);
        for (int i = 0; i < points / 4; i++) {
            double angle = Math.toRadians(((double) i / points) * 360d);
            double r = Math.cos(angle) * radius;
            double y = Math.sin(angle) * radius;
            var upPoint = location.clone().add(new Vector(0, y, 0));
            var downPoint = location.clone().add(new Vector(0, -y, 0));
            drawCircleForPlayer(player, world, r, upPoint, space, color, maxDistanceToDraw);
            drawCircleForPlayer(player, world, r, downPoint, space, color, maxDistanceToDraw);
        }
    }

    public static void drawCylinderForPlayer(Player player, World world, Vector location, double radius, double height, double space, Color color, double maxDistanceToDraw) {
        var pos = location.clone().add(new Vector(0, -height / 2, 0));
        var totalSteps = height/space;
        for (int i = 0; i < totalSteps; i++) {
            drawCircleForPlayer(player, world, radius, pos, space, color, maxDistanceToDraw);
            pos.add(new Vector(0, space, 0));
        }
    }

    public static void drawBezierForPlayer(Player player, List<Vector> controlSets, double space, Color color, double maxDistanceToDraw) {
        List<Vector> points = getBezierChain(controlSets);
        for (int i = 0; i < points.size() - 1; i++) {
            drawLineForPlayer(player, points.get(i), points.get(i + 1), space, color, maxDistanceToDraw);
        }
    }
    
    private static List<Vector> getBezierChain(List<Vector> controlSets) {
        if (controlSets.size() < 4)
            return List.of();
        
        List<Vector> points = new ArrayList<>();
        if (controlSets.size() == 4) {
            for (int i = 0; i < 21; i++) {
                double t = (double) i / 20;
                points.add(new Vector(
                        bezierPoint(t, controlSets.get(0).getX(), controlSets.get(1).getX(), controlSets.get(2).getX(), controlSets.get(3).getX()),
                        bezierPoint(t, controlSets.get(0).getY(), controlSets.get(1).getY(), controlSets.get(2).getY(), controlSets.get(3).getY()),
                        bezierPoint(t, controlSets.get(0).getZ(), controlSets.get(1).getZ(), controlSets.get(2).getZ(), controlSets.get(3).getZ())
                ));
            }
        } else {
            for (int chain = 0; chain < controlSets.size() / 4; chain++) {
                int start = chain * 4;
                int end = Math.min(start + 4, controlSets.size());
                points.addAll(getBezierChain(controlSets.subList(start, end)));
            }
        }
        
        return points;
    }

    private static double bezierPoint(double t, double p0, double p1, double p2, double p3) {
        return p0*Math.pow(1-t, 3) + 3*p1*Math.pow(1-t, 2)*t + 3*p2*(1-t)*t*t + p3*t*t*t;
    }
    //#endregion
}
