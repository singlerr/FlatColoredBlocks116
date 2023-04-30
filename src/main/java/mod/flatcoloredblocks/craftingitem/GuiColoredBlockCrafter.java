package mod.flatcoloredblocks.craftingitem;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import mod.flatcoloredblocks.FlatColoredBlocks;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.gui.GuiUtils;
import org.lwjgl.opengl.GL11;

/**
 * GuiContainer for crafting item's gui manages render, scroll bar and defers
 * scroll position to Container.
 */
public class GuiColoredBlockCrafter extends ContainerScreen {

    private static final ResourceLocation CRAFTER_GUI_TEXTURE = new ResourceLocation(FlatColoredBlocks.MODID, "textures/gui/container/coloredcrafting.png");

    private final ContainerColoredBlockCrafter myContainer;
    private boolean isScrolling = false;
    private float currentScroll = 0;

    public GuiColoredBlockCrafter(
            final PlayerEntity player,
            final World world,
            final int x,
            final int y,
            final int z) {

        super(new ContainerColoredBlockCrafter(player, world, x, y, z), player.inventory, new StringTextComponent("colored_crafter"));
        myContainer = (ContainerColoredBlockCrafter) getContainer();
        ySize = 239;
        xSize = 195;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack stack,
                                                   final int mouseX,
                                                   final int mouseY) {

        font.drawString(stack, FlatColoredBlocks.instance.itemColoredBlockCrafting.getDisplayName(null).getUnformattedComponentText(), 8, 6, 0x404040);
        font.drawString(stack, I18n.format("container.inventory"), 8, ySize - 93, 0x404040);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(
            MatrixStack stack,
            final float partialTicks,
            final int mouseX,
            final int mouseY) {

        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        minecraft.getTextureManager().bindTexture(CRAFTER_GUI_TEXTURE);
        final int xOffset = (width - xSize) / 2;
        final int yOffset = (height - ySize) / 2;
        GuiUtils.drawTexturedModalRect(xOffset, yOffset, 0, 0, xSize, ySize, 1f);

        final int scrollBarLeft = guiLeft + 175;
        final int scrollBarTop = guiTop + 18;
        final int scrollBarBottom = scrollBarTop + 112 + 14;

        final int scrollNobOffsetX = 232;
        final int scrollNobOffsetY = 0;

        minecraft.getTextureManager().bindTexture(CRAFTER_GUI_TEXTURE);

        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        final int rowsOfScrolling = Math.max((myContainer.getItemCount() + 8) / 9 - 7, 0);
        if (rowsOfScrolling <= 0) {
            myContainer.setScroll(currentScroll = 0);
        }

        GuiUtils.drawTexturedModalRect(scrollBarLeft, scrollBarTop + (int) ((scrollBarBottom - scrollBarTop - 17) * currentScroll), scrollNobOffsetX + (rowsOfScrolling > 0 ? 0 : 12),
                scrollNobOffsetY, 12, 15, 1f);
    }

    @Override
    public boolean mouseScrolled(
            double x,
            double y,
            double mouseWheelChange) {
        if (mouseWheelChange != 0) {

            final int rowsToScroll = myContainer.getItemCount() / 9 - 7;

            if (mouseWheelChange > 0) {
                mouseWheelChange = 1;
            }
            if (mouseWheelChange < 0) {
                mouseWheelChange = -1;
            }

            currentScroll = (float) (currentScroll - mouseWheelChange / (double) rowsToScroll);
            currentScroll = MathHelper.clamp(currentScroll, 0.0F, 1.0F);
            myContainer.setScroll(currentScroll);
            return true;
        }
        return false;
    }

    protected boolean func_195376_a(
            double x,
            double y) {
        final int scrollBarLeft = guiLeft + 175;
        final int scrollBarTop = guiTop + 18;
        final int scrollBarRight = scrollBarLeft + 14;
        final int scrollBarBottom = scrollBarTop + 112 + 14;

        return x >= (double) scrollBarLeft && y >= (double) scrollBarTop && x < (double) scrollBarRight && y < (double) scrollBarBottom;
    }

    public boolean mouseClicked(
            double x,
            double y,
            int button) {
        if (button == 0 && this.func_195376_a(x, y)) {
            this.isScrolling = true;
            return true;
        }

        return super.mouseClicked(x, y, button);
    }

    public boolean mouseReleased(
            double x,
            double y,
            int button) {
        if (button == 0) {
            this.isScrolling = false;
        }

        return super.mouseReleased(x, y, button);
    }

    public boolean mouseDragged(
            double x,
            double y,
            int button,
            double sx,
            double sy) {
        if (this.isScrolling) {
            final int scrollBarTop = guiTop + 18;
            final int scrollBarBottom = scrollBarTop + 112 + 14;
            currentScroll = (float) (y - scrollBarTop - 7.5F) / (scrollBarBottom - scrollBarTop - 15.0F);
            currentScroll = MathHelper.clamp(currentScroll, 0.0F, 1.0F);
            myContainer.setScroll(this.currentScroll);
            return true;
        } else {
            return super.mouseDragged(x, y, button, sx, sy);
        }
    }

    @Override
    public void render(
            MatrixStack stack,
            int mouseX,
            int mouseY,
            float partialTicks) {

        //  this.drawDefaultBackground();
        super.render(stack, mouseX, mouseY, partialTicks);
        renderHoveredTooltip(stack, mouseX, mouseY);
    }

}
