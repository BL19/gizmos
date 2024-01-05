package net.bl19.gizmos.nms;


import org.jetbrains.annotations.NotNull;

public interface GizmosNMS {
    
    public @NotNull NMSPacketSerializer createPacketSerializer();
    
}
