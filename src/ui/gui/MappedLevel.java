package ui.gui;

import utils.LevelUtils;

import java.awt.Point;

public class MappedLevel {
    private final int id;
    private final Point[][] map;
    private int width;
    private Point spawnpoint;
    private int link;

    public MappedLevel() {
        int x, y;

        id = LevelUtils.getNewId();
        map = new Point[300][10];
        for (x = 0; x < 300; x++)
            for (y = 0; y < 10; y++)
                map[x][y] = new Point(0, 0);
        width = 20;
        spawnpoint = new Point(2, 8);
        link = -1;
    }

    public int getId() {
        return id;
    }

    public Point[][] getMap() {
        return map;
    }

    public int getWidth() {
        return width;
    }

    public Point getSpawnpoint() {
        return spawnpoint;
    }

    public int getLink() {
        return link;
    }

    public void setWidth(int value) {
        width = value;
    }

    public void setLink(int value) {
        link = value;
    }

    public void setSpawnpoint(Point value) {
        spawnpoint = value;
    }

    public void setSpawnpoint(int x, int y) {
        spawnpoint = new Point(x, y);
    }

    public Point getTile(int locationX, int locationY) {
        return map[locationX][locationY];
    }

    public void setTile(Point tile, int locationX, int locationY) {
        map[locationX][locationY] = tile;
    }

    public Point getMostUsedTile() {
        int[][] tilemapUsage = new int[8][7];
        Point mostUsedTile = new Point();

        // Inizializzo la tabella di conteggio tiles
        for (int x = 0; x < 8; x++)
            for (int y = 0; y < 7; y++)
                tilemapUsage[x][y] = 0;

        // Conteggio i tiles che compaiono nel livello
        for (int x = 0; x < width; x++)
            for (int y = 0; y < 10; y++)
                if (map[x][y].x > 0 && map[x][y].y == 0 || map[x][y].x < 8 && map[x][y].y == 1)
                    tilemapUsage[map[x][y].x][map[x][y].y]++;

        // Cerco il tile più utilizzato
        for (int x = 0; x < 8; x++)
            for (int y = 0; y < 7; y++)
                if (tilemapUsage[x][y] > tilemapUsage[mostUsedTile.x][mostUsedTile.y])
                    mostUsedTile = new Point(x, y);

        return mostUsedTile;
    }
}