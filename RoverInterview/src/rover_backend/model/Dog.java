package rover_backend.model;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "dogs", schema = "RoverReviews")
public class Dog {
    private int id;
    private String ownerEmail;
    private String name;

    public Dog() {}

    public Dog(String ownerEmail, String name) {
        this.ownerEmail = ownerEmail;
        this.name = name;
    }

    @Id @GeneratedValue
    @Column(name = "id", nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "ownerEmail", nullable = false, length = 100)
    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    @Basic
    @Column(name = "name", length = 100)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dog dog = (Dog) o;
        return id == dog.id &&
                Objects.equals(ownerEmail, dog.ownerEmail) &&
                Objects.equals(name, dog.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, ownerEmail, name);
    }
}
