package begad.mc.placeholders;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.regex.Matcher;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PlaceholderTest {
    private Placeholder placeholder1;
    private Placeholder placeholder2;
    private String testString1;
    private String replaceTestString1;
    private String outputTestString1;
    private String testString2;
    private String[] replaceTestStrings2;
    private String outputTestString2;
    private String testString3;
    private String outputTestString3;

    @BeforeAll
    public void setUp() {
        testString1 = "Test, test, TEST, Test case, will it fail?\n" +
                "Test, test.";
        replaceTestString1 = "test";
        outputTestString1 = "test, test, TEST, test case, will it fail?\n" +
                "test, test.";
        placeholder1 = new Placeholder("Test", "Test", (String input, Matcher matcher) -> input.replace("Test", replaceTestString1), 0);
        testString2 = "Welcome to Network\n" +
                "Players in hub: %bungee_hub:players%\n" +
                "Players in unknown: %bungee_a:players%\n" +
                "Players in kitpvp: %bungee_kitpvp:players%";
        replaceTestStrings2 = new String[]{"5", "120"};
        outputTestString2 = "Welcome to Network\n" +
                "Players in hub: 5\n" +
                "Players in unknown: Not Found\n" +
                "Players in kitpvp: 120";

        placeholder2 = new Placeholder("Test2", "%bungee_(.*):players%", (String input, Matcher matcher) -> {
            String output = input;
            if (matcher.find()) {
                String server = matcher.group(2);
                if (server.equals("hub")) {
                    output = output.replaceFirst("%bungee_" + server + ":players%", replaceTestStrings2[0]);
                } else if (server.equals("kitpvp")) {
                    output = output.replaceFirst("%bungee_" + server + ":players%", replaceTestStrings2[1]);
                } else {
                    output = output.replaceFirst("%bungee_" + server + ":players%", "Not Found");
                }
            }
            return output;
        }, 0);
        testString3 = "Welcome to Network\n" +
                "Players in kitpvp: %bungee_kitpvp:players%\n" +
                "Players in hub: %bungee_hub:players%\n" +
                "Players in unknown: %bungee_a:players%";
        outputTestString3 = "Welcome to Network\n" +
                "Players in kitpvp: 120\n" +
                "Players in hub: 5\n" +
                "Players in unknown: Not Found";

    }

    @AfterAll
    public void tearDown() {
        this.placeholder1 = null;
        this.placeholder2 = null;
        this.testString1 = null;
        this.testString2 = null;
        this.testString3 = null;
        this.replaceTestString1 = null;
        this.replaceTestStrings2 = null;
        this.outputTestString1 = null;
        this.outputTestString2 = null;
        this.outputTestString3 = null;
    }

    @Test
    public void oneMatch1() {
        assertTrue(placeholder1.oneMatch(testString1));
    }

    @Test
    public void replace1() {
        assertEquals(outputTestString1, placeholder1.replace(testString1));
    }

    @Test
    public void oneMatch2() {
        assertTrue(placeholder2.oneMatch(testString2));
    }

    @Test
    public void replace2() {
        assertEquals(outputTestString2, placeholder2.replace(testString2));
    }

    @Test
    public void oneMatch3() {
        assertTrue(placeholder2.oneMatch(testString3));
    }

    @Test
    public void replace3() {
        assertEquals(outputTestString3, placeholder2.replace(testString3));
    }
}