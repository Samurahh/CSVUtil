package com.spartaglobal.io;

import java.io.*;

public class ValueReader implements AutoCloseable {

    private final BufferedReader reader;
    private final char[] lineDelimiter;
    private final char[] valueDelimiter;
    private boolean eof;
    private boolean eol;

    public ValueReader(File file, char[] lineDelimiter, char[] valueDelimiter) throws FileNotFoundException {
        this.reader = new BufferedReader(new FileReader(file));
        this.lineDelimiter = lineDelimiter;
        this.valueDelimiter = valueDelimiter;
    }

    // reads until valueDelimiter or lineDelimiter - implemented quotes
    public String nextValue() throws IOException {
        if (eof) {
            throw new EOFException();
        } else if (eol) {
            eol = false;
        }
        StringBuilder s = new StringBuilder();
        int iChr;
        iChr = reader.read();
        boolean quoted = false;
        boolean escape = false;
        if (iChr != -1) {
            if (iChr == '"') {
                quoted = true;
                escape = true;
            } else {
                s.append((char) iChr);
            }
        }
        while (!eof) {
            iChr = reader.read();
            if (iChr == -1) {
                eof = true;
                eol = true;
                break;
            }
            if (iChr == '"') {
                if (escape) {
                    quoted = escape = false;
                    s.append((char) iChr);
                } else {
                    if(quoted){
                        quoted= false;
                        escape = true;
                    }else{
                        quoted = escape = true;
                    }
                }
            } else {
                if (escape) {
                    escape = false;
                }
                s.append((char) iChr);
            }
            if (!quoted) {
                // value delimiter
                if (check(s, valueDelimiter)) {
                    s.delete(s.length() - valueDelimiter.length, s.length());
                    break;
                }
                // line delimiter
                if (check(s, lineDelimiter)) {
                    s.delete(s.length() - lineDelimiter.length, s.length());
                    eol = true;
                    break;
                }
            }
        }
        return s.toString();
    }

    private boolean check(StringBuilder s, char[] endValues) {
        if (s.length() < endValues.length) {
            return false;
        }
        for (int i = endValues.length - 1; i >= 0; i--) {
            if (s.charAt(s.length() - (endValues.length - i)) != endValues[i]) {
                return false;
            }
        }
        return true;
    }

    // returns false if End of file has been reached
    public boolean hasNext() {
        return !eof;
    }

    // returns true if End of line has been reached
    public boolean isEOL() {
        return eol;
    }

    @Override
    public void close() throws Exception {
        reader.close();
    }
}
