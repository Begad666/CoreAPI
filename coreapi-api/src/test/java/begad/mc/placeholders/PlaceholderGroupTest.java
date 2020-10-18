package begad.mc.placeholders;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PlaceholderGroupTest {
    private PlaceholderGroup placeholderGroup;
    private String testString1;
    private String outputTestString1;
    private String testString2;
    private String outputTestString2;

    @BeforeAll
    public void setUp() {
        placeholderGroup = new PlaceholderGroup("Test", "%bungee_([1-Z]+):([A-Z]+)%", Pattern.CASE_INSENSITIVE);
        placeholderGroup.addPlaceholder(new Placeholder("TestPlayers", "%bungee_(.*):players%", (String input, Matcher matcher) -> {
            String output = input;
            if (matcher.find()) {
                String server = matcher.group(2);
                if (server.equals("hub")) {
                    output = output.replaceFirst("%bungee_" + server + ":players%", "5");
                } else if (server.equals("kitpvp")) {
                    output = output.replaceFirst("%bungee_" + server + ":players%", "120");
                } else {
                    output = output.replaceFirst("%bungee_" + server + ":players%", "Not Found");
                }
            }
            return output;
        }, 0));
        placeholderGroup.addPlaceholder(new Placeholder("TestName", "%bungee_(.*):name%", (String input, Matcher matcher) -> {
            String output = input;
            // Please its just a example, don't take it seriously ;)
            if (matcher.find()) {
                String server = matcher.group(2);
                if (server.equals("hub")) {
                    output = output.replaceFirst("%bungee_" + server + ":name%", "Ultimate hub");
                } else if (server.equals("kitpvp")) {
                    output = output.replaceFirst("%bungee_" + server + ":name%", "Kit master");
                } else {
                    output = output.replaceFirst("%bungee_" + server + ":name%", "Unknown");
                }
            }
            return output;
        }, 0));
        testString1 = "Welcome to Network\n" +
                "Players in %bungee_hub:name%: %bungee_hub:players%\n" +
                "Players in %bungee_a:name%: %bungee_a:players%\n" +
                "Players in %bungee_kitpvp:name%: %bungee_kitpvp:players%";
        outputTestString1 = "Welcome to Network\n" +
                "Players in Ultimate hub: 5\n" +
                "Players in Unknown: Not Found\n" +
                "Players in Kit master: 120";
        testString2 = "Welcome to Network\n" +
                "Players in %bungee_kitpvp:name%: %bungee_kitpvp:players%\n" +
                "Players in %bungee_hub:name%: %bungee_hub:players%\n" +
                "Players in %bungee_a:name%: %bungee_a:players%";
        outputTestString2 = "Welcome to Network\n" +
                "Players in Kit master: 120\n" +
                "Players in Ultimate hub: 5\n" +
                "Players in Unknown: Not Found";
    }

    @AfterAll
    public void tearDown() {
        placeholderGroup = null;
        testString1 = null;
        outputTestString1 = null;
        testString2 = null;
        outputTestString2 = null;
    }

    @Test
    public void oneMatch1() {
        assertTrue(placeholderGroup.oneMatch(testString1));
    }

    @Test
    public void oneMatch2() {
        assertTrue(placeholderGroup.oneMatch(testString2));
    }

    @Test
    public void replace1() {
        assertEquals(outputTestString1, placeholderGroup.replace(testString1));
    }

    @Test
    public void replace2() {
        assertEquals(outputTestString2, placeholderGroup.replace(testString2));
    }
}