package net.bl19.gizmos.api;

import net.bl19.gizmos.api.managers.NamespaceManager;
import net.bl19.gizmos.api.managers.RenderManager;
import org.bukkit.Bukkit;

import java.util.logging.Logger;

public class Gizmos {

    private static Object plugin;
    private static NamespaceManager namespaceManager;
    private static RenderManager renderManager;
    private static boolean isPluginMissing = false;
    
    // Methods to get the singleton instances of the managers from the plugin class
    private static Object getPlugin() {
        if (isPluginMissing) {
            return null;
        }
        if (plugin == null) {
            plugin = Bukkit.getPluginManager().getPlugin("Gizmos");
            if (plugin == null) {
                Logger.getLogger("GizmosAPI").warning("Gizmos plugin not found, some features may not work.");
                isPluginMissing = true;
            }
        }
        return plugin;
    }
    
    private static <T> T getTypeFromPlugin(Class<T> type) {
        Object plugin = getPlugin();
        if (plugin == null) {
            return null;
        }
        // Use reflection to get the type from the plugin class
        var fields = plugin.getClass().getDeclaredFields();
        for (var field : fields) {
            if (field.getType().equals(type)) {
                try {
                    return (T) field.get(plugin);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
    
    public static NamespaceManager getNamespaceManager() {
        if (namespaceManager == null) {
            namespaceManager = getTypeFromPlugin(NamespaceManager.class);
        }
        return namespaceManager;
    }
    
    public static RenderManager getRenderManager() {
        if (renderManager == null) {
            renderManager = getTypeFromPlugin(RenderManager.class);
        }
        return renderManager;
    }
    
}
