package begad.mc.placeholders;

import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Placeholder {
    public final String name;
    protected final Pattern pattern;
    protected BiFunction<String, Matcher, String> replacer;

    public Placeholder(String name, String regex, BiFunction<String, Matcher, String> function, int flags) {
        this.name = name;
        this.pattern = Pattern.compile("(" + regex + ")", flags);
        this.replacer = function;
    }

    protected Placeholder(String name, String regex, int flags) {
        this.name = name;
        this.pattern = Pattern.compile("(" + regex + ")", flags);
    }

    public boolean oneMatch(String input) {
        return this.pattern.matcher(input).find();
    }

    public String replace(String input) {
        if (oneMatch(input)) {
            if (replacer != null) {
                String output = input;
                Matcher matcher = pattern.matcher(output);
                while (matcher.find()) {
                    String matched = matcher.group(1);
                    String replaced = replacer.apply(matched, pattern.matcher(matched));
                    // This will not work, but will have more performance, I could use matcher.reset(output) but will reset everything
                    // If you have any solutions open an issue
                    // output = new StringBuilder(output).replace(matcher.start(1), matcher.end(1), replaced).toString();
                    output = new StringBuilder(output).replace(matcher.start(1) - (matcher.regionEnd() - output.length()), matcher.end(1) - (matcher.regionEnd() - output.length()), replaced).toString();
                }
                return output;
            } else {
                return input;
            }
        } else {
            return input;
        }
    }
}
