package net.bl19.gizmos.nms.v1_20_R2;

import com.comphenix.protocol.events.PacketContainer;
import io.netty.buffer.Unpooled;
import net.bl19.gizmos.nms.NMSPacketSerializer;
import net.minecraft.core.BlockPosition;
import net.minecraft.network.PacketDataSerializer;
import org.bukkit.Location;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.util.function.Function;

public class NMSPacketSerializerImpl extends NMSPacketSerializer {

    PacketDataSerializer data;

    public NMSPacketSerializerImpl() {
        data = new PacketDataSerializer(Unpooled.buffer());
    }

    @Override
    public void writeByte(int b) {
        data.writeByte(b);
    }

    @Override
    public void writeBlockLocation(Location location) {
        data.a(new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
    }

    @Override
    public void writeInt(int value) {
        data.writeInt(value);
    }
    

    @Override
    public void writeDouble(double value) {
        data.writeDouble(value);
    }

    @Override
    public void writeFloat(float value) {
        data.writeFloat(value);
    }

    @Override
    public void writeLong(long value) {
        data.writeLong(value);
    }

    @Override
    public void writeString(String value) {
        data.a(value);
    }
    

    @Override
    public void writeToPacket(PacketContainer container, int field) {
        container.getSpecificModifier(PacketDataSerializer.class).write(field, data);
    }
}
