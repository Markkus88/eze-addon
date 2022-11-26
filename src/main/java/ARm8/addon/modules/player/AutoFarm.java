package ARm8.addon.modules.player;

import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.block.Block;
import net.minecraft.block.CropBlock;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Direction;
import meteordevelopment.meteorclient.utils.player.FindItemResult;

public class AutoFarm extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Boolean> breakCrops = sgGeneral.add(new BoolSetting.Builder()
            .name("break-crops")
            .description("Breaks fully grown crops.")
            .defaultValue(false)
            .build()
    );

    private final Setting<Boolean> plantCrops = sgGeneral.add(new BoolSetting.Builder()
            .name("plant-crops")
            .description("Plant seeds from your hotbar on empty farmland.")
            .defaultValue(false)
            .build()
    );

    private final Setting<Boolean> center = sgGeneral.add(new BoolSetting.Builder()
            .name("center")
            .description("Automatically centers you when planting or breaking crops.")
            .defaultValue(false)
            .build()
    );

    private final Setting<Boolean> onGround = sgGeneral.add(new BoolSetting.Builder()
            .name("on-ground")
            .description("Only plant or break crops when you are on ground.")
            .defaultValue(false)
            .build()
    );

    private final Setting<Integer> horizontalRadius = sgGeneral.add(new IntSetting.Builder()
            .name("horizontal-radius")
            .defaultValue(4)
            .sliderMin(1)
            .sliderMax(10)
            .build()
    );

    private final Setting<Integer> verticalRadius = sgGeneral.add(new IntSetting.Builder()
            .name("vertical-radius")
            .defaultValue(2)
            .sliderMin(1)
            .sliderMax(10)
            .build()
    );

    MinecraftClient mc = MinecraftClient.getInstance();

    public AutoFarm() {
        super(Categories.Player, "auto-farm", "Automatically plants and breaks crops.");
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        BlockPos blockPos = getFarmLand();
        if (blockPos == null) {
            return;
        }

        FindItemResult itemResult; 
        itemResult = InvUtils.findInHotbar(Items.CARROT, Items.POTATO, Items.WHEAT_SEEDS, Items.BEETROOT_SEEDS, Items.MELON_SEEDS, Items.PUMPKIN_SEEDS);

        if (!itemResult.found()) {
            return;
        }

        if (onGround.get() && !mc.player.isOnGround()) {
            return;
        }
        
        if (center.get()) PlayerUtils.centerPlayer();
        Rotations.rotate(Rotations.getYaw(blockPos), Rotations.getPitch(blockPos), () -> {
            InvUtils.swap(itemResult.slot(), false);
            mc.player.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, new BlockHitResult(Utils.vec3d(blockPos), Direction.UP, blockPos, false), 0));
            mc.player.swingHand(Hand.MAIN_HAND);
        });
    }

    private BlockPos getFarmLand() {
        for (int x = -horizontalRadius.get(); x < horizontalRadius.get(); x++) {
            for (int y = -verticalRadius.get(); y < verticalRadius.get(); y++) {
                for (int z = -horizontalRadius.get(); z < horizontalRadius.get(); z++) {
                    BlockPos blockPos = mc.player.getBlockPos().add(x, y, z);
                    Block block = mc.world.getBlockState(blockPos).getBlock();
                    if (block instanceof FarmlandBlock farmLandBlock && !(mc.world.getBlockState(blockPos.up()).getBlock() instanceof CropBlock)) {
                        if (plantCrops.get()) {
                            return blockPos;
                        }
                    }
                    if (mc.world.getBlockState(blockPos.up()).getBlock() instanceof CropBlock cropBlock) {
                        if (cropBlock.isMature(mc.world.getBlockState(blockPos.up())) && breakCrops.get()) {
                            breakCrops(blockPos.up());
                        }
                    }
                }
            }
        }
        return null;
    }

    private void breakCrops(BlockPos blockPos) {
        mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, blockPos, net.minecraft.util.math.Direction.UP));
        mc.player.swingHand(Hand.MAIN_HAND);
        mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, blockPos, net.minecraft.util.math.Direction.UP));
    }

    @EventHandler
    private void onRender(Render3DEvent event) {
        BlockPos farmLand = getFarmLand();
        if (farmLand == null || !InvUtils.findInHotbar(Items.CARROT, Items.POTATO, Items.WHEAT_SEEDS, Items.BEETROOT_SEEDS, Items.MELON_SEEDS, Items.PUMPKIN_SEEDS).found()) return;
        event.renderer.box(farmLand, Color.WHITE, Color.WHITE, ShapeMode.Lines, 0);
    }
}
