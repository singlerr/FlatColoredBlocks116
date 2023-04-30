package mod.flatcoloredblocks.craftingitem;

import mod.flatcoloredblocks.ModUtil;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

class ItemCraftingSource {
    private final IInventory inv;
    private final int slot;
    boolean simulate = false;
    private int used = 0;

    public ItemCraftingSource(
            final PlayerInventory ip,
            final int x) {
        inv = ip;
        slot = x;
    }

    @Override
    public int hashCode() {
        return inv.hashCode() ^ slot;
    }

    @Override
    public boolean equals(
            final Object obj) {
        final ItemCraftingSource s = (ItemCraftingSource) obj;
        return inv == s.inv && slot == s.slot;
    }

    public void consume(
            final int i) {
        if (simulate) {
            ++used;
        } else {
            inv.decrStackSize(slot, i);
        }
    }

    public ItemStack getStack() {
        if (simulate) {
            ItemStack is = inv.getStackInSlot(slot);
            if (is != null) {
                is = is.copy();
                ModUtil.alterStack(is, -used);
            }
            return is;
        }

        return inv.getStackInSlot(slot);
    }

}