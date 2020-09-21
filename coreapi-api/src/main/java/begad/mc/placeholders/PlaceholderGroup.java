package begad.mc.placeholders;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

public class PlaceholderGroup extends Placeholder {
    private final List<Placeholder> placeholders = new ArrayList<>();

    public PlaceholderGroup(String name, String regex, int flags) {
        super(name, regex, flags);
        /* this.replacer = (String input) -> {
            String output;
            for (Placeholder placeholder : placeholders) {
                if (placeholder.oneMatch(input)) {
                    String got = placeholder.replace(input);
                    StringBuilder builder = new StringBuilder(input);
                    builder.replace(matcher.start(1), matcher.end(1), got);
                    input = builder.toString();
                }
            }
            return input;
        }; */
    }

    public void addPlaceholder(Placeholder placeholder) {
        if (placeholders.contains(placeholder)) {
            return;
        }
        placeholders.add(placeholder);
    }

    @Override
    public String replace(String input) {
        if (oneMatch(input)) {
            String output = input;
            Matcher matcher = pattern.matcher(output);
            while (matcher.find()) {
                String matched = matcher.group(1);
                for (Placeholder placeholder : placeholders) {
                    if (placeholder.oneMatch(matched)) {
                        String replaced = placeholder.replace(matched);
                        output = new StringBuilder(output).replace(matcher.start(1) - (matcher.regionEnd() - output.length()), matcher.end(1) - (matcher.regionEnd() - output.length()), replaced).toString();
                    }
                }
            }
            return output;
        } else {
            return input;
        }
    }
}
