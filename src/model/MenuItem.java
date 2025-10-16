package model;

public class MenuItem {
    private int id;
    private String name;
    private String category;
    private double price;
    private boolean available;
    private String imagePath;

    // --- Default constructor (required for DAO) ---
    public MenuItem() {}

    // --- Constructor used when adding new items ---
    public MenuItem(String name, String category, double price, boolean available) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.available = available;
    }

    // --- Getters and Setters ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    @Override
    public String toString() {
        return String.format("%s (%s) - ₹%.2f [%s]",
                name, category, price, available ? "Available" : "Out of Stock");
    }
}
