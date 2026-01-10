package ui.components;

import java.awt.*;
import java.io.Serial;
import javax.swing.*;
import javax.swing.tree.*;

public class CustomTreeView extends JTree {
    @Serial
    private static final long serialVersionUID = 1L;

    public CustomTreeView() {
        super();
        reset();
    }

    public void reset() {
        DefaultMutableTreeNode root;

        root = new DefaultMutableTreeNode();
        root.add(new DefaultMutableTreeNode("Levels"));
        root.add(new DefaultMutableTreeNode("Warpzones"));
        this.setModel(new DefaultTreeModel(root));
        setRootVisible(false);
    }

    public void addLevel() {
        DefaultTreeModel model;
        DefaultMutableTreeNode root, node;

        model = (DefaultTreeModel) getModel();
        root = (DefaultMutableTreeNode) model.getRoot();
        node = ((DefaultMutableTreeNode) root.getChildAt(0));
        node.add(new DefaultMutableTreeNode("Level " + (node.getChildCount() + 1)));
        model.reload(root);
    }

    public void addWarpzone() {
        DefaultTreeModel model;
        DefaultMutableTreeNode root, node;

        model = (DefaultTreeModel) getModel();
        root = (DefaultMutableTreeNode) model.getRoot();
        node = ((DefaultMutableTreeNode) root.getChildAt(1));
        node.add(new DefaultMutableTreeNode("Warpzone " + (node.getChildCount() + 1)));
        model.reload(root);
    }

    public void removeLevel() {
        DefaultTreeModel model;
        DefaultMutableTreeNode root, node;

        model = (DefaultTreeModel) getModel();
        root = (DefaultMutableTreeNode) model.getRoot();
        node = ((DefaultMutableTreeNode) root.getChildAt(0));
        node.remove(node.getChildCount() - 1);
        model.reload(root);
    }

    public void removeLevel(int index) {
        DefaultTreeModel model;
        DefaultMutableTreeNode root, node;

        model = (DefaultTreeModel) getModel();
        root = (DefaultMutableTreeNode) model.getRoot();
        node = ((DefaultMutableTreeNode) root.getChildAt(0));
        node.remove(index);
        model.reload(root);
    }

    public void removeWarpzone() {
        DefaultTreeModel model;
        DefaultMutableTreeNode root, node;

        model = (DefaultTreeModel) getModel();
        root = (DefaultMutableTreeNode) model.getRoot();
        node = ((DefaultMutableTreeNode) root.getChildAt(1));
        node.remove(node.getChildCount() - 1);
        model.reload(root);
    }

    public void removeWarpzone(int index) {
        DefaultTreeModel model;
        DefaultMutableTreeNode root, node;

        model = (DefaultTreeModel) getModel();
        root = (DefaultMutableTreeNode) model.getRoot();
        node = ((DefaultMutableTreeNode) root.getChildAt(1));
        node.remove(index);
        model.reload(root);
    }

    public Point getSelectedLocation() {
        Point location;
        DefaultTreeModel model;
        DefaultMutableTreeNode root, node;

        model = (DefaultTreeModel) getModel();
        root = (DefaultMutableTreeNode) model.getRoot();
        node = (DefaultMutableTreeNode) getSelectionPath().getLastPathComponent();
        location = new Point();
        location.setLocation(root.getIndex(node.getParent()), node.getParent().getIndex(node));

        return location;
    }

    public DefaultMutableTreeNode getSelected() {
        if (getSelectionPath() == null)
            return null;

        return ((DefaultMutableTreeNode) getSelectionPath().getLastPathComponent());
    }
}