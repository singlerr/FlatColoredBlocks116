package mod.flatcoloredblocks.network;

import net.minecraft.network.INetHandler;
import net.minecraft.network.PacketBuffer;

import java.io.IOException;

public interface Packet {

    void readPacketData(PacketBuffer buffer) throws IOException;

    void writePacketData(PacketBuffer buffer) throws IOException;

    void processPacket(INetHandler handler);
}
