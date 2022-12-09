package ARm8.eze.utils.player;

import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.world.CardinalDirection;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.math.BlockPos;

import static meteordevelopment.meteorclient.MeteorClient.mc;

import ARm8.eze.utils.network.PacketManager;
import ARm8.eze.utils.world.BlockHelper;

public class Interactions {
    public static int lastSlot = -1;
    
    public static Item getMainHandItem() { return mc.player.getInventory().getMainHandStack().getItem(); }
    
    public static boolean isHolding(Item item) {return getMainHandItem().equals(item);}
    public static boolean isHolding(FindItemResult itemResult) {return isHolding(getItemFromSlot(itemResult.slot()));}

    public static Item getItemFromSlot(Integer slot) {
        if (slot == -1) return null;
        if (slot == 45) return mc.player.getOffHandStack().getItem();
        return mc.player.getInventory().getStack(slot).getItem();
    }

    public static boolean isInHole() {return isInHole(mc.player);}
    public static boolean isBurrowed() {return isBurrowed(mc.player);}

    public static boolean isInHole(PlayerEntity p) {
        BlockPos center = p.getBlockPos();
        for (CardinalDirection cd : CardinalDirection.values()) if (!BlockHelper.isBlastRes(center.offset(cd.toDirection()))) return false;
        return true;
    }

    public static boolean isBurrowed(PlayerEntity p) {
        if (p == null) return false;
        return BlockHelper.isBurrowBlock(p.getBlockPos());
    }

    public static void setSlot(int slot, boolean packet) {
        if (slot < 0) return;
        lastSlot = mc.player.getInventory().selectedSlot;
        if (packet) {
            PacketManager.updateSlot(slot);
        } else {
            InvUtils.swap(slot, false);
        }
    }

    public static void swapBack() {
        setSlot(lastSlot, false);
    }
}
