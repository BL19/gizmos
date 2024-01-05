package net.bl19.gizmos.api.managers;

import java.awt.*;
import java.util.List;

public interface NamespaceManager {
    
    public Namespace createNamespace(String name, String permission);
    public Namespace getNamespace(String name);
    default public Namespace getOrCreateNamespace(String name, String permission) {
        var namespace = getNamespace(name);
        if (namespace == null) {
            namespace = createNamespace(name, permission);
        }
        return namespace;
    }
    public void removeNamespace(Namespace namespace);
    public List<Namespace> getNamespaces();

    Color getNextColor(String name);
}
