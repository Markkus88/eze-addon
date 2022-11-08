package ARm8.addon.modules.world;

import meteordevelopment.meteorclient.events.game.GameJoinedEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.s2c.play.WorldBorderInitializeS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldBorderSizeChangedS2CPacket;

public class NoWorldBorder extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    // General

    private final Setting<Boolean> initialize = sgGeneral.add(new BoolSetting.Builder()
            .name("initialize")
            .description("Cancels the WorldBorderInitializeS2CPacket.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> size = sgGeneral.add(new BoolSetting.Builder()
            .name("size")
            .description("Cancels the WorldBorderSizeChangedS2CPacket.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> modify = sgGeneral.add(new BoolSetting.Builder()
            .name("modify")
            .description("Modifies the world-border to behave like the vanilla border.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> information = sgGeneral.add(new BoolSetting.Builder()
            .name("info")
            .description("Informs you when world-border actions happen.")
            .defaultValue(true)
            .build()
    );

    private int timer;

    // Constructor

    public NoWorldBorder() {
        super(Categories.World, "no-world-border", "Removes certain aspects of the world border.");
    }

    // Overrides

    @Override
    public void onActivate() {
        timer = 0;
    }

    // Events to modify the World Border

    @EventHandler
    private void onPreTick(TickEvent.Pre event) {
        if (timer != -1 && modify.get()) {
            if (timer > 20) {
                mc.world.getWorldBorder().setSize(Integer.MAX_VALUE);
                mc.world.getWorldBorder().setCenter(0, 0);
                timer = -1;
            } else {
                timer++;
            }
        }
    }

    @EventHandler
    private void onGameJoined(GameJoinedEvent event) {
        if (modify.get()) {
            mc.world.getWorldBorder().setSize(Integer.MAX_VALUE);
            mc.world.getWorldBorder().setCenter(0, 0);
        }
    }

    // Receive Packet Event

    @EventHandler
    private void onReceivePacket(PacketEvent.Receive event) {
        if (event.packet instanceof WorldBorderInitializeS2CPacket packet && initialize.get()) {
            if (information.get()) info("WorldBorderInitializeS2CPacket: [size=" + packet.getSize() + "; [" + packet.getCenterX() + ", " + packet.getCenterZ() + "]]");
            event.cancel();
        }

        if (event.packet instanceof WorldBorderSizeChangedS2CPacket packet && size.get()) {
            if (information.get()) info("WorldBorderSizeChangedS2CPacket: [size=" + packet.getSizeLerpTarget() + "]");
            event.cancel();
        }
    }
}