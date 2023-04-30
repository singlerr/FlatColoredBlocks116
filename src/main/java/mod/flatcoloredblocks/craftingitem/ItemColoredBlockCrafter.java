package mod.flatcoloredblocks.craftingitem;

import com.google.common.base.Stopwatch;
import mod.flatcoloredblocks.FlatColoredBlocks;
import mod.flatcoloredblocks.gui.ModGuiTypes;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ItemColoredBlockCrafter extends Item {

    public int scrollIndex = -1;
    List<Item> options = new ArrayList<Item>();
    Stopwatch stopWatch;

    public ItemColoredBlockCrafter() {
        super((new Item.Properties()).group(FlatColoredBlocks.instance.creativeTab));
        setRegistryName(FlatColoredBlocks.MODID, "coloredcraftingitem");
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(
            final World worldIn,
            final PlayerEntity playerIn,
            final Hand hand) {

        final ItemStack itemStackIn = playerIn.getHeldItem(hand);

        if (worldIn.isRemote) {

            return ActionResult.resultSuccess(
                    itemStackIn);
        }

        if (playerIn instanceof ServerPlayerEntity) {
            NetworkHooks.openGui((ServerPlayerEntity) playerIn, ModGuiTypes.colored_crafter.create(playerIn, worldIn, 0, 0, 0), (BlockPos) null);
        }

        return ActionResult.resultSuccess(itemStackIn);
    }

    @Override
    public void addInformation(
            ItemStack stack,
            World worldIn,
            List<ITextComponent> tooltip,
            ITooltipFlag flagIn) {
        if (scrollIndex == -1 && worldIn != null) {
            scrollIndex = 0;
            stopWatch = Stopwatch.createStarted();

            options.clear();
            options.addAll(ItemTags.createOptional(new ResourceLocation(FlatColoredBlocks.instance.config.solidCraftingBlock)).getAllElements());
            options.addAll(ItemTags.createOptional(new ResourceLocation(FlatColoredBlocks.instance.config.glowingCraftingBlock)).getAllElements());
            options.addAll(ItemTags.createOptional(new ResourceLocation(FlatColoredBlocks.instance.config.transparentCraftingBlock)).getAllElements());
        }

        if (!options.isEmpty() && scrollIndex >= 0) {
            if (stopWatch.elapsed(TimeUnit.SECONDS) >= 1.2) {
                scrollIndex = ++scrollIndex % options.size();
                stopWatch = Stopwatch.createStarted();
            }

            Item it = options.get(scrollIndex);
            tooltip.add(new TranslationTextComponent("item.flatcoloredblocks.coloredcraftingitem.tip1", it.getDisplayName(it.getDefaultInstance())));
        }

        tooltip.add(new TranslationTextComponent("item.flatcoloredblocks.coloredcraftingitem.tip2"));

        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

}
