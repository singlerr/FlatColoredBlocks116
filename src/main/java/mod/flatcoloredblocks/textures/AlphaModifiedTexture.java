package mod.flatcoloredblocks.textures;

import mod.flatcoloredblocks.FlatColoredBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.textures.ITextureAtlasSpriteLoader;

import java.awt.image.BufferedImage;
import java.util.function.Function;

public class AlphaModifiedTexture extends DynamicTexture {
    final NativeImage image;

    protected AlphaModifiedTexture(
            final String spriteName,
            final NativeImage image )
    {
        super( image );

        this.image = image;
    }

    public static DynamicTexture generate(
            final String name,
            final NativeImage bi,
            final float alphaMultiplier
    )
    {
        final NativeImage image = new NativeImage(NativeImage.PixelFormat.RGBA, bi.getWidth(), bi.getHeight() ,true);
        final int xx = bi.getWidth();
        final int yy = bi.getHeight();

        for ( int x = 0; x < xx; ++x )
        {
            for ( int y = 0; y < yy; ++y )
            {
                final int color = bi.getPixelRGBA( x, y );
                final int a = (int) ( ( color >> 24 & 0xff ) * alphaMultiplier );

                image.setPixelRGBA( x, y, color & 0xffffff | a << 24 );
            }
        }

        final AlphaModifiedTexture out = new AlphaModifiedTexture( name, image );
        return out;
    }


    /*
    @Override
    public boolean load(IResourceManager manager, ResourceLocation location, Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
        final BufferedImage[] images = new BufferedImage[Minecraft.getInstance().gameSettings.mipmapLevels + 1];
        images[0] = image;
        final int[][] pixels = new int[Minecraft.getInstance().gameSettings.mipmapLevels + 1][];
        pixels[0] = new int[image.getWidth() * image.getHeight()];
        image.getRGB( 0, 0, image.getWidth(), image.getHeight(), pixels[0], 0, image.getWidth() );
        framesTextureData.add( pixels );
        ITextureAtlasSpriteLoader
        return false;
    }


    public void register(
            final TextureMap map )
    {
        map.setTextureEntry( this );
    }

     */
}
