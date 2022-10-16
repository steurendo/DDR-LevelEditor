import java.awt.Point;

public class MappedLevel
{
	private Point[][] map;
	private int width;
	private Point spawnpoint;
	private int link;

	public MappedLevel(int width)
	{
		int x, y;
		
		map = new Point[300][10];
		for (x = 0; x < 300; x++)
			for (y = 0; y < 10; y++)
				map[x][y] = new Point(0, 0);
		this.width = width;
		spawnpoint = new Point(2, 8);
		link = -1;
	}
	public MappedLevel() { this(20); }
	
	public Point[][] getMap() { return map; }
	public int getWidth() { return width; }
	public Point getSpawnpoint() { return spawnpoint; }
	public int getLink() { return link; }

	public void setMap(Point[][] value) { map = value; }
	public void setWidth(int value) { width = value; }
	public void setLink(int value) { link = value; }
	public void setSpawnpoint(Point value) { spawnpoint = value; }
	public void setSpawnpoint(int x, int y) { spawnpoint = new Point(x, y); }
	
	public Point getTile(int locationX, int locationY) { return map[locationX][locationY]; }
	public void setTile(Point tile, int locationX, int locationY) { map[locationX][locationY] = tile; }
}