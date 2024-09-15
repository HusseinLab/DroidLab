package lb.edu.aust.sellerdroid.dto;

/**
 * Created by AUST Student on 19/12/2015.
 */
public class ProductSummary extends Entity {

    public float averageRating = 2.5f;
    public float currentUserRating = -1;

    public ProductSummary() {}

    public ProductSummary(String theName, String theDescription) {
        this.name = theName;
        this.description = theDescription;
    }
}
