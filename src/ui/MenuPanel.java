package ui;

import dao.MenuItemDAO;
import model.MenuItem;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuPanel extends JPanel {

    private final JTabbedPane tabbedPane;
    private final MenuItemDAO dao = new MenuItemDAO();

    private final JTextField nameField = new JTextField(15);
    private final JTextField priceField = new JTextField(7);
    private final JCheckBox availBox = new JCheckBox("Available", true);
    private final JLabel imagePreview = new JLabel("", JLabel.CENTER);
    private String selectedImageName = null;

    private final String[] categories = {"Appetizers", "Starter", "Main", "Soup", "desserts"};
    private final Map<String, JPanel> categoryPanels = new HashMap<>();
    private final Map<Integer, JPanel> cardMap = new HashMap<>();
    private int selectedItemId = -1;

    private JButton addBtn;
    private JButton updateBtn;
    private JButton delBtn;

    public MenuPanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(245, 248, 255));

        JLabel title = new JLabel("🍽️  Menu Management", JLabel.LEFT);
        title.setFont(new Font("Segoe UI Semibold", Font.BOLD, 22));
        title.setBorder(new EmptyBorder(10, 20, 5, 0));
        title.setForeground(new Color(50, 70, 130));
        add(title, BorderLayout.NORTH);

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        add(tabbedPane, BorderLayout.CENTER);

        // Create category tabs
        for (String cat : categories) {
            JPanel panel = new JPanel(new WrapLayout(FlowLayout.LEFT, 20, 20));
            panel.setOpaque(false);
            JScrollPane scroll = new JScrollPane(panel);
            scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            tabbedPane.addTab(cat, scroll);
            categoryPanels.put(cat, panel);
        }

        // Form panel
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        JComboBox<String> categoryBox = new JComboBox<>(categories);
        JButton uploadBtn = styledButton("Upload Image", new Color(100, 149, 237));
        uploadBtn.addActionListener(this::onUploadImage);

        imagePreview.setPreferredSize(new Dimension(70, 70));
        imagePreview.setBorder(new LineBorder(new Color(200, 200, 200), 1, true));
        imagePreview.setOpaque(true);
        imagePreview.setBackground(Color.WHITE);

        gbc.gridx = 0; gbc.gridy = 0; form.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1; form.add(nameField, gbc);
        gbc.gridx = 2; form.add(new JLabel("Category:"), gbc);
        gbc.gridx = 3; form.add(categoryBox, gbc);

        gbc.gridx = 0; gbc.gridy = 1; form.add(new JLabel("Price (₹):"), gbc);
        gbc.gridx = 1; form.add(priceField, gbc);
        gbc.gridx = 2; form.add(availBox, gbc);
        gbc.gridx = 3; form.add(uploadBtn, gbc);
        gbc.gridx = 4; form.add(imagePreview, gbc);

        addBtn = styledButton("Add", new Color(70, 130, 180));
        updateBtn = styledButton("Update", new Color(60, 179, 113));
        delBtn = styledButton("Delete", new Color(220, 53, 69));
        updateBtn.setEnabled(false);
        delBtn.setEnabled(false);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        btnPanel.setOpaque(false);
        btnPanel.add(addBtn); btnPanel.add(updateBtn); btnPanel.add(delBtn);

        JPanel formContainer = new JPanel(new BorderLayout());
        formContainer.setOpaque(false);
        formContainer.add(form, BorderLayout.CENTER);
        formContainer.add(btnPanel, BorderLayout.SOUTH);
        add(formContainer, BorderLayout.SOUTH);

        // Button actions
        addBtn.addActionListener(e -> onAdd((String) categoryBox.getSelectedItem()));
        updateBtn.addActionListener(e -> onUpdate((String) categoryBox.getSelectedItem()));
        delBtn.addActionListener(e -> onDelete());

        loadData();
    }

    private JButton styledButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBorder(new EmptyBorder(8, 15, 8, 15));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void onUploadImage(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select Menu Image");
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedImageName = chooser.getSelectedFile().getName();
            imagePreview.setIcon(loadImage(selectedImageName, 70, 70));
        }
    }

    private ImageIcon loadImage(String fileName, int width, int height) {
        if (fileName == null || fileName.isEmpty()) return null;
        try {
            return new ImageIcon(new ImageIcon(
                    getClass().getResource("/images/" + fileName))
                    .getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH));
        } catch (Exception e) {
            return null;
        }
    }

    private void loadData() {
        try {
            cardMap.clear();
            categoryPanels.values().forEach(panel -> {
                panel.removeAll();
                panel.revalidate();
                panel.repaint();
            });

            List<MenuItem> list = dao.getAll();
            for (MenuItem m : list) {
                JPanel card = createMenuCard(m);
                JPanel catPanel = categoryPanels.get(m.getCategory());
                if (catPanel != null) catPanel.add(card);
                cardMap.put(m.getId(), card);
            }
            categoryPanels.values().forEach(JPanel::revalidate);
            categoryPanels.values().forEach(JPanel::repaint);
            clearSelection();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private JPanel createMenuCard(MenuItem m) {
        JPanel card = new JPanel(new BorderLayout());
        card.setPreferredSize(new Dimension(180, 240));
        card.setOpaque(true);
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1, true),
                new EmptyBorder(10, 10, 10, 10)
        ));

        // Hover effect
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                card.setBorder(new CompoundBorder(
                        new LineBorder(new Color(100, 149, 237), 2, true),
                        new EmptyBorder(10, 10, 10, 10)
                ));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                card.setBorder(new CompoundBorder(
                        new LineBorder(new Color(200, 200, 200), 1, true),
                        new EmptyBorder(10, 10, 10, 10)
                ));
            }

            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) { selectItem(m); }
        });

        JLabel imgLabel = new JLabel("", JLabel.CENTER);
        imgLabel.setPreferredSize(new Dimension(160, 120));
        imgLabel.setIcon(loadImage(m.getImagePath(), 160, 120));
        card.add(imgLabel, BorderLayout.CENTER);

        JPanel info = new JPanel(new GridLayout(3, 1, 5, 2));
        info.setOpaque(false);
        info.add(new JLabel(m.getName(), JLabel.CENTER));
        info.add(new JLabel("₹ " + m.getPrice(), JLabel.CENTER));
        JLabel avail = new JLabel(m.isAvailable() ? "Available" : "Out of Stock", JLabel.CENTER);
        avail.setForeground(m.isAvailable() ? new Color(46, 204, 113) : new Color(231, 76, 60));
        info.add(avail);
        card.add(info, BorderLayout.SOUTH);

        return card;
    }

    private void selectItem(MenuItem m) {
        selectedItemId = m.getId();
        nameField.setText(m.getName());
        priceField.setText(String.valueOf(m.getPrice()));
        availBox.setSelected(m.isAvailable());
        selectedImageName = m.getImagePath();
        imagePreview.setIcon(loadImage(selectedImageName, 70, 70));
        updateBtn.setEnabled(true);
        delBtn.setEnabled(true);
    }

    private void clearSelection() {
        selectedItemId = -1;
        updateBtn.setEnabled(false);
        delBtn.setEnabled(false);
        nameField.setText("");
        priceField.setText("");
        availBox.setSelected(true);
        imagePreview.setIcon(null);
        selectedImageName = null;
    }

    private void onAdd(String category) {
        try {
            double price = Double.parseDouble(priceField.getText().trim());
            MenuItem m = new MenuItem(nameField.getText().trim(), category, price, availBox.isSelected());
            m.setImagePath(selectedImageName);
            dao.add(m);
            clearSelection();
            loadData();
        } catch (Exception e) { JOptionPane.showMessageDialog(this, e.getMessage()); }
    }

    private void onUpdate(String category) {
        try {
            double price = Double.parseDouble(priceField.getText().trim());
            MenuItem m = new MenuItem(nameField.getText().trim(), category, price, availBox.isSelected());
            m.setId(selectedItemId);
            m.setImagePath(selectedImageName);
            dao.update(m);
            clearSelection();
            loadData();
        } catch (Exception e) { JOptionPane.showMessageDialog(this, e.getMessage()); }
    }

    private void onDelete() {
        try {
            dao.delete(selectedItemId);
            clearSelection();
            loadData();
        } catch (Exception e) { JOptionPane.showMessageDialog(this, e.getMessage()); }
    }

    // Simple wrap layout for cards
    static class WrapLayout extends FlowLayout {
        public WrapLayout(int align, int hgap, int vgap) { super(align, hgap, vgap); }
        public Dimension preferredLayoutSize(Container target) { return layoutSize(target, true); }
        public Dimension minimumLayoutSize(Container target) { return layoutSize(target, false); }
        private Dimension layoutSize(Container target, boolean preferred) {
            synchronized (target.getTreeLock()) {
                int targetWidth = target.getWidth();
                if (targetWidth == 0) targetWidth = Integer.MAX_VALUE;
                Insets insets = target.getInsets();
                int maxWidth = targetWidth - (insets.left + insets.right + getHgap()*2);
                Dimension dim = new Dimension(0,0);
                int rowWidth=0,rowHeight=0;
                for (Component comp : target.getComponents()) {
                    if (comp.isVisible()) {
                        Dimension d = preferred ? comp.getPreferredSize() : comp.getMinimumSize();
                        if (rowWidth + d.width > maxWidth) {
                            dim.width = Math.max(dim.width,rowWidth);
                            dim.height += rowHeight + getVgap();
                            rowWidth=0; rowHeight=0;
                        }
                        rowWidth += d.width + getHgap();
                        rowHeight = Math.max(rowHeight,d.height);
                    }
                }
                dim.width = Math.max(dim.width,rowWidth);
                dim.height += rowHeight + insets.top + insets.bottom + getVgap()*2;
                return dim;
            }
        }
    }
}
