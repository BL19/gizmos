package net.bl19.gizmos.api.managers;

import net.bl19.gizmos.api.Gizmos;
import net.bl19.gizmos.api.objects.Gizmo;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Namespace {

    private String name;
    private List<Gizmo> gizmos = new ArrayList<Gizmo>();
    private String permission;
    private List<UUID> watchers = new ArrayList<>();

    private Color color;

    public Namespace(String name, String permission) {
        this.name = name;
        this.permission = permission;
        this.color = Gizmos.getNamespaceManager().getNextColor(name);
    }

    public String getName() {
        return name;
    }

    public List<Gizmo> getGizmos() {
        return gizmos;
    }

    public String getPermission() {
        return permission;
    }

    public void addGizmo(Gizmo gizmo) {
        gizmos.add(gizmo);
        notifyChange(gizmo);
    }

    public void removeGizmo(Gizmo gizmo) {
        gizmo.setEnabled(false);
        gizmos.remove(gizmo);
        for (UUID uuid : watchers) {
            Gizmos.getRenderManager().stopRendering(Bukkit.getPlayer(uuid), gizmo);
        }
    }

    public void removeGizmo(String name) {
        for (Gizmo gizmo : gizmos) {
            if (gizmo.getName().equals(name)) {
                removeGizmo(gizmo);
                return;
            }
        }
    }

    public Gizmo getGizmo(String name) {
        for (Gizmo gizmo : gizmos) {
            if (gizmo.getName().equals(name)) {
                return gizmo;
            }
        }
        return null;
    }

    public boolean hasGizmoWithName(String name) {
        for (Gizmo gizmo : gizmos) {
            if (gizmo.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasPermission() {
        return permission != null;
    }

    public boolean hasPermission(Player player) {
        return permission != null && player.hasPermission(permission);
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public List<UUID> getWatchers() {
        return watchers;
    }

    public void addWatcher(UUID uuid) {
        watchers.add(uuid);
        for (Gizmo gizmo : gizmos) {
            Gizmos.getRenderManager().render(Bukkit.getPlayer(uuid), gizmo);
        }
    }

    public void removeWatcher(UUID uuid) {
        watchers.remove(uuid);
        for (Gizmo gizmo : gizmos) {
            Gizmos.getRenderManager().stopRendering(Bukkit.getPlayer(uuid), gizmo);
        }
    }

    public boolean isWatching(UUID uuid) {
        return watchers.contains(uuid);
    }

    public void notifyChange(Gizmo gizmo) {
        for (UUID watcher : watchers) {
            Player player = Bukkit.getPlayer(watcher);
            if (player != null) {
                Gizmos.getRenderManager().render(player, gizmo);
            }
        }
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
