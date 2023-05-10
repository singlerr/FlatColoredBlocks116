package mod.flatcoloredblocks.textures;

import mod.flatcoloredblocks.FlatColoredBlocks;
import mod.flatcoloredblocks.Log;
import mod.flatcoloredblocks.block.EnumFlatBlockType;
import mod.flatcoloredblocks.client.ClientSide;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.resources.IResource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.DeferredRegister;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TextureGenerator {
    private final Map<Integer, DynamicTexture> generatedTransparentTexture = new HashMap<Integer, DynamicTexture>();
    private DynamicTexture glowingTexture;

    @SubscribeEvent
    void registerTransparentTextures(
            final TextureStitchEvent.Pre ev )
    {
        try
        {
            final ResourceLocation sourceLoc = ClientSide.instance.getTextureResourceLocation( EnumFlatBlockType.TRANSPARENT );
            final IResource iresource = Minecraft.getInstance().getResourceManager().getResource( sourceLoc );
            final NativeImage bi = NativeImage.read( iresource.getInputStream() );
            for ( final int varient : FlatColoredBlocks.instance.transparent.shadeConvertVariant )
            {
                final String name = ClientSide.instance.getTextureStringName( EnumFlatBlockType.TRANSPARENT, varient );
                final DynamicTexture out = AlphaModifiedTexture.generate( name, bi, varient / 255.0f );
                // register.
                generatedTransparentTexture.put( varient, out );
            }
        }
        catch ( final IOException e )
        {
            // just load the texture if for some reason the above fails.
            Log.logError( "Unable to load Base Texture", e );

            ev.addSprite( new ResourceLocation( ClientSide.instance.getBaseTextureNameWithBlocks( EnumFlatBlockType.TRANSPARENT ) ) );

            /*
            for ( final int varient : FlatColoredBlocks.instance.transparent.shadeConvertVariant )
            {
                generatedTransparentTexture.put( varient, out );
            }

             */
        }

        if ( !FlatColoredBlocks.instance.config.GLOWING_EMITS_LIGHT )
        {
            ev.addSprite( new ResourceLocation( ClientSide.instance.getBaseTextureNameWithBlocks( EnumFlatBlockType.GLOWING ) ) );
        }
    }

    private TextureAtlasSprite orMissing(
            TextureAtlasSprite texture )
    {
        TextureAtlasSprite.
        return texture != null ? texture : Minecraft.getInstance().getTextureManager().;
    }

    public TextureAtlasSprite getGlowingTexture(
            final int varient )
    {
        return orMissing( glowingTexture );
    }

    public TextureAtlasSprite getTransparentTexture(
            final int varient )
    {
        return orMissing( generatedTransparentTexture.get( varient ) );
    }
}
