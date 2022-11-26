package ARm8.eze.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.systems.commands.Command;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.command.CommandSource;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class SoftLeaveCommand extends Command {
    public SoftLeaveCommand() {
        super("soft-leave", "Returns to the menu without disconnecting from the server.");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            mc.disconnect(new TitleScreen());
            return SINGLE_SUCCESS;
        });
    }
}
