import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ControllerDrawings
        implements MouseListener, AdjustmentListener {
    private final View view;
    private final Model model;
    private boolean threadFlag;

    public ControllerDrawings(View view, Model model) {
        this.view = view;
        this.model = model;
        threadFlag = false;
    }

    private void onTilemap(int input) {
        threadFlag = true;
        new MouseThread(input) {
            public void run() {
                while (threadFlag) {
                    Point mouse;

                    mouse = view.getTilemapMouseCoord();
                    if (mouse != null) {
                        mouse = getRelativePoint(mouse);
                        model.setSelectedTile(input, mouse);
                        view.redrawSelectedTiles();
                    }
                    try {
                        sleep(5);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }.start();
    }

    private void onLevel(int input) {
        threadFlag = true;
        new MouseThread(input) {
            public void run() {
                while (threadFlag) {
                    Point mouse;

                    mouse = view.getLevelMouseCoord();
                    if (mouse != null) {
                        mouse = getRelativePoint(mouse, view.getScrollValue());
                        if (view.getSpawnpointMode()) {
                            if (model.getState() == 1) {
                                if (!model.getCurrentLevelSpawnpoint().equals(mouse)) {
                                    view.drawOnCurrentLevel(model.getCurrentLevelSpawnpoint(),
                                            model.getCurrentMap()[model.getCurrentLevelSpawnpoint().x][model.getCurrentLevelSpawnpoint().y]);
                                    model.setCurrentLevelSpawnpoint(mouse);
                                    view.drawSpawnpoint(mouse);
                                    view.redrawLevel();
                                }
                            } else if (model.getState() == 2) {
                                if (!model.getCurrentWarpzoneSpawnpoint().equals(mouse)) {
                                    view.drawOnCurrentLevel(model.getCurrentWarpzoneSpawnpoint(),
                                            model.getCurrentMap()[model.getCurrentWarpzoneSpawnpoint().x][model.getCurrentWarpzoneSpawnpoint().y]);
                                    model.setCurrentWarpzoneSpawnpoint(mouse);
                                    view.drawSpawnpoint(mouse);
                                    view.redrawLevel();
                                }
                            }
                        } else {
                            if (!model.getCurrentMap()[mouse.x][mouse.y].equals(model.getSelectedTile(input))) {
                                if (model.getState() == 1)
                                    model.drawOnLevel(mouse, input);
                                else if (model.getState() == 2)
                                    model.drawOnWarpzone(mouse, input);
                                view.drawOnCurrentLevel(mouse, model.getSelectedTile(input));
                                view.redrawLevel();
                            }
                        }
                    }
					try {
						sleep(5);
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
                }
            }
        }.start();
    }

    //AdjustmentListener
    public void adjustmentValueChanged(AdjustmentEvent e) {
        JScrollBar src;

        src = (JScrollBar) e.getSource();
        if (src.getName().equals("scrollLevel"))
            view.redrawLevel();
    }

    //MouseListener
    public void mousePressed(MouseEvent e) {
        JComponent src;

        src = (JComponent) e.getSource();
        if (src.getName().equals("pictureTilemap"))
            onTilemap(e.getButton() / 2);
        else if (src.getName().equals("pictureLevel"))
            if (model.getState() != 0)
                onLevel(e.getButton() / 2);
    }

    public void mouseReleased(MouseEvent e) {
        threadFlag = false;
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    private Point getRelativePoint(Point point) {
        return getRelativePoint(point, 0);
    }

    private Point getRelativePoint(Point point, int startX) {
        return new Point((point.x + startX) / 32, point.y / 32);
    }
}