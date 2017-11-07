package hotelapp;

import java.util.ArrayList;
import java.util.List;

/**
 * Class ExpediaAttractions.
 * A attraction object for Expedia result.
 *
 * @author Brian Sung
 */
public class ExpediaAttractions {
    private String id;
    private String name;
    private String link;
    private double rating;
    private double activityPrice;
    private String description;
    private String activityLocation;
    private List<String> meetingOrRedemptionPoint;

    /**
     * Constructor for class ExpediaAttractions.
     *
     * @param id
     * @param name
     * @param link
     * @param rating
     * @param activityPrice
     * @param description
     * @param activityLocation
     */
    public ExpediaAttractions(String id, String name, String link, double rating, double activityPrice
            , String description, String activityLocation) {
        this.id = id;
        this.name = name;
        this.link = link;
        this.rating = rating;
        this.activityPrice = activityPrice;
        this.description = description;
        this.activityLocation = activityLocation;
        this.meetingOrRedemptionPoint = new ArrayList<>();
    }

    /**
     * Add meeting point address.
     *
     * @param point
     */
    public void addMeetingOrRedemptionPoint(String point) {
        this.meetingOrRedemptionPoint.add(point);
    }

    /**
     * Return the id of this attraction.
     *
     * @return String
     */
    public String getId() {
        return this.id;
    }

    /**
     * Return the name of this attraction.
     *
     * @return String
     */
    public String getName() {
        return this.name;
    }

    /**
     * Return the link of this attraction on Expedia.
     *
     * @return String
     */
    public String getLink() {
        return this.link;
    }

    /**
     * Return the rating of this attraction.
     *
     * @return double
     */
    public double getRating() {
        return this.rating;
    }

    /**
     * Return the price of this attraction.
     *
     * @return double
     */
    public double getActivityPrice() {
        return this.activityPrice;
    }

    /**
     * Return the description of this attraction.
     *
     * @return String
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Return the location of this attraction.
     *
     * @return String
     */
    public String getActivityLocation() {
        return (activityLocation.endsWith(" ") ?
                activityLocation.substring(0, activityLocation.length() - 1) : activityLocation);
    }

    /**
     * Copy and return a list of the meeting point address of this attraction.
     *
     * @return List of address
     */
    public List<String> getMeetingOrRedemptionPoint() {
        List<String> result = new ArrayList<>();

        for (String mp : this.meetingOrRedemptionPoint) {
            result.add((mp.endsWith(" ") ? mp.substring(0, mp.length() - 1) : mp));
        }

        return result;
    }
}
