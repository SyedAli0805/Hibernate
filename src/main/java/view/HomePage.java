package view;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.BorderFactory;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HomePage extends JPanel {
    private JLabel dateTimeLabel;
    private JLabel totalOrdersLabel;
    private JLabel totalSalesLabel;

    public HomePage(Dashboard dashboard) {
        setLayout(new BorderLayout());

        // Date and Time Box
        dateTimeLabel = new JLabel();
        dateTimeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        dateTimeLabel.setHorizontalAlignment(JLabel.CENTER);
        updateDateTime();
        Timer timer = new Timer(1000, (ActionEvent e) -> updateDateTime());
        timer.start();

        // Top panel with Date and Time
        JPanel dateTimePanel = new JPanel();
        dateTimePanel.setBorder(BorderFactory.createTitledBorder("Current Date and Time"));
        dateTimePanel.add(dateTimeLabel);
        add(dateTimePanel, BorderLayout.NORTH);

        // Main Panel for Total Orders and Total Sales
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new GridLayout(1, 2, 20, 20));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Total Orders Box
        totalOrdersLabel = createInfoBox("Total Orders Today", "120");
        statsPanel.add(totalOrdersLabel);

        // Total Sales Box
        totalSalesLabel = createInfoBox("Total Sales (PKR)", "50,000");
        statsPanel.add(totalSalesLabel);

        add(statsPanel, BorderLayout.CENTER);
    }

    private JLabel createInfoBox(String title, String value) {
        JLabel label = new JLabel("<html><div style='text-align: center;'>" + title + "<br/><strong>" + value + "</strong></div></html>");
        label.setFont(new Font("Arial", Font.BOLD, 18));
        label.setOpaque(true);
        label.setBackground(new Color(204, 229, 255));
        label.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setVerticalAlignment(JLabel.CENTER);
        label.setPreferredSize(new Dimension(200, 100));
        return label;
    }

    private void updateDateTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        dateTimeLabel.setText(formatter.format(new Date()));
    }
}
