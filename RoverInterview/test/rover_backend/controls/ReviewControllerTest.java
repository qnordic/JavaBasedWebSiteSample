package rover_backend.controls;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rover_backend.model.Review;

import java.sql.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReviewControllerTest {

    private SessionFactory sessionFactory;
    private Session session;
    private ReviewController reviewController;
    private long longStartDate = 151208640L * 1000;
    private long longEndDate = 151217280L * 1000;

    @BeforeEach
    void setUp() {
        // setup the session factory
        Configuration configuration = new Configuration();
        configuration.addAnnotatedClass(SitterController.class);
        configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        configuration.setProperty("hibernate.connection.driver_class", "org.h2.Driver");
        String urlConfigValue = "jdbc:h2:mem:test;";
        String urlConfigMode = "MODE=Mysql;";
        String urlConfigInit = "INIT=CREATE SCHEMA IF NOT EXISTS ROVERREVIEWS";
        String urlConfigs = urlConfigValue + urlConfigMode + urlConfigInit;
        configuration.setProperty("hibernate.connection.url", urlConfigs);
        configuration.setProperty("hibernate.hbm2ddl.auto", "create");
        configuration.addAnnotatedClass(Review.class);
        sessionFactory = configuration.buildSessionFactory();
        session = sessionFactory.openSession();
    }

    @AfterEach
    void tearDown() {
        session.close();
        sessionFactory.close();
    }

    @Test
    void addReviewAndValidate() {
        reviewController = new ReviewController(session);
        reviewController.addReview("sitter@test.com", "owner@test.com", "dog",
                new Date(longStartDate), new Date(longEndDate), 5, "Blah blah blah");
        List<Review> reviewList = new ReviewController(session).listReviews();
        assertEquals(1, reviewList.size());
    }

    @Test
    void addReviewWithBadDates() {
        reviewController = new ReviewController(session);
        assertThrows( IllegalArgumentException.class,
                () -> {
                    reviewController.addReview("sitter@test.com", "owner@test.com", "dog",
                            new Date(longEndDate), new Date(longStartDate), 5,
                            "Blah blah blah");
                },
                "End date must come after start date."
        );
    }

    @Test
    void addReviewWithBadReviewInput() {
        reviewController = new ReviewController(session);
        assertThrows( IllegalArgumentException.class,
                () -> {
                    reviewController.addReview("sitter@test.com", "owner@test.com", "dog",
                            new Date(longStartDate), new Date(longEndDate), 7,
                            "Blah blah blah");
                },
                "Rating can only be in [1,5]"
        );
    }

    @Test
    void listReviews() {
        reviewController = new ReviewController(session);
        assertEquals(0, reviewController.listReviews().size());
        reviewController.addReview("sitter@test.com", "owner@test.com", "dog",
                new Date(longStartDate), new Date(longEndDate), 5, "Blah blah blah");
        List<Review> reviewList = new ReviewController(session).listReviews();
        assertEquals(1, reviewList.size());
    }
}
