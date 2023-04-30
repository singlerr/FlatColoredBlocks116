package mod.flatcoloredblocks.network;

import io.netty.buffer.Unpooled;
import mod.flatcoloredblocks.FlatColoredBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.ThreadQuickExitException;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.ICustomPacket;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.event.EventNetworkChannel;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Sends packets and handles networking.
 */
public class NetworkRouter {

    public static NetworkRouter instance;

    final ServerPacketHandler serverPacketHandler;

    final ClientPacketHandler clientPacketHandler;
    private final EventNetworkChannel ec;
    private final ResourceLocation channel;

    public NetworkRouter() {
        ModPacketTypes.init();
        channel = new ResourceLocation(FlatColoredBlocks.MODID, "basic");

        ec = NetworkRegistry.newEventChannel(channel, () -> "1.0.0", s -> true, s -> true);

        clientPacketHandler = new ClientPacketHandler();
        ec.addListener(this::clientPacket);

        serverPacketHandler = new ServerPacketHandler();
        ec.addListener(this::serverPacket);
    }

    public void clientPacket(
            final NetworkEvent.ServerCustomPayloadEvent ev) {
        try {
            if (clientPacketHandler != null) {
                clientPacketHandler.onPacketData(ev.getPayload());
            }
        } catch (final ThreadQuickExitException ext) {
        }
    }

    public void serverPacket(
            final NetworkEvent.ClientCustomPayloadEvent ev) {
        // find player
        NetworkEvent.Context context = ev.getSource().get();

        try {
            if (serverPacketHandler != null) {
                serverPacketHandler.onPacketData(ev.getPayload(), context.getSender());
            }
        } catch (final ThreadQuickExitException ext) {
        }
    }

    private ModPacket parsePacket(
            final PacketBuffer buffer) {
        final int id = buffer.readByte();

        try {
            final ModPacket packet = ModPacketTypes.constructByID(id);
            packet.readPayload(buffer);
            return packet;
        } catch (final InstantiationException e) {
            throw new RuntimeException(e);
        } catch (final IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void sendToServer(
            ModPacket packet) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft != null) {
            ClientPlayNetHandler netHandler = minecraft.getConnection();
            if (netHandler != null) {
                int id = ModPacketTypes.getID(packet.getClass());
                PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
                buffer.writeVarInt(id);
                packet.getPayload(buffer);

                Pair<PacketBuffer, Integer> packetData = new ImmutablePair<PacketBuffer, Integer>(buffer, id);
                ICustomPacket<IPacket<?>> mcPacket = NetworkDirection.PLAY_TO_SERVER.buildPacket(packetData, channel);
                netHandler.sendPacket(mcPacket.getThis());

            }
        }
    }

    public void sendPacketToClient(
            ModPacket packet,
            ServerPlayerEntity player) {
        if (player != null && player.connection != null) {
            int id = ModPacketTypes.getID(packet.getClass());
            PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
            buffer.writeVarInt(id);
            packet.getPayload(buffer);

            Pair<PacketBuffer, Integer> packetData = new ImmutablePair<PacketBuffer, Integer>(buffer, id);
            ICustomPacket<IPacket<?>> mcPacket = NetworkDirection.PLAY_TO_CLIENT.buildPacket(packetData, channel);
            player.connection.sendPacket(mcPacket.getThis());
        }
    }

    /**
     * Receives packets on the client and dispatches them to the processor.
     */
    private class ClientPacketHandler {

        public void onPacketData(
                final PacketBuffer buffer) {
            final ModPacket innerPacket = parsePacket(buffer);

            Minecraft.getInstance().enqueue(new Runnable() {

                @Override
                public void run() {
                    innerPacket.client();
                }
            });
        }

    }

    /**
     * Receives packets on the server and dispatches them to the processor.
     */
    private class ServerPacketHandler {

        public void onPacketData(
                final PacketBuffer buffer,
                final ServerPlayerEntity playerEntity) {
            if (playerEntity == null) {
                return;
            }

            final ModPacket innerPacket = parsePacket(buffer);
            innerPacket.serverEntity = playerEntity;

            playerEntity.getServer().execute(new Runnable() {

                @Override
                public void run() {
                    innerPacket.server(playerEntity);
                }
            });
        }
    }
}