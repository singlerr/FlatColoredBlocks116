package mod.flatcoloredblocks.block;

import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.DyeColor;

import java.util.Set;

public enum EnumFlatColorAttributes {
    // non-colors
    black(true, true, DyeColor.BLACK, MaterialColor.BLACK),
    grey(true, true, DyeColor.GRAY, MaterialColor.GRAY),
    silver(true, true, DyeColor.LIGHT_GRAY, MaterialColor.LIGHT_GRAY),
    white(true, true, DyeColor.WHITE, MaterialColor.SNOW),

    // colors...
    red(true, false, DyeColor.RED, MaterialColor.RED),
    orange(true, false, DyeColor.ORANGE, MaterialColor.ADOBE),
    yellow(true, false, DyeColor.YELLOW, MaterialColor.YELLOW),
    lime(true, false, DyeColor.LIME, MaterialColor.LIME),
    green(true, false, DyeColor.GREEN, MaterialColor.GREEN),
    emerald(true, false, DyeColor.GREEN, DyeColor.CYAN, MaterialColor.GREEN),
    cyan(true, false, DyeColor.CYAN, MaterialColor.CYAN),
    azure(true, false, DyeColor.BLUE, DyeColor.CYAN, MaterialColor.LIGHT_BLUE),
    blue(true, false, DyeColor.BLUE, MaterialColor.BLUE),
    violet(true, false, DyeColor.PURPLE, MaterialColor.PURPLE),
    magenta(true, false, DyeColor.MAGENTA, MaterialColor.MAGENTA),
    pink(true, false, DyeColor.PINK, MaterialColor.PINK),

    // color modifiers
    dark(false, false, DyeColor.BLACK, MaterialColor.BLACK),
    light(false, false, DyeColor.WHITE, MaterialColor.SNOW);

    // description of characteristic
    public final boolean isModifier;
    public final boolean isSaturated;

    // dye information
    public final DyeColor primaryDye;
    public final DyeColor secondaryDye;

    // map color
    public final MaterialColor mapColor;

    EnumFlatColorAttributes(
            final boolean isColor,
            final boolean isSaturated,
            final DyeColor dye1,
            final DyeColor dye2,
            final MaterialColor mapColor) {
        isModifier = !isColor;
        this.isSaturated = isSaturated;
        primaryDye = dye1;
        secondaryDye = dye1;
        this.mapColor = mapColor;
    }

    EnumFlatColorAttributes(
            final boolean isColor,
            final boolean isSaturated,
            final DyeColor dye,
            final MaterialColor mapColor) {
        isModifier = !isColor;
        this.isSaturated = isSaturated;
        primaryDye = dye;
        secondaryDye = dye;
        this.mapColor = mapColor;
    }

    public static DyeColor getAlternateDye(
            final Set<EnumFlatColorAttributes> characteristics) {
        if (characteristics.contains(EnumFlatColorAttributes.orange) && characteristics.contains(EnumFlatColorAttributes.dark)) {
            return DyeColor.BROWN;
        }

        if (characteristics.contains(EnumFlatColorAttributes.blue) && characteristics.contains(EnumFlatColorAttributes.light)) {
            return DyeColor.LIGHT_BLUE;
        }

        return null;
    }

}
