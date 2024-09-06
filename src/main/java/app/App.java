package app;



import view.Dashboard;


import javax.swing.*;

public class App {
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new Dashboard();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setVisible(true);
        });

    }
}
