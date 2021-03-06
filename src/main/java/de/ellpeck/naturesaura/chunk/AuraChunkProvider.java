package de.ellpeck.naturesaura.chunk;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AuraChunkProvider implements ICapabilityProvider, INBTSerializable<NBTTagCompound> {

    private final Chunk chunk;
    private IAuraChunk auraChunk;

    public AuraChunkProvider(Chunk chunk) {
        this.chunk = chunk;
    }

    private IAuraChunk getAuraChunk() {
        if (this.auraChunk == null)
            this.auraChunk = new AuraChunk(this.chunk, IAuraType.forWorld(this.chunk.getWorld()));
        return this.auraChunk;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == NaturesAuraAPI.capAuraChunk;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        return capability == NaturesAuraAPI.capAuraChunk ? (T) this.getAuraChunk() : null;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        return this.getAuraChunk().serializeNBT();
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        this.getAuraChunk().deserializeNBT(nbt);
    }
}
