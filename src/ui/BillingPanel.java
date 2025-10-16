package ui;

import dao.OrderDAO;
import model.Order;
import model.OrderItem;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.FileWriter;
import java.sql.SQLException;
import java.util.List;

public class BillingPanel extends JPanel {
    private OrderDAO orderDao = new OrderDAO();
    private JPanel ordersPanel;

    public BillingPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.decode("#e0f7fa"));

        JLabel title = new JLabel("Billing Dashboard", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setBorder(new EmptyBorder(15, 0, 15, 0));
        title.setForeground(Color.decode("#00796b"));
        add(title, BorderLayout.NORTH);

        ordersPanel = new JPanel();
        ordersPanel.setLayout(new GridLayout(0, 3, 15, 15));
        ordersPanel.setBackground(Color.decode("#e0f7fa"));

        JScrollPane scrollPane = new JScrollPane(ordersPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        startAutoRefresh();
    }

    private void loadOrders() {
        SwingUtilities.invokeLater(() -> {
            ordersPanel.removeAll();
            try {
                List<Order> readyOrders = orderDao.getOrdersByStatus("Ready");
                for (Order o : readyOrders) {
                    ordersPanel.add(createOrderCard(o));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            ordersPanel.revalidate();
            ordersPanel.repaint();
        });
    }

    private JPanel createOrderCard(Order o) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(new EmptyBorder(10, 10, 10, 10));
        card.setOpaque(false);

        // Gradient background panel
        JPanel gradientPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                Color start = Color.decode("#b2ebf2");
                Color end = Color.decode("#80deea");
                GradientPaint gp = new GradientPaint(0, 0, start, 0, getHeight(), end);
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                super.paintComponent(g);
            }
        };
        gradientPanel.setLayout(new BorderLayout(5, 5));
        gradientPanel.setOpaque(false);
        gradientPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top panel: Table + Status
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel tableLabel = new JLabel("Table #" + o.getTableNumber());
        tableLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JLabel statusLabel = new JLabel(getStatusIcon(o.getStatus()) + " " + o.getStatus());
        statusLabel.setOpaque(true);
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        statusLabel.setBackground(getStatusColor(o.getStatus()));
        statusLabel.setBorder(new EmptyBorder(2, 6, 2, 6));

        top.add(tableLabel, BorderLayout.WEST);
        top.add(statusLabel, BorderLayout.EAST);

        gradientPanel.add(top, BorderLayout.NORTH);

        // Items
        JPanel itemsPanel = new JPanel();
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
        itemsPanel.setOpaque(false);

        Color[] bulletColors = {Color.decode("#ff8a65"), Color.decode("#ffd54f"), Color.decode("#4db6ac"), Color.decode("#64b5f6")};
        int idx = 0;
        for (OrderItem it : o.getItems()) {
            JLabel lbl = new JLabel("• " + it.getMenuName() + " x" + it.getQuantity());
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            lbl.setForeground(bulletColors[idx % bulletColors.length]);
            idx++;
            itemsPanel.add(lbl);
        }

        gradientPanel.add(itemsPanel, BorderLayout.CENTER);

        // Bottom: Bill Button
        JButton billBtn = new JButton("Bill");
        billBtn.setBackground(Color.decode("#00796b"));
        billBtn.setForeground(Color.WHITE);
        billBtn.setFocusPainted(false);
        billBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        billBtn.addActionListener(e -> generateBill(o));

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setOpaque(false);
        bottomPanel.add(billBtn);

        gradientPanel.add(bottomPanel, BorderLayout.SOUTH);

        card.add(gradientPanel, BorderLayout.CENTER);

        // Hover shadow effect
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                card.setBorder(BorderFactory.createLineBorder(Color.decode("#00796b"), 2));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                card.setBorder(new EmptyBorder(10, 10, 10, 10));
            }
        });

        return card;
    }

    private String getStatusIcon(String status) {
        return switch (status.toLowerCase()) {
            case "ready" -> "⚡";
            case "pending" -> "⏳";
            case "in progress" -> "🔄";
            default -> "";
        };
    }

    private Color getStatusColor(String status) {
        return switch (status.toLowerCase()) {
            case "ready" -> Color.decode("#34a853");
            case "pending" -> Color.decode("#ff6f61");
            case "in progress" -> Color.decode("#fbbc04");
            default -> Color.GRAY;
        };
    }

    private void generateBill(Order o) {
        try {
            JDialog dialog = new JDialog((Frame) null, "Bill for Table " + o.getTableNumber(), true);
            dialog.setLayout(new BorderLayout(10, 10));
            dialog.getContentPane().setBackground(Color.WHITE);

            JTextArea area = new JTextArea(15, 40);
            area.setEditable(false);
            area.setFont(new Font("Consolas", Font.PLAIN, 14));
            area.setBackground(Color.decode("#f9f9f9"));
            area.setBorder(new EmptyBorder(10, 10, 10, 10));

            StringBuilder sb = new StringBuilder();
            sb.append("Order ID: ").append(o.getId())
              .append("\nTable: ").append(o.getTableNumber()).append("\n\n");

            double calculatedTotal = 0;
            for (OrderItem it : o.getItems()) {
                double t = it.getQuantity() * it.getPrice();
                sb.append(it.getMenuName()).append(" x ").append(it.getQuantity())
                  .append(" = ").append(t).append("\n");
                calculatedTotal += t;
            }

            // Make total effectively final for lambda
            final double total = calculatedTotal;
            sb.append("\nTotal: ").append(total);

            area.setText(sb.toString());
            dialog.add(new JScrollPane(area), BorderLayout.CENTER);

            JButton download = new JButton("Download TXT & Mark Paid");
            download.setBackground(Color.decode("#00796b"));
            download.setForeground(Color.WHITE);
            download.setFont(new Font("Segoe UI", Font.BOLD, 13));
            download.setFocusPainted(false);

            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            btnPanel.setOpaque(false);
            btnPanel.add(download);
            dialog.add(btnPanel, BorderLayout.SOUTH);

            download.addActionListener(ev -> {
                try {
                    FileWriter fw = new FileWriter("Bill_Order_" + o.getId() + ".txt");
                    fw.write(area.getText());
                    fw.close();
                    orderDao.markAsPaid(o.getId());
                    loadOrders();
                    dialog.dispose();
                    JOptionPane.showMessageDialog(this, "Bill downloaded & order marked Paid!");
                } catch (Exception ex) { ex.printStackTrace(); }
            });

            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);

        } catch (Exception e) { e.printStackTrace(); }
    }

    private void startAutoRefresh() {
        new Thread(() -> {
            while (true) {
                loadOrders();
                try { Thread.sleep(5000); } catch (Exception e) { e.printStackTrace(); }
            }
        }).start();
    }
}
