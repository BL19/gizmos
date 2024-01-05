package net.bl19.gizmos.nms.v1_20_R2;

import net.bl19.gizmos.nms.GizmosNMS;
import net.bl19.gizmos.nms.NMSPacketSerializer;

public class GizmosNMSImpl implements GizmosNMS {
    @Override
    public NMSPacketSerializer createPacketSerializer() {
        return new NMSPacketSerializerImpl();
    }
}
