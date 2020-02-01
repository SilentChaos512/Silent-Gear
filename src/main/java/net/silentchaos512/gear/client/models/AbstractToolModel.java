package net.silentchaos512.gear.client.models;

@SuppressWarnings("deprecation")
public abstract class AbstractToolModel /*extends LayeredBakedModel*/ {

    /*protected final ImmutableMap<TransformType, TRSRTransformation> transforms;

    public AbstractToolModel(IModel parent, ImmutableList<ImmutableList<BakedQuad>> immutableList, VertexFormat format,
                             ImmutableMap<TransformType, TRSRTransformation> transforms,
                             Map<String, IBakedModel> cache) {

        super(parent, immutableList, format);

        this.transforms = itemTransforms();
    }

    @Nonnull
    @Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(@Nonnull TransformType cameraTransformType) {
        final boolean debug = false;
        return PerspectiveMapWrapper.handlePerspective(this, debug ? itemTransforms() : this.transforms, cameraTransformType);
    }

    protected ImmutableMap<TransformType, TRSRTransformation> itemTransforms() {
        // TODO: Crossbow angles: -90, 0, 45 (at least for first-person right hand)
        ImmutableMap.Builder<TransformType, TRSRTransformation> builder = ImmutableMap.builder();
        builder.put(TransformType.GROUND, get(0, 2, 0, 0, 0, 0, 0.5f));
        builder.put(TransformType.HEAD, get(0, 13, 7, 0, 180, 0, 1));
        builder.put(TransformType.FIXED, get(0, 0, 0, 0, 180, 0, 1));
        builder.put(TransformType.THIRD_PERSON_RIGHT_HAND, get(0, 4, 0.5f, 0, -90, 55, 0.85f));
        builder.put(TransformType.THIRD_PERSON_LEFT_HAND, get(0, 4, 0.5f, 0, 90, -55, 0.85f));
        builder.put(TransformType.FIRST_PERSON_RIGHT_HAND, get(1.13f, 3.2f, 1.13f, 0, -90, 25, 0.68f));
        builder.put(TransformType.FIRST_PERSON_LEFT_HAND, get(1.13f, 3.2f, 1.13f, 0, 90, -25, 0.68f));
        return builder.build();
    }

    protected TRSRTransformation get(float tx, float ty, float tz, float ax, float ay, float az, float s) {
        return TRSRTransformation.blockCenterToCorner(new TRSRTransformation(
                new Vector3f(tx / 16, ty / 16, tz / 16),
                TRSRTransformation.quatFromXYZDegrees(new Vector3f(ax, ay, az)), new Vector3f(s, s, s), null));
    }

    @Override
    public boolean isAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isGui3d() {
        return false;
    }

    @Nonnull
    @Override
    public TextureAtlasSprite getParticleTexture() {
        return null;
    }

    @Nonnull
    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return ItemCameraTransforms.DEFAULT;
    }*/
}
