package ui.gui;

import java.awt.event.*;
import javax.swing.*;

public class ControllerSettings
        implements ActionListener {
    private final View view;
    private final Model model;

    public ControllerSettings(View view, Model model) {
        this.view = view;
        this.model = model;
    }

    private void setWidth() {
        if (view.getWorkingNodeIndex() == 1)
            model.setCurrentLevelWidth(view.getWidthToSet());
        else if (view.getWorkingNodeIndex() == 2)
            model.setCurrentWarpzoneWidth(view.getWidthToSet());
    }

    private void add() {
        if (view.getWorkingNodeIndex() == 1) {
            view.addLevelImage();
            model.addLevel();
        } else if (view.getWorkingNodeIndex() == 2) {
            view.addWarpzoneImage();
            model.addWarpzone();
        }
    }

    private void remove() {
        if (view.getWorkingNodeIndex() == 1) {
            if (model.isLevelInitialized(model.getLevelsCount() - 1))
                if (!view.askToRemove())
                    return;

            view.removeLevelImage();
            model.removeLevel();
        } else if (view.getWorkingNodeIndex() == 2) {
            if (model.isWarpzoneInitialized(model.getWarpzonesCount() - 1))
                if (!view.askToRemove())
                    return;
            view.removeWarpzoneImage();
            model.removeWarpzone();
        }
    }

    private void removeCurrent() {
        if (view.getWorkingNodeIndex() == 1) {
            if (view.askToRemoveCurrentLevel()) {
                view.removeLevelImage(model.getCurrentLevel());
                model.removeCurrentLevel();
            }
        } else if (view.getWorkingNodeIndex() == 2) {
            if (view.askToRemoveCurrentLevel()) {
                view.removeWarpzoneImage(model.getCurrentWarpzone());
                model.removeCurrentWarpzone();
            }
        }
    }

    private void setLinkedWarpzone() {
        model.setCurrentLinkedWarpzone(view.getLinkedWarpzone());
    }

    //ActionListener
    public void actionPerformed(ActionEvent e) {
        JComponent src;

        src = (JComponent) e.getSource();
        if (src.getName().equals("buttonLevelSet"))
            setWidth();
        else if (src.getName().equals("buttonLevelAdd"))
            add();
        else if (src.getName().equals("buttonLevelRemove"))
            remove();
        else if (src.getName().equals("buttonLevelRemoveCurrent"))
            removeCurrent();
        else if (src.getName().equals("comboLinkedWarpzone"))
            if (model.getCurrentLevel() >= 0) {
                setLinkedWarpzone();

                return;
            }
        view.update();
    }
}