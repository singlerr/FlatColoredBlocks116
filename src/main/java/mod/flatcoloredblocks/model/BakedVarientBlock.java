package mod.flatcoloredblocks.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import mod.flatcoloredblocks.FlatColoredBlocks;
import mod.flatcoloredblocks.block.EnumFlatBlockType;
import mod.flatcoloredblocks.client.ClientSide;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.TransformationMatrix;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.pipeline.BakedQuadBuilder;
import net.minecraftforge.client.model.pipeline.LightUtil;
import net.minecraftforge.common.model.TransformationHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class BakedVarientBlock implements IBakedModel {
    private static final Matrix4f ground;
    private static final Matrix4f gui;
    private static final Matrix4f fixed;
    private static final Matrix4f firstPerson_righthand;
    private static final Matrix4f firstPerson_lefthand;
    private static final Matrix4f thirdPerson_righthand;
    private static final Matrix4f thirdPerson_lefthand;

    @SuppressWarnings( "unchecked" )
    final List<BakedQuad>[] face = new List[6];

    final TextureAtlasSprite texture;

    public BakedVarientBlock(
            final EnumFlatBlockType type,
            final int varient,
            final VertexFormat format )
    {
        // create lists...
        face[0] = new ArrayList<BakedQuad>();
        face[1] = new ArrayList<BakedQuad>();
        face[2] = new ArrayList<BakedQuad>();
        face[3] = new ArrayList<BakedQuad>();
        face[4] = new ArrayList<BakedQuad>();
        face[5] = new ArrayList<BakedQuad>();

        final float[] afloat = new float[] { 0, 0, 16, 16 };
        final BlockFaceUV uv = new BlockFaceUV( afloat, 0 );
        final FaceBakery faceBakery = new FaceBakery();

        if ( type == EnumFlatBlockType.TRANSPARENT )
        {
            texture = ClientSide.instance.textureGenerator.getTransparentTexture( varient );
        }
        else
        {
            texture = ClientSide.instance.textureGenerator.getGlowingTexture( varient );
        }

        final Vector3f to = new Vector3f( 0.0f, 0.0f, 0.0f );
        final Vector3f from = new Vector3f( 16.0f, 16.0f, 16.0f );

        final BlockPartRotation bpr = null;
        final ModelRotation mr = ModelRotation.X0_Y0;

        final float maxLightmap = 32.0f / 0xffff;
        int lightValue = 0;

        if ( !FlatColoredBlocks.instance.config.GLOWING_EMITS_LIGHT && type == EnumFlatBlockType.GLOWING )
        {
            lightValue = varient * 15 / 255;
        }

        final float lightMap = maxLightmap * Math.max( 0, Math.min( 15, lightValue ) );

        for ( final Direction side : Direction.values() )
        {
            final BlockPartFace bpf = new BlockPartFace( side, 1, "", uv );

            Vector3f toB, fromB;

            switch ( side )
            {
                case UP:
                    toB = new Vector3f( to.getX(), from.getY(), to.getZ() );
                    fromB = new Vector3f( from.getX(), from.getY(), from.getZ() );
                    break;
                case EAST:
                    toB = new Vector3f( from.getX(), to.getY(), to.getZ() );
                    fromB = new Vector3f( from.getX(), from.getY(), from.getZ() );
                    break;
                case NORTH:
                    toB = new Vector3f( to.getX(), to.getY(), to.getZ() );
                    fromB = new Vector3f( from.getX(), from.getY(), to.getZ() );
                    break;
                case SOUTH:
                    toB = new Vector3f( to.getX(), to.getY(), from.getZ() );
                    fromB = new Vector3f( from.getX(), from.getY(), from.getZ() );
                    break;
                case DOWN:
                    toB = new Vector3f( to.getX(), to.getY(), to.getZ() );
                    fromB = new Vector3f( from.getX(), to.getY(), from.getZ() );
                    break;
                case WEST:
                    toB = new Vector3f( to.getX(), to.getY(), to.getZ() );
                    fromB = new Vector3f( to.getX(), from.getY(), from.getZ() );
                    break;
                default:
                    throw new NullPointerException();
            }

            final BakedQuad g = faceBakery.bakeQuad( toB, fromB, bpf, texture, side, mr, bpr, false, null);
            face[side.ordinal()].add( finishFace( g, side, format, lightMap ) );
        }
    }

    private BakedQuad finishFace(
            final BakedQuad g,
            final Direction myFace,
            final VertexFormat format,
            final float lightMap )
    {
        final int[] vertData = g.getVertexData();
        final int wrapAt = vertData.length / 4;

        final BakedQuadBuilder b = new BakedQuadBuilder( );
        b.setQuadOrientation( myFace );
        b.setQuadTint( 1 );
        b.setTexture( g.getSprite() );

        for ( int vertNum = 0; vertNum < 4; vertNum++ )
        {
            for ( int elementIndex = 0; elementIndex < format.getElements().size(); elementIndex++ )
            {
                final VertexFormatElement element = format.getElements().get(elementIndex);
                switch ( element.getUsage() )
                {
                    case POSITION:
                        b.put( elementIndex, Float.intBitsToFloat( vertData[0 + wrapAt * vertNum] ), Float.intBitsToFloat( vertData[1 + wrapAt * vertNum] ), Float.intBitsToFloat( vertData[2 + wrapAt * vertNum] ) );
                        break;

                    case COLOR:
                        final float light = LightUtil.diffuseLight( myFace );
                        b.put( elementIndex, light, light, light, 1f );
                        break;

                    case NORMAL:
                        b.put( elementIndex, myFace.getXOffset(), myFace.getYOffset(), myFace.getZOffset() );
                        break;

                    case UV:

                        if ( element.getIndex() == 1 )
                        {
                            b.put( elementIndex, lightMap, lightMap );
                        }
                        else
                        {
                            final float u = Float.intBitsToFloat( vertData[4 + wrapAt * vertNum] );
                            final float v = Float.intBitsToFloat( vertData[5 + wrapAt * vertNum] );
                            b.put( elementIndex, u, v );
                        }

                        break;

                    default:
                        b.put( elementIndex );
                        break;
                }
            }
        }

        return b.build();
    }

    public List<BakedQuad>[] getFace()
    {
        return face;
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
        return getQuads(state, side, rand);
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand) {
        if ( side == null )
        {
            return Collections.emptyList();
        }

        return face[side.ordinal()];
    }


    @Override
    public boolean isAmbientOcclusion()
    {
        return true;
    }

    @Override
    public boolean isGui3d()
    {
        return true;
    }

    @Override
    public boolean isSideLit() {
        return true;
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms()
    {
        return ItemCameraTransforms.DEFAULT;
    }

    @Override
    public TextureAtlasSprite getParticleTexture()
    {
        return texture;
    }

    static
    {
        // for some reason these are not identical to vanilla's Block.json, I
        // don't know why.. but its close.

        {
            final Vector3f translation = new Vector3f( 0, 0, 0 );
            final Vector3f scale = new Vector3f( 0.625f, 0.625f, 0.625f );
            final Quaternion rotation = TransformationHelper.quatFromXYZ( new Vector3f( 30, 225, 0 ) , true );
            TransformationMatrix transform = new TransformationMatrix(translation, rotation, scale,null);
            gui = transform.getMatrix();
        }

        {
            final Vector3f translation = new Vector3f( 0, 0, 0 );
            final Vector3f scale = new Vector3f( 0.25f, 0.25f, 0.25f );
            final Quaternion rotation = TransformationHelper.quatFromXYZ( new Vector3f( 0, 0, 0 ) , true);

            final TransformationMatrix transform = new TransformationMatrix( translation, rotation, scale, null );
            ground = transform.getMatrix();
        }

        {
            final Vector3f translation = new Vector3f( 0, 0, 0 );
            final Vector3f scale = new Vector3f( 0.5f, 0.5f, 0.5f );
            final Quaternion rotation = TransformationHelper.quatFromXYZ( new Vector3f( 0, 0, 0 ), true );

            final TransformationMatrix transform = new TransformationMatrix( translation, rotation, scale, null );
            fixed = transform.getMatrix();
        }

        {
            final Vector3f translation = new Vector3f( 0, 0, 0 );
            final Vector3f scale = new Vector3f( 0.375f, 0.375f, 0.375f );
            final Quaternion rotation = TransformationHelper.quatFromXYZ( new Vector3f( 75, 45, 0 ), true );

            final TransformationMatrix transform = new TransformationMatrix( translation, rotation, scale, null );
            thirdPerson_lefthand = thirdPerson_righthand = transform.getMatrix();
        }

        {
            final Vector3f translation = new Vector3f( 0, 0, 0 );
            final Vector3f scale = new Vector3f( 0.40f, 0.40f, 0.40f );
            final Quaternion rotation = TransformationHelper.quatFromXYZ( new Vector3f( 0, 45, 0 ) , true);

            final TransformationMatrix transform = new TransformationMatrix( translation, rotation, scale, null );
            firstPerson_righthand = transform.getMatrix();
        }

        {
            final Vector3f translation = new Vector3f( 0, 0, 0 );
            final Vector3f scale = new Vector3f( 0.40f, 0.40f, 0.40f );
            final Quaternion rotation = TransformationHelper.quatFromXYZ( new Vector3f( 0, 225, 0 ), true );

            final TransformationMatrix transform = new TransformationMatrix( translation, rotation, scale, null );
            firstPerson_lefthand = transform.getMatrix();
        }
    }

    @Override
    public IBakedModel handlePerspective(ItemCameraTransforms.TransformType cameraTransformType, MatrixStack mat) {

        return this;
    }

    @Override
    public boolean isBuiltInRenderer()
    {
        return false;
    }

    @Override
    public ItemOverrideList getOverrides()
    {
        return ItemOverrideList.EMPTY;
    }
}
