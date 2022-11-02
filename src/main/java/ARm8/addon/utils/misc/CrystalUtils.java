package ARm8.addon.utils.misc;

import ARm8.addon.modules.combat.CrystalAura;
import ARm8.addon.utils.entity.ezeEntityUtils;
import ARm8.addon.modules.combat.CevBreaker;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.combat.KillAura;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class CrystalUtils {
    static CrystalAura ca = Modules.get().get(CrystalAura.class);

    public static int getPlaceDelay() {
        if (isBurrowBreaking()) return ca.burrowBreakDelay.get();
        else if (isSurroundBreaking()) return ca.surroundBreakDelay.get();
        else return ca.placeDelay.get();
    }

    public static void attackCrystal(Entity entity) {
        // Attack
        mc.player.networkHandler.sendPacket(PlayerInteractEntityC2SPacket.attack(entity, mc.player.isSneaking()));

        if (ca.renderSwing.get()) mc.player.swingHand(Hand.MAIN_HAND);
        if (!ca.hideSwings.get()) mc.getNetworkHandler().sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
        ca.attacks++;

        getBreakDelay();

        if (ca.debug.get()) ca.warning("Breaking");
    }

    // Damage Ignores

    public static boolean targetJustPopped() {
        if (ca.targetPopInvincibility.get()) {
            return !ca.targetPoppedTimer.passedMillis(ca.targetPopInvincibilityTime.get());
        }

        return false;
    }

    public static boolean shouldIgnoreSelfPlaceDamage() {
        return (ca.PDamageIgnore.get() == CrystalAura.DamageIgnore.Always
                || (ca.PDamageIgnore.get() == CrystalAura.DamageIgnore.WhileSafe && (ezeEntityUtils.isSurrounded(mc.player, ezeEntityUtils.BlastResistantType.Any) || ezeEntityUtils.isBurrowed(mc.player, ezeEntityUtils.BlastResistantType.Any)))
                || (ca.selfPopInvincibility.get() && ca.selfPopIgnore.get() != CrystalAura.SelfPopIgnore.Break && !ca.selfPoppedTimer.passedMillis(ca.selfPopInvincibilityTime.get())));
    }

    public static boolean shouldIgnoreSelfBreakDamage() {
        return (ca.BDamageIgnore.get() == CrystalAura.DamageIgnore.Always
                || (ca.BDamageIgnore.get() == CrystalAura.DamageIgnore.WhileSafe && (ezeEntityUtils.isSurrounded(mc.player, ezeEntityUtils.BlastResistantType.Any) || ezeEntityUtils.isBurrowed(mc.player, ezeEntityUtils.BlastResistantType.Any)))
                || (ca.selfPopInvincibility.get() && ca.selfPopIgnore.get() != CrystalAura.SelfPopIgnore.Place && !ca.selfPoppedTimer.passedMillis(ca.selfPopInvincibilityTime.get())));
    }

    private static void getBreakDelay() {
        if (isSurroundHolding() && ca.surroundHoldMode.get() != CrystalAura.SlowMode.Age) {
            ca.breakTimer = ca.surroundHoldDelay.get();
        } else if (ca.slowFacePlace.get() && ca.slowFPMode.get() != CrystalAura.SlowMode.Age && isFacePlacing() && ca.bestTarget != null && ca.bestTarget.getY() < ca.placingCrystalBlockPos.getY()) {
            ca.breakTimer = ca.slowFPDelay.get();
        } else ca.breakTimer = ca.breakDelay.get();
    }

    // Face Place
    public static boolean shouldFacePlace(BlockPos crystal) {
        // Checks if the provided crystal position should face place to any target
        for (PlayerEntity target : ca.targets) {
            BlockPos pos = target.getBlockPos();
            if (ca.CevPause.get() && Modules.get().isActive(CevBreaker.class)) return false;
            if (ca.KAPause.get() && (Modules.get().isActive(KillAura.class) || Modules.get().isActive(KillAura.class))) return false;
            if (ezeEntityUtils.isFaceSurrounded(target, ezeEntityUtils.BlastResistantType.Any)) return false;
            if (ca.surrHoldPause.get() && isSurroundHolding()) return false;

            if (crystal.getY() == pos.getY() + 1 && Math.abs(pos.getX() - crystal.getX()) <= 1 && Math.abs(pos.getZ() - crystal.getZ()) <= 1) {
                if (meteordevelopment.meteorclient.utils.entity.EntityUtils.getTotalHealth(target) <= ca.facePlaceHealth.get()) return true;

                for (ItemStack itemStack : target.getArmorItems()) {
                    if (itemStack == null || itemStack.isEmpty()) {
                        if (ca.facePlaceArmor.get()) return true;
                    }
                    else {
                        if ((float) (itemStack.getMaxDamage() - itemStack.getDamage()) / itemStack.getMaxDamage() * 100 <= ca.facePlaceDurability.get()) return true;
                    }
                }
            }
        }

        return false;
    }

    public static boolean isFacePlacing() {
        return (ca.facePlace.get() || ca.forceFacePlace.get().isPressed());
    }

    // Burrow Break

    public static boolean shouldBurrowBreak(BlockPos crystal) {
        BlockPos pos = ca.bestTarget.getBlockPos();

        if (!isBurrowBreaking()) return false;

        return ((crystal.getY() == pos.getY() - 1 || crystal.getY() == pos.getY()) && Math.abs(pos.getX() - crystal.getX()) <= 1 && Math.abs(pos.getZ() - crystal.getZ()) <= 1);
    }

    public static boolean isBurrowBreaking() {
        if (ca.burrowBreak.get() || ca.forceBurrowBreak.get().isPressed()) {
            if (ca.bestTarget != null && ezeEntityUtils.isBurrowed(ca.bestTarget, ezeEntityUtils.BlastResistantType.Mineable)) {
                switch (ca.burrowBWhen.get()) {
                    case BothTrapped -> {
                        return ezeEntityUtils.isBothTrapped(ca.bestTarget, ezeEntityUtils.BlastResistantType.Any);
                    }
                    case AnyTrapped -> {
                        return ezeEntityUtils.isAnyTrapped(ca.bestTarget, ezeEntityUtils.BlastResistantType.Any);
                    }
                    case TopTrapped -> {
                        return ezeEntityUtils.isTopTrapped(ca.bestTarget, ezeEntityUtils.BlastResistantType.Any);
                    }
                    case FaceTrapped -> {
                        return ezeEntityUtils.isFaceSurrounded(ca.bestTarget, ezeEntityUtils.BlastResistantType.Any);
                    }
                    case Always -> {
                        return true;
                    }
                }
            }
        }

        return false;
    }


    // Surround Break

    // Todo : improve this
    public static boolean shouldSurroundBreak(BlockPos crystal) {
        BlockPos pos = ca.bestTarget.getBlockPos();

        // Checking right criteria
        if (!isSurroundBreaking()) return false;

        // Checking valid crystal position
        return
                (!ezeEntityUtils.isBedrock(pos.north(1))
                        && (crystal.equals(pos.north(2))
                        || (ca.surroundBHorse.get() && (crystal.equals(pos.north(2).west()) || crystal.equals(pos.north(2).east())))
                        || (ca.surroundBDiagonal.get() && (crystal.equals(pos.north().west()) || crystal.equals(pos.north().east())))
                ))

                        || (!ezeEntityUtils.isBedrock(pos.south(1))
                        && (crystal.equals(pos.south(2))
                        || (ca.surroundBHorse.get() && (crystal.equals(pos.south(2).west()) || crystal.equals(pos.south(2).east())))
                        || (ca.surroundBDiagonal.get() && (crystal.equals(pos.south().west()) || crystal.equals(pos.south().east())))
                ))

                        || (!ezeEntityUtils.isBedrock(pos.west(1))
                        && (crystal.equals(pos.west(2))
                        || (ca.surroundBHorse.get() && (crystal.equals(pos.west(2).north()) || crystal.equals(pos.west(2).south())))
                        || (ca.surroundBDiagonal.get() && (crystal.equals(pos.west().north()) || crystal.equals(pos.west().south())))
                ))

                        || (!ezeEntityUtils.isBedrock(pos.east(1))
                        && (crystal.equals(pos.east(2))
                        || (ca.surroundBHorse.get() && (crystal.equals(pos.east(2).north()) || crystal.equals(pos.east(2).south())))
                        || (ca.surroundBDiagonal.get() && (crystal.equals(pos.east().north()) || crystal.equals(pos.east().south())))
                ));

        // I tried this one below, and it doesn't work very well, still I think there's a better way of doing this tho, this takes so much computing power
        /*
        BlockPos targetSurround = EntityUtils.getCityBlock(playerTarget);

            if (targetSurround != null) {
                // Checking around targets city block
                for (Direction direction : Direction.values()) {
                    if (direction == Direction.DOWN || direction == Direction.UP) continue;

                    // If one of the positions matches the current pos, ignore minDamage
                    if (pos.equals(targetSurround.down().offset(direction))) return true;
                }         */
    }

    public static boolean isSurroundBreaking() {
        if (ca.surroundBreak.get() || ca.forceSurroundBreak.get().isPressed()) {
            if (ca.bestTarget != null && ezeEntityUtils.isSurrounded(ca.bestTarget, ezeEntityUtils.BlastResistantType.Mineable)) {
                switch (ca.surroundBWhen.get()) {
                    case BothTrapped -> {
                        return ezeEntityUtils.isBothTrapped(ca.bestTarget, ezeEntityUtils.BlastResistantType.Any);
                    }
                    case AnyTrapped -> {
                        return ezeEntityUtils.isAnyTrapped(ca.bestTarget, ezeEntityUtils.BlastResistantType.Any);
                    }
                    case TopTrapped -> {
                        return ezeEntityUtils.isTopTrapped(ca.bestTarget, ezeEntityUtils.BlastResistantType.Any);
                    }
                    case FaceTrapped -> {
                        return ezeEntityUtils.isFaceSurrounded(ca.bestTarget, ezeEntityUtils.BlastResistantType.Any);
                    }
                    case Always -> {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static boolean isSurroundHolding() {
        if (ca.surroundHold.get() && ca.bestTarget != null && ezeEntityUtils.isSurroundBroken(ca.bestTarget, ezeEntityUtils.BlastResistantType.Any)) {
            switch (ca.surroundHWhen.get()) {
                case BothTrapped -> {
                    return ezeEntityUtils.isBothTrapped(ca.bestTarget, ezeEntityUtils.BlastResistantType.Any);
                }
                case AnyTrapped -> {
                    return ezeEntityUtils.isAnyTrapped(ca.bestTarget, ezeEntityUtils.BlastResistantType.Any);
                }
                case TopTrapped -> {
                    return ezeEntityUtils.isTopTrapped(ca.bestTarget, ezeEntityUtils.BlastResistantType.Any);
                }
                case FaceTrapped -> {
                    return ezeEntityUtils.isFaceSurrounded(ca.bestTarget, ezeEntityUtils.BlastResistantType.Any);
                }
                case Always -> {
                    return true;
                }
            }
        }

        return false;
    }
}
