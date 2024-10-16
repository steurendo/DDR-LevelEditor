import java.awt.Point;
import java.io.*;
import java.util.*;

public class Model {
    private final static int LV = 1997;
    private final static int PW = 94;

    private ArrayList<MappedLevel> levels;
    private ArrayList<MappedLevel> warpzones;
    private int currentLevel;
    private int currentWarpzone;
    private final Point[] selectedTiles;
    // 0 -> NIENTE SELEZIONATO; 1 -> LIVELLO SELEZIONATA; 2 -> WARPZONE SELEZIONATA
    private int state;

    public Model() {
        try {
            loadLastProject();
        } catch (Exception e) {
            newEmptyProject();
        }
        selectedTiles = new Point[2];
        selectedTiles[0] = new Point(1, 0);
        selectedTiles[1] = new Point(0, 0);
    }

    //GET-SET
    public int getState() {
        return state;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public int getCurrentWarpzone() {
        return currentWarpzone;
    }

    public Point getSelectedTile(int index) {
        return selectedTiles[index];
    }

    public void setCurrentLevel(int value) {
        state = 1;
        currentLevel = value;
        currentWarpzone = -1;
    }

    public void setCurrentWarpzone(int value) {
        state = 2;
        currentWarpzone = value;
        currentLevel = -1;
    }

    public void setSelectedTile(int index, Point value) {
        selectedTiles[index] = value;
    }

    //LEVELS [GET-SET]
    public Point[][] getLevelMap(int index) {
        return levels.get(index).getMap();
    }

    public int getLevelWidth(int index) {
        return levels.get(index).getWidth();
    }

    public int getCurrentLevelWidth() {
        return getLevelWidth(currentLevel);
    }

    public Point getCurrentLevelSpawnpoint() {
        return levels.get(currentLevel).getSpawnpoint();
    }

    public int getCurrentLinkedWarpzone() {
        return levels.get(currentLevel).getLink();
    }

    public void setCurrentLevelWidth(int width) {
        levels.get(currentLevel).setWidth(width);
    }

    public void setCurrentLevelSpawnpoint(Point point) {
        levels.get(currentLevel).setSpawnpoint(point);
    }

    public void setCurrentLinkedWarpzone(int warpzone) {
        levels.get(currentLevel).setLink(warpzone);
    }

    //WARPZONES [GET-SET]
    public Point[][] getWarpzoneMap(int index) {
        return warpzones.get(index).getMap();
    }

    public int getWarpzoneWidth(int index) {
        return warpzones.get(index).getWidth();
    }

    public int getCurrentWarpzoneWidth() {
        return getWarpzoneWidth(currentWarpzone);
    }

    public Point getCurrentWarpzoneSpawnpoint() {
        return warpzones.get(currentWarpzone).getSpawnpoint();
    }

    public void setCurrentWarpzoneWidth(int width) {
        warpzones.get(currentWarpzone).setWidth(width);
    }

    public void setCurrentWarpzoneSpawnpoint(Point point) {
        warpzones.get(currentWarpzone).setSpawnpoint(point);
    }

    //PUBLICS
    public Point[][] getCurrentMap() {
        return (state == 1 ? levels : warpzones).get(state == 1 ? currentLevel : currentWarpzone).getMap();
    }

    public int getLevelsCount() {
        return levels.size();
    }

    public int getWarpzonesCount() {
        return warpzones.size();
    }

    public boolean isLevelInitialized(int index) {
        return (levels.get(index) != null);
    }

    public boolean isWarpzoneInitialized(int index) {
        return (warpzones.get(index) != null);
    }

    private void draw(Point location, int input, MappedLevel destination) {
        destination.setTile(selectedTiles[input], location.x, location.y);
    }

    public void drawOnLevel(Point location, int input) {
        draw(location, input, levels.get(currentLevel));
    }

    public void drawOnWarpzone(Point location, int input) {
        draw(location, input, warpzones.get(currentWarpzone));
    }

    public void addLevel() {
        levels.add(null);
    }

    public void addWarpzone() {
        warpzones.add(null);
    }

    public void removeLevel() {
        if (currentLevel == levels.size() - 1)
            removeCurrentLevel();
        else {
            levels.removeLast();
        }
    }

    public void removeWarpzone() {
        if (currentWarpzone == warpzones.size() - 1)
            removeCurrentWarpzone();
        else {
            for (MappedLevel level : levels)
                if (level != null)
                    if (level.getLink() == (warpzones.size() - 1))
                        level.setLink(-1);
            warpzones.removeLast();
        }
    }

    public void removeCurrentLevel() {
        levels.remove(currentLevel);
        state = 0;
        currentLevel = -1;
    }

    public void removeCurrentWarpzone() {
        for (MappedLevel level : levels)
            if (level != null) {
                if (level.getLink() == currentWarpzone)
                    level.setLink(-1);
                else if (level.getLink() > currentWarpzone)
                    level.setLink(level.getLink() - 1);
            }
        warpzones.remove(currentWarpzone);
        state = 0;
        currentWarpzone = -1;
    }

    public void initializeLevel(int index) {
        levels.set(index, new MappedLevel());
    }

    public void initializeWarpzone(int index) {
        warpzones.set(index, new MappedLevel());
    }

    public void newEmptyProject() {
        levels = new ArrayList<MappedLevel>();
        warpzones = new ArrayList<MappedLevel>();
        currentLevel = -1;
        currentWarpzone = -1;
        state = 0;
    }

    public void openProject(InputStreamReader src) {
        try {
            int i, x, y, levelsCount, warpzonesCount;
            BufferedReader r;
            MappedLevel level;
            String line;
            String[] subLine, ssubLine;

            newEmptyProject();
            r = new BufferedReader(src);

            //HEADER
            line = r.readLine();
            subLine = line.split(";");
            levelsCount = Integer.parseInt(subLine[0]);
            warpzonesCount = Integer.parseInt(subLine[1]);

            //LEVELS
            for (i = 0; i < levelsCount; i++) {
                level = new MappedLevel();
                line = r.readLine();
                subLine = line.split(";");
                level.setWidth(Integer.parseInt(subLine[0]));
                level.setSpawnpoint(Integer.parseInt(subLine[1]), Integer.parseInt(subLine[2]));
                level.setLink(Integer.parseInt(subLine[3]));
                for (x = 0; x < level.getWidth(); x++) {
                    line = r.readLine();
                    subLine = line.split(" ");
                    for (y = 0; y < 10; y++) {
                        ssubLine = subLine[y].split(";");
                        level.setTile(new Point(Integer.parseInt(ssubLine[0]), Integer.parseInt(ssubLine[1])), x, y);
                    }
                }
                levels.add(level);
            }

            //WARPZONES
            for (i = 0; i < warpzonesCount; i++) {
                level = new MappedLevel();
                line = r.readLine();
                subLine = line.split(";");
                level.setWidth(Integer.parseInt(subLine[0]));
                level.setSpawnpoint(Integer.parseInt(subLine[1]), Integer.parseInt(subLine[2]));
                level.setLink(Integer.parseInt(subLine[3]));
                for (x = 0; x < level.getWidth(); x++) {
                    line = r.readLine();
                    subLine = line.split(" ");
                    for (y = 0; y < 10; y++) {
                        ssubLine = subLine[y].split(";");
                        level.setTile(new Point(Integer.parseInt(ssubLine[0]), Integer.parseInt(ssubLine[1])), x, y);
                    }
                }
                warpzones.add(level);
            }

            r.close();
        } catch (Exception e) {
            System.out.println("openProject: " + e.getMessage());
        }
    }

    public void openProject(File src) {
        try {
            int i, x, y, levelsCount, warpzonesCount;
            BufferedReader r;
            MappedLevel level;
            String line;
            String[] subLine, ssubLine;

            newEmptyProject();
            r = new BufferedReader(new FileReader(src));

            //HEADER
            line = r.readLine();
            subLine = line.split(";");
            levelsCount = Integer.parseInt(subLine[0]);
            warpzonesCount = Integer.parseInt(subLine[1]);

            //LEVELS
            for (i = 0; i < levelsCount; i++) {
                level = new MappedLevel();
                line = r.readLine();
                subLine = line.split(";");
                level.setWidth(Integer.parseInt(subLine[0]));
                level.setSpawnpoint(Integer.parseInt(subLine[1]), Integer.parseInt(subLine[2]));
                level.setLink(Integer.parseInt(subLine[3]));
                for (x = 0; x < level.getWidth(); x++) {
                    line = r.readLine();
                    subLine = line.split(" ");
                    for (y = 0; y < 10; y++) {
                        ssubLine = subLine[y].split(";");
                        level.setTile(new Point(Integer.parseInt(ssubLine[0]), Integer.parseInt(ssubLine[1])), x, y);
                    }
                }
                levels.add(level);
            }

            //WARPZONES
            for (i = 0; i < warpzonesCount; i++) {
                level = new MappedLevel();
                line = r.readLine();
                subLine = line.split(";");
                level.setWidth(Integer.parseInt(subLine[0]));
                level.setSpawnpoint(Integer.parseInt(subLine[1]), Integer.parseInt(subLine[2]));
                level.setLink(Integer.parseInt(subLine[3]));
                for (x = 0; x < level.getWidth(); x++) {
                    line = r.readLine();
                    subLine = line.split(" ");
                    for (y = 0; y < 10; y++) {
                        ssubLine = subLine[y].split(";");
                        level.setTile(new Point(Integer.parseInt(ssubLine[0]), Integer.parseInt(ssubLine[1])), x, y);
                    }
                }
                warpzones.add(level);
            }

            r.close();
        } catch (Exception e) {
            System.out.println("openProject: " + e.getMessage());
        }
    }

    public void saveProject(File dst) {
        try {
            int i, x, y;
            BufferedWriter w;
            MappedLevel level;

            w = new BufferedWriter(new FileWriter(dst));

            //HEADER
            w.write(levels.size() + ";" + warpzones.size());
            w.newLine();

            //LEVELS
            for (i = 0; i < levels.size(); i++) {
                if (!isLevelInitialized(i))
                    initializeLevel(i);
                level = levels.get(i);
                w.write(level.getWidth() + ";" + level.getSpawnpoint().x + ";" + level.getSpawnpoint().y + ";" + level.getLink());
                w.newLine();
                for (x = 0; x < level.getWidth(); x++) {
                    for (y = 0; y < 10; y++)
                        w.write(level.getTile(x, y).x + ";" + level.getTile(x, y).y + " ");
                    w.newLine();
                }
            }

            //WARPZONES
            for (i = 0; i < warpzones.size(); i++) {
                if (!isWarpzoneInitialized(i))
                    initializeWarpzone(i);
                level = warpzones.get(i);
                w.write(level.getWidth() + ";" + level.getSpawnpoint().x + ";" + level.getSpawnpoint().y + ";" + level.getLink());
                w.newLine();
                for (x = 0; x < level.getWidth(); x++) {
                    for (y = 0; y < 10; y++)
                        w.write(level.getTile(x, y).x + ";" + level.getTile(x, y).y + " ");
                    w.newLine();
                }
            }

            w.close();
        } catch (Exception e) {
            System.out.println("saveProject: " + e.getMessage());
        }
    }

    public void exportData(File dst) {
        try {
            int i, x, y;
            BufferedWriter w;
            MappedLevel level;

            w = new BufferedWriter(new FileWriter(dst));

            //HEADER
            w.write(crypt(levels.size()) + crypt(warpzones.size()));

            //LEVELS
            for (i = 0; i < levels.size(); i++) {
                level = levels.get(i);
                w.write(crypt(level.getWidth()) + crypt(level.getSpawnpoint().x + level.getWidth() * level.getSpawnpoint().y) + crypt(level.getLink()));
                for (x = 0; x < level.getWidth(); x++)
                    for (y = 0; y < 10; y++)
                        w.write(crypt(level.getTile(x, y).x + 8 * level.getTile(x, y).y));
            }

            //WARPZONES
            for (i = warpzones.size() - 1; i >= 0; i--) {
                level = warpzones.get(i);
                w.write(crypt(level.getWidth()) + crypt(level.getSpawnpoint().x + level.getWidth() * level.getSpawnpoint().y) + crypt(level.getLink()));
                for (x = 0; x < level.getWidth(); x++)
                    for (y = 0; y < 10; y++)
                        w.write(crypt(level.getTile(x, y).x + 8 * level.getTile(x, y).y));
            }

            w.close();
        } catch (Exception e) {
            System.out.println("exportData: " + e.getMessage());

        }

    }

    public void importLevel(File src) {
        try {
            int x, y;
            BufferedReader r;
            MappedLevel level;
            String line;
            String[] subLine, ssubLine;

            r = new BufferedReader(new FileReader(src));

            level = new MappedLevel();
            line = r.readLine();
            subLine = line.split(";");
            level.setWidth(Integer.parseInt(subLine[0]));
            level.setSpawnpoint(Integer.parseInt(subLine[1]), Integer.parseInt(subLine[2]));
            level.setLink(Integer.parseInt(subLine[3]));
            for (x = 0; x < level.getWidth(); x++) {
                line = r.readLine();
                subLine = line.split(" ");
                for (y = 0; y < 10; y++) {
                    ssubLine = subLine[y].split(";");
                    level.setTile(new Point(Integer.parseInt(ssubLine[0]), Integer.parseInt(ssubLine[1])), x, y);
                }
            }
            if (state == 1)
                levels.set(currentLevel + currentWarpzone + 1, level);

            r.close();
        } catch (Exception e) {
            System.out.println("importLevel: " + e.getMessage());
        }
    }

    public void exportLevel(File dst) {
        try {
            int x, y;
            BufferedWriter w;
            MappedLevel level;

            w = new BufferedWriter(new FileWriter(dst));

            level = (state == 1 ? levels : warpzones).get(currentLevel + currentWarpzone + 1);
            w.write(level.getWidth() + ";" + level.getSpawnpoint().x + ";" + level.getSpawnpoint().y + ";" + level.getLink());
            w.newLine();
            for (x = 0; x < level.getWidth(); x++) {
                for (y = 0; y < 10; y++)
                    w.write(level.getTile(x, y).x + ";" + level.getTile(x, y).y + " ");
                w.newLine();
            }

            w.close();
        } catch (Exception e) {
            System.out.println("exportLevel: " + e.getMessage());
        }
    }

    private void loadLastProject() {
        openProject(new File("temp.ddp"));
    }

    private static String toHex(int x) {
        int i;
        String[] values = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};
        StringBuilder ret;

        ret = new StringBuilder();
        for (i = 0; i < 8; i++) {
            ret.insert(0, values[x % 16]);
            x /= 16;
        }

        return ret.toString();
    }

    /*private int toDec(String x)
    {
    	int ret, i;
    	
    	ret = 0;
    	for (i = 0; i < 8; i++)
    		ret += (x.charAt(i) >= 65 && x.charAt(i) <= 70 ? (10 + (int)x.charAt(i) - 65) : Integer.parseInt("" + x.charAt(i))) * (int)Math.pow(16, (7 - i));
    	
    	return ret;
    }*/
    private String crypt(int x) {
        return crypt(x, LV, PW);
    }

    private String crypt(int x, int lv, int pw) {
        return toHex((x + lv) * pw);
    }
    /*private int decrypt(String x) { return decrypt(x, LV, PW); }
    private int decrypt(String x, int lv, int pw) { return ((toDec(x) / pw) - lv); }*/
}