import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.*;

public class View extends JFrame {
    @Serial
	private static final long serialVersionUID = 1L;
    private static final int SCROLLING_VALUE = 32;

    private final BufferedImage[][] tilemap;
    private BufferedImage spawnpointPicture;
    private ArrayList<BufferedImage> imageLevels;
    private ArrayList<BufferedImage> imageWarpzones;
    private final Model model;
    private final Controller controller;
    private final ControllerForm controllerForm;
    private final ControllerDrawings controllerDrawings;
    private final ControllerSettings controllerSettings;
    private final JMenuBar menuBar;
    private final JMenu menuProject;
    private final JMenu menuCurrentLevel;
    private final JMenu subMenuNew;
    private final JMenuItem menuItemEmpty;
    private final JMenuItem menuItemBase;
    private final JMenuItem menuItemOpen;
    private final JMenuItem menuItemSave;
    private final JMenuItem menuItemExportData;
    private final JMenuItem menuItemImport;
    private final JMenuItem menuItemExport;
    private final CustomTreeView treeLevels;
    private final JScrollPane scrollPaneLevels;
    private final JPanel panelSettings;
    private final JPicture pictureLevel;
    private final JPicture pictureSelectedTile1;
    private final JPicture pictureSelectedTile2;
    private final JPicture pictureTilemap;
    private final JScrollBar scrollLevel;
    private final JLabel labelSelectedTiles;
    private final JLabel labelCurrentLevel;
    private final JLabel labelWidth;
    private final JLabel labelLinkWarpzone;
    private final JLabel labelSpawnpointMode;
    private final JCheckBox checkboxSpawnpointMode;
    private final Button buttonSet;
    private final Button buttonAdd;
    private final Button buttonRemove;
    private final Button buttonRemoveCurrent;
    private final JTextField textWidth;
    private final CustomComboBox comboLinkedWarpzone;
    private int workingNodeIndex;

