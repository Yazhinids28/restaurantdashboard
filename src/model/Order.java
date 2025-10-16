package model;

import java.util.List;

public class Order {
    private int id;
    private String tableNumber, status;
    private double total;
    private List<OrderItem> items;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTableNumber() { return tableNumber; }
    public void setTableNumber(String tableNumber) { this.tableNumber = tableNumber; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }
}
