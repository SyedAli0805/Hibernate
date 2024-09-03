package view;


import javax.swing.*;
import java.awt.BorderLayout;

public class Dashboard extends JFrame {
    private JPanel contentPanel;

    public Dashboard() {
        super("Restaurant POS");

        // Set layout
        setLayout(new BorderLayout());

        // Create navigation bar
        NavigationBar navBar = new NavigationBar(this);
        add(navBar, BorderLayout.NORTH);

        // Content panel to switch pages
        contentPanel = new JPanel(new BorderLayout());
        add(contentPanel, BorderLayout.CENTER);

        // Show initial page
        showPage(new HomePage(this));
    }

    public void showPage(JPanel page) {
        contentPanel.removeAll();
        contentPanel.add(page, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
}
