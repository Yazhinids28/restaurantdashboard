package ui;

import javax.swing.*;

public class MainFrame extends JFrame {

    private JTabbedPane tabs;

    public MainFrame() {
        setTitle("Restaurant Dashboard");
        setSize(1000, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Show login first
        setContentPane(new LoginPanel(this));
        setVisible(true);
    }

    public void showDashboard(String role) {
        tabs = new JTabbedPane();

        switch (role) {
            case "admin" -> {
                tabs.addTab("Menu", new MenuPanel());
                tabs.addTab("Orders", new OrderPanel());
                tabs.addTab("Kitchen", new KitchenDashboardPanel());
                tabs.addTab("Billing", new BillingPanel());
            }
            case "user" -> tabs.addTab("Orders", new OrderPanel());
            case "kitchen" -> tabs.addTab("Kitchen", new KitchenDashboardPanel());
        }

        setContentPane(tabs);
        revalidate();
        repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainFrame::new);
    }
}
