package ARm8.eze.mixins;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.misc.DiscordPresence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import ARm8.eze.modules.misc.Config;

@Mixin(value = DiscordPresence.class, remap = false)
public class DiscordPresenceMixin {
    @ModifyArg(method = "onActivate", at = @At(value = "INVOKE", target = "Lmeteordevelopment/discordipc/DiscordIPC;start(JLjava/lang/Runnable;)Z"))
    private long modifyAppId(long appId) {
        if (Modules.get().get(Config.class).discordPresence.get()) {
            return 829457935914500117L;
        } else {
            return appId;
        }
    }

    @ModifyArgs(method = "onActivate", at = @At(value = "INVOKE", target = "Lmeteordevelopment/discordipc/RichPresence;setLargeImage(Ljava/lang/String;Ljava/lang/String;)V"))
    private void modifyLargeImage(Args args) {
        if (Modules.get().get(Config.class).discordPresence.get()) {
            args.set(0, "icon");
            args.set(1, "eze 0.0.0");
        } else {
            args.set(0, "meteor_client");
            args.set(1, "Meteor Client " + MeteorClient.VERSION);
        }
    }

    @ModifyArg(method = "onTick", at = @At(value = "INVOKE", target = "Lmeteordevelopment/discordipc/RichPresence;setDetails(Ljava/lang/String;)V", ordinal = 1))
    private String modifyDetails(String details) {
        if (Modules.get().get(Config.class).discordPresence.get()) {
            return null;
        } else {
            return details;
        }
    }
}