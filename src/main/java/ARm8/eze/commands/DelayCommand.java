package ARm8.eze.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.systems.commands.Command;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.minecraft.command.CommandSource;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class DelayCommand extends Command{
    public DelayCommand() {
        super ("delay", "Delays the command by a certain amount of time.");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("delay", IntegerArgumentType.integer())
                .then(argument("command", StringArgumentType.string())).executes(context -> {
            Integer delay = IntegerArgumentType.getInteger(context, "delay");
            try {
                Thread.sleep(delay);
                ChatUtils.sendPlayerMsg(String.valueOf(StringArgumentType.getString(context, "command")));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return SINGLE_SUCCESS;
        }));
    }
}
