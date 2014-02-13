package net.robyf.dbpatcher.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;

public final class ScriptParser {
    
    private static final String DEFAULT_DELIMITER = ";";
    private static final String COMMENT_START = "--";
    private static final String DELIMITER_CHANGE_START = "--DELIMITER";

    private ScriptParser() {
    }
    
    public static List<String> parse(final File script) {
        return ScriptParser.parse(script, Charset.forName("ISO-8859-1"));
    }

    public static List<String> parse(final File script, final Charset charset) {
        String delimiter = DEFAULT_DELIMITER;
        StringBuilder builder = new StringBuilder();
        try {
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(new FileInputStream(script),
                                                             charset));
            boolean firstLine = true;
            String line = reader.readLine();
            while (line != null) {
                if (firstLine) {
                    if (line.trim().startsWith(DELIMITER_CHANGE_START)) {
                        int start = line.indexOf("\"") + 1;
                        int end = line.indexOf("\"", start);
                        delimiter = line.substring(start, end);
                    }
                    firstLine = false;
                }
                if (!"".equals(line.trim()) && !line.trim().startsWith(COMMENT_START)) {
                    builder.append(line).append(" ");
                }
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException ioe) {
            throw new ParsingException("Error reading script file", ioe);
        }

        List<String> result = new LinkedList<String>();
        for (String fragment : builder.toString().split(delimiter)) {
            if (!"".equals(fragment.trim())) {
                result.add(fragment.trim());
            }
        }
        
        return result;
    }

}
