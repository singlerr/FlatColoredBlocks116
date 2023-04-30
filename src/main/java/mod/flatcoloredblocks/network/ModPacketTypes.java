package mod.flatcoloredblocks.network;

import mod.flatcoloredblocks.network.packets.ScrolingGuiPacket;

import java.util.HashMap;

/**
 * Registry of Packets that can be sent and recieved.
 */
public enum ModPacketTypes {
    ADJUST_SCROLL(ScrolingGuiPacket.class);

    private static final HashMap<Class<? extends ModPacket>, Integer> fromClassToId = new HashMap<Class<? extends ModPacket>, Integer>();
    private static final HashMap<Integer, Class<? extends ModPacket>> fromIdToClass = new HashMap<Integer, Class<? extends ModPacket>>();
    private final Class<? extends ModPacket> packetClass;

    ModPacketTypes(
            final Class<? extends ModPacket> clz) {
        packetClass = clz;
    }

    public static void init() {
        for (final ModPacketTypes p : ModPacketTypes.values()) {
            fromClassToId.put(p.packetClass, p.ordinal());
            fromIdToClass.put(p.ordinal(), p.packetClass);
        }
    }

    public static int getID(
            final Class<? extends ModPacket> clz) {
        return fromClassToId.get(clz);
    }

    public static ModPacket constructByID(
            final int id) throws InstantiationException, IllegalAccessException {
        return fromIdToClass.get(id).newInstance();
    }

}
