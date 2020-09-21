package begad.mc.bc.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class Command extends net.md_5.bungee.api.plugin.Command implements TabExecutor {
    protected int args;
    protected String requiredArgsMessage;

    public Command(String name) {
        super(name);
        this.args = 0;
        this.requiredArgsMessage = null;
    }

    public Command(String name, int args, String requiredArgsMessage) {
        super(name);
        this.args = args;
        this.requiredArgsMessage = requiredArgsMessage;
    }

    public Command(String name, String permission, String... aliases) {
        super(name, permission, aliases);
        this.args = 0;
        this.requiredArgsMessage = null;
    }

    public Command(String name, String permission, int args, String requiredArgsMessage, String... aliases) {
        super(name, permission, aliases);
        this.args = args;
        this.requiredArgsMessage = requiredArgsMessage;
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        if (strings.length < this.args) {
            commandSender.sendMessage(new TextComponent(this.requiredArgsMessage));
        } else {
            run(commandSender, new ArrayList<>(Arrays.asList(strings)));
        }
    }

    public abstract void run(CommandSender commandSender, ArrayList<String> args);
}
