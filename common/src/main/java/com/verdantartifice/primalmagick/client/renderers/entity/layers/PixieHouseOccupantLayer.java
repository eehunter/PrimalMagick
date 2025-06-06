package com.verdantartifice.primalmagick.client.renderers.entity.layers;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.verdantartifice.primalmagick.client.fx.FxDispatcher;
import com.verdantartifice.primalmagick.client.renderers.entity.BasicPixieRenderer;
import com.verdantartifice.primalmagick.client.renderers.entity.GrandPixieRenderer;
import com.verdantartifice.primalmagick.client.renderers.entity.MajesticPixieRenderer;
import com.verdantartifice.primalmagick.client.renderers.entity.model.PixieHouseModel;
import com.verdantartifice.primalmagick.client.renderers.entity.model.PixieModel;
import com.verdantartifice.primalmagick.client.renderers.models.ModelLayersPM;
import com.verdantartifice.primalmagick.common.entities.pixies.PixieRank;
import com.verdantartifice.primalmagick.common.entities.misc.PixieHouseEntity;
import com.verdantartifice.primalmagick.common.items.misc.DrainedPixieItem;
import com.verdantartifice.primalmagick.common.items.misc.IPixieItem;
import com.verdantartifice.primalmagick.common.sources.Source;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;

import java.util.Map;

public class PixieHouseOccupantLayer extends RenderLayer<PixieHouseEntity, PixieHouseModel> {
    private static final Map<PixieRank, ResourceLocation> TEXTURES = ImmutableMap.of(
            PixieRank.BASIC, BasicPixieRenderer.TEXTURE,
            PixieRank.GRAND, GrandPixieRenderer.TEXTURE,
            PixieRank.MAJESTIC, MajesticPixieRenderer.TEXTURE);

    private final PixieModel basePixieModel;
    private final PixieModel royalPixieModel;
    private final PixieModel baseDrainedPixieModel;
    private final PixieModel royalDrainedPixieModel;

    public PixieHouseOccupantLayer(RenderLayerParent<PixieHouseEntity, PixieHouseModel> pRenderer, EntityModelSet pModelSet) {
        super(pRenderer);
        this.basePixieModel = new PixieModel(pModelSet.bakeLayer(ModelLayersPM.PIXIE_BASIC));
        this.royalPixieModel = new PixieModel(pModelSet.bakeLayer(ModelLayersPM.PIXIE_ROYAL));
        this.baseDrainedPixieModel = new PixieModel(pModelSet.bakeLayer(ModelLayersPM.PIXIE_BASIC));
        this.royalDrainedPixieModel = new PixieModel(pModelSet.bakeLayer(ModelLayersPM.PIXIE_ROYAL));
    }

    @Override
    public void render(PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, PixieHouseEntity pLivingEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTicks, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        ItemStack pixieStack = pLivingEntity.getHousedPixie();
        if (pixieStack.getItem() instanceof IPixieItem pixieItem && pLivingEntity.getDeployedPixieUUID().isEmpty()) {
            // Render pixie house occupant if present and not deployed
            PixieRank rank = pixieItem.getPixieRank();
            PixieModel model = rank == PixieRank.MAJESTIC ? this.royalPixieModel : this.basePixieModel;
            double yBob = -0.125D * Mth.sin(pAgeInTicks / 6F);
            pPoseStack.pushPose();
            pPoseStack.translate(0D, -0.25D + yBob, 0D);
            pPoseStack.scale(0.25F, 0.25F, 0.25F);
            model.setupAnim(null, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
            VertexConsumer vertexConsumer = pBuffer.getBuffer(model.renderType(TEXTURES.get(rank)));
            model.renderToBuffer(pPoseStack, vertexConsumer, pPackedLight, OverlayTexture.NO_OVERLAY);
            pPoseStack.popPose();

            // Render falling pixie dust
            RandomSource random = pLivingEntity.getRandom();
            Source source = pixieItem.getPixieSource();
            double px = pLivingEntity.getX() + (random.nextGaussian() * 0.125D);
            double py = pLivingEntity.getY() + 1.5D - yBob;
            double pz = pLivingEntity.getZ() + (random.nextGaussian() * 0.125D);
            FxDispatcher.INSTANCE.pixieDust(px, py, pz, source.getColor());
        } else if (pixieStack.getItem() instanceof DrainedPixieItem drainedPixieItem) {
            // Render pixie house occupant convalescing if drained
            PixieRank rank = drainedPixieItem.getPixieRank();
            PixieModel model = rank == PixieRank.MAJESTIC ? this.royalDrainedPixieModel : this.baseDrainedPixieModel;
            pPoseStack.pushPose();
            pPoseStack.translate(0D, 0.27D, -0.25D);
            pPoseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
            pPoseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
            pPoseStack.scale(0.25F, 0.25F, 0.25F);
            VertexConsumer vertexConsumer = pBuffer.getBuffer(model.renderType(TEXTURES.get(rank)));
            model.renderToBuffer(pPoseStack, vertexConsumer, pPackedLight, OverlayTexture.NO_OVERLAY);
            pPoseStack.popPose();
        }
    }
}
