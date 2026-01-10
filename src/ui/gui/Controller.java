package ui.gui;

import ui.components.CustomTreeView;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.Objects;

public class Controller
        implements ActionListener, MouseListener, TreeSelectionListener {
    private final View view;
    private final Model model;

    public Controller(View view, Model model) {
        this.view = view;
        this.model = model;
    }

    private void setLevel(DefaultMutableTreeNode node) {
        int selected;

        selected = (node.getParent()).getIndex(node);
        if (((DefaultMutableTreeNode) node.getParent()).getUserObject().equals("Levels")) {
            model.setCurrentLevel(selected);
            if (!model.isLevelInitialized(selected)) {
                model.initializeLevel(selected);
                view.createLevelImage(selected);
            }
        } else {
            model.setCurrentWarpzone(selected);
            if (!model.isWarpzoneInitialized(selected)) {
                model.initializeWarpzone(selected);
                view.createWarpzoneImage(selected);
            }
        }
    }

    private void newProject(boolean baseProject) {
        boolean askingToSave = (model.getLevelsCount() + model.getWarpzonesCount()) > 0;

        if (askingToSave) {
            int chose;

            chose = view.askToSave();
            if (chose == 1) {
                File fd = view.askSaveProject();
                if (fd == null) return; // L'utente ha cancellato l'operazione di salvataggio
                model.saveProject(fd);
            } else if (chose == -1) return; // L'utente ha scelto di annullare
        }
        if (baseProject)
            model.openProject(new InputStreamReader(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("base.ddp"))));
        else model.newEmptyProject();
        view.startupImages();
        view.buildTreeView();
    }

    private void openProject() {
        File fd;

        fd = view.askOpenProject();
        if (fd != null) {
            model.openProject(fd);
            view.startupImages();
            view.buildTreeView();
        }
    }

    private void saveProject() {
        File fd;

        fd = view.askSaveProject();
        if (fd != null)
            model.saveProject(fd);
    }

    private void exportData() {
        File fd;

        fd = view.askExportData();
        if (fd != null)
            model.exportData(fd);
    }

    private void importLevel() {
        File fd;

        fd = view.askImportLevel();
        if (fd != null) {
            try {
                model.importLevel(new BufferedReader(new FileReader(fd)));
                if (model.getState() == 1)
                    view.createLevelImage(model.getCurrentLevel());
                else if (model.getState() == 2)
                    view.createWarpzoneImage(model.getCurrentWarpzone());
            } catch (Exception e) {
                System.out.println("importLevel: " + e.getMessage());
            }
        }
    }

    private void exportLevel() {
        File fd;

        fd = view.askExportLevel();
        if (fd != null)
            model.exportLevel(fd);
    }

    //ActionListener
    public void actionPerformed(ActionEvent e) {
        JComponent source;

        source = (JComponent) e.getSource();
        if (source.getName().equals("menuItemEmpty"))
            newProject(false);
        else if (source.getName().equals("menuItemBase"))
            newProject(true);
        else if (source.getName().equals("menuItemOpen"))
            openProject();
        else if (source.getName().equals("menuItemSave"))
            saveProject();
        else if (source.getName().equals("menuItemExportData"))
            exportData();
        else if (source.getName().equals("menuItemImport"))
            importLevel();
        else if (source.getName().equals("menuItemExport"))
            exportLevel();
        view.update();
    }

    //MouseListener
    public void mouseClicked(MouseEvent e) {
        if (e.getSource() instanceof CustomTreeView) {
            DefaultMutableTreeNode node;

            node = ((CustomTreeView) e.getSource()).getSelected();
            if (node != null)
                if (e.getClickCount() == 2)
                    if (!node.getUserObject().equals("Levels") && !node.getUserObject().equals("Warpzones")) {
                        setLevel(node);
                        view.update();
                        view.resetScrollBar();
                    }
        }
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void valueChanged(TreeSelectionEvent e) {
        DefaultMutableTreeNode node;
        CustomTreeView treeView;

        treeView = (CustomTreeView) e.getSource();
        node = (DefaultMutableTreeNode) treeView.getLastSelectedPathComponent();
        if (node != null) {
            if (!node.getUserObject().equals("Levels") && !node.getUserObject().equals("Warpzones"))
                node = (DefaultMutableTreeNode) node.getParent();
            view.setWorkingNodeIndex(node.getUserObject().equals("Levels") ? 1 : 2);
        }
        view.adjustSettings();
    }
}