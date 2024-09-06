package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NavigationBar extends JPanel {
    public NavigationBar(Dashboard dashboard) {
        setLayout(new FlowLayout(FlowLayout.LEFT));

        // Home button
        JButton homeButton = new JButton("Home");
        homeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dashboard.showPage(new HomePage(dashboard));
            }
        });
        add(homeButton);

        // OrderPage button
        JButton orderButton = new JButton("OrderPage");
        orderButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dashboard.showPage(new OrderPage(dashboard));
            }
        });
        add(orderButton);

        // Products button
        JButton productsButton = new JButton("Products");
        productsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dashboard.showPage(new ProductsPage(dashboard, "Show All Products"));
            }
        });
        add(productsButton);

        // Raw Material button
        JButton rawMaterialButton = new JButton("Raw Material");
        rawMaterialButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dashboard.showPage(new RawMaterialPage(dashboard, "Show All Raw Material"));
            }
        });
        add(rawMaterialButton);

        // Recipe button
        JButton recipeButton = new JButton("Recipe");
        recipeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dashboard.showPage(new RecipePage(dashboard, "Show All Recipes"));
            }
        });
        add(recipeButton);

        // File button
        JButton fileButton = new JButton("Reports");
        fileButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Create a dropdown menu or buttons for different reports
                JPopupMenu reportMenu = new JPopupMenu();

                JMenuItem orderReport = new JMenuItem("Show OrderPage Report");
                orderReport.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        dashboard.showPage(new FilePage(dashboard, "Show OrderPage Report"));
                    }
                });
                reportMenu.add(orderReport);

                JMenuItem salesReport = new JMenuItem("Show Sales Report");
                salesReport.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        dashboard.showPage(new FilePage(dashboard, "Show Sales Report"));
                    }
                });
                reportMenu.add(salesReport);

                JMenuItem formulatedConsumptionReport = new JMenuItem("Show Formulated Consumption Report");
                formulatedConsumptionReport.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        dashboard.showPage(new FilePage(dashboard, "Show Formulated Consumption Report"));
                    }
                });
                reportMenu.add(formulatedConsumptionReport);

                // Show the dropdown menu below the button
                reportMenu.show(fileButton, fileButton.getWidth(), fileButton.getHeight());
            }
        });
        add(fileButton);
    }
}
