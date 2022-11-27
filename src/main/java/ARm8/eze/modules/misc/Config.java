package ARm8.eze.modules.misc;

import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.misc.DiscordPresence;
import meteordevelopment.orbit.EventHandler;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class Config extends Module {
    public final SettingGroup sgGeneral = settings.getDefaultGroup();
    public final SettingGroup sgDiscordPresence = settings.createGroup("Discord Presence");
    public final SettingGroup sgChat = settings.createGroup("Chat");

    // Discord Presence

    public final Setting<Boolean> discordPresence = sgDiscordPresence.add(new BoolSetting.Builder()
        .name("discord-presence")
        .description("Shows that you are using eze in Discord.")
        .onChanged((c) -> {
			if (isActive()) {
				if (Modules.get().get(DiscordPresence.class).isActive()) {
                    Modules.get().get(DiscordPresence.class).toggle();
                    Modules.get().get(DiscordPresence.class).toggle();
                } else if (!Modules.get().get(DiscordPresence.class).isActive()) {
                    Modules.get().get(DiscordPresence.class).toggle();
                }
			}
		})
        .defaultValue(true)
        .build());

    // Chat

    public final Setting<Boolean> ezePrefix = sgChat.add(new BoolSetting.Builder()
        .name("eze-prefix")
        .description("Replaces Meteor prefix with eze prefix.")
        .defaultValue(true)
        .build());

    public final Setting<Boolean> chatFormatting = sgChat.add(new BoolSetting.Builder()
        .name("chat-formatting")
        .description("Changes style of messages.")
        .defaultValue(false)
        .build());

    private final Setting<ChatFormatting> formattingMode = sgChat.add(new EnumSetting.Builder<ChatFormatting>()
        .name("mode")
        .description("The style of messages.")
        .defaultValue(ChatFormatting.Bold)
        .visible(chatFormatting::get)
        .build());

    public Config() {
        super(Categories.Misc, "config", "Configuration of eze");

        runInMainMenu = true;
    }

    @EventHandler
    public void chatFormatting(PacketEvent.Receive event) {
        if (!(event.packet instanceof GameMessageS2CPacket) || !chatFormatting.get()) return;
        Text message = ((GameMessageS2CPacket) event.packet).content();

        mc.inGameHud.getChatHud().addMessage(Text.literal("").setStyle(Style.EMPTY.withFormatting(getFormatting(formattingMode.get()))).append(message));
        event.cancel();
    }

    private Formatting getFormatting(ChatFormatting chatFormatting) {
        return switch (chatFormatting) {
            case Obfuscated -> Formatting.OBFUSCATED;
            case Bold -> Formatting.BOLD;
            case Strikethrough -> Formatting.STRIKETHROUGH;
            case Underline -> Formatting.UNDERLINE;
            case Italic -> Formatting.ITALIC;
        };
    }

    public enum ChatFormatting {
        Obfuscated, Bold, Strikethrough, Underline, Italic
    }
}
