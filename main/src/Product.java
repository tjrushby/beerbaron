public class Product {
    private String id;
    private String name;
    private String avgPrice;

    public Product(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAvgPrice() {
        return avgPrice;
    }
}