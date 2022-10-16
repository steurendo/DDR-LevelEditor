import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.swing.*;
import javax.imageio.*;

public class JPicture extends JComponent
{
	private static final long serialVersionUID = 1L;
	
	protected BufferedImage picture;
	
	public JPicture(String filePath, int x, int y, int width, int height)
	{
		super();
		try
		{
			picture = ImageIO.read(getClass().getResourceAsStream(filePath));
		}
		catch (IOException e)
		{
			picture = null;
		}
		finally
		{
			setBounds(x, y, width, height);
		}
	}
	public JPicture(int x, int y, int width, int height) { this("", x, y, width, height); }
	public JPicture() { this("", 0, 0, 0, 0); }
	
	public BufferedImage getPicture() { return picture; }
	
	public void setPicture(BufferedImage picture)
	{
		this.picture = picture;
		repaint();
	}
	public void setPicture(String filePath)
	{
		try
		{
			picture = ImageIO.read(this.getClass().getResourceAsStream(filePath));
		}
		catch (IOException e)
		{
			picture = null;
		}
		repaint();
	}
	
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		if (picture != null)
			g.drawImage(picture, 0, 0, getWidth(), getHeight(), this);
	}
}
