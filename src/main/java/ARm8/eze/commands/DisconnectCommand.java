package ARm8.eze.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.systems.commands.Command;
import net.minecraft.command.CommandSource;
import net.minecraft.network.packet.s2c.play.DisconnectS2CPacket;
import net.minecraft.text.Text;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class DisconnectCommand extends Command {
    public DisconnectCommand() {
        super("disconnect", "Leaves the server...", "leave");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(thing -> {
            mc.player.networkHandler.onDisconnect(new DisconnectS2CPacket(Text.of("Disconnected by Command.")));

            return SINGLE_SUCCESS;
        });
    }
}
