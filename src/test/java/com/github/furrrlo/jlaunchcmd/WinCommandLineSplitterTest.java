package com.github.furrrlo.jlaunchcmd;

import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class WinCommandLineSplitterTest {

    @ParameterizedTest
    @MethodSource("provideCommands")
    void batchSplitCommand(String cmd, String[] expected) {
        String[] actual;
        assertArrayEquals(
                expected,
                actual = WinCommandLineSplitter.doSplitCommand(cmd),
                String.format("Split wrong (command: '%s',\nexpected:\t'%s',\nactual:\t\t'%s' )", cmd, Arrays.toString(expected), Arrays.toString(actual)));
    }

    @ParameterizedTest
    @MethodSource("provideCommands")
    // On *nix, the above method covers all tests regardless,
    // as the provideCommands method is generated using data from here
    @EnabledOnOs({ OS.WINDOWS })
    void splitTheSameWay(String cmd) {
        String[] expected, actual;
        assertArrayEquals(
                expected = new WinJnaCommandLineSplitter().splitCommand(cmd),
                actual = WinCommandLineSplitter.doSplitCommand(cmd),
                String.format("Split wrong (command: '%s',\nexpected:\t'%s',\nactual:\t\t'%s' )", cmd, Arrays.toString(expected), Arrays.toString(actual)));
    }

    private static Stream<Arguments> provideCommands() {
        return Stream.of(
                // Weird quotes
                Arguments.of("\"Argument With Spaces\"", new String[] { "Argument With Spaces" }),
                Arguments.of("program.exe \"Argument With Spaces\"", new String[] { "program.exe","Argument With Spaces" }),
                Arguments.of("     program.exe \"Argument With Spaces\"       ", new String[] { "", "program.exe", "Argument With Spaces" }),
                Arguments.of("\"Argument With Spaces\"", new String[] { "Argument With Spaces" }),
                Arguments.of("program.exe \"Argument With Spaces\"", new String[] { "program.exe","Argument With Spaces" }),
                Arguments.of("     program.exe \"Argument With Spaces\"       ", new String[] { "", "program.exe", "Argument With Spaces" }),
                Arguments.of("Argument\" \"With\" \"Spaces", new String[] { "Argument\"","With","Spaces" }),
                Arguments.of("program.exe Argument\" \"With\" \"Spaces", new String[] { "program.exe","Argument With Spaces" }),
                Arguments.of("     program.exe Argument\" \"With\" \"Spaces       ", new String[] { "", "program.exe", "Argument With Spaces" }),
                Arguments.of("\"Argument \"With\" Spaces\"", new String[] { "Argument ","With Spaces" }),
                Arguments.of("program.exe \"Argument \"With\" Spaces\"", new String[] { "program.exe","Argument With Spaces" }),
                Arguments.of("     program.exe \"Argument \"With\" Spaces\"       ", new String[] { "", "program.exe", "Argument With Spaces" }),
                Arguments.of("Argument\" With Sp\"aces", new String[] { "Argument\"","With","Spaces" }),
                Arguments.of("program.exe Argument\" With Sp\"aces", new String[] { "program.exe","Argument With Spaces" }),
                Arguments.of("     program.exe Argument\" With Sp\"aces       ", new String[] { "", "program.exe", "Argument With Spaces" }),
                Arguments.of("\"Argument With Spaces", new String[] { "Argument With Spaces" }),
                Arguments.of("program.exe \"Argument With Spaces", new String[] { "program.exe","Argument With Spaces" }),
                Arguments.of("     program.exe \"Argument With Spaces       ", new String[] { "", "program.exe", "Argument With Spaces       " }),
                Arguments.of("\"Ar\"g\"um\"e\"n\"t\" W\"it\"h Sp\"aces\"\"", new String[] { "Ar","gument With Spaces" }),
                Arguments.of("program.exe \"Ar\"g\"um\"e\"n\"t\" W\"it\"h Sp\"aces\"\"", new String[] { "program.exe","Argument With Spaces" }),
                Arguments.of("     program.exe \"Ar\"g\"um\"e\"n\"t\" W\"it\"h Sp\"aces\"\"       ", new String[] { "", "program.exe", "Argument With Spaces" }),
                Arguments.of("\"Ar\\\"g\"um\"e\"n\"t\" W\"it\"h Sp\"aces\"\"", new String[] { "Ar\\","gument With Spaces" }),
                Arguments.of("program.exe \"Ar\\\"g\"um\"e\"n\"t\" W\"it\"h Sp\"aces\"\"", new String[] { "program.exe","Ar\"gument","With","Spaces\"" }),
                Arguments.of("     program.exe \"Ar\\\"g\"um\"e\"n\"t\" W\"it\"h Sp\"aces\"\"       ", new String[] { "", "program.exe", "Ar\"gument", "With", "Spaces\"" }),
                Arguments.of("\"Ar\\\"g\"um\"e\"n\"t\" W\"it\"h Sp\"aces\"\" \"", new String[] { "Ar\\","gument With Spaces","" }),
                Arguments.of("program.exe \"Ar\\\"g\"um\"e\"n\"t\" W\"it\"h Sp\"aces\"\" \"", new String[] { "program.exe","Ar\"gument","With","Spaces\"","" }),
                Arguments.of("     program.exe \"Ar\\\"g\"um\"e\"n\"t\" W\"it\"h Sp\"aces\"\" \"       ", new String[] { "", "program.exe", "Ar\"gument", "With", "Spaces\"", "       " }),
                // Escaping
                Arguments.of("test\\\\", new String[] { "test\\\\" }),
                Arguments.of("program.exe test\\\\", new String[] { "program.exe","test\\\\" }),
                Arguments.of("     program.exe test\\\\       ", new String[] { "", "program.exe", "test\\\\" }),
                Arguments.of("test\\\\\\", new String[] { "test\\\\\\" }),
                Arguments.of("program.exe test\\\\\\", new String[] { "program.exe","test\\\\\\" }),
                Arguments.of("     program.exe test\\\\\\       ", new String[] { "", "program.exe", "test\\\\\\" }),
                Arguments.of("test\\\\\\\"", new String[] { "test\\\\\\\"" }),
                Arguments.of("program.exe test\\\\\\\"", new String[] { "program.exe","test\\\"" }),
                Arguments.of("     program.exe test\\\\\\\"       ", new String[] { "", "program.exe", "test\\\"" }),
                Arguments.of("\"test\\\\", new String[] { "test\\\\" }),
                Arguments.of("program.exe \"test\\\\", new String[] { "program.exe","test\\\\" }),
                Arguments.of("     program.exe \"test\\\\       ", new String[] { "", "program.exe", "test\\\\       " }),
                Arguments.of("\"test\\\\\\", new String[] { "test\\\\\\" }),
                Arguments.of("program.exe \"test\\\\\\", new String[] { "program.exe","test\\\\\\" }),
                Arguments.of("     program.exe \"test\\\\\\       ", new String[] { "", "program.exe", "test\\\\\\       " }),
                Arguments.of("\"test\\\\\\\"", new String[] { "test\\\\\\" }),
                Arguments.of("program.exe \"test\\\\\\\"", new String[] { "program.exe","test\\\"" }),
                Arguments.of("     program.exe \"test\\\\\\\"       ", new String[] { "", "program.exe", "test\\\"       " }),
                // Escaped quotes
                Arguments.of(
                        "--userProperties \"{ \\\"preferredLanguage\\\" : [ \\\"en-us\\\" ], \\\"registrationCountry\\\" : [ \\\"GB\\\" ] }\"",
                        new String[] { "--userProperties","{ \"preferredLanguage\" : [ \"en-us\" ], \"registrationCountry\" : [ \"GB\" ] }" }
                ),
                Arguments.of(
                        "program.exe --userProperties \"{ \\\"preferredLanguage\\\" : [ \\\"en-us\\\" ], \\\"registrationCountry\\\" : [ \\\"GB\\\" ] }\"",
                        new String[] { "program.exe","--userProperties","{ \"preferredLanguage\" : [ \"en-us\" ], \"registrationCountry\" : [ \"GB\" ] }" }
                ),
                Arguments.of(
                        "     program.exe --userProperties \"{ \\\"preferredLanguage\\\" : [ \\\"en-us\\\" ], \\\"registrationCountry\\\" : [ \\\"GB\\\" ] }\"       ",
                        new String[] { "", "program.exe", "--userProperties", "{ \"preferredLanguage\" : [ \"en-us\" ], \"registrationCountry\" : [ \"GB\" ] }" }
                ),
                // Trailing space
                Arguments.of("test ", new String[] { "test" }),
                Arguments.of("program.exe test ", new String[] { "program.exe","test" }),
                Arguments.of("     program.exe test        ", new String[] { "", "program.exe", "test" }),
                Arguments.of("test  test", new String[] { "test","test" }),
                Arguments.of("program.exe test  test", new String[] { "program.exe","test","test" }),
                Arguments.of("     program.exe test  test       ", new String[] { "", "program.exe", "test", "test" })
        );
    }
}