    public View() {
        BufferedImage tilemapPicture;

        model = new Model();
        //CARICO LE IMMAGINI
        try {
            tilemapPicture = ImageIO.read(this.getClass().getResourceAsStream("tilemap.png"));
            spawnpointPicture = ImageIO.read(this.getClass().getResourceAsStream("spawnpoint.png"));
        } catch (Exception e) {
            tilemapPicture = null;
            spawnpointPicture = null;
        }
        //DEFINISCO I TILES DISPONIBILI
        tilemap = new BufferedImage[8][7];
        for (int x = 0; x < 8; x++)
            for (int y = 0; y < 7; y++)
                tilemap[x][y] = tilemapPicture.getSubimage(x * 32, y * 32, 32, 32);
        //CARICO ED INIZIALIZZO LE IMMAGINI DEI LIVELLI
        //DI UN EVENTUALE PROGETTO PRECEDENTE
        startupImages();
        //INIZIALIZZO I CONTROLLERS
        controller = new Controller(this, model);
        controllerForm = new ControllerForm(model);
        controllerDrawings = new ControllerDrawings(this, model);
        controllerSettings = new ControllerSettings(this, model);

        //FORM
        setTitle("Dangerous Dave - Reloaded [Level Editor]");
        setSize(1100, 445);
        try {
            setIconImage(ImageIO.read(Objects.requireNonNull(this.getClass().getResourceAsStream("icon.png"))));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        setResizable(false);
        UIManager.put("Label.font", new Font("Arial", Font.PLAIN, 11));
        UIManager.put("Button.font", new Font("Arial", Font.PLAIN, 11));
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        //COMPONENTI
        menuBar = new JMenuBar();
        menuBar.setBounds(0, 0, getWidth(), 25);
        menuProject = new JMenu("Project");
        menuCurrentLevel = new JMenu("Current level");
        subMenuNew = new JMenu("New");
        menuItemEmpty = new JMenuItem("Empty");
        menuItemEmpty.setName("menuItemEmpty");
        menuItemBase = new JMenuItem("Base");
        menuItemBase.setName("menuItemBase");
        menuItemOpen = new JMenuItem("Open");
        menuItemOpen.setName("menuItemOpen");
        menuItemSave = new JMenuItem("Save");
        menuItemSave.setName("menuItemSave");
        menuItemExportData = new JMenuItem("Generate game data");
        menuItemExportData.setName("menuItemExportData");
        menuItemImport = new JMenuItem("Import");
        menuItemImport.setName("menuItemImport");
        menuItemExport = new JMenuItem("Export");
        menuItemExport.setName("menuItemExport");
        treeLevels = new CustomTreeView();
        workingNodeIndex = 1;
        scrollPaneLevels = new JScrollPane(treeLevels);
        scrollPaneLevels.setBounds(10, 30, 155, 240);
        panelSettings = new JPanel();
        panelSettings.setLayout(null);
        panelSettings.setBounds(10, 275, 155, 133);
        panelSettings.setBorder(BorderFactory.createTitledBorder("Levels"));
        pictureLevel = new JPicture();
        pictureLevel.setName("pictureLevel");
        pictureLevel.setBounds(175, 30, 640, 320);
        pictureLevel.setBorder(BorderFactory.createTitledBorder(""));
        pictureLevel.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
        pictureSelectedTile1 = new JPicture();
        pictureSelectedTile1.setPicture(tilemap[model.getSelectedTile(0).x][model.getSelectedTile(0).y]);
        pictureSelectedTile1.setBounds(915, 37, 32, 32);
        pictureSelectedTile1.setBorder(BorderFactory.createTitledBorder(""));
        pictureSelectedTile2 = new JPicture();
        pictureSelectedTile2.setPicture(tilemap[model.getSelectedTile(1).x][model.getSelectedTile(1).y]);
        pictureSelectedTile2.setBounds(960, 37, 32, 32);
        pictureSelectedTile2.setBorder(BorderFactory.createTitledBorder(""));
        pictureTilemap = new JPicture();
        pictureTilemap.setName("pictureTilemap");
        pictureTilemap.setPicture("tilemap.png");
        pictureTilemap.setBounds(825, 85, 256, 224);
        pictureTilemap.setBorder(BorderFactory.createTitledBorder(""));
        pictureTilemap.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
        scrollLevel = new JScrollBar(Adjustable.HORIZONTAL, 0, 32, 0, 99);
        scrollLevel.setName("scrollLevel");
        scrollLevel.setBounds(175, 360, 640, 43);
        scrollLevel.setUnitIncrement(16);
        scrollLevel.setBlockIncrement(32 * 5);
        labelSelectedTiles = new JLabel("Selected tiles: ");
        labelSelectedTiles.setBounds(825, 45, 100, 15);
        labelWidth = new JLabel("Width:");
        labelWidth.setBounds(10, 25, 50, 15);
        labelLinkWarpzone = new JLabel("Link warpzone");
        labelLinkWarpzone.setBounds(10, 105, 100, 15);
        labelLinkWarpzone.setVisible(false);
        labelCurrentLevel = new JLabel("Current level: none");
        labelCurrentLevel.setBounds(825, 388, 200, 15);
        labelSpawnpointMode = new JLabel("Spawnpoint mode");
        labelSpawnpointMode.setBounds(845, 360, 200, 15);
        checkboxSpawnpointMode = new JCheckBox();
        checkboxSpawnpointMode.setName("checkboxSpawnpointMode");
        checkboxSpawnpointMode.setBounds(825, 360, 20, 15);
        buttonSet = new Button("Set");
        buttonSet.setName("buttonLevelSet");
        buttonSet.setBounds(92, 23, 53, 20);
        buttonAdd = new Button("+");
        buttonAdd.setName("buttonLevelAdd");
        buttonAdd.setBounds(10, 50, 65, 20);
        buttonRemove = new Button("-");
        buttonRemove.setName("buttonLevelRemove");
        buttonRemove.setBounds(80, 50, 65, 20);
        buttonRemoveCurrent = new Button("Remove current");
        buttonRemoveCurrent.setName("buttonLevelRemoveCurrent");
        buttonRemoveCurrent.setBounds(10, 77, 135, 20);
        textWidth = new LimitedTextField();
        textWidth.setBounds(55, 23, 30, 20);
        comboLinkedWarpzone = new CustomComboBox();
        comboLinkedWarpzone.setName("comboLinkedWarpzone");
        comboLinkedWarpzone.setBounds(100, 102, 45, 20);
        comboLinkedWarpzone.setVisible(false);

        //LISTENER
        this.addWindowListener(controllerForm);
        menuItemEmpty.addActionListener(controller);
        menuItemBase.addActionListener(controller);
        menuItemOpen.addActionListener(controller);
        menuItemSave.addActionListener(controller);
        menuItemExportData.addActionListener(controller);
        menuItemImport.addActionListener(controller);
        menuItemExport.addActionListener(controller);
        treeLevels.addMouseListener(controller);
        treeLevels.addTreeSelectionListener(controller);
        buttonAdd.addActionListener(controllerSettings);
        buttonRemove.addActionListener(controllerSettings);
        buttonRemoveCurrent.addActionListener(controllerSettings);
        buttonSet.addActionListener(controllerSettings);
        comboLinkedWarpzone.addActionListener(controllerSettings);
        checkboxSpawnpointMode.addActionListener(controller);
        pictureTilemap.addMouseListener(controllerDrawings);
        pictureLevel.addMouseListener(controllerDrawings);
        scrollLevel.addAdjustmentListener(controllerDrawings);

        //INSERIMENTO COMPONENTI NEL FORM
        menuBar.add(menuProject);
        menuBar.add(menuCurrentLevel);
        menuProject.add(subMenuNew);
        menuProject.add(menuItemOpen);
        menuProject.add(menuItemSave);
        menuProject.add(menuItemExportData);
        menuCurrentLevel.add(menuItemImport);
        menuCurrentLevel.add(menuItemExport);
        subMenuNew.add(menuItemEmpty);
        subMenuNew.add(menuItemBase);
        panelSettings.add(labelWidth);
        panelSettings.add(labelLinkWarpzone);
        panelSettings.add(buttonSet);
        panelSettings.add(buttonAdd);
        panelSettings.add(buttonRemove);
        panelSettings.add(buttonRemoveCurrent);
        panelSettings.add(textWidth);
        panelSettings.add(comboLinkedWarpzone);
        add(menuBar);
        add(scrollPaneLevels);
        add(panelSettings);
        add(pictureLevel);
        add(pictureSelectedTile1);
        add(pictureSelectedTile2);
        add(pictureTilemap);
        add(scrollLevel);
        add(labelSelectedTiles);
        add(labelCurrentLevel);
        add(labelSpawnpointMode);
        add(checkboxSpawnpointMode);
        add(new JLabel());
        update();
    }

    public void start() {
        setVisible(true);
    }

    public int getModelState() {
        return model.getState();
    }

    public void update() {
        buildTreeView();
        adjustSettings();
        setLevelIndicator();
        redrawLevel();
        redrawSelectedTiles();
        setScrollBar();
    }

    //RITORNA VERO O FALSO SE SI STA SETTANDO IL PUNTO DI SPAWN
    public boolean getSpawnpointMode() {
        return checkboxSpawnpointMode.isSelected();
    }

    //OTTIENE L'INDICE DELLA WARPZONE COLLEGATA
    public int getLinkedWarpzone() {
        return (comboLinkedWarpzone.getSelectedIndex() - 1);
    }

    //OTTIENE IL VALORE DELLA SCROLLBAR
    public int getScrollValue() {
        return scrollLevel.getValue();
    }

    //FUNZIONI RELATIVE AL RAMO SELEZIONATO
    public int getWorkingNodeIndex() {
        return workingNodeIndex;
    }

    public void setWorkingNodeIndex(int workingNodeIndex) {
        this.workingNodeIndex = workingNodeIndex;
    }

    //OTTIENE LE COORDINATE DEL MOUSE RELATIVE ALL'IMMAGINE DEL LIVELLO
    public Point getLevelMouseCoord() {
        return pictureLevel.getMousePosition();
    }

    //OTTIENE LE COORDINATE DEL MOUSE RELATIVE ALL'IMMAGINE DELLA TILEMAP
    public Point getTilemapMouseCoord() {
        return pictureTilemap.getMousePosition();
    }

    //INIZIALIZZA GLI ARRAY DELLE IMMAGINI
    public void startupImages() {
        imageLevels = new ArrayList<BufferedImage>();
        for (int i = 0; i < model.getLevelsCount(); i++) {
            imageLevels.add(null);
            createLevelImage(i);
        }
        imageWarpzones = new ArrayList<BufferedImage>();
        for (int i = 0; i < model.getWarpzonesCount(); i++) {
            imageWarpzones.add(null);
            createWarpzoneImage(i);
        }
    }

    //AGGIORNA L'IMMAGINE DELLA SCROLLBAR CHE PERMETTE DI VISUALIZZARE IL LIVELLO
    public void setScrollBar() {
        if (getModelState() == 0)
            scrollLevel.setVisible(false);
        else {
            //PROBLEMA STRANO
            if (getModelState() == 1) {
                scrollLevel.setVisible(model.getCurrentLevelWidth() > 20);
                if (getScrollValue() > (model.getCurrentLevelWidth() - 20 + 1) * SCROLLING_VALUE - 1)
                    scrollLevel.setValue((model.getCurrentLevelWidth() - 20 + 1) * SCROLLING_VALUE - 1);
                scrollLevel.setMaximum((model.getCurrentLevelWidth() - 20 + 1) * SCROLLING_VALUE - 1);
            } else if (getModelState() == 2) {
                scrollLevel.setVisible(model.getCurrentWarpzoneWidth() > 20);
                if (getScrollValue() > (model.getCurrentWarpzoneWidth() - 20 + 1) * SCROLLING_VALUE - 1)
                    scrollLevel.setValue((model.getCurrentWarpzoneWidth() - 20 + 1) * SCROLLING_VALUE - 1);
                scrollLevel.setMaximum((model.getCurrentWarpzoneWidth() - 20 + 1) * SCROLLING_VALUE - 1);
            }
        }
    }

    //RIDISEGNA I TILES SELEZIONATI
    public void redrawSelectedTiles() {
        pictureSelectedTile1.setPicture(tilemap[model.getSelectedTile(0).x][model.getSelectedTile(0).y]);
        pictureSelectedTile2.setPicture(tilemap[model.getSelectedTile(1).x][model.getSelectedTile(1).y]);
    }

    //RIDISEGNA GRAFICAMENTE IL LIVELLO
    public void redrawLevel() {
        if (getModelState() == 0)
            pictureLevel.setPicture((BufferedImage) null);
        else {
            BufferedImage subPicture;

            if (getModelState() == 1) {
                if (checkboxSpawnpointMode.isSelected())
                    drawSpawnpoint(model.getCurrentLevelSpawnpoint());
                else
                    drawOnCurrentLevel(model.getCurrentLevelSpawnpoint(),
                            model.getCurrentMap()[model.getCurrentLevelSpawnpoint().x][model.getCurrentLevelSpawnpoint().y]);
                subPicture = imageLevels.get(model.getCurrentLevel()).getSubimage(getScrollValue(), 0, 640, 320);
                pictureLevel.setPicture(subPicture);
            } else if (getModelState() == 2) {
                if (checkboxSpawnpointMode.isSelected())
                    drawSpawnpoint(model.getCurrentWarpzoneSpawnpoint());
                else
                    drawOnCurrentLevel(model.getCurrentWarpzoneSpawnpoint(),
                            model.getCurrentMap()[model.getCurrentWarpzoneSpawnpoint().x][model.getCurrentWarpzoneSpawnpoint().y]);
                subPicture = imageWarpzones.get(model.getCurrentWarpzone()).getSubimage(getScrollValue(), 0, 640, 320);
                pictureLevel.setPicture(subPicture);
            }
        }
    }

    //DISEGNA UN TILE SULL'IMMAGINE RAFFIGURANTE IL LIVELLO
    public void drawOnCurrentLevel(Point location, Point tile) {
        BufferedImage imageLevel;
        Graphics gD;

        if (getModelState() == 1)
            imageLevel = imageLevels.get(model.getCurrentLevel());
        else if (getModelState() == 2)
            imageLevel = imageWarpzones.get(model.getCurrentWarpzone());
        else
            return;
        gD = imageLevel.getGraphics(); //ECEPCION
        gD.drawImage(tilemap[tile.x][tile.y], location.x * 32, location.y * 32, null);
        gD.dispose();
        if (getModelState() == 1)
            imageLevels.set(model.getCurrentLevel(), imageLevel);
        else if (getModelState() == 2)
            imageWarpzones.set(model.getCurrentWarpzone(), imageLevel);
    }

    //DISEGNA IL SIMBOLO DELLO SPAWNPOINT NEL LIVELLO
    public void drawSpawnpoint(Point location) {
        BufferedImage imageLevel;
        Graphics gD;

        if (getModelState() == 1)
            imageLevel = imageLevels.get(model.getCurrentLevel());
        else if (getModelState() == 2)
            imageLevel = imageWarpzones.get(model.getCurrentWarpzone());
        else
            return;
        gD = imageLevel.getGraphics();
        gD.drawImage(spawnpointPicture, location.x * 32, location.y * 32, null);
        gD.dispose();
        if (getModelState() == 1)
            imageLevels.set(model.getCurrentLevel(), imageLevel);
        else if (getModelState() == 2)
            imageWarpzones.set(model.getCurrentWarpzone(), imageLevel);
    }

    //***
    //VARIE FUNZIONI PER IL SALVATAGGIO/APERTURA FILE
    //***
    public int askToSave() {
        int result;

        result = JOptionPane.showConfirmDialog(this, "Do You want to save the current project?");
        if (result == JOptionPane.YES_OPTION)
            return 1;
        else if (result == JOptionPane.NO_OPTION)
            return 0;

        return -1;
    }

    public File askOpenProject() {
        JFileChooser fc;

        fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("DDR Project File", "ddp"));
        fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
        fc.showOpenDialog(this);
        if (fc.getSelectedFile() == null)
            return null;

        return fc.getSelectedFile();
    }

