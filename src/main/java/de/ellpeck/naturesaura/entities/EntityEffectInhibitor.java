package de.ellpeck.naturesaura.entities;

import com.google.common.collect.ListMultimap;
import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.misc.IWorldData;
import de.ellpeck.naturesaura.api.render.IVisualizable;
import de.ellpeck.naturesaura.items.ItemEffectPowder;
import de.ellpeck.naturesaura.items.ModItems;
import de.ellpeck.naturesaura.misc.WorldData;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class EntityEffectInhibitor extends Entity implements IVisualizable {

    private static final DataParameter<String> INHIBITED_EFFECT = EntityDataManager.createKey(EntityEffectInhibitor.class, DataSerializers.STRING);
    private static final DataParameter<Integer> COLOR = EntityDataManager.createKey(EntityEffectInhibitor.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> AMOUNT = EntityDataManager.createKey(EntityEffectInhibitor.class, DataSerializers.VARINT);

    @SideOnly(Side.CLIENT)
    public int renderTicks;

    public EntityEffectInhibitor(World worldIn) {
        super(worldIn);
    }

    public static void place(World world, ItemStack stack, double posX, double posY, double posZ) {
        ResourceLocation effect = ItemEffectPowder.getEffect(stack);
        EntityEffectInhibitor entity = new EntityEffectInhibitor(world);
        entity.setInhibitedEffect(effect);
        entity.setColor(NaturesAuraAPI.EFFECT_POWDERS.get(effect));
        entity.setAmount(stack.getCount());
        entity.setPosition(posX, posY, posZ);
        world.spawnEntity(entity);
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        this.addToPowderList();
    }

    @Override
    public void onRemovedFromWorld() {
        this.removeFromPowderList();
        super.onRemovedFromWorld();
    }

    @Override
    public void setPosition(double x, double y, double z) {
        boolean should = x != this.posX || y != this.posY || z != this.posZ;
        if (should)
            this.removeFromPowderList();
        super.setPosition(x, y, z);
        if (should)
            this.addToPowderList();
    }

    private void addToPowderList() {
        if (!this.isAddedToWorld())
            return;
        List<Tuple<Vec3d, Integer>> powders = this.getPowderList();
        powders.add(new Tuple<>(this.getPositionVector(), this.getAmount()));
    }

    private void removeFromPowderList() {
        if (!this.isAddedToWorld())
            return;
        List<Tuple<Vec3d, Integer>> powders = this.getPowderList();
        Vec3d pos = this.getPositionVector();
        for (int i = 0; i < powders.size(); i++)
            if (pos.equals(powders.get(i).getFirst())) {
                powders.remove(i);
                break;
            }
    }

    private List<Tuple<Vec3d, Integer>> getPowderList() {
        ListMultimap<ResourceLocation, Tuple<Vec3d, Integer>> powders = ((WorldData) IWorldData.getWorldData(this.world)).effectPowders;
        return powders.get(this.getInhibitedEffect());
    }

    @Override
    protected void entityInit() {
        this.setSize(0.25F, 0.25F);
        this.dataManager.register(INHIBITED_EFFECT, null);
        this.dataManager.register(COLOR, 0);
        this.dataManager.register(AMOUNT, 0);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        this.setInhibitedEffect(new ResourceLocation(compound.getString("effect")));
        this.setColor(compound.getInteger("color"));
        this.setAmount(compound.hasKey("amount") ? compound.getInteger("amount") : 24);
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        compound.setString("effect", this.getInhibitedEffect().toString());
        compound.setInteger("color", this.getColor());
        compound.setInteger("amount", this.getAmount());
    }

    @Override
    public void onEntityUpdate() {
        if (this.world.isRemote) {
            if (this.world.getTotalWorldTime() % 5 == 0) {
                NaturesAura.proxy.spawnMagicParticle(
                        this.posX + this.world.rand.nextGaussian() * 0.1F,
                        this.posY,
                        this.posZ + this.world.rand.nextGaussian() * 0.1F,
                        this.world.rand.nextGaussian() * 0.005F,
                        this.world.rand.nextFloat() * 0.03F,
                        this.world.rand.nextGaussian() * 0.005F,
                        this.getColor(), this.world.rand.nextFloat() * 3F + 1F, 120, 0F, true, true);
            }
            this.renderTicks++;
        }
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (source instanceof EntityDamageSource && !this.world.isRemote) {
            this.setDead();
            this.entityDropItem(this.getDrop(), 0F);
            return true;
        } else
            return super.attackEntityFrom(source, amount);
    }

    public ItemStack getDrop() {
        return ItemEffectPowder.setEffect(new ItemStack(ModItems.EFFECT_POWDER, this.getAmount()), this.getInhibitedEffect());
    }

    public void setInhibitedEffect(ResourceLocation effect) {
        this.removeFromPowderList();
        this.dataManager.set(INHIBITED_EFFECT, effect.toString());
        this.addToPowderList();
    }

    public ResourceLocation getInhibitedEffect() {
        return new ResourceLocation(this.dataManager.get(INHIBITED_EFFECT));
    }

    public void setColor(int color) {
        this.dataManager.set(COLOR, color);
    }

    public int getColor() {
        return this.dataManager.get(COLOR);
    }

    public void setAmount(int amount) {
        this.removeFromPowderList();
        this.dataManager.set(AMOUNT, amount);
        this.addToPowderList();
    }

    public int getAmount() {
        return this.dataManager.get(AMOUNT);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getVisualizationBounds(World world, BlockPos pos) {
        return Helper.aabb(this.getPositionVector()).grow(this.getAmount());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getVisualizationColor(World world, BlockPos pos) {
        return this.getColor();
    }
}
