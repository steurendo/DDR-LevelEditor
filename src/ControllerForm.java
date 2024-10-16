import java.awt.event.*;
import java.io.*;

public class ControllerForm extends WindowAdapter {
    private final Model model;

    public ControllerForm(Model model) {
        this.model = model;
    }

    public void windowClosing(WindowEvent e) {
        try {
            model.saveProject(new File("temp.ddp"));
        } catch (Exception ex) {
            System.out.println("Opening: " + ex.getMessage());
        }
        super.windowClosing(e);
    }
}