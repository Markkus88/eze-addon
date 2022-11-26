package ARm8.addon.mixins;

import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.encryption.NetworkEncryptionException;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginKeyC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginQueryResponseC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import ARm8.addon.modules.exploits.CorruptLoginPacket;
import ARm8.addon.modules.exploits.CorruptLoginPacket.Packets;
import ARm8.addon.modules.movement.PacketDigits;

import java.util.Queue;

@Mixin(ClientConnection.class)
public abstract class ClientConnectionMixin {
    @Shadow public abstract boolean isOpen();
    @Shadow protected abstract void sendQueuedPackets();
    @Shadow protected abstract void sendImmediately(Packet<?> packet, @Nullable PacketCallbacks callbacks);

    @Shadow @Final private Queue<ClientConnection.QueuedPacket> packetQueue;

    @Inject(method = "send(Lnet/minecraft/network/Packet;Lnet/minecraft/network/PacketCallbacks;)V", at = @At("HEAD"), cancellable = true)
    private void onSend(Packet<?> packet, PacketCallbacks callbacks, CallbackInfo info) throws NetworkEncryptionException {
        PacketDigits digits = Modules.get().get(PacketDigits.class);
        MinecraftClient mc = MinecraftClient.getInstance();

        if (digits.isActive()) {
            if (packet instanceof PlayerMoveC2SPacket.PositionAndOnGround move) {
                PlayerMoveC2SPacket.PositionAndOnGround modified = new PlayerMoveC2SPacket.PositionAndOnGround(
                        digits.round(move.getX(0)),
                        digits.shouldModifyY() ? digits.round(move.getY(0)) : move.getY(0),
                        digits.round(move.getZ(0)),

                        move.isOnGround()
                );

                info.cancel();

                if (this.isOpen()) {
                    this.sendQueuedPackets();
                    this.sendImmediately(modified, callbacks);
                } else {
                    this.packetQueue.add(new ClientConnection.QueuedPacket(modified, callbacks));
                }
            } else if (packet instanceof PlayerMoveC2SPacket.Full move) {
                PlayerMoveC2SPacket.Full modified = new PlayerMoveC2SPacket.Full(
                        digits.round(move.getX(0)),
                        digits.shouldModifyY() ? digits.round(move.getY(0)) : move.getY(0),
                        digits.round(move.getZ(0)),

                        move.getYaw(mc.player.getYaw()),
                        move.getPitch(mc.player.getPitch()),
                        move.isOnGround()
                );

                info.cancel();

                if (this.isOpen()) {
                    this.sendQueuedPackets();
                    this.sendImmediately(modified, callbacks);
                } else {
                    this.packetQueue.add(new ClientConnection.QueuedPacket(modified, callbacks));
                }
            }

            if (packet instanceof VehicleMoveC2SPacket move) {
                BoatEntity entity = new BoatEntity(EntityType.BOAT, mc.world);

                entity.setPos(
                        digits.round(move.getX()),
                        digits.shouldModifyY() ? digits.round(move.getY()) : move.getY(),
                        digits.round(move.getZ())
                );

                entity.setYaw(move.getYaw());
                entity.setPitch(move.getPitch());

                VehicleMoveC2SPacket modified = new VehicleMoveC2SPacket(entity);

                info.cancel();

                if (this.isOpen()) {
                    this.sendQueuedPackets();
                    this.sendImmediately(modified, callbacks);
                } else {
                    this.packetQueue.add(new ClientConnection.QueuedPacket(modified, callbacks));
                }
            }
        }

        if (Modules.get().get(CorruptLoginPacket.class).isActive()) {
            if (Modules.get().get(CorruptLoginPacket.class).Packet.get() == Packets.LoginHelloC2SPacket && packet instanceof LoginHelloC2SPacket login) {
                LoginHelloC2SPacket modified = new LoginHelloC2SPacket(null);

                info.cancel();

                if (this.isOpen()) {
                    this.sendQueuedPackets();
                    this.sendImmediately(modified, callbacks);
                } else {
                    this.packetQueue.add(new ClientConnection.QueuedPacket(modified, callbacks));
                }
            } else if (Modules.get().get(CorruptLoginPacket.class).Packet.get() == Packets.LoginKeyC2SPacket && packet instanceof LoginKeyC2SPacket login) {
                LoginKeyC2SPacket modified = new LoginKeyC2SPacket(null, null, null);

                info.cancel();

                if (this.isOpen()) {
                    this.sendQueuedPackets();
                    this.sendImmediately(modified, callbacks);
                } else {
                    this.packetQueue.add(new ClientConnection.QueuedPacket(modified, callbacks));
                }
            } else if (Modules.get().get(CorruptLoginPacket.class).Packet.get() == Packets.LoginQueryResponseC2SPacket && packet instanceof LoginQueryResponseC2SPacket login) {
                LoginQueryResponseC2SPacket modified = new LoginQueryResponseC2SPacket(null);

                info.cancel();

                if (this.isOpen()) {
                    this.sendQueuedPackets();
                    this.sendImmediately(modified, callbacks);
                } else {
                    this.packetQueue.add(new ClientConnection.QueuedPacket(modified, callbacks));
                }
            }
        }
    }
}