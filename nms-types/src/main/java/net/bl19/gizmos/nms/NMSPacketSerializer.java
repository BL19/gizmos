package net.bl19.gizmos.nms;

import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.Location;

import java.util.Collection;
import java.util.Collections;
import java.util.function.BiConsumer;
import java.util.function.Function;

public abstract class NMSPacketSerializer {
    
    public abstract void writeByte(int b);
    public abstract void writeBlockLocation(Location location);
    public abstract void writeInt(int value);
    
    public void writeVarInt(int i) {
        while((i & -128) != 0) {
            this.writeByte(i & 127 | 128);
            i >>>= 7;
        }

        this.writeByte(i);
    }
    
    public abstract void writeDouble(double value);
    public abstract void writeFloat(float v);
    public abstract void writeLong(long value);
    public void writeVarLong(long i) {
        while((i & -128L) != 0L) {
            this.writeByte((int)(i & 127L) | 128);
            i >>>= 7;
        }

        this.writeByte((int)i);
    }
    
    public void writeEnum(Enum<?> value) {
        this.writeVarInt(value.ordinal());
    }
    
    public <T> void writeCollection(Collection<T> collection, BiConsumer<NMSPacketSerializer, T> converter) {
        if (collection == null) {
            writeByte(0);
            return;
        }
        this.writeVarInt(collection.size());
        collection.forEach((o) -> {
            converter.accept(this, o);
        });
    }
    
    public abstract void writeString(String value);
    
    public abstract void writeToPacket(PacketContainer container, int field);
}
