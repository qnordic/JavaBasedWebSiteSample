package rover_backend.model;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "sitters", schema = "RoverReviews")
public class Sitter {
    private String sitterEmail;
    private String phoneNumber;
    private String name;
    private String imageUrl;
    private Double sitterScore;

    public Sitter() {}

    public Sitter(String sitterEmail, String phoneNumber, String name, String image) {
        this.sitterEmail = sitterEmail;
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.imageUrl = image;
        this.sitterScore = calculateSitterScore(name);
    }

    public Sitter(String sitterEmail, String phoneNumber, String name, String image, Double sitterScore) {
        this.sitterEmail = sitterEmail;
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.imageUrl = image;
        this.sitterScore = sitterScore;
    }

    private Double calculateSitterScore(String name) {
        Set<Character> uniqueLetters = new HashSet<>();
        for (char c : name.toLowerCase().toCharArray()) {
            if (Character.isLetter(c)) {
                uniqueLetters.add(c);
            }
        }
        return 5.0 * uniqueLetters.size() / 26;
    }

    @Id
    @Column(name = "sitterEmail", nullable = false, length = 100)
    public String getSitterEmail() {
        return sitterEmail;
    }

    public void setSitterEmail(String sitterEmail) {
        this.sitterEmail = sitterEmail;
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
        this.sitterScore = this.calculateSitterScore(name);
    }

    @Basic
    @Column(name = "imageUrl")
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Basic
    @Column(name = "sitterScore")
    protected Double getSitterScore() {
        return sitterScore;
    }

    protected void setSitterScore(Double sitterScore) {
        this.sitterScore = sitterScore;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sitter that = (Sitter) o;
        return Objects.equals(sitterEmail, that.sitterEmail) &&
                Objects.equals(phoneNumber, that.phoneNumber) &&
                Objects.equals(name, that.name) &&
                Objects.equals(imageUrl, that.imageUrl) &&
                Objects.equals(sitterScore, that.sitterScore);
    }

    @Override
    public int hashCode() {

        return Objects.hash(sitterEmail, phoneNumber, name, imageUrl, sitterScore);
    }
}
