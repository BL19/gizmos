package net.bl19.gizmos.plugin.commands;

import net.bl19.gizmos.api.Gizmos;
import net.bl19.gizmos.api.managers.Namespace;
import net.bl19.gizmos.plugin.renderers.debug_renderer_fabric.DebugRendererFabricRenderer;
import org.bukkit.Instrument;
import org.bukkit.Note;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Calendar;
import java.util.List;

public class GizmosCommand implements CommandExecutor, TabCompleter {
    
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("[GIZMOS] You must be a player to execute this command!");
            return true;
        }
        
        if (args.length < 1) {
            player.sendMessage("[GIZMOS] Please specify a subcommand");
            player.playNote(player.getLocation(), Instrument.BANJO, Note.flat(1, Note.Tone.G));
            return true;
        }
        
        switch (args[0]) {
            case "help":
                player.sendMessage("[GIZMOS] Available sub commands are:\n  '/gizmos ns <namespace>' - Toggle a debug namespace\n  '/gizmos gizmos <namespace>' - Print what gizmos are present in the specified namespace\n  '/gizmos toggle-renderer <renderer>' - Toggle a renderer supported by the plugin. Notice may require a modified client!");
                break;
            case "ns":
                var ns = Gizmos.getNamespaceManager().getNamespace(args[1]);
                if (ns == null) {
                    sender.sendMessage("[GIZMOS] Namespace with name '" + args[1] + "' does not exist!");
                    player.playNote(player.getLocation(), Instrument.BANJO, Note.flat(1, Note.Tone.G));
                } else if (!ns.getWatchers().contains(player.getUniqueId())) {
                    ns.addWatcher(player.getUniqueId());
                    sender.sendMessage("[GIZMOS] You are now watching the namespace " + ns.getName());
                } else {
                    ns.removeWatcher(player.getUniqueId());
                    sender.sendMessage("[GIZMOS] You are no longer watching the namespace " + ns.getName());
                }
                break;
            case "list":
                var namespaces = Gizmos.getNamespaceManager().getNamespaces();
                if (namespaces.isEmpty()) {
                    sender.sendMessage("[GIZMOS] There are no namespaces");
                } else {
                    sender.sendMessage("[GIZMOS] Namespaces:");
                    for (Namespace namespace : namespaces) {
                        sender.sendMessage("  " + namespace.getName());
                    }
                }
                break;
            case "gizmos":
                var ns1 = Gizmos.getNamespaceManager().getNamespace(args[1]);
                if (ns1 == null) {
                    sender.sendMessage("[GIZMOS] Namespace with name '" + args[1] + "' does not exist!");
                    player.playNote(player.getLocation(), Instrument.BANJO, Note.flat(1, Note.Tone.G));
                } else {
                    var gizmos = ns1.getGizmos();
                    if (gizmos.isEmpty()) {
                        sender.sendMessage("[GIZMOS] There are no gizmos in the namespace " + ns1.getName());
                    } else {
                        sender.sendMessage("[GIZMOS] Gizmos in namespace " + ns1.getName() + ":");
                        for (var gizmo : gizmos) {
                            sender.sendMessage("  " + gizmo.getName() + " - " + gizmo.getType().getName() + " - " + (gizmo.isWithinRenderDistance(player.getLocation()) ? "In range" : "Out of range"));
                        }
                    }
                }
                break;
            case "toggle-renderer":
                switch (args[1]) {
                    case "debug_renderer_fabric":
                        if(DebugRendererFabricRenderer.playersWithMod.contains(player.getUniqueId())) {
                            DebugRendererFabricRenderer.playersWithMod.remove(player.getUniqueId());
                            sender.sendMessage("[GIZMOS] You are no longer using the debug renderer fabric");
                        } else {
                            DebugRendererFabricRenderer.playersWithMod.add(player.getUniqueId());
                            sender.sendMessage("[GIZMOS] You are now using the debug renderer fabric");
                        }    
                        break;
                    default:
                        sender.sendMessage("[GIZMOS] Renderer with name '" + args[1] + "' does not exist!");
                        player.playNote(player.getLocation(), Instrument.BANJO, Note.flat(1, Note.Tone.G));
                }
                break;
            default:
                player.sendMessage("[GIZMOS] That sub command does not exist! Write '/gizmos help' for help");
                player.playNote(player.getLocation(), Instrument.BANJO, Note.flat(1, Note.Tone.G));
        }
        
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if(args.length == 1) {
            return List.of("help", "ns", "gizmos", "toggle-renderer");
        } else if(args.length == 2) {
            if(args[0].equals("ns") || args[0].equals("gizmos")) {
                return Gizmos.getNamespaceManager().getNamespaces().stream().map(Namespace::getName).filter(x -> x.toLowerCase().startsWith(args[1].toLowerCase())).toList();
            } else if (args[0].equalsIgnoreCase("toggle-renderer")) {
                return List.of("debug_renderer_fabric");
            }
        }
        
        return null;
    }
    
}
