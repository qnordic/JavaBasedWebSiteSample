package rover_backend.controls;

import org.hibernate.Session;
import rover_backend.model.Review;
import rover_backend.util.HibernateUtil;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReviewController {

    private List<Review> reviews = new ArrayList<>();
    private Session session;

    /*
    Primary constructor used in production.
     */
    public ReviewController() {
        this(HibernateUtil.getSessionFactory().getCurrentSession());
    }

    /*
    Construction primarily used for testing, this allows a custom session type to be put in place of the production
    setup.
     */
    public ReviewController(Session session) {
        if (session == null)
            throw new NullPointerException("Session cannot be null");
        this.session = session;
        init();
    }

    /*
    Helper function to run synchronization process between Java objects and the database.
    Initializes the session and factory to be used for accessing and writing to the database. Also updates the local
    in memory object anytime the object is created.
     */
    private synchronized void init() {
        if (reviews.size() >= 1) {
            reviews.clear();
        }

        session.beginTransaction();

        List reviewResults = session.createQuery("from Review r").list();

        for (Object review : reviewResults) {
            reviews.add((Review) review);
        }

        session.getTransaction().commit();
    }

    /**
     * Add review to the database and local in memory list object.
     * @param sitterEmail - email address of the sitter, this is enforced by the logged in user info (for sake of
     *                    project the validation code was not written here)
     * @param ownerEmail - email address of the owner, enforced by the owner profile (for sake of this project
     *                   validation code was not written here)
     * @param dog - dog name(s), separated by a "|"
     * @param startDate - start date in java "long" form
     * @param endDate - end date in java "long" form
     * @param rating - integer rating between 1-5
     * @param reviewText - any text left by owner
     */
    public void addReview(String sitterEmail, String ownerEmail, String dog, Date startDate, Date endDate, Integer rating, String reviewText) {
        // Validate dog does not have any special characters and only has |
        Pattern regex = Pattern.compile("[$&+,:;=?@#'<>.^*()%!-]");
        Matcher matcher = regex.matcher(dog);
        if (matcher.find()) {
            throw new IllegalArgumentException("Dog name can only contain letters and must be separated by a \"|\"");
        }

        if (endDate.before(startDate)) {
            throw new IllegalArgumentException("End date must come after start date.");
        }

        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating can only be in [1,5]");
        }

        session.beginTransaction();
        Review r = new Review(startDate, endDate, rating, reviewText, ownerEmail, sitterEmail, dog);
        session.save(r);
        session.getTransaction().commit();
        reviews.add(r);
    }

    public List<Review> listReviews() {
        return reviews;
    }
}
