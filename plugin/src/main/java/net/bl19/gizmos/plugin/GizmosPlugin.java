package net.bl19.gizmos.plugin;

import net.bl19.gizmos.api.managers.NamespaceManager;
import net.bl19.gizmos.api.managers.RenderManager;
import net.bl19.gizmos.plugin.commands.GizmosCommand;
import net.bl19.gizmos.plugin.hooks.PluginHook;
import net.bl19.gizmos.plugin.hooks.ServerSignsHook;
import net.bl19.gizmos.plugin.hooks.WorldGuardHook;
import net.bl19.gizmos.plugin.managers.NamespaceManagerImpl;
import net.bl19.gizmos.plugin.managers.RenderManagerImpl;
import net.bl19.gizmos.plugin.renderers.debug_renderer_fabric.DebugRendererFabricRenderer;
import net.bl19.gizmos.plugin.renderers.vanilla.VanillaRenderer;
import net.bl19.gizmos.plugin.renderers.vanilla.impl.DebugMarkerRenderer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class GizmosPlugin extends JavaPlugin {

    private static GizmosPlugin instance;
    
    public static GizmosPlugin getInstance() {
        return instance;
    }
    
    public NamespaceManager namespaceManager;
    public RenderManager renderManager;
    
    @Override
    public void onEnable() {
        instance = this;
        DebugMarkerRenderer.start();
        DebugRendererFabricRenderer.startQueueProcessor();
        
        namespaceManager = new NamespaceManagerImpl();
        renderManager = new RenderManagerImpl();

        renderManager.registerRenderer(new DebugRendererFabricRenderer());
        renderManager.registerRenderer(new VanillaRenderer());

        getCommand("gizmos").setExecutor(new GizmosCommand());
        getCommand("gizmos").setTabCompleter(new GizmosCommand());
        registerHooks();
        enableHooks();
    }
    
    private static final List<PluginHook> hooks = new ArrayList<>();
    
    private void registerHooks() {
        if(Bukkit.getPluginManager().isPluginEnabled("WorldGuard")) {
            registerHook(new WorldGuardHook());
        } else {
            getLogger().warning("WorldGuard not found, not registering WorldGuard hook");
        }
        
        if(Bukkit.getPluginManager().isPluginEnabled("ServerSigns")) {
            registerHook(new ServerSignsHook());
        } else {
            getLogger().warning("ServerSigns not found, not registering ServerSigns hook");
        }
    }
    
    private void unregisterHooks() {
        hooks.forEach(PluginHook::disable);
    }
    
    private void enableHooks() {
        hooks.forEach(PluginHook::enable);
    }

    
    private void registerHook(PluginHook hook) {
        hooks.add(hook);
    }

    @Override
    public void onDisable() {
        unregisterHooks();
        DebugMarkerRenderer.stop();
    }
    
    
}
