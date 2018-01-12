package rover_backend.controls;

import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import rover_backend.model.Sitter;
import rover_backend.util.HibernateUtil;

import java.util.*;

public class SitterController {

    private HashMap<String, Sitter> sitters = new HashMap<>();
    private Session session;

    public SitterController() {
        this(HibernateUtil.getSessionFactory().getCurrentSession());
    }

    public SitterController(Session session) {
        if (session == null)
            throw new NullPointerException("Session cannot be null");
        this.session = session;
        init();
    }

    /**
     * Initialization function for session and getting select * query to run from database.
     * Creates the underlying hashmap of sitters to be propagated for serving data from.
     */
    private synchronized void init() {
        if (sitters.size() >= 1) {
            sitters.clear();
        }
        session.beginTransaction();

        List sitterResults = session.createQuery("FROM Sitter s").list();

        session.getTransaction().commit();

        for (Object sitter : sitterResults) {
            sitters.put(((Sitter) sitter).getSitterEmail(), (Sitter) sitter);
        }
    }

    /**
     * Function to return hashmap of all available sitters
     * @return - map of sitter email address to sitter object
     */
    public HashMap<String, Sitter> getSitters() {
        return sitters;
    }

    /**
     * Get the sitter object based on the siter email. Throw an error if the sitter does not exist in map.
     * @param sitterEmail - sitter email
     * @return - Sitter object based on sitter email
     */
    public Sitter getSitter(String sitterEmail) {
        if (!sitters.containsKey(sitterEmail)) {
            throw new ObjectNotFoundException(sitterEmail, "Sitter was found found with email: " + sitterEmail);
        }
        return sitters.get(sitterEmail);
    }

    /**
     * Add a sitter to the database based on valid inputs.
     * @param sitterEmail - sitter email address
     * @param phoneNumber - sitter phone numbrer
     * @param name - sitter name
     * @param image - sitter image url
     */
    public void addSitter(String sitterEmail, String phoneNumber, String name, String image) {
        session.beginTransaction();
        Sitter s = new Sitter(sitterEmail, phoneNumber, name, image);
        session.save(s);
        session.getTransaction().commit();
        sitters.put(s.getSitterEmail(), s);
    }

    /**
     * Update sitter information based on new data about the sitter. If the sitter updates their email address than the
     * record must be first deleted then reentered into the database as the email is used as the primary key. All inputs
     * can be null if nothing to update.
     * @param oldSitterInfo - sitter object for sitter to be udpated
     * @param newEmail - new email address
     * @param phoneNumber - new phone number
     * @param name - new name
     * @param image - new image
     */
    public void updateSitter(Sitter oldSitterInfo, String newEmail, String phoneNumber, String name, String image) {

        Sitter newSitter = sitters.get(oldSitterInfo.getSitterEmail());

        // Assign values for the new sitter obejct.
        newSitter.setSitterEmail((newEmail!=null && !newEmail.isEmpty()) ? newEmail : oldSitterInfo.getSitterEmail());
        newSitter.setPhoneNumber((phoneNumber!=null && !phoneNumber.isEmpty()) ? phoneNumber : oldSitterInfo.getPhoneNumber());
        newSitter.setName((name!=null && !name.isEmpty()) ? name : oldSitterInfo.getName());
        newSitter.setImageUrl((image!=null && !image.isEmpty()) ? image : oldSitterInfo.getImageUrl());

        if (!oldSitterInfo.equals(newSitter)) {
            if (!newSitter.getSitterEmail().equals(oldSitterInfo.getSitterEmail())) {
                // If the sitter changed their email address delete the previous database entry and add the sitter back with
                // their new email address. This is due to the email address being the primary key in the sitters table.
                deleteSitter(oldSitterInfo.getSitterEmail());
                addSitter(newSitter.getSitterEmail(), newSitter.getPhoneNumber(), newSitter.getName(), newSitter.getImageUrl());
            } else {
                // If only the contact info changed for the sitter or image then update the existing entry in the database.
                session.beginTransaction();
                session.evict(oldSitterInfo);
                session.update(newSitter);
                session.getTransaction().commit();

                // Update the sitter within the local hashmap if anything changed. This prevents an additional query to the
                // database.
                sitters.remove(oldSitterInfo.getSitterEmail());
                sitters.put(newSitter.getSitterEmail(), newSitter);
            }
        }
    }

    /**
     * Delete the sitter record based on the given email address. If the sitter does not exist than an error is thrown.
     * @param sitterEmail - sitter email
     */
    public void deleteSitter(String sitterEmail) {
        if (!sitters.containsKey(sitterEmail)) {
            throw new ObjectNotFoundException(sitterEmail, "Sitter was found found with email: " + sitterEmail);
        }
        session.beginTransaction();
        session.delete(sitters.get(sitterEmail));
        session.getTransaction().commit();
        sitters.remove(sitterEmail);
    }
}
