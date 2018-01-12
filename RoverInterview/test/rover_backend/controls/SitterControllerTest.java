package rover_backend.controls;

import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import rover_backend.model.Sitter;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class SitterControllerTest {
    private SessionFactory sessionFactory;
    private Session session;
    private SitterController sitterController;

    private String email = "test@test.com";
    private String phoneNumber = "5555555555";
    private String name = "Test User";
    private String imageUrl = "http://www.test.com/user";
    private Sitter expectedSitter = new Sitter(email, phoneNumber, name, imageUrl);

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
    void returnsNoSittersWhenDBIsEmpty() {
        sitterController = new SitterController(session);
        HashMap<String, Sitter> sitters = sitterController.getSitters();

        assertEquals(0, sitters.size());
    }

    @Test
    void addSitterAndValidatePresent() {
        sitterController = new SitterController(session);
        sitterController.addSitter(email, phoneNumber, name, imageUrl);
        HashMap<String, Sitter> sitters = sitterController.getSitters();

        assertEquals(1, sitters.size());
        assertEquals(expectedSitter, sitters.get(email));
    }

    @Test
    void addSitterThenUpdateAllNull() {
        sitterController = new SitterController(session);
        sitterController.addSitter(email, phoneNumber, name, imageUrl);
        HashMap<String, Sitter> sitters = sitterController.getSitters();
        assertEquals(1, sitters.size());
        assertEquals(expectedSitter, sitters.get(email));
        sitterController.updateSitter(expectedSitter, null, null, null, null);
        sitters = sitterController.getSitters();
        assertEquals(expectedSitter, sitters.get(email));
    }

    @Test
    void addSitterThenUpdateWithSameInformation() {
        sitterController = new SitterController(session);
        sitterController.addSitter(email, phoneNumber, name, imageUrl);
        HashMap<String, Sitter> sitters = sitterController.getSitters();
        assertEquals(1, sitters.size());
        assertEquals(expectedSitter, sitters.get(email));
        sitterController.updateSitter(expectedSitter, email, phoneNumber, name, imageUrl);
        sitterController = new SitterController(session);
        sitters = sitterController.getSitters();
        assertEquals(expectedSitter, sitters.get(email));
    }

    @Test
    void addSitterThenUpdateEmail() {
        sitterController = new SitterController(session);
        sitterController.addSitter(email, phoneNumber, name, imageUrl);
        HashMap<String, Sitter> sitters = sitterController.getSitters();
        assertEquals(1, sitters.size());
        assertEquals(expectedSitter, sitters.get(email));
        String newEmail = "testupdated@test.com";
        sitterController.updateSitter(expectedSitter, newEmail, null, null, null);
        Sitter updatedSitterExpected = new Sitter(newEmail, phoneNumber, name, imageUrl);
        sitters = sitterController.getSitters();
        assertEquals(1, sitters.size());
        assertEquals(updatedSitterExpected, sitters.get(newEmail));
    }

    @Test
    void addSitterThenUpdatePhoneNumber() {
        sitterController = new SitterController(session);
        sitterController.addSitter(email, phoneNumber, name, imageUrl);
        HashMap<String, Sitter> sitters = sitterController.getSitters();
        assertEquals(1, sitters.size());
        assertEquals(expectedSitter, sitters.get(email));
        String newPhoneNumber = "1111111111";
        sitterController.updateSitter(expectedSitter, null, newPhoneNumber, null, null);
        Sitter updatedSitterExpected = new Sitter(email, newPhoneNumber, name, imageUrl);
        sitters = sitterController.getSitters();
        assertEquals(1, sitters.size());
        assertEquals(updatedSitterExpected, sitters.get(email));
    }

    @Test
    void addSitterThenUpdateName() {
        sitterController = new SitterController(session);
        sitterController.addSitter(email, phoneNumber, name, imageUrl);
        HashMap<String, Sitter> sitters = sitterController.getSitters();
        assertEquals(1, sitters.size());
        assertEquals(expectedSitter, sitters.get(email));
        String newName = "New Name";
        sitterController.updateSitter(expectedSitter, null, null, newName, null);
        Sitter updatedSitterExpected = new Sitter(email, phoneNumber, newName, imageUrl);
        sitters = sitterController.getSitters();
        assertEquals(1, sitters.size());
        assertEquals(updatedSitterExpected, sitters.get(email));
    }

    @Test
    void addSitterThenUpdateImage() {
        sitterController = new SitterController(session);
        sitterController.addSitter(email, phoneNumber, name, imageUrl);
        HashMap<String, Sitter> sitters = sitterController.getSitters();
        assertEquals(1, sitters.size());
        assertEquals(expectedSitter, sitters.get(email));
        String newImage = "http://www.newimage.com/newimage";
        sitterController.updateSitter(expectedSitter, null, phoneNumber, null, newImage);
        Sitter updatedSitterExpected = new Sitter(email, phoneNumber, name, newImage);
        sitters = sitterController.getSitters();
        assertEquals(1, sitters.size());
        assertEquals(updatedSitterExpected, sitters.get(email));
    }

    @Test
    void deleteSitterFromDatabase() {
        sitterController = new SitterController(session);
        sitterController.addSitter(email, phoneNumber, name, imageUrl);
        HashMap<String, Sitter> sitters = sitterController.getSitters();
        assertEquals(1, sitters.size());
        assertEquals(expectedSitter, sitters.get(email));
        sitterController.deleteSitter(email);
        sitterController = new SitterController(session);
        sitters = sitterController.getSitters();
        assertEquals(0, sitters.size());
    }

    @Test
    void deleteNonExistentSitter() {
        sitterController = new SitterController(session);
        sitterController.addSitter(email, phoneNumber, name, imageUrl);
        HashMap<String, Sitter> sitters = sitterController.getSitters();
        assertEquals(1, sitters.size());
        assertEquals(expectedSitter, sitters.get(email));
        assertThrows(ObjectNotFoundException.class,
                () -> { sitterController.deleteSitter("nonexistent@test.com"); },
                "Sitter was found found with email: nonexistent@test.com");
    }

    @Test
    void getSitterExists() {
        sitterController = new SitterController(session);
        sitterController.addSitter(email, phoneNumber, name, imageUrl);
        Sitter sitter = sitterController.getSitter(email);
        assertEquals(expectedSitter, sitter);
    }

    @Test
    void getSitterDoesNotExist() {
        sitterController = new SitterController(session);
        sitterController.addSitter(email, phoneNumber, name, imageUrl);
        assertThrows(ObjectNotFoundException.class,
                () -> { sitterController.getSitter("nonexistent@test.com"); },
                "Sitter was found found with email: nonexistent@test.com");
    }
}