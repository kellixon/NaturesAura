package de.ellpeck.naturesaura.entities.render;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.entities.EntityMoverMinecart;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderMinecart;
import net.minecraft.util.ResourceLocation;

public class RenderMoverMinecart extends RenderMinecart<EntityMoverMinecart> {

    private static final ResourceLocation RES = new ResourceLocation(NaturesAura.MOD_ID, "textures/models/mover_cart.png");
    private final ModelMoverMinecart model = new ModelMoverMinecart();

    public RenderMoverMinecart(RenderManager renderManagerIn) {
        super(renderManagerIn);
    }

    @Override
    protected void renderCartContents(EntityMoverMinecart cart, float partialTicks, IBlockState state) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(0, 22 / 16F, 0);
        GlStateManager.rotate(180, 1, 0, 0);
        this.bindTexture(RES);
        this.model.render();
        GlStateManager.popMatrix();
    }

    private static class ModelMoverMinecart extends ModelBase {

        private final ModelRenderer box;

        public ModelMoverMinecart() {
            this.box = new ModelRenderer(this, 0, 0);
            this.box.setTextureSize(64, 64);
            this.box.addBox(0, 0, 0, 16, 24, 16);
        }

        public void render() {
            this.box.render(1 / 16F);
        }
    }
}
