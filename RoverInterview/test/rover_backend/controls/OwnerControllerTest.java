package rover_backend.controls;

import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rover_backend.model.Dog;
import rover_backend.model.Owner;

import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OwnerControllerTest {
    private SessionFactory sessionFactory;
    private Session session;
    private OwnerController ownerController;

    private String email = "test@test.com";
    private String phoneNumber = "5555555555";
    private String name = "Test User";
    private String imageUrl = "http://www.test.com/user";
    private Owner expectedOwner = new Owner(email, phoneNumber, name, imageUrl);

    private String dogName = "Fido";
    private Dog expectedDog = new Dog(email, dogName);

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
        configuration.addAnnotatedClass(Owner.class);
        configuration.addAnnotatedClass(Dog.class);
        sessionFactory = configuration.buildSessionFactory();
        session = sessionFactory.openSession();
    }

    @AfterEach
    void tearDown() {
        session.close();
        sessionFactory.close();
    }

    @Test
    void returnsNoOwnersWhenDBIsEmpty() {
        ownerController = new OwnerController(session);
        HashMap<String, Owner> owners = ownerController.getOwners();

        assertEquals(0, owners.size());
    }

    @Test
    void addOwnerAndValidatePresent() {
        ownerController = new OwnerController(session);
        ownerController.addOwner(email, phoneNumber, name, imageUrl);
        HashMap<String, Owner> owners = ownerController.getOwners();

        assertEquals(1, owners.size());
        assertEquals(expectedOwner, owners.get(email));
    }

    @Test
    void addOwnerThenUpdateAllNull() {
        ownerController = new OwnerController(session);
        ownerController.addOwner(email, phoneNumber, name, imageUrl);
        HashMap<String, Owner> owners = ownerController.getOwners();
        assertEquals(1, owners.size());
        assertEquals(expectedOwner, owners.get(email));
        ownerController.updateOwner(expectedOwner, null, null, null, null);
        owners = ownerController.getOwners();
        assertEquals(expectedOwner, owners.get(email));
    }

    @Test
    void addOwnerThenUpdateWithSameInformation() {
        ownerController = new OwnerController(session);
        ownerController.addOwner(email, phoneNumber, name, imageUrl);
        HashMap<String, Owner> owners = ownerController.getOwners();
        assertEquals(1, owners.size());
        assertEquals(expectedOwner, owners.get(email));
        ownerController.updateOwner(expectedOwner, email, phoneNumber, name, imageUrl);
        ownerController = new OwnerController(session);
        owners = ownerController.getOwners();
        assertEquals(expectedOwner, owners.get(email));
    }

    @Test
    void addOwnerThenUpdateEmail() {
        ownerController = new OwnerController(session);
        ownerController.addOwner(email, phoneNumber, name, imageUrl);
        HashMap<String, Owner> owners = ownerController.getOwners();
        assertEquals(1, owners.size());
        assertEquals(expectedOwner, owners.get(email));
        String newEmail = "testupdated@test.com";
        ownerController.updateOwner(expectedOwner, newEmail, null, null, null);
        Owner updatedOwnerExpected = new Owner(newEmail, phoneNumber, name, imageUrl);
        owners = ownerController.getOwners();
        assertEquals(1, owners.size());
        assertEquals(updatedOwnerExpected, owners.get(newEmail));
    }

    @Test
    void addOwnerThenUpdatePhoneNumber() {
        ownerController = new OwnerController(session);
        ownerController.addOwner(email, phoneNumber, name, imageUrl);
        HashMap<String, Owner> owners = ownerController.getOwners();
        assertEquals(1, owners.size());
        assertEquals(expectedOwner, owners.get(email));
        String newPhoneNumber = "1111111111";
        ownerController.updateOwner(expectedOwner, null, newPhoneNumber, null, null);
        Owner updatedOwnerExpected = new Owner(email, newPhoneNumber, name, imageUrl);
        owners = ownerController.getOwners();
        assertEquals(1, owners.size());
        assertEquals(updatedOwnerExpected, owners.get(email));
    }

    @Test
    void addOwnerThenUpdateName() {
        ownerController = new OwnerController(session);
        ownerController.addOwner(email, phoneNumber, name, imageUrl);
        HashMap<String, Owner> owners = ownerController.getOwners();
        assertEquals(1, owners.size());
        assertEquals(expectedOwner, owners.get(email));
        String newName = "New Name";
        ownerController.updateOwner(expectedOwner, null, null, newName, null);
        Owner updatedOwnerExpected = new Owner(email, phoneNumber, newName, imageUrl);
        owners = ownerController.getOwners();
        assertEquals(1, owners.size());
        assertEquals(updatedOwnerExpected, owners.get(email));
    }

    @Test
    void addOwnerThenUpdateImage() {
        ownerController = new OwnerController(session);
        ownerController.addOwner(email, phoneNumber, name, imageUrl);
        HashMap<String, Owner> owners = ownerController.getOwners();
        assertEquals(1, owners.size());
        assertEquals(expectedOwner, owners.get(email));
        String newImage = "http://www.newimage.com/newimage";
        ownerController.updateOwner(expectedOwner, null, phoneNumber, null, newImage);
        Owner updatedOwnerExpected = new Owner(email, phoneNumber, name, newImage);
        owners = ownerController.getOwners();
        assertEquals(1, owners.size());
        assertEquals(updatedOwnerExpected, owners.get(email));
    }

    @Test
    void deleteOwnerFromDatabase() {
        ownerController = new OwnerController(session);
        ownerController.addOwner(email, phoneNumber, name, imageUrl);
        HashMap<String, Owner> owners = ownerController.getOwners();
        assertEquals(1, owners.size());
        assertEquals(expectedOwner, owners.get(email));
        ownerController.deleteOwner(email);
        ownerController = new OwnerController(session);
        owners = ownerController.getOwners();
        assertEquals(0, owners.size());
    }

    @Test
    void deleteNonExistentOwner() {
        ownerController = new OwnerController(session);
        ownerController.addOwner(email, phoneNumber, name, imageUrl);
        HashMap<String, Owner> owners = ownerController.getOwners();
        assertEquals(1, owners.size());
        assertEquals(expectedOwner, owners.get(email));
        assertThrows(ObjectNotFoundException.class,
                () -> { ownerController.deleteOwner("nonexistent@test.com"); },
                "Owner was found found with email: nonexistent@test.com");
    }

    @Test
    void getOwnerExists() {
        ownerController = new OwnerController(session);
        ownerController.addOwner(email, phoneNumber, name, imageUrl);
        Owner owner = ownerController.retrieveOwner(email);
        assertEquals(expectedOwner, owner);
    }

    @Test
    void getOwnerDoesNotExist() {
        ownerController = new OwnerController(session);
        ownerController.addOwner(email, phoneNumber, name, imageUrl);
        assertThrows(ObjectNotFoundException.class,
                () -> { ownerController.retrieveOwner("nonexistent@test.com"); },
                "Owner was found found with email: nonexistent@test.com");
    }

    @Test
    void returnsNoDogsWhenDBIsEmpty() {
        ownerController = new OwnerController(session);
        HashMap<String, List<String>> dogs = ownerController.getDogs();

        assertEquals(0, dogs.size());
    }

    @Test
    void addDogAndValidatePresent() {
        ownerController = new OwnerController(session);
        ownerController.addDog(email, dogName);
        HashMap<String, List<String>> dogs = ownerController.getDogs();

        assertEquals(1, dogs.size());
        assertEquals(expectedDog, ownerController.getDog(email, dogName));
    }

    @Test
    void getDogExists() {
        ownerController = new OwnerController(session);
        ownerController.addDog(email, dogName);
        Dog dog = ownerController.getDog(email, dogName);
        assertEquals(expectedDog, dog);
    }

    @Test
    void getDogWhenOwnerEmailDoesNotExist() {
        ownerController = new OwnerController(session);
        ownerController.addDog(email, dogName);
        assertThrows(ObjectNotFoundException.class,
                () -> { ownerController.getDog("nonexistent@test.com", "fido"); },
                "Owner does not exist");
    }

    @Test
    void getDogWhenDogNameDoesNotExist() {
        ownerController = new OwnerController(session);
        ownerController.addDog(email, dogName);
        assertThrows(ObjectNotFoundException.class,
                () -> { ownerController.getDog("test@test.com", "not_fido"); },
                "Dog does not exist.");

    }

    @Test
    void getDogWhenInputIsNull() {
        ownerController = new OwnerController(session);
        ownerController.addDog(email, dogName);
        assertThrows(NullPointerException.class,
                () -> { ownerController.getDog(null, null); },
                "Owner email and dog name must both be not null");
    }
}
