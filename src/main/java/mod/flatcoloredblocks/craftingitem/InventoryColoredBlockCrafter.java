package mod.flatcoloredblocks.craftingitem;

import mod.flatcoloredblocks.FlatColoredBlocks;
import mod.flatcoloredblocks.ModUtil;
import mod.flatcoloredblocks.block.BlockFlatColored;
import mod.flatcoloredblocks.block.EnumFlatBlockType;
import mod.flatcoloredblocks.block.EnumFlatColorAttributes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;

import java.util.*;
import java.util.Map.Entry;

/**
 * Generates and Crafts items that are seen in the crafting item's gui.
 */
public class InventoryColoredBlockCrafter implements IInventory {

    private final PlayerEntity thePlayer;
    private final ContainerColoredBlockCrafter craftingContainer;

    private final ArrayList<ItemStack> options = new ArrayList<ItemStack>();
    public int offset = 0;

    public InventoryColoredBlockCrafter(
            final PlayerEntity thePlayer,
            final ContainerColoredBlockCrafter coloredCrafterContainer) {
        this.thePlayer = thePlayer;
        craftingContainer = coloredCrafterContainer;
    }

    public static HashMap<Object, Collection<Item>> getDyeList() {
        final HashMap<Object, Collection<Item>> dyeList = new HashMap<Object, Collection<Item>>();

        tagIntoList(dyeList, DyeColor.BLACK, Tags.Items.DYES_BLACK);
        tagIntoList(dyeList, DyeColor.RED, Tags.Items.DYES_RED);
        tagIntoList(dyeList, DyeColor.GREEN, Tags.Items.DYES_GREEN);
        tagIntoList(dyeList, DyeColor.BROWN, Tags.Items.DYES_BROWN);
        tagIntoList(dyeList, DyeColor.BLUE, Tags.Items.DYES_BLUE);
        tagIntoList(dyeList, DyeColor.PURPLE, Tags.Items.DYES_PURPLE);
        tagIntoList(dyeList, DyeColor.CYAN, Tags.Items.DYES_CYAN);
        tagIntoList(dyeList, DyeColor.LIGHT_GRAY, Tags.Items.DYES_LIGHT_GRAY);
        tagIntoList(dyeList, DyeColor.GRAY, Tags.Items.DYES_GRAY);
        tagIntoList(dyeList, DyeColor.PINK, Tags.Items.DYES_PINK);
        tagIntoList(dyeList, DyeColor.LIME, Tags.Items.DYES_LIME);
        tagIntoList(dyeList, DyeColor.YELLOW, Tags.Items.DYES_YELLOW);
        tagIntoList(dyeList, DyeColor.LIGHT_BLUE, Tags.Items.DYES_LIGHT_BLUE);
        tagIntoList(dyeList, DyeColor.MAGENTA, Tags.Items.DYES_MAGENTA);
        tagIntoList(dyeList, DyeColor.ORANGE, Tags.Items.DYES_ORANGE);
        tagIntoList(dyeList, DyeColor.WHITE, Tags.Items.DYES_WHITE);
        tagIntoList(dyeList, EnumFlatBlockType.NORMAL, getItems(FlatColoredBlocks.instance.config.solidCraftingBlock));
        tagIntoList(dyeList, EnumFlatBlockType.GLOWING, getItems(FlatColoredBlocks.instance.config.glowingCraftingBlock));
        tagIntoList(dyeList, EnumFlatBlockType.TRANSPARENT, getItems(FlatColoredBlocks.instance.config.transparentCraftingBlock));

        return dyeList;
    }

    private static void tagIntoList(
            HashMap<Object, Collection<Item>> dyeList,
            Enum<?> e,
            Tag<Item> itemList) {
        dyeList.put(e, itemList.getAllElements());
    }
    private static void tagIntoList(
            HashMap<Object, Collection<Item>> dyeList,
            Enum<?> e,
            Tags.IOptionalNamedTag<Item> itemList) {
        dyeList.put(e, itemList.getAllElements());

    }
    private static Tag<Item> getItems(
            final String name) {

        return (Tag<Item>) ItemTags.createOptional(new ResourceLocation(name));
    }

