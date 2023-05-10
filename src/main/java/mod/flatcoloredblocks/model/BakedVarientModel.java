package mod.flatcoloredblocks.model;

import com.mojang.datafixers.util.Pair;
import mod.flatcoloredblocks.block.EnumFlatBlockType;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Function;

public class BakedVarientModel implements IUnbakedModel{

    private final int varient;
    public final EnumFlatBlockType type;

    public BakedVarientModel(
            final EnumFlatBlockType type,
            final int varient )
    {

        this.type = type;
        this.varient = varient;
    }

    @Override
    public Collection<ResourceLocation> getDependencies()
    {
        return Collections.emptyList();
    }

    @Override
    public Collection<RenderMaterial> getTextures(Function<ResourceLocation, IUnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
        return Collections.emptyList();
    }

    @Nullable
    @Override
    public IBakedModel bakeModel(ModelBakery modelBakeryIn, Function<RenderMaterial, TextureAtlasSprite> spriteGetterIn, IModelTransform transformIn, ResourceLocation locationIn) {
        return new BakedVarientBlock(type, varient, DefaultVertexFormats.BLOCK);
    }



}
