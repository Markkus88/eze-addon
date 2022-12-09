package ARm8.eze.modules.movement;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;

import java.util.Comparator;
import java.util.List;

public class WideScaffold extends Module {

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Boolean> packetPlace = sgGeneral.add(new BoolSetting.Builder().name("packet-place").defaultValue(false).build());
    private final Setting<Boolean> rotation = sgGeneral.add(new BoolSetting.Builder().name("rotate").defaultValue(false).build());
    private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder().name("delay").defaultValue(1).min(1).sliderMax(10).build());
    private final Setting<Integer> blocksPerTick = sgGeneral.add(new IntSetting.Builder().name("blocks-per-tick").defaultValue(3).min(1).sliderMax(5).build());
    private final Setting<Integer> rRange = sgGeneral.add(new IntSetting.Builder().name("radius").defaultValue(5).min(1).sliderMax(5).build());
    private final Setting<List<Block>> blocks = sgGeneral.add(new BlockListSetting.Builder().name("blocks").build());

    private int blocksPlaced, rendersAdded, timer;

    public WideScaffold() {
        super(Categories.Movement, "wide-scaffold", "Scaffold but wider.");
    }

    @Override
    public void onActivate() {
        blocksPlaced = 0;
        rendersAdded = 0;
        timer = 0;
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {

        if (timer <= 0) {
            timer = delay.get();
        } else {
            timer--;
            return;
        }

        // reset
        blocksPlaced = 0;
        rendersAdded = 0;

        // Get nearby blocks
        BlockPos playerPos = mc.player.getBlockPos();
        BlockPos belowPos = playerPos.down();
        List<BlockPos> nearby = ARm8.eze.utils.world.BlockHelper.getSphere(playerPos, rRange.get(), 1);
        // Remove any blocks not on our y level
        nearby.removeIf(blockPos -> blockPos.getY() != belowPos.getY());
        // Remove any blocks we can't place
        nearby.removeIf(blockPos -> !BlockUtils.canPlace(blockPos));
        // Sort all the blocks by shortest -> the longest distance
        nearby.sort(Comparator.comparingDouble(PlayerUtils::distanceTo));
        // Place the scaffold blocks
        for (BlockPos pos : nearby) {
            if (blocksPlaced >= blocksPerTick.get()) break;
            placeScaffoldBlock(pos);
        }
    }


    private void placeScaffoldBlock(BlockPos pos) {
        FindItemResult block = InvUtils.findInHotbar(itemStack -> blocks.get().contains(Block.getBlockFromItem(itemStack.getItem())));
        if (!block.found()) return;
        blocksPlaced++;
        rendersAdded++;
        ARm8.eze.utils.world.BlockHelper.place(pos, block, rotation.get(), packetPlace.get());
    }
}
