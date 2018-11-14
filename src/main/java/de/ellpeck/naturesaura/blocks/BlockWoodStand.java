package de.ellpeck.naturesaura.blocks;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.recipes.TreeRitualRecipe;
import de.ellpeck.naturesaura.blocks.multi.Multiblocks;
import de.ellpeck.naturesaura.blocks.tiles.TileEntityWoodStand;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.SaplingGrowTreeEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BlockWoodStand extends BlockContainerImpl {

    private static final AxisAlignedBB BOUND_BOX = new AxisAlignedBB(3 / 16F, 0F, 3 / 16F, 13 / 16F, 13 / 16F, 13 / 16F);

    public BlockWoodStand() {
        super(Material.WOOD, "wood_stand", TileEntityWoodStand.class, "wood_stand");
        this.setHardness(1.5F);
        this.setSoundType(SoundType.WOOD);
        this.setHarvestLevel("axe", 0);

        MinecraftForge.TERRAIN_GEN_BUS.register(this);
    }

    @SubscribeEvent
    public void onTreeGrow(SaplingGrowTreeEvent event) {
        World world = event.getWorld();
        BlockPos pos = event.getPos();
        if (!world.isRemote) {
            if (Multiblocks.TREE_RITUAL.isComplete(world, pos)) {
                IBlockState sapling = world.getBlockState(pos);
                ItemStack saplingStack = sapling.getBlock().getItem(world, pos, sapling);
                if (!saplingStack.isEmpty()) {
                    for (TreeRitualRecipe recipe : NaturesAuraAPI.TREE_RITUAL_RECIPES.values()) {
                        if (Helper.areItemsEqual(saplingStack, recipe.saplingType, true)) {
                            List<ItemStack> required = new ArrayList<>(Arrays.asList(recipe.items));
                            MutableObject<TileEntityWoodStand> toPick = new MutableObject<>();

                            boolean fine = Multiblocks.TREE_RITUAL.forEach(pos, 'W', (tilePos, matcher) -> {
                                TileEntity tile = world.getTileEntity(tilePos);
                                if (tile instanceof TileEntityWoodStand) {
                                    TileEntityWoodStand stand = (TileEntityWoodStand) tile;
                                    ItemStack stack = stand.items.getStackInSlot(0);
                                    if (!stack.isEmpty()) {
                                        int index = Helper.getItemIndex(required, stack, true);
                                        if (index >= 0) {
                                            required.remove(index);

                                            if (toPick.getValue() == null) {
                                                toPick.setValue(stand);
                                            }
                                        } else {
                                            return false;
                                        }
                                    }
                                }
                                return true;
                            });

                            if (fine && required.isEmpty()) {
                                toPick.getValue().setRitual(pos, recipe);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        return Helper.putStackOnTile(playerIn, hand, pos, 0, true);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return BOUND_BOX;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
        return false;
    }

    @Override
    public boolean isSideSolid(IBlockState baseState, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return false;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }
}