    private InventorySummary scanPlayerInventory() {
        final EnumSet<DyeColor> dyes = EnumSet.noneOf(DyeColor.class);
        final PlayerInventory ip = thePlayer.inventory;

        final HashMap<Object, Collection<Item>> dyeList = getDyeList();
        final HashMap<Object, HashSet<ItemCraftingSource>> stacks = new HashMap<Object, HashSet<ItemCraftingSource>>();

        boolean hasCobblestone = false;
        boolean hasGlowstone = false;
        boolean hasGlass = false;

        for (final Entry<Object, Collection<Item>> items : dyeList.entrySet()) {
            stacks.put(items.getKey(), new HashSet<ItemCraftingSource>());
        }

        stacks.put(null, new HashSet<ItemCraftingSource>());

        for (int x = 0; x < ip.getSizeInventory(); ++x) {
            final ItemStack is = ip.getStackInSlot(x);

            if (is != null) {
                for (final Entry<Object, Collection<Item>> items : dyeList.entrySet()) {
                    for (final Item ore : items.getValue()) {
                        if (is.getItem() == ore) {

                            if (items.getKey() instanceof DyeColor) {
                                dyes.add((DyeColor) items.getKey());
                            } else {
                                if (items.getKey() == EnumFlatBlockType.NORMAL) {
                                    hasCobblestone = true;
                                }

                                if (items.getKey() == EnumFlatBlockType.TRANSPARENT) {
                                    hasGlass = true;
                                }

                                if (items.getKey() == EnumFlatBlockType.GLOWING) {
                                    hasGlowstone = true;
                                }
                            }

                            stacks.get(items.getKey()).add(new ItemCraftingSource(ip, x));
                        }
                    }
                }
            }
        }

        return new InventorySummary(hasCobblestone, hasGlowstone, hasGlass, stacks, dyes);
    }

    /**
     * recalculate the entire container.
     */
    public void updateContents() {
        options.clear();
        BlockFlatColored.getAllShades(options);

        final InventorySummary da = scanPlayerInventory();
        final EnumSet<DyeColor> dyes = da.dyes;

        final Iterator<ItemStack> i = options.iterator();
        while (i.hasNext()) {
            final ItemStack is = i.next();
            final Block blk = Block.getBlockFromItem(is.getItem());
            final BlockState state = ModUtil.getFlatColoredBlockState((BlockFlatColored) blk, is);

            final Set<EnumFlatColorAttributes> charistics = ((BlockFlatColored) blk).getFlatColorAttributes(state);
            boolean isGood = true;

            for (final EnumFlatColorAttributes cc : charistics) {
                if (!dyes.contains(cc.primaryDye) || !dyes.contains(cc.secondaryDye)) {
                    isGood = false;
                    break;
                }
            }

            final DyeColor alternateDye = EnumFlatColorAttributes.getAlternateDye(charistics);
            if (alternateDye != null && dyes.contains(alternateDye)) {
                isGood = true;
            }

            if (!isGood || !da.has(((BlockFlatColored) blk).getCraftable())) {
                i.remove();
            }
        }

        craftingContainer.setScroll(craftingContainer.scrollPercent);
    }

    /*
    @Override
    public boolean hasCustomName()
    {
        return false;
    }

    @Override
    public ITextComponent getDisplayName()
    {
        return null;
    }
*/
    @Override
    public int getSizeInventory() {
        return options.size();
    }

    @Override
    public ItemStack getStackInSlot(
            int index) {

        index += offset;
        if (index < options.size()) {
            return options.get(index).copy();
        }

        return ModUtil.getEmptyStack();
    }

    @Override
    public ItemStack decrStackSize(
            int index,
            final int count) {

        index += offset;
        if (index < options.size()) {
            ItemStack out = options.get(index);

            if (ModUtil.getStackSize(out) <= 0) {
                out = null;
            }

            return out;
        }

        return ModUtil.getEmptyStack();
    }

