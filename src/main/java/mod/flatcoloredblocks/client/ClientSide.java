package mod.flatcoloredblocks.client;

import mod.flatcoloredblocks.FlatColoredBlocks;
import mod.flatcoloredblocks.ModUtil;
import mod.flatcoloredblocks.block.BlockFlatColored;
import mod.flatcoloredblocks.block.EnumFlatBlockType;
import mod.flatcoloredblocks.gui.ModGuiRouter;
import mod.flatcoloredblocks.resource.CustomResourceInjector;
import mod.flatcoloredblocks.resource.ResourceGenerator;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.item.ItemStack;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.SimpleReloadableResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.resource.IResourceType;
import net.minecraftforge.resource.ISelectiveResourceReloadListener;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

public class ClientSide {

    public static final ClientSide instance = new ClientSide();

    public ResourceGenerator resourceGenerator = new ResourceGenerator();

    private ClientSide() {
    }

    public void preinit() {
        resourceGenerator.init();
        MinecraftForge.EVENT_BUS.register(resourceGenerator);

        IResourceManager manager = Minecraft.getInstance().getResourceManager();
        if (manager instanceof IReloadableResourceManager) {
            ((IReloadableResourceManager) manager).addReloadListener(new ISelectiveResourceReloadListener() {

                @Override
                public void onResourceManagerReload(
                        IResourceManager resourceManager,
                        Predicate<IResourceType> resourcePredicate) {
                    if (FlatColoredBlocks.instance.itemColoredBlockCrafting != null) {
                        FlatColoredBlocks.instance.itemColoredBlockCrafting.scrollIndex = -1;
                    }
                }

            });
            if (manager instanceof SimpleReloadableResourceManager) {
                ((SimpleReloadableResourceManager) manager).addResourcePack(CustomResourceInjector.generatedFiles);
                resourceGenerator.populateResources();
            }
        }
    }

    public void createResources() {
        resourceGenerator.populateResources();
    }

    public void init(
            FMLLoadCompleteEvent ev) {
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, new ModGuiRouter());
        //resourceGenerator.populateResources();
        //  clientItems();
        //  clientBlocks();
    }

    public void clientItems(ItemColors colors) {
        Block[] flatColoredBlocks = BlockFlatColored.getAllBlocks().toArray(new Block[BlockFlatColored.getAllBlocks().size()]);
        //ItemColors colors = Minecraft.getInstance().getItemColors();

        colors.register(new IItemColor() {

            @Override
            @Nonnull
            public int getColor(
                    final ItemStack stack,
                    final int tintIndex) {
                final Block blk = Block.getBlockFromItem(stack.getItem());
                return ((BlockFlatColored) blk).colorFromState(ModUtil.getFlatColoredBlockState(((BlockFlatColored) blk), stack));
            }
        }, flatColoredBlocks);
    }

    public void clientBlocks(BlockColors colors) {
        Block[] flatColoredBlocks = BlockFlatColored.getAllBlocks().toArray(new Block[BlockFlatColored.getAllBlocks().size()]);
        //BlockColors colors = Minecraft.getInstance().getBlockColors();


        colors.register(new IBlockColor() {

            @Override
            public int getColor(
                    final BlockState state,
                    final IBlockDisplayReader p_186720_2_,
                    final BlockPos pos,
                    final int tintIndex) {
                return ((BlockFlatColored) state.getBlock()).colorFromState(state);
            }

        }, flatColoredBlocks);
    }

    public ResourceLocation getTextureName(
            final EnumFlatBlockType type,
            final int varient) {
        return new ResourceLocation(FlatColoredBlocks.MODID, getBaseTextureName(type) + "_" + varient);
    }

    public String getBaseTextureName(
            final EnumFlatBlockType type) {
        return "flatcoloredblock" + getTextureFor(type);
    }

    public String getBaseTextureNameWithBlocks(
            final EnumFlatBlockType type) {
        return "blocks/flatcoloredblock" + getTextureFor(type);
    }

    public ResourceLocation getTextureResourceLocation(
            final EnumFlatBlockType type) {
        return new ResourceLocation(FlatColoredBlocks.MODID, "textures/blocks/flatcoloredblock_" + getTextureFileFor(type) + ".png");
    }

    public String getTextureRawLocation(final EnumFlatBlockType type) {
        return "assets.flatcoloredblocks/textures/blocks/flatcoloredblock_" + getTextureFileFor(type) + ".png";
    }

    private String getTextureFileFor(
            final EnumFlatBlockType type) {
        switch (type) {
            case GLOWING:
                return FlatColoredBlocks.instance.config.DISPLAY_TEXTURE_GLOWING.resourceName();
            case TRANSPARENT:
                return FlatColoredBlocks.instance.config.DISPLAY_TEXTURE_TRANSPARENT.resourceName();
            default:
                return FlatColoredBlocks.instance.config.DISPLAY_TEXTURE.resourceName();
        }
    }

    private String getTextureFor(
            final EnumFlatBlockType type) {
        switch (type) {
            case GLOWING:
                return "_glowing";
            case TRANSPARENT:
                return "_transparent";
            default:
                return "";
        }
    }

}
