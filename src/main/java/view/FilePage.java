package view;

import javax.swing.JPanel;
import javax.swing.JLabel;

public class FilePage extends JPanel {
    public FilePage(Dashboard dashboard,String action) {
        add(new JLabel("File Page -" + action));
    }
}

