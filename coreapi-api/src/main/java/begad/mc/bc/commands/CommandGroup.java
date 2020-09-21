package begad.mc.bc.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.*;

public abstract class CommandGroup extends Command implements TabExecutor {
    private final boolean executable;
    private final String notFound;
    protected Map<String, Command> commands = new HashMap<>();

    public CommandGroup(String name, String permission, String requiredArgsMessage, String notFoundMessage, boolean executable, String... aliases) {
        super(name, permission, aliases);
        this.requiredArgsMessage = requiredArgsMessage;
        this.executable = executable;
        this.notFound = notFoundMessage;
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        if (strings.length == 0) {
            if (executable) {
                run(commandSender, new ArrayList<>(Arrays.asList(strings)));
            } else {
                commandSender.sendMessage(new TextComponent(this.requiredArgsMessage));
            }
        } else {
            Command command = commands.get(strings[0]);
            if (command != null) {
                ArrayList<String> args = new ArrayList<>(Arrays.asList(strings));
                args.remove(0);
                command.run(commandSender, args);
            } else {
                commandSender.sendMessage(new TextComponent(this.notFound));
            }
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender commandSender, String[] arg) {
        ArrayList<String> args = new ArrayList<>(Arrays.asList(arg));

        ArrayList<String> matches = new ArrayList<>();
        if (args.size() == 0) {
            return matches;
        } else {
            switch (args.size()) {
                case 1: {
                    for (String name : commands.keySet()) {
                        if (name.startsWith(args.get(0))) {
                            matches.add(name);
                        }
                    }
                    break;
                }
                case 2: {
                    Command command = commands.get(args.get(0));
                    if (command != null) {
                        args.remove(0);
                        String[] arr = new String[args.size()];
                        arr = args.toArray(arr);
                        matches = new ArrayList<>((Collection<? extends String>) command.onTabComplete(commandSender, arr));
                    }
                    break;
                }
            }
        }
        return matches;
    }
}
