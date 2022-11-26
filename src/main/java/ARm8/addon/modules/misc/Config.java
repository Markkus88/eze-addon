package ARm8.addon.modules.misc;

import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
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
    public final SettingGroup sgChat = settings.createGroup("Chat");

    // Chat

    public final Setting<Boolean> ezePrefix = sgChat.add(new BoolSetting.Builder().name("eze-prefix").description("Replaces Meteor prefix with ez prefix.").defaultValue(true).build());
    public final Setting<Boolean> chatFormatting = sgChat.add(new BoolSetting.Builder().name("chat-formatting").description("Changes style of messages.").defaultValue(false).build());
    private final Setting<ChatFormatting> formattingMode = sgChat.add(new EnumSetting.Builder<ChatFormatting>().name("mode").description("The style of messages.").defaultValue(ChatFormatting.Bold).visible(chatFormatting::get).build());

    public Config() {
        super(Categories.Misc, "config", "Configuration of eze");
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
