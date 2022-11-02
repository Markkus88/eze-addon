package ARm8.addon.modules.misc;

import meteordevelopment.meteorclient.events.game.SendMessageEvent;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringListSetting;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.network.PlayerListEntry;

import java.util.List;

import ARm8.addon.Addon;

public class GroupChat extends Module {
    final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<List<String>> players = sgGeneral.add(new StringListSetting.Builder()
            .name("players")
            .description("Players to message.")
            .build()
    );

    private final Setting<String> command = sgGeneral.add(new StringSetting.Builder()
            .name("command")
            .description("How the message command is set up on the server.")
            .defaultValue("/msg {player} {message}")
            .build()
    );

    public GroupChat() {
        super(Addon.MISC, "group-chat", "Talk with people privately with /msg.");
    }

    @EventHandler
    private void onMessageSend(SendMessageEvent event) {
        for(String playerString: players.get()) {
            for(PlayerListEntry onlinePlayer: mc.getNetworkHandler().getPlayerList()) {
                if(onlinePlayer.getProfile().getName().equalsIgnoreCase(playerString)) {
                    ChatUtils.sendPlayerMsg(command.get().replace("{player}", onlinePlayer.getProfile().getName()).replace("{message}", event.message));
                    break;
                }
            }
        }

        event.cancel();
    }
}
