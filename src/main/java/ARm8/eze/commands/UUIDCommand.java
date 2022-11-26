package ARm8.eze.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import ARm8.eze.commands.arguments.PlayerArgumentType;
import meteordevelopment.meteorclient.systems.commands.Command;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.PlayerEntity;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class UUIDCommand extends Command {
    public UUIDCommand() {
        super("uuid", "Returns a players uuid.");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            info("Your UUID is " + mc.player.getUuid().toString());

            return SINGLE_SUCCESS;
        });

        builder.then(argument("player", PlayerArgumentType.player()).executes(context -> {
            PlayerEntity player = PlayerArgumentType.getPlayer(context, "player");

            if (player != null) {
                info(player.getEntityName() + "'s UUID is " + player.getUuid().toString());
            }

            return SINGLE_SUCCESS;
        }));
    }
}
