package io.github.furrrlo.jlaunchcmd;

import java.util.ArrayList;
import java.util.List;

class NixCommandLineSplitter implements CommandLineSplitter {

    private static final char SEPARATOR_CHAR = ' ';
    private static final char ESCAPE_CHAR = '\\';
    private static final char SINGLE_QUOTE_CHAR = '\'';
    private static final char DOUBLE_QUOTE_CHAR = '"';

    @Override
    public String[] splitCommand(String cmd) {
        return bashSplitCommand(cmd);
    }

    /*
     * Based on the Bash manual
     * See <a href="http://www.gnu.org/software/bash/manual/html_node/Quoting.html#Quoting">the bash manual</a>
     */
    static String[] bashSplitCommand(String cmd) {
        final List<String> args = new ArrayList<>();
        StringBuilder currArg = new StringBuilder();

        boolean isEscaped = false;
        boolean isInSingleQuotes = false;
        boolean isInDoubleQuotes = false;

        for(char c : cmd.toCharArray()) {

            if(isInSingleQuotes) {

                // A single quote may not occur between single quotes,
                // even when preceded by a backslash
                // So fuck the escape

                if(c == SINGLE_QUOTE_CHAR)
                    isInSingleQuotes = false;
                else
                    currArg.append(c);

            } else if(isInDoubleQuotes) {

                if(isEscaped) {

                    // The backslash retains its special meaning only when followed
                    // by one of the following characters: '"’, ‘\’
                    // Backslashes that are followed by one of these characters are removed
                    // Backslashes preceding characters without a special meaning are left unmodified

                    if(c == ESCAPE_CHAR)
                        currArg.append(ESCAPE_CHAR);
                    else if(c == DOUBLE_QUOTE_CHAR)
                        currArg.append(DOUBLE_QUOTE_CHAR);
                    else {
                        currArg.append(ESCAPE_CHAR);
                        currArg.append(c);
                    }

                    isEscaped = false;
                } else if(c == ESCAPE_CHAR) {
                    isEscaped = true;
                } else if(c == DOUBLE_QUOTE_CHAR) {
                    isInDoubleQuotes = false;
                } else {
                    currArg.append(c);
                }

            } else if(isEscaped) {
                currArg.append(c);
                isEscaped = false;
            } else {
                if(c == ESCAPE_CHAR) {
                    isEscaped = true;
                } else if(c == SINGLE_QUOTE_CHAR) {
                    isInSingleQuotes = true;
                } else if(c == DOUBLE_QUOTE_CHAR) {
                    isInDoubleQuotes = true;
                } else if(c == SEPARATOR_CHAR) {
                    args.add(currArg.toString());
                    currArg = new StringBuilder();
                } else {
                    currArg.append(c);
                }
            }
        }

        args.add(currArg.toString());
        return args.toArray(new String[0]);
    }
}
