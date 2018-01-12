package rover_backend.model;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "owners", schema = "RoverReviews")
public class Owner {
    private String ownerEmail;
    private String phoneNumber;
    private String name;
    private String imageUrl;

    public Owner() {}

    public Owner(String ownerEmail, String phoneNumber, String name, String image) {
        this.ownerEmail = ownerEmail;
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.imageUrl = image;
    }

    @Id
    @Column(name = "ownerEmail", nullable = false, length = 100)
    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    @Basic
    @Column(name = "phoneNumber", length = 100)
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Basic
    @Column(name = "name", length = 100)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "imageUrl")
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Owner owner = (Owner) o;
        return Objects.equals(ownerEmail, owner.ownerEmail) &&
                Objects.equals(phoneNumber, owner.phoneNumber) &&
                Objects.equals(name, owner.name) &&
                Objects.equals(imageUrl, owner.imageUrl);
    }

    @Override
    public int hashCode() {

        return Objects.hash(ownerEmail, phoneNumber, name, imageUrl);
    }
}
