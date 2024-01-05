package net.bl19.gizmos.plugin.managers;

import net.bl19.gizmos.api.managers.Namespace;
import net.bl19.gizmos.api.managers.NamespaceManager;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class NamespaceManagerImpl implements NamespaceManager {
    
    private final Map<String, Namespace> namespaces = new HashMap<>();
    
    @Override
    public Namespace createNamespace(String name, String permission) {
        Namespace namespace = new Namespace(name, permission);
        namespaces.put(name, namespace);
        return namespace;
    }

    @Override
    public Namespace getNamespace(String name) {
        return namespaces.get(name);
    }

    @Override
    public void removeNamespace(Namespace namespace) {
        for (var gizmo : namespace.getGizmos()) {
            namespace.removeGizmo(gizmo);
        }
        namespaces.remove(namespace.getName());
    }

    @Override
    public List<Namespace> getNamespaces() {
        return List.copyOf(namespaces.values());
    }

    @Override
    public Color getNextColor(String name) {
        // Generate a random color from the name of the namespace
        Random random = new Random(name.hashCode());
        return new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255));
    }
}
