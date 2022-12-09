package ARm8.eze.utils.world;

import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.world.CardinalDirection;
import net.minecraft.block.*;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

import ARm8.eze.utils.network.PacketManager;
import ARm8.eze.utils.player.Interactions;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class BlockHelper {
    public static Block getBlock(BlockPos p) {return mc.world.getBlockState(p).getBlock();}
    public static BlockState getState(BlockPos p) {return mc.world.getBlockState(p);}
    public static boolean isBlastRes(BlockPos pos) {return mc.world.getBlockState(pos).getBlock().getBlastResistance() >= 600;}
    public static boolean isReplacable(BlockPos pos) {return getState(pos).getMaterial().isReplaceable();}
    public static boolean isSolid(BlockPos pos) {return getState(pos).isSolidBlock(mc.world, pos);}
    public static boolean isAir(BlockPos p) {return getBlock(p) == Blocks.AIR;}
    public static boolean isBurrowBlock(BlockPos pos) {return isTrapBlock(pos) || isAnvilBlock(pos);}
    public static boolean isTrapBlock(BlockPos pos) {return isObby(pos) || isEchest(pos) || isAnchor(pos);}
    public static boolean isObby(BlockPos pos) {return getBlock(pos) == Blocks.OBSIDIAN || getBlock(pos) == Blocks.CRYING_OBSIDIAN;}
    public static boolean isAnvilBlock(BlockPos pos) {return getBlock(pos) == Blocks.ANVIL || getBlock(pos) == Blocks.CHIPPED_ANVIL || getBlock(pos) == Blocks.DAMAGED_ANVIL;}
    public static boolean isEchest(BlockPos pos) {return getBlock(pos) == Blocks.ENDER_CHEST;}
    public static boolean isAnchor(BlockPos pos) {return getBlock(pos) == Blocks.RESPAWN_ANCHOR;}
    public static Vec3d vec3d(BlockPos pos) {return new Vec3d(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);}
    
    public static boolean isHole(BlockPos p) {
        for (CardinalDirection cd : CardinalDirection.values()) if (isAir(p.offset(cd.toDirection()))) return false;
        return true;
    }

    public static List<BlockPos> getHoles(BlockPos startingPos, int rangeH, int rangeV) {
        ArrayList<BlockPos> holes = new ArrayList<>();
        List<BlockPos> blocks = BlockHelper.getSphere(startingPos, rangeH, rangeV);
        blocks.removeIf(b -> BlockHelper.getBlock(b) != Blocks.AIR); // only want air blocks
        blocks.removeIf(block -> BlockHelper.getBlock(block.down()) == Blocks.AIR); // make sure there is a block below it
        blocks.removeIf(block -> !BlockHelper.isHole(block)); // remove any non-hole position
        blocks.removeIf(block -> mc.player.getBlockPos().equals(block)); // remove our own position
        if (!blocks.isEmpty()) holes.addAll(blocks);
        return holes;
    }

    public static double distanceBetween(BlockPos pos1, BlockPos pos2) {
        double d = pos1.getX() - pos2.getX();
        double e = pos1.getY() - pos2.getY();
        double f = pos1.getZ() - pos2.getZ();
        return MathHelper.sqrt((float) (d * d + e * e + f * f));
    }

    public static List<BlockPos> getSphere(BlockPos centerPos, int radius, int height) {
        ArrayList<BlockPos> blocks = new ArrayList<>();
        for (int i = centerPos.getX() - radius; i < centerPos.getX() + radius; i++) {
            for (int j = centerPos.getY() - height; j < centerPos.getY() + height; j++) {
                for (int k = centerPos.getZ() - radius; k < centerPos.getZ() + radius; k++) {
                    BlockPos pos = new BlockPos(i, j, k);
                    if (distanceBetween(centerPos, pos) <= radius && !blocks.contains(pos)) blocks.add(pos);
                }
            }
        }
        return blocks;
    }

    public static Direction getPlaceSide(BlockPos blockPos) {
        for (Direction side : Direction.values()) {
            BlockPos neighbor = blockPos.offset(side);
            Direction side2 = side.getOpposite();

            BlockState state = mc.world.getBlockState(neighbor);

            // Check if neighbour isn't empty
            if (state.isAir() || isClickable(state.getBlock())) continue;

            // Check if neighbour is a fluid
            if (!state.getFluidState().isEmpty()) continue;

            return side2;
        }

        return null;
    }

    public static boolean isClickable(Block block) {
        return block instanceof CraftingTableBlock || block instanceof AnvilBlock || block instanceof AbstractButtonBlock || block instanceof AbstractPressurePlateBlock || block instanceof BlockWithEntity || block instanceof BedBlock || block instanceof FenceGateBlock || block instanceof DoorBlock || block instanceof NoteBlock || block instanceof TrapdoorBlock;
    }

    public static void place(BlockPos pos, FindItemResult item, boolean rotate, boolean packet) {
        boolean swap = false;
        if (item == null || !item.found() || !item.isHotbar() || !BlockHelper.canPlace(pos)) return;
        if (!Interactions.isHolding(item)) {
            Interactions.setSlot(item.slot() , false);
            swap = true;
        }
        Direction side = getPlaceSide(pos);
        if (side == null) PacketManager.sendInteract(item.getHand(), item, new BlockHitResult(vec3d(pos), Direction.UP, pos, false), rotate, false);
        else PacketManager.sendInteract(item.getHand(), item, new BlockHitResult(vec3d(pos).add((double) side.getOffsetX() * 0.5D, (double) side.getOffsetY() * 0.5D, (double) side.getOffsetZ() * 0.5D), side, pos.offset(side.getOpposite()), false), rotate, packet);
        if (swap) Interactions.swapBack();
    }

    public static boolean canPlace(BlockPos pos) {
        if (pos == null) return false;
        if (isSolid(pos) || !World.isValid(pos) || !isReplacable(pos)) return false;
        if (!mc.world.canPlace(mc.world.getBlockState(pos), pos, ShapeContext.absent())) return false;
        return mc.world.getBlockState(pos).isAir() || mc.world.getBlockState(pos).getFluidState().getFluid() instanceof FlowableFluid;
    }
}
