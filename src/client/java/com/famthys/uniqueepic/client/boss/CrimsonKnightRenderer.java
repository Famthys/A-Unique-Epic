package com.famthys.uniqueepic.client.boss;

import com.famthys.uniqueepic.AUniqueEpic;
import com.famthys.uniqueepic.boss.CrimsonKnight;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;

/**
 * 猩红骑的实体渲染器。
 *
 * <p>几何坐标整体放大了 2 倍（见 {@link CrimsonKnightModel}），所以这里用
 * {@link #scale} 缩回原尺寸——脚底仍落在 Java y=48，配合原版的 -1.501 位移正好贴地。</p>
 */
public class CrimsonKnightRenderer
        extends MobRenderer<CrimsonKnight, CrimsonKnightRenderState, CrimsonKnightModel> {

    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(AUniqueEpic.id("crimson_knight"), "main");

    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(
            "aunique-epic", "textures/entity/crimson_knight_model.png");

    public CrimsonKnightRenderer(EntityRendererProvider.Context context) {
        super(context, new CrimsonKnightModel(context.bakeLayer(LAYER)), 0.9F);
    }

    @Override
    public CrimsonKnightRenderState createRenderState() {
        return new CrimsonKnightRenderState();
    }

    @Override
    public void extractRenderState(CrimsonKnight entity, CrimsonKnightRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);

        // 交付说明第 4 步：动画时间 = (客户端游戏时间 - 开始时间 + partialTick) / 20
        state.crimsonPhase = entity.clientPhase();
        long start = entity.clientPhaseStart();
        long now = entity.level().getGameTime();
        state.crimsonAnimSeconds = Math.max(0.0F, (now - start + partialTick) / 20.0F);
    }

    @Override
    public Identifier getTextureLocation(CrimsonKnightRenderState state) {
        return TEXTURE;
    }

    @Override
    protected void scale(CrimsonKnightRenderState state, PoseStack poseStack) {
        float s = CrimsonKnightModel.MODEL_SCALE;
        poseStack.scale(s, s, s);
    }
}
