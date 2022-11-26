package ARm8.addon.utils.player;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.collection.DefaultedList;

import java.util.List;

import static meteordevelopment.meteorclient.MeteorClient.mc;
public class InventoryUtils {
    public static void clickInvSlot(int slot, int button, SlotActionType action) {
        clickSlot(0, 129, slot, button, action, mc.player.currentScreenHandler);
    }

    public static void clickSlot(int syncId, int slot, int button, SlotActionType action) {
        clickSlot(syncId, mc.player.currentScreenHandler.getRevision(), slot, button, action, mc.player.currentScreenHandler);
    }

    public static void clickSlot(int syncId, int revision, int slot, int button, SlotActionType action) {
        clickSlot(syncId, revision, slot, button, action, mc.player.currentScreenHandler);
    }

    public static void clickSlot(int syncId, int slot, int button, SlotActionType action, ScreenHandler handler) {
        clickSlot(syncId, handler.getRevision(), slot, button, action, handler.slots, handler.getCursorStack());
    }

    public static void clickSlot(int syncId, int revision, int slot, int button, SlotActionType action, ScreenHandler handler) {
        clickSlot(syncId, revision, slot, button, action, handler.slots, handler.getCursorStack());
    }

    public static void clickSlot(int syncId, int revision, int id, int button, SlotActionType action, DefaultedList<Slot> slots, ItemStack cursorStack) {
        Int2ObjectOpenHashMap<ItemStack> stacks = new Int2ObjectOpenHashMap<>();
        List<ItemStack> list = Lists.newArrayListWithCapacity(slots.size());

        for (Slot slot : slots) list.add(slot.getStack().copy());

        for (int slot = 0; slot < slots.size(); slot++) {
            ItemStack stack1 = list.get(slot);
            ItemStack stack2 = slots.get(slot).getStack();

            if (!ItemStack.areEqual(stack1, stack2)) stacks.put(slot, stack2.copy());
        }

        mc.getNetworkHandler().sendPacket(new ClickSlotC2SPacket(syncId, revision, id, button, action, cursorStack.copy(), stacks));
    }
}
