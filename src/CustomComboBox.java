import java.util.*;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

public class CustomComboBox extends JComboBox<String>
{
	private static final long serialVersionUID = 1L;

	public void setSelectedIndex(int index)
	{
		if (index < 0 || index >= dataModel.getSize())
			throw new IllegalArgumentException("illegal index: " + index);
		else
			dataModel.setSelectedItem(dataModel.getElementAt(index));
	}
	public void setModel(ArrayList<String> model)
	{
		setModel(new DefaultComboBoxModel<String>(new Vector<String>(model)));
	}
}