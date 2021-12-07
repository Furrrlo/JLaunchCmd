package io.github.furrrlo.jlaunchcmd;

import java.util.ArrayList;
import java.util.List;

class WinCommandLineSplitter implements CommandLineSplitter {

    @Override
    public String[] splitCommand(String cmd) {
        return doSplitCommand(cmd);
    }

    /**
     * Splits command as the Windows command line would
     * See <a>https://daviddeley.com/autohotkey/parameters/parameters.htm#WINARGV</a>
     * See <a>https://docs.microsoft.com/en-us/cpp/cpp/main-function-command-line-args?redirectedfrom=MSDN&view=msvc-160</a>
     * See <a>https://docs.microsoft.com/en-us/windows/win32/api/shellapi/nf-shellapi-commandlinetoargvw</a>
     */
    static String[] doSplitCommand(String cmd) {
        final List<String> res = new ArrayList<>();

        boolean inQuotesMode = false;
        boolean isProgramName = true;
        boolean first = true;
        StringBuilder currStr = new StringBuilder();

        // If lpCmdLine starts with any amount of whitespace, CommandLineToArgvW will consider
        // the first argument to be an empty string.
        if(cmd.startsWith(" ")) {
            res.add("");
            isProgramName = false;
        }

        final char[] arr = cmd.toCharArray();
        for (int idx = 0; idx < arr.length; idx++) {
            char c = arr[idx];
            switch (c) {
                case '\\':
                    // Slashes in program name have no special behavior
                    if(isProgramName) {
                        currStr.append('\\');
                        break;
                    }

                    // Count slashes
                    int n;
                    //noinspection StatementWithEmptyBody
                    for (n = 0; idx < arr.length && arr[idx] == '\\'; n++, idx++);

                    boolean isNextCharQuote = idx < arr.length && arr[idx] == '"';
                    // Quotes get processed here, the rest of the characters need to be processed by the outer loop
                    if(!isNextCharQuote) idx--;

                    // If the next character is not a quotation mark, treat slashes as normal characters
                    if (!isNextCharQuote) {
                        for (int i = 0; i < n; i++)
                            currStr.append('\\');
                        break;
                    }
                    // If n = 2x backslashes followed by a quotation mark produce n/2 backslashes followed by begin/end quote.
                    if (n % 2 == 0) {
                        for (int i = 0; i < n / 2; i++)
                            currStr.append('\\');
                        inQuotesMode = !inQuotesMode;
                    }
                    // If n = 2x + 1 backslashes followed by a quotation mark produce n/2 backslashes followed by a
                    // quotation mark literal ("). This does not toggle the "in quotes" mode.
                    for (int i = 0; i < n / 2; i++)
                        currStr.append('\\');
                    currStr.append('"');
                    break;
                case '"':
                    if (isProgramName && first) {
                        inQuotesMode = true;
                        break;
                    }

                    if (isProgramName) {
                        // If there is an ending quote, but no starting quote, it gets appended in the program name
                        if (!inQuotesMode)
                            currStr.append('"');
                        if (currStr.length() > 0)
                            res.add(currStr.toString());
                        currStr = new StringBuilder();

                        isProgramName = false;
                        inQuotesMode = false;
                        break;
                    }

                    inQuotesMode = !inQuotesMode;
                    // undocumented rule, see "Prior to 2008" rule from
                    // http://daviddeley.com/autohotkey/parameters/parameters.htm section 5.2
                    if(!inQuotesMode && idx + 1 < arr.length && arr[idx + 1] == '"') {
                        currStr.append('"');
                        idx++;
                    }
                    break;
                default:
                    if (c == ' ' && !inQuotesMode) {
                        if (currStr.length() > 0)
                            res.add(currStr.toString());
                        currStr = new StringBuilder();
                        isProgramName = false;
                        break;
                    }

                    currStr.append(c);
            }

            first = false;
        }

        if (currStr.length() > 0 || inQuotesMode)
            res.add(currStr.toString());
        return res.toArray(new String[0]);
    }
}
