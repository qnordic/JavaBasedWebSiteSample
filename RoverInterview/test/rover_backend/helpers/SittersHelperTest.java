package rover_backend.helpers;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rover_backend.controls.OwnerController;
import rover_backend.controls.ReviewController;
import rover_backend.controls.SitterController;
import rover_backend.model.Review;
import rover_backend.model.Sitter;

import java.sql.Date;

import static org.junit.jupiter.api.Assertions.*;

class SittersHelperTest {
    private SessionFactory sessionFactory;
    private Session session;
    private SitterController sitterController;
    private ReviewController reviewController;
    private SittersHelper sittersHelper;

    private long longStartDate = 151208640L * 1000;
    private long longEndDate = 151217280L * 1000;
    private String email = "test@test.com";
    private String phoneNumber = "5555555555";
    private String name = "Test User";
    private String imageUrl = "http://www.test.com/user";
    private Sitter expectedSitter = new Sitter(email, phoneNumber, name, imageUrl);

    @BeforeEach
    void setUp() {
        // setup the session factory
        Configuration configuration = new Configuration();
        configuration.addAnnotatedClass(OwnerController.class);
        configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        configuration.setProperty("hibernate.connection.driver_class", "org.h2.Driver");
        String urlConfigValue = "jdbc:h2:mem:test;";
        String urlConfigMode = "MODE=Mysql;";
        String urlConfigInit = "INIT=CREATE SCHEMA IF NOT EXISTS ROVERREVIEWS";
        String urlConfigs = urlConfigValue + urlConfigMode + urlConfigInit;
        configuration.setProperty("hibernate.connection.url", urlConfigs);
        configuration.setProperty("hibernate.hbm2ddl.auto", "create");
        configuration.addAnnotatedClass(Review.class);
        configuration.addAnnotatedClass(Sitter.class);
        sessionFactory = configuration.buildSessionFactory();
        session = sessionFactory.openSession();
    }

    @AfterEach
    void tearDown() {
        session.close();
        sessionFactory.close();
    }

    @Test
    void testNothingIsReturnedWhenSitterAndReviewIsEmpty() {
        sittersHelper = new SittersHelper(session);

        assertEquals(0, sittersHelper.getSitterReviews().size());
    }

    @Test
    void testReviewedSitterIsReturnedWhenDataIsPresent() {
        sittersHelper = new SittersHelper();

        assertEquals(100, sittersHelper.getSitterReviews().size());
    }
}