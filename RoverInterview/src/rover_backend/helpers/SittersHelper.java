package rover_backend.helpers;

import org.hibernate.Session;
import rover_backend.controls.ReviewController;
import rover_backend.controls.SitterController;
import rover_backend.model.Review;
import rover_backend.model.ReviewedSitter;
import rover_backend.model.Sitter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SittersHelper {
    /**
     * Helper class that handles the creation of sitters with their complete reviews as well as necessary information
     * to populate the Sitter UI in the web page.
     */

    private HashMap<ReviewedSitter, ReviewedSitter> reviewedSitters;
    private Session session;

    /*
    Primary constructor used in production.
     */
    public SittersHelper() { }

    /*
    Construction primarily used for testing, this allows a custom session type to be put in place of the production
    setup.
     */
    public SittersHelper(Session session) {
        if (session == null)
            throw new NullPointerException("Session cannot be null");
        this.session = session;
    }

    public List<ReviewedSitter> getSitterReviews() {
        setupSitterReviewsHelper();
        return new ArrayList<>(reviewedSitters.keySet());
    }

    private void setupSitterReviewsHelper() {
        List<Review> reviewList;
        HashMap<String, Sitter> sitterMap;
        HashMap<Sitter, List<Review>> sitterToReviewList;

        if (session != null) {
            sitterMap = new SitterController(session).getSitters();
            reviewList = new ReviewController(session).listReviews();
        } else {
            sitterMap = new SitterController().getSitters();
            reviewList = new ReviewController().listReviews();
        }

        sitterToReviewList = new HashMap<>();

        for (Review r : reviewList) {
            Sitter s = sitterMap.get(r.getSitterEmail());
            List<Review> tempList = sitterToReviewList.containsKey(s) ? sitterToReviewList.get(s) : new ArrayList<>();
            tempList.add(r);
            sitterToReviewList.put(s, tempList);
        }

        for (Sitter s : sitterMap.values()) {
            if (!sitterToReviewList.containsKey(s))
                sitterToReviewList.put(s, new ArrayList<>());
        }

        reviewedSitters = new HashMap<>();

        for (Sitter s : sitterToReviewList.keySet()) {
            ReviewedSitter rs = new ReviewedSitter(s, sitterToReviewList.get(s));
            reviewedSitters.put(rs, rs);
        }
    }
}
