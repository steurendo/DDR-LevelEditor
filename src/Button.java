import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Button
	extends JComponent
	implements MouseListener
{
	private static final long serialVersionUID = 1L;
	private static final Color colorDefault = new Color(90, 200, 255);
	private static final Color colorOver = new Color(118, 210, 255);
	private static final Color colorAnimation = new Color(198, 237, 255);
	
	private Color background;
	private boolean mousePressed;
	private boolean mouseOver;
	private Rectangle mask;
	private int maskAlpha;
	private int cornersRound;
	private String text;
	private int buttonHeight;
	private int iterator;
	private int animationVelocity;
	
	public Button(String text)
	{
		super();

		this.text = text;
		background = colorDefault;
		cornersRound = 10;
		mousePressed = false;
		mouseOver = false;
		mask = new Rectangle();
		maskAlpha = 0;
		buttonHeight = 2;
		iterator = 0;
		animationVelocity = 70;
		addMouseListener(this);
	}
	public Button() { this(null); }
	
	public String getText() { return text; }
	
	public void setText(String value) { text = value; }
	public void setCornersRoundValue(int value) { cornersRound = value; }
	
    public void addActionListener(ActionListener listener) { listenerList.add(ActionListener.class, listener); }
    public void removeActionListener(ActionListener listener) { listenerList.remove(ActionListener.class, listener); }
	private void fireActionPerformed()
	{
        ActionListener[] listeners;
        
        listeners = listenerList.getListeners(ActionListener.class);
        if (listeners != null)
			if (listeners.length > 0)
				listeners[0].actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, ""));
    }
    
	public void paintComponent(Graphics g)
	{
		int inc;
		RenderingHints qualityHints;
		
		//QUALITY
		qualityHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		qualityHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		((Graphics2D)g).setRenderingHints(qualityHints);
		inc = 0;
		if (mousePressed && mouseOver)
			inc += buttonHeight;
		else
		{
			//SHADOW
			g.setColor(Color.GRAY);
			g.fillRoundRect(buttonHeight, cornersRound / 2 - buttonHeight, getWidth() - buttonHeight, getHeight() - (cornersRound / 2) + buttonHeight, cornersRound, cornersRound);
		}
		//BUTTON
		g.setColor(isEnabled() ? background : new Color(230, 230, 230));
		g.fillRoundRect(inc, inc, getWidth() - buttonHeight, getHeight() - buttonHeight, cornersRound, cornersRound);
		//ANIMATION
		if (maskAlpha < 0)
			maskAlpha = 0;
		g.setColor(new Color(colorAnimation.getRed(), colorAnimation.getGreen(), colorAnimation.getBlue(), maskAlpha));
		g.fillRoundRect(mask.x + inc, mask.y + inc, mask.width, mask.height, cornersRound, cornersRound);
		//TEXT
		if (text != null)
		{
			int x, y;
			FontMetrics fm;

			fm = g.getFontMetrics();
			x = (getWidth() - fm.stringWidth(text)) / 2 + inc;
			y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent() + inc;
			g.setColor(Color.black);
			g.drawString(text, x, y);
		}
	}

	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e)
	{
		if (isEnabled())
		{
			mouseOver = true;
			background = colorOver;
			repaint();
		}
	}
	public void mouseExited(MouseEvent e)
	{
		if (isEnabled())
		{
			mouseOver = false;
			background = colorDefault;
			repaint();
		}
	}
	public void mousePressed(MouseEvent e)
	{
		if (isEnabled())
		{
			Thread thread;
			
			mousePressed = true;
			thread = new Thread()
			{
				public void run()
				{
					int distanceX, distanceY;

					distanceX = getWidth() / 2 - 1;
					distanceY = getHeight() / 2 - 1;
					mask.setBounds(distanceX, distanceY, 0, 0);
					maskAlpha = 255;
					iterator = 0;
					while (mousePressed)
					{
						if (mouseOver)
						{
							if (iterator <= animationVelocity)
								iterator++;
						}
						else
						{
							if (iterator >= 0)
								iterator--;
						}
						mask.setBounds(distanceX - (iterator * distanceX / animationVelocity),
									   distanceY - (iterator * distanceY / animationVelocity),
									   ((iterator * distanceX / animationVelocity)) * 2,
									   ((iterator * distanceY / animationVelocity)) * 2);
						try { sleep(mouseOver ? 7 : 3); } catch (Exception e) {}
						repaint();
					}
					mask.setBounds(0, 0, getWidth() - buttonHeight, getHeight() - buttonHeight);
					repaint();
				}
			};
			thread.start();
		}
	}
	public void mouseReleased(MouseEvent e)
	{
		if (isEnabled())
		{
			Thread thread;
			
			mousePressed = false;
			thread = new Thread()
			{
				public void run()
				{
					int distanceX, distanceY;
					
					distanceX = getWidth() / 2 - 1;
					distanceY = getHeight() / 2 - 1;
					if (mouseOver)
					{
						fireActionPerformed();
						while (iterator <= animationVelocity)
						{
							mask.setBounds(distanceX - (iterator * distanceX / animationVelocity),
										   distanceY - (iterator * distanceY / animationVelocity),
										   ((iterator * distanceX / animationVelocity)) * 2,
										   ((iterator * distanceY / animationVelocity)) * 2);
							try { sleep(3); } catch (Exception e) {}
							repaint();
							iterator++;
							if (maskAlpha > 0)
								maskAlpha--;
						}
						mask.setBounds(0, 0, getWidth() - buttonHeight, getHeight() - buttonHeight);
					}
					else
					{
						while (iterator >= 0)
						{
							mask.setBounds(distanceX - (iterator * distanceX / animationVelocity),
										   distanceY - (iterator * distanceY / animationVelocity),
										   ((iterator * distanceX / animationVelocity)) * 2,
										   ((iterator * distanceY / animationVelocity)) * 2);
							try { sleep(3); } catch (Exception e) {}
							repaint();
							iterator--;
							if (maskAlpha > 0)
								maskAlpha--;
						}
						maskAlpha = 0;
						mask.setBounds(0, 0, 0, 0);
					}
					while (maskAlpha > 0 && !mousePressed)
					{
						repaint();
						maskAlpha--;
						try { sleep(4); } catch (Exception e) {}
					}
					repaint();
				}
			};
			thread.start();
			
		}
	}
}
