package ui;

import dao.OrderDAO;
import model.Order;
import model.OrderItem;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class KitchenDashboardPanel extends JPanel {
    private final OrderDAO orderDao = new OrderDAO();
    private final JPanel ordersPanel;

    public KitchenDashboardPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.decode("#f5f5f5"));

        // Title
        JLabel title = new JLabel("Kitchen Dashboard", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setBorder(new EmptyBorder(15, 0, 15, 0));
        title.setForeground(Color.decode("#00796b"));
        add(title, BorderLayout.NORTH);

        // Orders panel: 3-column grid
        ordersPanel = new JPanel(new GridLayout(0, 3, 10, 10));
        ordersPanel.setBackground(Color.decode("#f5f5f5"));

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
                // Fetch Pending and Ready orders
                List<Order> pending = orderDao.getOrdersByStatus("Pending");
                List<Order> ready = orderDao.getOrdersByStatus("Ready");

                for (Order o : pending) ordersPanel.add(createCard(o, true));
                for (Order o : ready) ordersPanel.add(createCard(o, false));

            } catch (SQLException e) {
                e.printStackTrace();
            }
            ordersPanel.revalidate();
            ordersPanel.repaint();
        });
    }

    private JPanel createCard(Order o, boolean isPending) {
        JPanel card = new JPanel(new BorderLayout(5, 5)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, Color.decode("#e0f7fa"), 0, getHeight(), Color.decode("#b2ebf2"));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(8, 8, 8, 8));

        // Top: Table + Status
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JLabel tableLabel = new JLabel("Table #" + o.getTableNumber());
        tableLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        tableLabel.setForeground(Color.decode("#004d40"));

        JLabel statusLabel = new JLabel(o.getStatus(), SwingConstants.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        statusLabel.setOpaque(true);
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setBackground(getStatusColor(o.getStatus()));
        statusLabel.setBorder(new EmptyBorder(2, 6, 2, 6));

        topPanel.add(tableLabel, BorderLayout.WEST);
        topPanel.add(statusLabel, BorderLayout.EAST);
        card.add(topPanel, BorderLayout.NORTH);

        // Items list
        JPanel itemsPanel = new JPanel();
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
        itemsPanel.setOpaque(false);

        Color[] bulletColors = {Color.decode("#ff8a65"), Color.decode("#ffd54f"), Color.decode("#4db6ac"), Color.decode("#64b5f6")};
        int idx = 0;
        for (OrderItem item : o.getItems()) {
            JLabel itemLabel = new JLabel("• " + item.getMenuName() + " x" + item.getQuantity());
            itemLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            itemLabel.setForeground(bulletColors[idx % bulletColors.length]);
            itemsPanel.add(itemLabel);
            idx++;
        }
        card.add(itemsPanel, BorderLayout.CENTER);

        // Bottom panel: Total + Ready Button
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);

        JLabel totalLabel = new JLabel("Total Items: " + o.getItems().stream().mapToInt(OrderItem::getQuantity).sum());
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        totalLabel.setForeground(Color.decode("#004d40"));

        if (isPending) {
            JButton readyBtn = new JButton("✅ Ready for Billing");
            readyBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
            readyBtn.setBackground(Color.decode("#4CAF50"));
            readyBtn.setForeground(Color.WHITE);
            readyBtn.setFocusPainted(false);
            readyBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            readyBtn.addActionListener(ev -> {
                try {
                    orderDao.markAsReady(o.getId());
                    loadOrders();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            });

            bottomPanel.add(totalLabel, BorderLayout.WEST);
            bottomPanel.add(readyBtn, BorderLayout.EAST);
        } else {
            bottomPanel.add(totalLabel, BorderLayout.WEST);
        }

        card.add(bottomPanel, BorderLayout.SOUTH);

        // Hover effect
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                card.setBorder(BorderFactory.createLineBorder(Color.decode("#00796b"), 2));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                card.setBorder(new EmptyBorder(8, 8, 8, 8));
            }
        });

        return card;
    }

    private Color getStatusColor(String status) {
        return switch (status.toLowerCase()) {
            case "pending" -> Color.decode("#ff6f61");
            case "ready" -> Color.decode("#fbbc04");
            case "completed" -> Color.decode("#34a853");
            default -> Color.GRAY;
        };
    }

    private void startAutoRefresh() {
        new Thread(() -> {
            while (true) {
                loadOrders();
                try { Thread.sleep(5000); } catch (InterruptedException e) { e.printStackTrace(); }
            }
        }).start();
    }
}
