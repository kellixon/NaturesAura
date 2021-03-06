package de.ellpeck.naturesaura.proxy;

import de.ellpeck.naturesaura.reg.IColorProvidingBlock;
import de.ellpeck.naturesaura.reg.IColorProvidingItem;
import de.ellpeck.naturesaura.reg.ITESRProvider;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.util.function.Supplier;

public interface IProxy {
    void preInit(FMLPreInitializationEvent event);

    void init(FMLInitializationEvent event);

    void postInit(FMLPostInitializationEvent event);

    void registerRenderer(ItemStack stack, ModelResourceLocation location);

    void addColorProvidingItem(IColorProvidingItem item);

    void addColorProvidingBlock(IColorProvidingBlock block);

    void registerTESR(ITESRProvider provider);

    <T extends Entity> void registerEntityRenderer(Class<T> entityClass, Supplier<IRenderFactory<T>> renderFactory);

    void spawnMagicParticle(double posX, double posY, double posZ, double motionX, double motionY, double motionZ, int color, float scale, int maxAge, float gravity, boolean collision, boolean fade);

    void setParticleDepth(boolean depth);

    void setParticleSpawnRange(int range);

    void scheduleTask(Runnable runnable);
}
