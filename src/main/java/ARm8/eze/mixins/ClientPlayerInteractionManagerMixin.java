package ARm8.eze.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import ARm8.eze.modules.exploits.BowBomb;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;

@Mixin(ClientPlayerInteractionManagerMixin.class)
public class ClientPlayerInteractionManagerMixin {
    @Inject(method="stopUsingItem", at=@At("HEAD"), cancellable = true)
    public void stopUsingItem(PlayerEntity player, CallbackInfo info) {
        if (Modules.get().get(BowBomb.class).isActive()) {
            if (player.getInventory().getMainHandStack().getItem() instanceof BowItem) {
                BowBomb.INSTANCE.onBowRelease();
            }   
        }
    }
}
