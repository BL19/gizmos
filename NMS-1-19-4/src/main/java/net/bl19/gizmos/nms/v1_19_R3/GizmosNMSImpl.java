package net.bl19.gizmos.nms.v1_19_R3;

import net.bl19.gizmos.nms.GizmosNMS;
import net.bl19.gizmos.nms.NMSPacketSerializer;

public class GizmosNMSImpl implements GizmosNMS {
    @Override
    public NMSPacketSerializer createPacketSerializer() {
        return new NMSPacketSerializerImpl();
    }
}