    public File askSaveProject() {
        JFileChooser fc;

        fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("DDR Project File", "ddp"));
        fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
        fc.showSaveDialog(this);
        if (fc.getSelectedFile() == null)
            return null;
        if (!fc.getSelectedFile().getName().endsWith(".ddp"))
            return new File(fc.getSelectedFile().getAbsolutePath() + ".ddp");

        return fc.getSelectedFile();
    }

    public File askExportData() {
        JFileChooser fc;

        fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.setAcceptAllFileFilterUsed(false);
        fc.showOpenDialog(this);
        if (fc.getSelectedFile() == null)
            return null;
        return new File(fc.getSelectedFile() + "/gamedata.dat");
    }

    public File askImportLevel() {
        JFileChooser fc;

        fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("DDR Level File", "ddl"));
        fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
        fc.showOpenDialog(this);
        if (fc.getSelectedFile() == null)
            return null;

        return fc.getSelectedFile();
    }

    public File askExportLevel() {
        JFileChooser fc;

        fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("DDR Level File", "ddl"));
        fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
        fc.showSaveDialog(this);
        if (fc.getSelectedFile() == null)
            return null;
        if (!fc.getSelectedFile().getName().endsWith(".ddl"))
            return new File(fc.getSelectedFile().getAbsolutePath() + ".ddl");

        return fc.getSelectedFile();
    }

    //OTTIENE IL VALORE INSERITO IN INPUT PER LA LARGHEZZA
    public int getWidthToSet() {
        return Integer.parseInt(textWidth.getText());
    }

    //CHIEDE SE SI VUOLE RIMUOVERE L'ULTIMO LIVELLO
    public boolean askToRemove() {
        return JOptionPane.showConfirmDialog(this, "Remove the last " + (getWorkingNodeIndex() == 1 ? "level" : "warpzone") + "?") == JOptionPane.YES_OPTION;
    }

    //CHIEDE SE SI VUOLE RIMUOVERE IL LIVELLO SUL QUALE SI STA LAVORANDO
    public boolean askToRemoveCurrentLevel() {
        return JOptionPane.showConfirmDialog(this, "Remove the current working level?") == JOptionPane.YES_OPTION;
    }

    //OPERAZIONI CON LE IMMAGINI DEI LIVELLI
    public void addLevelImage() {
        imageLevels.add(null);
    }

    public void removeLevelImage(int index) {
        imageLevels.remove(index);
    }

    public void removeLevelImage() {
        removeLevelImage(imageLevels.size() - 1);
    }

    public void addWarpzoneImage() {
        imageWarpzones.add(null);
    }

    public void removeWarpzoneImage(int index) {
        imageWarpzones.remove(index);
    }

    public void removeWarpzoneImage() {
        imageWarpzones.removeLast();
    }

    public void createLevelImage(int index) {
        Point[][] levelMap;
        BufferedImage imageLevel;
        Graphics gD;

        levelMap = model.getLevelMap(index);
        imageLevel = new BufferedImage(300 * 32, 320, BufferedImage.TYPE_INT_RGB);
        gD = imageLevel.getGraphics();
        for (int x = 0; x < model.getLevelWidth(index); x++)
            for (int y = 0; y < 10; y++)
                gD.drawImage(tilemap[levelMap[x][y].x][levelMap[x][y].y], x * 32, y * 32, null);
        gD.dispose();
        imageLevels.set(index, imageLevel);
    }

    public void createWarpzoneImage(int index) {
        Point[][] levelMap;
        BufferedImage imageWarpzone;
        Graphics gD;

        levelMap = model.getWarpzoneMap(index);
        imageWarpzone = new BufferedImage(300 * 32, 320, BufferedImage.TYPE_INT_RGB);
        gD = imageWarpzone.getGraphics();
        for (int x = 0; x < model.getWarpzoneWidth(index); x++)
            for (int y = 0; y < 10; y++)
                gD.drawImage(tilemap[levelMap[x][y].x][levelMap[x][y].y], x * 32, y * 32, null);
        gD.dispose();
        imageWarpzones.set(index, imageWarpzone);
    }

    //RIDISEGNA IL DIAGRAMMA DEI LIVELLI
    private void buildTreeView() {
        DefaultMutableTreeNode root, node;

        root = new DefaultMutableTreeNode();
        root.add(new DefaultMutableTreeNode("Levels"));
        node = (DefaultMutableTreeNode) root.getChildAt(0);
        for (int i = 1; i <= model.getLevelsCount(); i++)
            node.add(new DefaultMutableTreeNode("Level " + i));
        treeLevels.setModel(new DefaultTreeModel(root));
        for (int i = 0; i < treeLevels.getRowCount(); i++)
            treeLevels.expandRow(i);
    }

    //VARIE IMPOSTAZIONI
    public void adjustSettings() {
        ArrayList<String> linkedWarpzones;

        if (getModelState() == 0) {
            textWidth.setEnabled(false);
            textWidth.setText("");
            buttonSet.setEnabled(false);
            comboLinkedWarpzone.setEnabled(false);
            linkedWarpzones = new ArrayList<String>();
            linkedWarpzones.add("");
            comboLinkedWarpzone.setModel(linkedWarpzones);
            comboLinkedWarpzone.setSelectedIndex(0);
            menuItemExport.setEnabled(false);
            menuItemImport.setEnabled(false);
        } else if (getModelState() == 1) {
            textWidth.setEnabled(true);
            textWidth.setText("" + model.getCurrentLevelWidth());
            buttonSet.setEnabled(true);
            comboLinkedWarpzone.setEnabled(true);
            linkedWarpzones = new ArrayList<String>();
            linkedWarpzones.add("");
            comboLinkedWarpzone.setModel(linkedWarpzones);
            for (int i = 1; i <= model.getWarpzonesCount(); i++)
                linkedWarpzones.add("" + i);
            comboLinkedWarpzone.setModel(linkedWarpzones);
            comboLinkedWarpzone.setSelectedIndex(model.getCurrentLinkedWarpzone() + 1);
            menuItemExport.setEnabled(true);
            menuItemImport.setEnabled(true);
        } else if (getModelState() == 2) {
            textWidth.setEnabled(true);
            textWidth.setText("" + model.getCurrentWarpzoneWidth());
            buttonSet.setEnabled(true);
            comboLinkedWarpzone.setEnabled(false);
            linkedWarpzones = new ArrayList<String>();
            linkedWarpzones.add("");
            comboLinkedWarpzone.setModel(linkedWarpzones);
            comboLinkedWarpzone.setSelectedIndex(0);
            menuItemExport.setEnabled(true);
            menuItemImport.setEnabled(true);
        }
        buttonAdd.setEnabled(getWorkingNodeIndex() == 1 && model.getLevelsCount() < 50 ||
                getWorkingNodeIndex() == 2 && model.getWarpzonesCount() < 50);
        buttonRemove.setEnabled(getWorkingNodeIndex() == 1 && model.getLevelsCount() > 0 ||
                getWorkingNodeIndex() == 2 && model.getWarpzonesCount() > 0);
        buttonRemoveCurrent.setEnabled(model.getCurrentLevel() != -1 || model.getCurrentWarpzone() != -1);
    }

    //MOSTRA IL LIVELLO O LA WARPZONE CHE SI STA MODIFICANDO
    private void setLevelIndicator() {
        String text;

        if (getModelState() == 0)
            text = "No level selected";
        else if (getModelState() == 1)
            text = "Current level: " + (model.getCurrentLevel() + 1);
        else if (getModelState() == 2)
            text = "Current warpzone: " + (model.getCurrentWarpzone() + 1);
        else
            text = "Error";
        labelCurrentLevel.setText(text);
    }
}