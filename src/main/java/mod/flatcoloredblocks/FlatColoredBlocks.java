package mod.flatcoloredblocks;

import mod.flatcoloredblocks.block.BlockFlatColored;
import mod.flatcoloredblocks.block.BlockHSVConfiguration;
import mod.flatcoloredblocks.block.EnumFlatBlockType;
import mod.flatcoloredblocks.block.ItemBlockFlatColored;
import mod.flatcoloredblocks.client.ClientSide;
import mod.flatcoloredblocks.config.ModConfig;
import mod.flatcoloredblocks.craftingitem.ItemColoredBlockCrafter;
import mod.flatcoloredblocks.network.NetworkRouter;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.IForgeRegistry;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

@Mod(FlatColoredBlocks.MODID)
public class FlatColoredBlocks {
    public static final String MODID = "flatcoloredblocks";
    private static final List<BlockItem> itemBlocks = new LinkedList<BlockItem>();
    // create creative tab...
    public static FlatColoredBlocks instance;
    public CreativeTab creativeTab;
    public ModConfig config;
    // @ObjectHolder( "flatcoloredblocks:coloredcraftingitem" )
    public ItemColoredBlockCrafter itemColoredBlockCrafting;
    public BlockHSVConfiguration normal;
    public BlockHSVConfiguration transparent;
    public BlockHSVConfiguration glowing;

    public FlatColoredBlocks() {
        instance = this;

        // configure creative tab.
        creativeTab = new CreativeTab();

        // configure networking and gui.
        NetworkRouter.instance = new NetworkRouter();

        config = new ModConfig(new File(FMLPaths.CONFIGDIR.get().toFile(), MODID));
        initHSVFromConfiguration(config);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
        {
            ClientSide.instance.preinit();
            FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientSide.instance::init);
        });

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::init);
    }

    private void init(
            final FMLCommonSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(this);

    }

    public int getFullNumberOfShades() {
        return normal.getNumberOfShades()
                + transparent.getNumberOfShades() * FlatColoredBlocks.instance.config.TRANSPARENCY_SHADES
                + glowing.getNumberOfShades() * FlatColoredBlocks.instance.config.GLOWING_SHADES;
    }

    public int getFullNumberOfBlocks() {
        return normal.getNumberOfShades()
                + transparent.getNumberOfShades()
                + glowing.getNumberOfShades();
    }

    public void initHSVFromConfiguration(
            final ModConfig config) {
        normal = new BlockHSVConfiguration(EnumFlatBlockType.NORMAL, config);
        transparent = new BlockHSVConfiguration(EnumFlatBlockType.TRANSPARENT, config);
        glowing = new BlockHSVConfiguration(EnumFlatBlockType.GLOWING, config);
    }

    @SubscribeEvent
    public void serverStarted(
            FMLServerStartedEvent event) {
        Log.info("Server start");
    }

    public void items(
            IForgeRegistry<Item> registry) {
        registry.register(FlatColoredBlocks.instance.itemColoredBlockCrafting = new ItemColoredBlockCrafter());

        for (BlockItem ib : itemBlocks) {
            registry.register(ib);
        }

    }

    public void blocks(
            IForgeRegistry<Block> registry) {
        final BlockHSVConfiguration[] configs = new BlockHSVConfiguration[]{FlatColoredBlocks.instance.normal, FlatColoredBlocks.instance.transparent, FlatColoredBlocks.instance.glowing};

        // any time we regenerate blocks we regenerate this.
        itemBlocks.clear();

        // create and configure all blocks.
        for (final BlockHSVConfiguration hsvconfig : configs) {
            for (int v = 0; v < hsvconfig.MAX_SHADE_VARIANT; ++v) {
                final BlockFlatColored cb = BlockFlatColored.construct(hsvconfig, v);
                registry.register(cb);

                final ItemBlockFlatColored cbi = new ItemBlockFlatColored(cb);
                itemBlocks.add(cbi);
            }
        }
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onBlocksRegistry(
                RegistryEvent.Register<Block> ev) {
            Log.debug("registering blocks : " + ev.getName());
            FlatColoredBlocks.instance.blocks(ev.getRegistry());
        }

        @SubscribeEvent
        public static void onItemsRegistry(
                RegistryEvent.Register<Item> ev) {
            Log.debug("registering items : " + ev.getName());
            FlatColoredBlocks.instance.items(ev.getRegistry());
        }


    }

}
