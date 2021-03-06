package de.ellpeck.naturesaura.blocks.tiles;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.container.IAuraContainer;
import de.ellpeck.naturesaura.api.aura.container.NaturalAuraContainer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

public class TileEntityAncientLeaves extends TileEntityImpl {

    private final NaturalAuraContainer container = new NaturalAuraContainer(NaturesAuraAPI.TYPE_OVERWORLD, 2000, 500) {
        @Override
        public int getAuraColor() {
            return 0xCE5489;
        }

        @Override
        public int drainAura(int amountToDrain, boolean simulate) {
            int amount = super.drainAura(amountToDrain, simulate);
            if (amount > 0 && !simulate) {
                TileEntityAncientLeaves.this.sendToClients();
            }
            return amount;
        }
    };

    @Override
    public IAuraContainer getAuraContainer(EnumFacing facing) {
        return this.container;
    }

    @Override
    public void writeNBT(NBTTagCompound compound, SaveType type) {
        super.writeNBT(compound, type);
        if (type != SaveType.BLOCK)
            this.container.writeNBT(compound);
    }

    @Override
    public void readNBT(NBTTagCompound compound, SaveType type) {
        super.readNBT(compound, type);
        if (type != SaveType.BLOCK)
            this.container.readNBT(compound);
    }
}
