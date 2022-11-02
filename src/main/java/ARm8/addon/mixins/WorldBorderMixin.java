package ARm8.addon.mixins;

import ARm8.addon.modules.misc.NoCollision;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.border.WorldBorder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldBorder.class)
public class WorldBorderMixin {
    @Inject(method = "asVoxelShape", at = @At("HEAD"), cancellable = true)
    private void onAsVoxelShape(CallbackInfoReturnable<VoxelShape> info) {
        if (Modules.get().isActive(NoCollision.class) && Modules.get().get(NoCollision.class).shouldCancelBorderCollision()) {
            info.setReturnValue(VoxelShapes.empty());
        }
    }
}