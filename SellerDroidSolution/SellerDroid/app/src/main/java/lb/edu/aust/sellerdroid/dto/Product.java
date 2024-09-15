package lb.edu.aust.sellerdroid.dto;

/**
 * AUST Student on 18/12/2015.
 */
public class Product extends ProductSummary {
    public String longDescription;
    public Supplier supplier;

    public Product(String productName, String productDescription) {
        super(productName, productDescription);
    }
}
