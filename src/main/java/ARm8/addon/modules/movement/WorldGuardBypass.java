package ARm8.addon.modules.movement;

import net.minecraft.client.MinecraftClient;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.math.Vec3d;

public class WorldGuardBypass extends Module {
    public static ClientPlayNetworkHandler networkHandler;
    public static MinecraftClient mc = MinecraftClient.getInstance();
    public static int flyingTimer = 0;

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Integer> timer = sgGeneral.add(new IntSetting.Builder()
            .name("timer")
            .description("The timer for the bypass.")
            .sliderMin(0)
            .sliderMax(80)
            .defaultValue(30)
            .build()
    );

    private final Setting<Double> maxDelta = sgGeneral.add(new DoubleSetting.Builder()
            .name("max-delta")
            .description("The max delta for the bypass.")
            .sliderMin(0)
            .sliderMax(5)
            .defaultValue(0.05)
            .build()
    );

    public WorldGuardBypass() {
        super(Categories.Movement, "WorldGuardBypass", "Bypasses WorldGuard's denied \"entry\" flag");
    }

    public static boolean inSameBlock(Vec3d vector, Vec3d other) {
        return other.x >= Math.floor(vector.x) && other.x <= Math.ceil(vector.x) &&
                other.y >= Math.floor(vector.y) && other.y <= Math.ceil(vector.y) &&
                other.z >= Math.floor(vector.z) && other.z <= Math.ceil(vector.z);
    }

    @Override
    public void onActivate() {
        flyingTimer = 0;
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (++flyingTimer > timer.get().intValue()) {  // Max 80, to bypass "Flying is not enabled"
            networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY() - 0.04, mc.player.getZ(), mc.player.isOnGround()));
            flyingTimer = 0;  // Reset
        } else {
            mc.player.setVelocity(0, 0, 0);

            Vec3d vec = new Vec3d(0, 0, 0);

            // Key presses changing position
            if (mc.player.input.jumping) {  // Move up
                vec = vec.add(new Vec3d(0, 1, 0));
            } else if (mc.player.input.sneaking) {  // Move down
                vec = vec.add(new Vec3d(0, -1, 0));
            } else {
            // Horizontal movement (not at the same time as vertical)
                if (mc.player.input.pressingForward) {
                    vec = vec.add(new Vec3d(0, 0, 1));
                }
                if (mc.player.input.pressingRight) {
                    vec = vec.add(new Vec3d(1, 0, 0));
                }
                if (mc.player.input.pressingBack) {
                    vec = vec.add(new Vec3d(0, 0, -1));
                }
                if (mc.player.input.pressingLeft) {
                    vec = vec.add(new Vec3d(-1, 0, 0));
                }
            }

            if (vec.length() > 0) {
                vec = vec.normalize();  // Normalize to length 1

                if (!(vec.x == 0 && vec.z == 0)) {  // Rotate by looking yaw (won't change length)
                    double moveAngle = Math.atan2(vec.x, vec.z) + Math.toRadians(mc.player.getYaw() + 90);
                    double x = Math.cos(moveAngle);
                    double z = Math.sin(moveAngle);
                    vec = new Vec3d(x, vec.y, z);
                }

                vec = vec.multiply(maxDelta.get().doubleValue());  // Scale to maxDelta

                Vec3d newPos = new Vec3d(mc.player.getX() + vec.x, mc.player.getY() + vec.y, mc.player.getZ() + vec.z);
                // If able to add more without going over a block boundary, add more
                boolean extra = false;
                if (mc.options.sprintKey.isPressed()) {  // Trigger by sprinting
                    while (inSameBlock(newPos.add(vec.multiply(1.5)), new Vec3d(mc.player.prevX, mc.player.prevY, mc.player.prevZ))) {
                        newPos = newPos.add(vec);
                        extra = true;
                    }
                }

                mc.player.setPosition(newPos);

                // Send tiny movement so delta is small enough
                PlayerMoveC2SPacket.Full smallMovePacket = new PlayerMoveC2SPacket.Full(mc.player.getX(), mc.player.getY(), mc.player.getZ(), mc.player.getYaw(), mc.player.getPitch(), mc.player.isOnGround());
                networkHandler.getConnection().send(smallMovePacket);

                // Send far away packet for "moving too quickly!" to reset position
                if (!extra) {
                    PlayerMoveC2SPacket.Full farPacket = new PlayerMoveC2SPacket.Full(mc.player.getX() + 1337.0, mc.player.getY() + 1337.0,
                    mc.player.getZ() + 1337.0, mc.player.getYaw(), mc.player.getPitch(), mc.player.isOnGround());
                    networkHandler.getConnection().send(farPacket);
                }
            }
        }
    }
}
