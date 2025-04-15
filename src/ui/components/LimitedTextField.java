package ui.components;

import javax.swing.*;
import javax.swing.text.*;
import java.io.Serial;

public class LimitedTextField extends JTextField {
    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    protected Document createDefaultModel() {
        return new PlainDocument() {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
                if ((getLength() + str.length()) <= 3) {
                    int i, j;

                    j = 0;
                    for (i = 0; i < str.length() && j != -1; i++) {
                        j = 0;
                        while (j < 10 && j != -1) {
                            j++;
                            try {
                                Integer.parseInt("" + str.charAt(i));
                            } catch (Exception e) {
                                j = -1;
                            }
                        }
                    }
                    if (j != -1)
                        super.insertString(offset, str, attr);
                }
            }
        };
    }
}