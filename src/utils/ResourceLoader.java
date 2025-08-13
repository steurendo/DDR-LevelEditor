package utils;

import java.io.InputStream;

public class ResourceLoader {
    public static InputStream load(Object context, String filename) {
        InputStream stream = context.getClass().getClassLoader().getResourceAsStream(filename);
        if (stream == null) throw new IllegalArgumentException(filename + " is not found");
        return stream;
    }
}
