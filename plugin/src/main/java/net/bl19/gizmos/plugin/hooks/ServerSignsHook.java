package net.bl19.gizmos.plugin.hooks;

import net.bl19.gizmos.api.Gizmos;
import net.bl19.gizmos.api.managers.Namespace;
import net.bl19.gizmos.api.objects.impl.BlockLocationGizmo;
import net.bl19.gizmos.plugin.GizmosPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.logging.Logger;

public class ServerSignsHook implements PluginHook {
    
    private static final String PLUGIN_NAME = "ServerSigns";
    private static final Map<String, BlockLocationGizmo> gizmos = new HashMap<>();
    private static final Map<String, Long> lastRead = new HashMap<>();
    private static Namespace namespace;
    
    private static final long READ_INTERVAL = 20*5;
    
    private static BukkitTask task;
    
    
    @Override
    public void enable() {
        var logger = Logger.getLogger("Gizmos ServerSigns Hook");
        var configDir = Bukkit.getPluginManager().getPlugin(PLUGIN_NAME).getDataFolder();
        var signsDir = new File(configDir, "signs");
        if (!signsDir.exists()) {
            return;
        }
        
        var files = signsDir.listFiles();
        if (files == null) {
            return;
        }
        
        namespace = Gizmos.getNamespaceManager().getOrCreateNamespace("serversigns", "gizmos.hooks.serversigns");;
        
        
        for (var file : files) {
            if (file.getName().endsWith(".yml")) {
                readFile(file);
            }
        }
        
        logger.info("Loaded " + gizmos.size() + " gizmos from ServerSigns");
        
        task = Bukkit.getScheduler().runTaskTimer(GizmosPlugin.getInstance(), () -> {
            var oldFiles = new HashSet<>(lastRead.keySet());
            for (var file : files) {
                oldFiles.remove(file.getName());
                if (file.getName().endsWith(".yml")) {
                    if (lastRead.containsKey(file.getName()) && lastRead.get(file.getName()) == file.lastModified()) {
                        continue;
                    }
                    readFile(file);
                    logger.info("Reloaded gizmo " + file.getName() + " from ServerSigns");
                }
            }
            
            for (var oldFile : oldFiles) {
                lastRead.remove(oldFile);
                namespace.removeGizmo(gizmos.get(oldFile));
                gizmos.remove(oldFile);
                logger.info("Removed gizmo " + oldFile + " from ServerSigns");
            }
        }, READ_INTERVAL, READ_INTERVAL);
    }

    private void readFile(File file) {
        lastRead.put(file.getName(), file.lastModified());
        var yaml = YamlConfiguration.loadConfiguration(file);
        var world = yaml.getString("world");
        var x = yaml.getInt("X");
        var y = yaml.getInt("Y");
        var z = yaml.getInt("Z");

        var location = new Location(Bukkit.getWorld(world), x, y, z);
        var commandsSection = yaml.getConfigurationSection("commands");
        if (commandsSection == null) {
            return;
        }

        var commands = commandsSection.getKeys(false);
        if (commands == null) {
            return;
        }

        // Get the first command
        commandsSection = commandsSection.getConfigurationSection(commands.stream().findFirst().orElse(null));
        if (commandsSection == null) {
            return;
        }

        var command = commandsSection.getString("command");
        
        if(gizmos.containsKey(location)) {
            namespace.removeGizmo(gizmos.get(location));
        }

        var gizmo = new BlockLocationGizmo(namespace, command, location);
        gizmos.put(file.getName(), gizmo);
        namespace.addGizmo(gizmo);
    }

    @Override
    public void disable() {
        if (task != null) {
            task.cancel();
        }
        Gizmos.getNamespaceManager().removeNamespace(namespace);
        gizmos.clear();
    }
}
