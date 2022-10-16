import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

public class Controller
	implements ActionListener, MouseListener, TreeSelectionListener
{
	private View view;
	private Model model;
	
	public Controller(View view, Model model)
	{
		this.view = view;
		this.model = model;
	}
	
	private void setLevel(DefaultMutableTreeNode node)
	{
		int selected;
		
		selected = (node.getParent()).getIndex(node);
		if (((DefaultMutableTreeNode)node.getParent()).getUserObject().equals("Levels"))
		{
			model.setCurrentLevel(selected);
			if (!model.isLevelInitialized(selected))
			{
				model.initializeLevel(selected);
				view.createLevelImage(selected);
			}
		}
		else
		{
			model.setCurrentWarpzone(selected);
			if (!model.isWarpzoneInitialized(selected))
			{
				model.initializeWarpzone(selected);
				view.createWarpzoneImage(selected);
			}
		}
	}
	private void newEmptyProject()
	{
		if ((model.getLevelsCount() + model.getWarpzonesCount()) > 0)
		{
			int chose;
			
			chose = view.askToSave();
			if (chose == 1)
			{
				File fd;
				
				fd = view.askSaveProject();
				if (fd != null)
				{
					model.saveProject(fd);
					model.newEmptyProject();
					view.startupImages();
				}
			}
			else if (chose == 0)
			{
				model.newEmptyProject();
				view.startupImages();
			}
		}
	}
	private void newBaseProject()
	{
		model.openProject(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("base.ddp")));
		view.startupImages();
	}
	private void openProject()
	{
		File fd;
		
		fd = view.askOpenProject();
		if (fd != null)
		{
			model.openProject(fd);
			view.startupImages();
		}
	}
	private void saveProject()
	{
		File fd;
		
		fd = view.askSaveProject();
		if (fd != null)
			model.saveProject(fd);
	}
	private void exportData()
	{
		File fd;
		
		fd = view.askExportData();
		if (fd != null)
			model.exportData(fd);
	}
	private void importLevel()
	{
		File fd;
		
		fd = view.askImportLevel();
		if (fd != null)
		{
			model.importLevel(fd);
			if (model.getState() == 1)
				view.createLevelImage(model.getCurrentLevel());
			else if (model.getState() == 2)
				view.createWarpzoneImage(model.getCurrentWarpzone());
		}
	}
	private void exportLevel()
	{
		File fd;
		
		fd = view.askExportLevel();
		if (fd != null)
			model.exportLevel(fd);
	}

	//ActionListener
	public void actionPerformed(ActionEvent e)
	{
		JComponent source;
		
		source = (JComponent)e.getSource();
		if (source.getName().equals("menuItemEmpty"))
			newEmptyProject();
		else if (source.getName().equals("menuItemBase"))
			newBaseProject();
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
	public void mouseClicked(MouseEvent e)
	{
		if (e.getSource() instanceof CustomTreeView)
		{
			DefaultMutableTreeNode node;
			
			node = ((CustomTreeView)e.getSource()).getSelected();
			if (node != null)
				if (e.getClickCount() == 2)
					if (!node.getUserObject().equals("Levels") && !node.getUserObject().equals("Warpzones"))
					{
						setLevel(node);
						view.update();
					}
		}
	}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}

	public void valueChanged(TreeSelectionEvent e)
	{
		DefaultMutableTreeNode node;
		CustomTreeView treeView;
		
		treeView = (CustomTreeView)e.getSource();
		node = (DefaultMutableTreeNode)treeView.getLastSelectedPathComponent();
		if (node != null)
		{
			if (!node.getUserObject().equals("Levels") && !node.getUserObject().equals("Warpzones"))
				node = (DefaultMutableTreeNode)node.getParent();
			view.setWorkingNodeIndex(node.getUserObject().equals("Levels") ? 1 : 2);
		}
		view.adjustSettings();
	}
}