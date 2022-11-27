package ARm8.eze.mixins;

import meteordevelopment.meteorclient.systems.modules.misc.DiscordPresence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(value = DiscordPresence.class, remap = false)
public class DiscordPresenceMixin {
    @ModifyArg(method = "onActivate", at = @At(value = "INVOKE", target = "Lmeteordevelopment/discordipc/DiscordIPC;start(JLjava/lang/Runnable;)Z"))
    private long modifyAppId(long appId) {
        return 829457935914500117L;
    }

    @ModifyArgs(method = "onActivate", at = @At(value = "INVOKE", target = "Lmeteordevelopment/discordipc/RichPresence;setLargeImage(Ljava/lang/String;Ljava/lang/String;)V"))
    private void modifyLargeImage(Args args) {
        args.set(0, "icon");
        args.set(1, "eze 0.0.0");
    }

    @ModifyArg(method = "onTick", at = @At(value = "INVOKE", target = "Lmeteordevelopment/discordipc/RichPresence;setDetails(Ljava/lang/String;)V", ordinal = 1))
    private String modifyDetails(String details) {
        return null;
    }
}