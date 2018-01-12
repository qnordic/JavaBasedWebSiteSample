package rover_backend.controls;

import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import rover_backend.model.Dog;
import rover_backend.model.Owner;
import rover_backend.util.HibernateUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OwnerController {

    /**
     * Private class objects
     *
     * owners - map of owner email address to owner object
     * dogs - map of owner email address to list of dog objects
     */
    private HashMap<String, Owner> owners = new HashMap<>();
    private HashMap<String, List<String>> dogs = new HashMap<>();
    private Session session;

    public OwnerController() {
        this(HibernateUtil.getSessionFactory().getCurrentSession());
    }

    public OwnerController(Session session) {
        this.session = session;
        init();
    }

    /**
     * Initialization method to setup class session and query the database for all owners and their dogs.
     */
    private synchronized void init() {
        if (owners.size() >= 1) {
            owners.clear();
        }

        session.beginTransaction();

        List dogResults = session.createQuery("from Dog d").list();
        List ownerResults = session.createQuery("from Owner o").list();

        for (Object dog : dogResults) {
            String ownerEmail = ((Dog) dog).getOwnerEmail();
            List<String> temp = (dogs.containsKey(ownerEmail)) ? dogs.get(ownerEmail) : new ArrayList<>();
            temp.add(((Dog) dog).getName());
            dogs.put(ownerEmail, temp);
        }

        for (Object owner : ownerResults) {
            Owner tempOwner = (Owner) owner;
            owners.put(tempOwner.getOwnerEmail(), tempOwner);
        }

        session.getTransaction().commit();
    }

    /**
     * Add an owner to the database.
     * @param ownerEmail - owner email
     * @param phoneNumber - owner phone number
     * @param name - owner name
     * @param image - owner image url
     */
    public void addOwner(String ownerEmail, String phoneNumber, String name, String image) {
        session.beginTransaction();
        Owner o = new Owner(ownerEmail, phoneNumber, name, image);
        session.save(o);
        session.getTransaction().commit();
        owners.put(o.getOwnerEmail(), o);
    }

    /**
     * Add a dog to the dog table
     * @param ownerEmail - owner email address
     * @param name - dog name
     */
    public void addDog(String ownerEmail, String name) {
        session.beginTransaction();
        Dog d = new Dog(ownerEmail, name);
        session.save(d);
        session.getTransaction().commit();

        List<String> temp = (dogs.containsKey(ownerEmail)) ? dogs.get(ownerEmail) : new ArrayList<>();
        temp.add(d.getName());
        dogs.put(ownerEmail, temp);
    }

    /**
     * Get the in memory list of dogs
     * @return - list of dogs
     */
    public HashMap<String, List<String>> getDogs() {
        return dogs;
    }

    /**
     * Get the dog based on the owner email address then dog name
     * @param ownerEmail - owner email
     * @param name - dog name
     * @return - dog object pertaining to the particular dog
     */
    public Dog getDog(String ownerEmail, String name) {
        if (ownerEmail == null || name == null)
            throw new NullPointerException("Owner email and dog name must both be not null");
        if (!dogs.containsKey(ownerEmail))
            throw new ObjectNotFoundException(ownerEmail, "Owner does not exist");

        List<String> temp = dogs.get(ownerEmail);
        int index = temp.indexOf(name);

        if (index < 0)
            throw new ObjectNotFoundException(name, "Dog does not exist.");

        return new Dog(ownerEmail, name);
    }

    /**
     * Return the map of owners
     * @return - map of owners
     */
    public HashMap<String, Owner> getOwners() {
        return owners;
    }

    /**
     * Get the individual owner. If owner does not exist then throw error
     * @param ownerEmail - owner email
     * @return - Owner object
     */
    public Owner retrieveOwner(String ownerEmail) {
        if (!owners.containsKey(ownerEmail)) {
            throw new ObjectNotFoundException(ownerEmail, "Owner was not found with email: " + ownerEmail);
        }
        return owners.get(ownerEmail);
    }

    /**
     * Update owner within the database. If the owner email changes, the record must first be deleted and then added
     * back as a new record due to the primary key set being used.
     * @param oldOwner - old owner object
     * @param newEmail - updated email address
     * @param phoneNumber - udpated phone number
     * @param name - updated name
     * @param image - updated image url
     */
    public void updateOwner(Owner oldOwner, String newEmail, String phoneNumber, String name, String image) {

        Owner newOwner = owners.get(oldOwner.getOwnerEmail());

        newOwner.setOwnerEmail((newEmail!=null && !newEmail.isEmpty()) ? newEmail : oldOwner.getOwnerEmail());
        newOwner.setPhoneNumber((phoneNumber != null && !phoneNumber.isEmpty()) ? phoneNumber : oldOwner.getPhoneNumber());
        newOwner.setName((name != null && !name.isEmpty()) ? name : oldOwner.getName());
        newOwner.setImageUrl((image != null && !image.isEmpty()) ? image : oldOwner.getImageUrl());

        if (!oldOwner.equals(newOwner)) {
            if (!newOwner.getOwnerEmail().equals(oldOwner.getOwnerEmail())) {
                // If the owner email changes the record needs to be deleted and a new record added to the database.
                deleteOwner(oldOwner.getOwnerEmail());
                addOwner(newOwner.getOwnerEmail(), newOwner.getPhoneNumber(), newOwner.getName(), newOwner.getImageUrl());
            } else {
                // If only contact info is changed update the record within the database
                session.beginTransaction();
                session.evict(oldOwner);
                session.update(newOwner);
                session.getTransaction().commit();

                // Once record is updated, the local in memory map needs to be updated as well.
                owners.remove(oldOwner.getOwnerEmail());
                owners.put(newOwner.getOwnerEmail(), newOwner);
            }
        }
    }

    /**
     * Delete the owner record from the database
     * @param ownerEmail - owner email
     */
    public void deleteOwner(String ownerEmail) {
        if (!owners.containsKey(ownerEmail)) {
            throw new ObjectNotFoundException(ownerEmail, "Sitter was found found with email: " + ownerEmail);
        }

        session.beginTransaction();
        session.delete(owners.get(ownerEmail));
        session.getTransaction().commit();
        owners.remove(ownerEmail);
    }
}
