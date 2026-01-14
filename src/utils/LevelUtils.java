package utils;

import ui.gui.MappedLevel;

import java.awt.*;
import java.io.BufferedReader;

public class LevelUtils {
    private static int nextId = 0;

    public static int getNewId() {
        return ++nextId;
    }

    public static MappedLevel readLevel(BufferedReader reader) throws Exception {
        int x, y;
        MappedLevel level;
        String line;
        String[] subLine, ssubLine;

        level = new MappedLevel();
        line = reader.readLine();
        subLine = line.split(";");
        level.setWidth(Integer.parseInt(subLine[0]));
        level.setSpawnpoint(Integer.parseInt(subLine[1]), Integer.parseInt(subLine[2]));
        level.setLink(Integer.parseInt(subLine[3]));
        for (x = 0; x < level.getWidth(); x++) {
            line = reader.readLine();
            subLine = line.split(" ");
            for (y = 0; y < 10; y++) {
                ssubLine = subLine[y].split(";");
                level.setTile(new Point(Integer.parseInt(ssubLine[0]), Integer.parseInt(ssubLine[1])), x, y);
            }
        }
        reader.close();
        return level;
    }

    // <id>;<width>;<spawnpointX>;<spawnpointY>;<nextLevelId>;<warpzoneId>
    public static String encryptLevel(MappedLevel level, int nextLevelId, int warpzoneId) {
        String line = "";
        line += CryptUtils.crypt(level.getId());
        line += CryptUtils.crypt(level.getWidth());
        line += CryptUtils.crypt(level.getSpawnpoint().x);
        line += CryptUtils.crypt(level.getSpawnpoint().y);
        line += CryptUtils.crypt(nextLevelId);
        line += CryptUtils.crypt(warpzoneId);
        return line;
    }
}
