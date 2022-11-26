package ARm8.eze.utils.world;

import meteordevelopment.meteorclient.utils.world.CardinalDirection;
import net.minecraft.block.*;
import net.minecraft.util.math.*;

import java.util.ArrayList;
import java.util.List;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class BlockHelper {
    public static Block getBlock(BlockPos p) {return mc.world.getBlockState(p).getBlock();}
    public static boolean isBlastRes(BlockPos pos) {return mc.world.getBlockState(pos).getBlock().getBlastResistance() >= 600;}
    public static boolean isAir(BlockPos p) {return getBlock(p) == Blocks.AIR;}
    public static boolean isBurrowBlock(BlockPos pos) {return isTrapBlock(pos) || isAnvilBlock(pos);}
    public static boolean isTrapBlock(BlockPos pos) {return isObby(pos) || isEchest(pos) || isAnchor(pos);}
    public static boolean isObby(BlockPos pos) {return getBlock(pos) == Blocks.OBSIDIAN || getBlock(pos) == Blocks.CRYING_OBSIDIAN;}
    public static boolean isAnvilBlock(BlockPos pos) {return getBlock(pos) == Blocks.ANVIL || getBlock(pos) == Blocks.CHIPPED_ANVIL || getBlock(pos) == Blocks.DAMAGED_ANVIL;}
    public static boolean isEchest(BlockPos pos) {return getBlock(pos) == Blocks.ENDER_CHEST;}
    public static boolean isAnchor(BlockPos pos) {return getBlock(pos) == Blocks.RESPAWN_ANCHOR;}
    
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
}
