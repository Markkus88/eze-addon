package ARm8.addon.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.systems.commands.Command;
import net.minecraft.command.CommandSource;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

import com.mojang.brigadier.arguments.IntegerArgumentType;

public class SleepCommand extends Command {
    public SleepCommand() {
        super("sleep", "Freezes game for x amount of miliseconds.", "freeze");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("delay", IntegerArgumentType.integer()).executes(context -> {
            Integer delay = IntegerArgumentType.getInteger(context, "delay");
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return SINGLE_SUCCESS;
        }));
    }
}