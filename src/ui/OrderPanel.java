package ui;

import dao.MenuItemDAO;
import dao.OrderDAO;
import model.MenuItem;
import model.Order;
import model.OrderItem;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.sql.SQLException;
import java.util.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class OrderPanel extends JPanel {

    private final JTabbedPane tabbedPane = new JTabbedPane();
    private final DefaultTableModel cartModel;
    private final JTable cartTable;
    private final JTextField tableField = new JTextField(5);
    private final JLabel grandTotalLabel = new JLabel("Total: ₹ 0.0");

    private final MenuItemDAO menuDao = new MenuItemDAO();
    private final OrderDAO orderDao = new OrderDAO();

    private final String[] categories = {"Main", "Starter", "Soup", "Desserts", "Appetizers"};
    private final Map<String, JPanel> categoryPanels = new HashMap<>();

    public OrderPanel() {
        setLayout(new BorderLayout(12, 12));
        setBackground(new Color(245, 248, 255));

        // ===== HEADER =====
        JLabel title = new JLabel("🧾  Order Management", JLabel.LEFT);
        title.setFont(new Font("Segoe UI Semibold", Font.BOLD, 22));
        title.setBorder(new EmptyBorder(10, 20, 5, 0));
        title.setForeground(new Color(50, 70, 130));
        add(title, BorderLayout.NORTH);

        // ===== TABBED CATEGORIES =====
        Color[] tabColors = {
                new Color(240, 245, 255),
                new Color(245, 255, 250),
                new Color(255, 250, 245),
                new Color(250, 245, 255),
                new Color(245, 245, 255)
        };

        for (int i = 0; i < categories.length; i++) {
            String cat = categories[i];
            JPanel panel = new JPanel(new WrapLayout(FlowLayout.LEFT, 20, 20));
            panel.setBackground(tabColors[i % tabColors.length]);
            panel.setBorder(new EmptyBorder(10, 10, 10, 10));
            categoryPanels.put(cat, panel);

            JScrollPane scroll = new JScrollPane(panel);
            scroll.setBorder(BorderFactory.createEmptyBorder());
            scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            scroll.getVerticalScrollBar().setUnitIncrement(16);

            tabbedPane.addTab(cat, scroll);
        }

        // ===== CART TABLE =====
        cartModel = new DefaultTableModel(new Object[]{"Name", "Category", "Qty", "Unit Price", "Total"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return col == 2; }
        };
        cartTable = new JTable(cartModel);
        cartTable.setRowHeight(32);
        cartTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cartTable.setGridColor(new Color(230, 230, 240));
        cartTable.setShowHorizontalLines(true);
        cartTable.setShowVerticalLines(false);
        cartTable.setIntercellSpacing(new Dimension(10, 5));
        cartTable.setBackground(Color.WHITE);
        cartTable.setSelectionBackground(new Color(220, 235, 250));
        cartTable.setSelectionForeground(new Color(20, 40, 80));

        JTableHeader header = cartTable.getTableHeader();
        header.setFont(new Font("Segoe UI Semibold", Font.BOLD, 14));
        header.setBackground(new Color(245, 247, 250));
        header.setForeground(new Color(70, 70, 90));
        header.setOpaque(false);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(220, 225, 240)));
        header.setReorderingAllowed(false);

        cartTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (isSelected) {
                    c.setBackground(new Color(220, 235, 250));
                    c.setForeground(new Color(20, 40, 80));
                } else {
                    c.setBackground(row % 2 == 0 ? new Color(252, 252, 255) : new Color(245, 247, 250));
                    c.setForeground(new Color(40, 40, 60));
                }
                setBorder(new EmptyBorder(0, 10, 0, 10));
                return c;
            }
        });
        cartModel.addTableModelListener(e -> updateGrandTotal());

        JScrollPane cartScroll = new JScrollPane(cartTable);
        cartScroll.getViewport().setBackground(Color.WHITE);
        cartScroll.setBorder(BorderFactory.createCompoundBorder(
                new TitledBorder(new LineBorder(new Color(225, 230, 245), 1, true),
                        "Cart", TitledBorder.LEFT, TitledBorder.TOP,
                        new Font("Segoe UI Semibold", Font.BOLD, 14), new Color(60, 80, 120)),
                new EmptyBorder(8, 8, 8, 8)
        ));

        JPanel cartCard = new JPanel(new BorderLayout());
        cartCard.setBackground(Color.WHITE);
        cartCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(220, 225, 240), 1, true),
                new EmptyBorder(5, 5, 5, 5)
        ));
        cartCard.add(cartScroll);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tabbedPane, cartCard);
        split.setDividerLocation(600);
        split.setResizeWeight(0.6);
        split.setBorder(null);
        add(split, BorderLayout.CENTER);

        // ===== BUTTON PANEL =====
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        btnPanel.setBackground(new Color(245, 248, 255));
        btnPanel.setBorder(new MatteBorder(1, 0, 0, 0, new Color(220, 225, 240)));

        JButton removeBtn = styledButton("🗑 Remove", new Color(220, 53, 69));
        JButton placeOrderBtn = styledButton("✅ Place Order", new Color(60, 179, 113));

        JLabel tableLabel = new JLabel("Table #: ");
        tableLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tableField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tableField.setPreferredSize(new Dimension(60, 25));
        tableField.setBorder(new LineBorder(new Color(200, 205, 220), 1, true));

        grandTotalLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        grandTotalLabel.setForeground(new Color(50, 70, 130));

        btnPanel.add(removeBtn);
        btnPanel.add(Box.createHorizontalStrut(10));
        btnPanel.add(tableLabel);
        btnPanel.add(tableField);
        btnPanel.add(placeOrderBtn);
        btnPanel.add(Box.createHorizontalStrut(20));
        btnPanel.add(grandTotalLabel);

        add(btnPanel, BorderLayout.SOUTH);

        removeBtn.addActionListener(e -> onRemoveFromCart());
        placeOrderBtn.addActionListener(e -> onPlaceOrder());

        loadMenu();
    }

    private JButton styledButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI Semibold", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(8, 15, 8, 15));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(120, 35));
        return btn;
    }

    private void loadMenu() {
        try {
            categoryPanels.values().forEach(p -> p.removeAll());
            List<MenuItem> items = menuDao.getAll();
            for (MenuItem m : items) {
                JPanel card = createMenuCard(m);
                JPanel panel = categoryPanels.get(m.getCategory());
                if (panel != null) panel.add(card);
            }
            categoryPanels.values().forEach(p -> {
                p.revalidate();
                p.repaint();
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private JPanel createMenuCard(MenuItem m) {
        JPanel card = new JPanel(new BorderLayout());
        card.setPreferredSize(new Dimension(150, 180));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(220, 220, 230), 1, true),
                new EmptyBorder(8, 8, 8, 8)
        ));

        JLabel imgLabel = new JLabel("", JLabel.CENTER);
        imgLabel.setPreferredSize(new Dimension(120, 80));
        ImageIcon icon = loadImage(m.getImagePath(), 120, 80);
        imgLabel.setIcon(icon);
        card.add(imgLabel, BorderLayout.CENTER);

        JLabel nameLabel = new JLabel(m.getName(), JLabel.CENTER);
        JLabel priceLabel = new JLabel("₹ " + m.getPrice(), JLabel.CENTER);

        JPanel info = new JPanel(new GridLayout(3, 1, 2, 2));
        info.setOpaque(false);
        info.add(nameLabel);
        info.add(priceLabel);

        JButton addBtn = new JButton("Add");
        addBtn.setBackground(new Color(70, 130, 180));
        addBtn.setForeground(Color.WHITE);
        addBtn.setFocusPainted(false);
        addBtn.setBorder(new EmptyBorder(5, 10, 5, 10));
        addBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addBtn.addActionListener(e -> addToCart(m));
        info.add(addBtn);

        card.add(info, BorderLayout.SOUTH);
        return card;
    }

    private ImageIcon loadImage(String fileName, int width, int height) {
        if (fileName == null || fileName.isEmpty()) return getPlaceholder(width, height);
        try {
            java.net.URL imgUrl = getClass().getResource("/images/" + fileName);
            if (imgUrl != null) {
                return new ImageIcon(new ImageIcon(imgUrl).getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH));
            } else return getPlaceholder(width, height);
        } catch (Exception e) {
            return getPlaceholder(width, height);
        }
    }

    private ImageIcon getPlaceholder(int width, int height) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setColor(new Color(220, 220, 220));
        g2.fillRect(0, 0, width, height);
        g2.setColor(Color.GRAY);
        g2.drawRect(0, 0, width - 1, height - 1);
        g2.dispose();
        return new ImageIcon(img);
    }

    private void addToCart(MenuItem m) {
        for (int i = 0; i < cartModel.getRowCount(); i++) {
            String name = (String) cartModel.getValueAt(i, 0);
            if (name.equals(m.getName())) {
                int qty = Integer.parseInt(cartModel.getValueAt(i, 2).toString()) + 1;
                cartModel.setValueAt(qty, i, 2);
                cartModel.setValueAt(qty * m.getPrice(), i, 4);
                updateGrandTotal();
                return;
            }
        }
        cartModel.addRow(new Object[]{m.getName(), m.getCategory(), 1, m.getPrice(), m.getPrice()});
        updateGrandTotal();
    }

    private void onRemoveFromCart() {
        int row = cartTable.getSelectedRow();
        if (row != -1) {
            cartModel.removeRow(row);
            updateGrandTotal();
        } else {
            JOptionPane.showMessageDialog(this, "Please select an item to remove!");
        }
    }

    private void onPlaceOrder() {
        if (cartModel.getRowCount() == 0 || tableField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cart is empty or Table number missing!");
            return;
        }
        try {
            List<OrderItem> items = new ArrayList<>();
            double total = 0;
            for (int i = 0; i < cartModel.getRowCount(); i++) {
                String name = (String) cartModel.getValueAt(i, 0);
                String category = (String) cartModel.getValueAt(i, 1);
                int qty = Integer.parseInt(cartModel.getValueAt(i, 2).toString());
                double price = (double) cartModel.getValueAt(i, 3);
                total += price * qty;

                OrderItem it = new OrderItem();
                it.setMenuName(name);
                it.setMenuItemId(menuDao.getByName(name).getId());
                it.setQuantity(qty);
                it.setPrice(price);
                items.add(it);
            }

            Order order = new Order();
            order.setTableNumber(tableField.getText().trim());
            order.setItems(items);
            order.setTotal(total);

            orderDao.addOrder(order);
            JOptionPane.showMessageDialog(this, "Order placed successfully!");
            cartModel.setRowCount(0);
            tableField.setText("");
            updateGrandTotal();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error placing order!");
        }
    }

    private void updateGrandTotal() {
        double total = 0;
        for (int i = 0; i < cartModel.getRowCount(); i++) {
            total += Double.parseDouble(cartModel.getValueAt(i, 4).toString());
        }
        grandTotalLabel.setText("Total: ₹ " + total);
    }

    // ===== WRAP LAYOUT HELPER =====
    public static class WrapLayout extends FlowLayout {
        public WrapLayout() { super(); }
        public WrapLayout(int align, int hgap, int vgap) { super(align, hgap, vgap); }

        @Override
        public Dimension preferredLayoutSize(Container target) {
            return layoutSize(target, true);
        }

        @Override
        public Dimension minimumLayoutSize(Container target) {
            return layoutSize(target, false);
        }

        private Dimension layoutSize(Container target, boolean preferred) {
            synchronized (target.getTreeLock()) {
                int targetWidth = target.getWidth();
                if (targetWidth == 0) targetWidth = Integer.MAX_VALUE;

                int hgap = getHgap();
                int vgap = getVgap();
                Insets insets = target.getInsets();
                int maxWidth = targetWidth - (insets.left + insets.right + hgap * 2);
                int x = 0, y = insets.top + vgap;
                int rowHeight = 0;
                Dimension dim = new Dimension(0, 0);

                for (Component c : target.getComponents()) {
                    if (!c.isVisible()) continue;
                    Dimension d = preferred ? c.getPreferredSize() : c.getMinimumSize();
                    if (x + d.width > maxWidth) {
                        x = 0;
                        y += rowHeight + vgap;
                        rowHeight = 0;
                    }
                    x += d.width + hgap;
                    rowHeight = Math.max(rowHeight, d.height);
                    dim.width = Math.max(dim.width, x);
                }
                dim.height = y + rowHeight + vgap;
                return dim;
            }
        }
    }
}