    public ItemStack craftItem(
            final ItemStack reqItem,
            final int count,
            final boolean simulate) {
        if (ModUtil.isEmpty(reqItem)) {
            return ModUtil.getEmptyStack();
        }

        int outAmount = 0;

        final InventorySummary da = scanPlayerInventory();
        final Block blk = Block.getBlockFromItem(reqItem.getItem());
        final BlockState state = ModUtil.getFlatColoredBlockState((BlockFlatColored) blk, reqItem);

        final Set<EnumFlatColorAttributes> charistics = ((BlockFlatColored) blk).getFlatColorAttributes(state);
        final Object Craftable = ((BlockFlatColored) blk).getCraftable();
        final HashSet<DyeColor> requiredDyes = new HashSet<DyeColor>();

        final int craftAmount = Craftable instanceof EnumFlatBlockType ? ((EnumFlatBlockType) Craftable).getOutputCount() : 1;

        final DyeColor alternateDye = EnumFlatColorAttributes.getAlternateDye(charistics);
        final HashSet<DyeColor> alternateSet = new HashSet<DyeColor>();

        if (alternateDye != null) {
            alternateSet.add(alternateDye);
        }

        for (final EnumFlatColorAttributes cc : charistics) {
            requiredDyes.add(cc.primaryDye);
            requiredDyes.add(cc.secondaryDye);
        }

        for (int x = 0; x < count && outAmount + craftAmount <= 64; ++x) {
            boolean isGood = true;

            final ItemCraftingSource isx = findItem(da.stacks.get(Craftable), simulate);
            if (isx == null) {
                isGood = false;
            }

            HashSet<DyeColor> usedSet = alternateSet;
            availableDyeTest:
            {

                // test to see if there is an alternate, and if there is, see if
                // the player has one...
                if (alternateDye != null) {
                    final ItemCraftingSource is = findItem(da.stacks.get(alternateDye), simulate);
                    if (is != null) {
                        break availableDyeTest;
                    }
                }

                // no alternate, try standard set.
                usedSet = requiredDyes;
                for (final DyeColor dye : requiredDyes) {
                    final ItemCraftingSource is = findItem(da.stacks.get(dye), simulate);
                    if (is == null) {
                        isGood = false;
                    }
                }
            }

            if (isGood && isx != null) {
                for (final DyeColor dye : usedSet) {
                    final ItemCraftingSource is = findItem(da.stacks.get(dye), simulate);

                    is.consume(1);
                }

                isx.consume(1);
                outAmount += craftAmount;
            } else {
                break;
            }
        }

        updateContents();

        if (outAmount <= 0) {
            return ModUtil.getEmptyStack();
        }

        final ItemStack out = reqItem.copy();
        ModUtil.setStackSize(out, outAmount);

        return out;
    }

    private ItemCraftingSource findItem(
            final HashSet<ItemCraftingSource> hashSet,
            final boolean simulate) {
        for (final ItemCraftingSource src : hashSet) {
            src.simulate = simulate;
            final ItemStack is = src.getStack();
            if (is != null && ModUtil.getStackSize(is) > 0) {
                return src;
            }
        }

        return null;
    }

    @Override
    public ItemStack removeStackFromSlot(
            final int index) {
        return ModUtil.getEmptyStack();
    }

    @Override
    public void setInventorySlotContents(
            final int index,
            final ItemStack stack) {

    }

    @Override
    public int getInventoryStackLimit() {
        return 0;
    }

    @Override
    public void markDirty() {

    }

    @Override
    public boolean isUsableByPlayer(
            final PlayerEntity player) {
        return true;
    }

    @Override
    public void openInventory(
            final PlayerEntity player) {

    }

    @Override
    public void closeInventory(
            final PlayerEntity player) {

    }

    @Override
    public boolean isItemValidForSlot(
            final int index,
            final ItemStack stack) {
        return false;
    }

    /*
        @Override
        public int getField(
                final int id )
        {
            return 0;
        }
        @Override
        public void setField(
                final int id,
                final int value )
        {

        }

        @Override
        public int getFieldCount()
        {
            return 0;
        }
    */
    @Override
    public void clear() {
        options.clear();
    }

    @Override
    public boolean isEmpty() // whatever this is...
    {
        for (final ItemStack itemstack : options) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }

        return true;
    }
    /*

    @Override
    public ITextComponent getCustomName()
    {
        return null;
    }

    @Override
    public ITextComponent getName()
    {
        return null;
    }

     */

}
