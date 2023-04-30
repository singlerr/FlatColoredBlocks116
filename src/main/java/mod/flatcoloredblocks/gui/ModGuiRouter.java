package mod.flatcoloredblocks.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.world.World;

import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * Client / Server Gui + Container Handler
 */
public class ModGuiRouter implements BiFunction<Minecraft, Screen, Screen>, Supplier<BiFunction<Minecraft, Screen, Screen>> {

    public static Container createContainer(
            ModGuiTypes type,
            final PlayerEntity player,
            final World world,
            final int x,
            final int y,
            final int z) {
        try {
            return (Container) type.container_construtor.newInstance(player, world, x, y, z);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Screen apply(
            Minecraft t, Screen u
    ) {
        try {
            final ModGuiTypes guiType = ModGuiTypes.valueOf(u.getTitle().getString());
            return (Screen) guiType.gui_construtor.newInstance(Minecraft.getInstance().player, Minecraft.getInstance().player.world, 0, 0, 0);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets a result.
     *
     * @return a result
     */
    @Override
    public BiFunction<Minecraft, Screen, Screen> get() {
        return this;
    }
/*
    @Override
    public Function<OpenContainer, Screen> get()
    {
        return this;
    }

 */
}
