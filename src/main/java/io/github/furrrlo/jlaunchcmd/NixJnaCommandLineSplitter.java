package io.github.furrrlo.jlaunchcmd;

import com.sun.jna.*;
import com.sun.jna.Structure.FieldOrder;

/**
 * Do NOT use this except for testing, it expands shell variables
 */
class NixJnaCommandLineSplitter implements CommandLineSplitter {

    public NixJnaCommandLineSplitter() {
    }

    NixJnaCommandLineSplitter(@SuppressWarnings("unused") Void check) throws ClassNotFoundException, LinkageError {
        Class.forName(LibC.INSTANCE.getClass().getName());
        Class.forName(LibC.INSTANCE.getClass().getName());
    }

    @Override
    public String[] splitCommand(String cmd) {
        LibC.wordexp_t p = new LibC.wordexp_t();

        int err;
        if ((err = LibC.INSTANCE.wordexp(cmd, p, 0)) != 0) {
            /* If the error was WRDE_NOSPACE,  then perhaps part of the result was allocated.  */
            if(err == LibC.Errors.WRDE_NOSPACE)
                LibC.INSTANCE.wordfree(p);

            throw new RuntimeException("Failed to split commandline using wordexp: " + err);
        }

        try {
            return p.we_wordv.getStringArray(0, p.we_wordc.intValue());
        } finally {
            // Note that on some OSX versions this does not free all memory (10.9.5)
            LibC.INSTANCE.wordfree(p);
        }
    }

    /**
     * https://github.com/bminor/glibc/blob/master/posix/wordexp.h
     * https://opensource.apple.com/source/Libc/Libc-1439.141.1/include/wordexp.h.auto.html
     */
    private interface LibC extends com.sun.jna.platform.unix.LibC {

        LibC INSTANCE = Native.load(NAME, LibC.class);

        /** Number of null pointers to prepend to we_wordv. */
        int WRDE_DOOFFS = Platform.isMac() ? 0x02 : 1;
        /** Append words to those previously generated. */
        int WRDE_APPEND = Platform.isMac() ? 0x01 : (1 << 1);
        /** Fail if command substitution is requested. */
        int WRDE_NOCMD = (1 << 2);
        /** The pwordexp argument was passed to a previous successful call to wordexp(), and has not been passed to wordfree(). The result is the same as if the application had called wordfree() and then called wordexp() without WRDE_REUSE. */
        int WRDE_REUSE = (1 << 3);
        /** Do not redirect stderr to /dev/null. */
        int WRDE_SHOWERR = (1 << 4);
        /** Report error on an attempt to expand an undefined shell variable. */
        int WRDE_UNDEF = (1 << 5);

        class Errors {
            public static final int WRDE_NOSYS;
            /** Attempt to allocate memory failed. */
            public static final int WRDE_NOSPACE;
            /** One of the unquoted characters- <newline>, '|', '&', ';', '<', '>', '(', ')', '{', '}' - appears in words in an inappropriate context. */
            public static final int WRDE_BADCHAR;
            /** Reference to undefined shell variable when WRDE_UNDEF is set in flags. */
            public static final int WRDE_BADVAL;
            /** Command substitution requested when WRDE_NOCMD was set in flags. */
            public static final int WRDE_CMDSUB;
            /** Shell syntax error, such as unbalanced parentheses or unterminated string. */
            public static final int WRDE_SYNTAX;

            static {
                if(Platform.isMac()) {
                    WRDE_BADCHAR = 1;
                    WRDE_BADVAL = 2;
                    WRDE_CMDSUB = 3;
                    WRDE_NOSPACE = 4;
                    WRDE_NOSYS = 5;
                    WRDE_SYNTAX = 6;
                } else {
                    WRDE_NOSYS = -1;
                    WRDE_NOSPACE = 1;
                    WRDE_BADCHAR = 2;
                    WRDE_BADVAL = 3;
                    WRDE_CMDSUB = 4;
                    WRDE_SYNTAX = 5;
                }
            }
        }

        int wordexp(String s, wordexp_t p, int flags);

        void wordfree(wordexp_t p);

        @FieldOrder({ "we_wordc", "we_wordv", "we_offs" })
        class wordexp_t extends Structure implements Structure.ByReference {
            /** Count of words matched by words. */
            public size_t we_wordc;
            /** Pointer to list of expanded words. */
            public Pointer we_wordv;
            /** Slots to reserve at the beginning of we_wordv. */
            public size_t we_offs;
        }
    }
}
