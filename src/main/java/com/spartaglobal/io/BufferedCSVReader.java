package com.spartaglobal.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;

public class BufferedCSVReader implements AutoCloseable {

    private final ValueReader valueReader;
    private String[] buffer;

    public BufferedCSVReader(File file, String lineDelimiter, String valueDelimiter) throws FileNotFoundException {
        this.valueReader = new ValueReader(file, lineDelimiter.toCharArray(), valueDelimiter.toCharArray());
    }

    public String[] nextLine() throws IOException {
        if (buffer == null) {
            buffer = readLine();
        }
        String[] temp = buffer;
        buffer = null;
        return temp;
    }

    public boolean hasNext() {
        if (buffer == null) {
            try {
                buffer = readLine();
            } catch (IOException e) {
                return false;
            }
        }
        return buffer != null;
    }

    private String[] readLine() throws IOException {
        LinkedList<String> values = new LinkedList<>();
        String value;
        while (valueReader.hasNext()) {
            values.add(valueReader.nextValue());
            if (valueReader.isEOL()) {
                break;
            }
        }
        if (values.isEmpty()) {
            return null;
        }
        return values.toArray(new String[0]);
    }


    @Override
    public void close() throws Exception {
        valueReader.close();
    }
}
