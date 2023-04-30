package mod.flatcoloredblocks.network;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.INetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;

import java.io.IOException;

/**
 * Base Packet to be implemented.
 */
@SuppressWarnings("rawtypes")
public abstract class ModPacket implements IPacket {

    ServerPlayerEntity serverEntity = null;

    public void server(
            final ServerPlayerEntity playerEntity) {

        throw new RuntimeException(getClass().getName() + " is not a server packet.");
    }

    public void client() {
        throw new RuntimeException(getClass().getName() + " is not a client packet.");
    }

    abstract public void getPayload(
            PacketBuffer buffer);

    abstract public void readPayload(
            PacketBuffer buffer);

    @Override
    public void readPacketData(
            final PacketBuffer buf) throws IOException {
        readPacketData(buf);
    }

    @Override
    public void writePacketData(
            final PacketBuffer buf) throws IOException {
        getPayload(buf);
    }

    @Override
    public void processPacket(
            final INetHandler handler) {
        if (serverEntity == null) {
            client();
        } else {
            server(serverEntity);
        }
    }

}
