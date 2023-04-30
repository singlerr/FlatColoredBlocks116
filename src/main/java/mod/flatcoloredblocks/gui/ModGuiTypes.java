package mod.flatcoloredblocks.gui;

import mod.flatcoloredblocks.FlatColoredBlocks;
import mod.flatcoloredblocks.craftingitem.ContainerColoredBlockCrafter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;

/**
 * Registry of Guis
 */
@SuppressWarnings("unused")
public enum ModGuiTypes {

    colored_crafter(ContainerColoredBlockCrafter.class);

    public final Constructor<?> container_construtor;
    public final Constructor<?> gui_construtor;
    private final Class<? extends Container> container;
    private final Class<?> gui;

    ModGuiTypes(
            final Class<? extends Container> c) {
        try {
            container = c;
            container_construtor = container.getConstructor(PlayerEntity.class, World.class, int.class, int.class, int.class);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }

        // by default...
        Class<?> g = null;
        Constructor<?> g_construtor = null;

        // attempt to get gui class/constructor...
        try {
            g = (Class<?>) container.getMethod("getGuiClass").invoke(null);
            g_construtor = g.getConstructor(PlayerEntity.class, World.class, int.class, int.class, int.class);
        } catch (final Exception e) {
            // if and only if we are on the client should this be considered an
            // error...
            if (FMLEnvironment.dist == Dist.CLIENT) {
                throw new RuntimeException(e);
            }

        }

        gui = g;
        gui_construtor = g_construtor;

    }

    public ResourceLocation getID() {
        return new ResourceLocation(FlatColoredBlocks.MODID, toString());
    }

    public INamedContainerProvider create(
            PlayerEntity player,
            World worldIn,
            int x,
            int y,
            int z) {

        final ModGuiTypes self = this;
        return new INamedContainerProvider() {

            @Nullable
            @Override
            public Container createMenu(int p_createMenu_1_, PlayerInventory p_createMenu_2_, PlayerEntity p_createMenu_3_) {
                return ModGuiRouter.createContainer(ModGuiTypes.colored_crafter, player, worldIn, 0, 0, 0);
            }

            @Override
            public ITextComponent getDisplayName() {
                return null;
            }


        };
    }
}
