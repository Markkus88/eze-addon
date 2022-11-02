package ARm8.addon.modules.misc;

import ARm8.addon.Addon;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;

public class AntiScreen extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    // General

    private final Setting<Boolean> endScreen = sgGeneral.add(new BoolSetting.Builder()
            .name("end-screen")
            .description("Removes the end screen after finishing the game.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> demoScreen = sgGeneral.add(new BoolSetting.Builder()
            .name("demo-screen")
            .description("Removes the demo screen.")
            .defaultValue(true)
            .build()
    );

    public AntiScreen() {
        super(Addon.MISC, "anti-screen", "Removes certain screens in the game.");
    }

    @EventHandler
    private void onReceivePacket(PacketEvent.Receive event) {
        if (event.packet instanceof GameStateChangeS2CPacket packet) {
            if (packet.getReason() == GameStateChangeS2CPacket.DEMO_MESSAGE_SHOWN && demoScreen.get() || packet.getReason() == GameStateChangeS2CPacket.GAME_WON && endScreen.get()) {
                event.cancel();
            }
        }
    }
}