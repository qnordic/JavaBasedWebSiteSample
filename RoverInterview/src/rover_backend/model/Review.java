package rover_backend.model;

import javax.persistence.*;
import java.sql.Date;
import java.util.Objects;

@Entity
@Table(name = "reviews", schema = "RoverReviews")
public class Review {
    private int id;
    private String sitterEmail;
    private String ownerEmail;
    private Date endDate;
    private Date startDate;
    private Integer rating;
    private String reviewText;
    private String dogs;

    public Review() {}

    public Review(Date startDate, Date endDate, Integer rating, String reviewText, String ownerEmail, String sitterEmail, String dogs) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.rating = rating;
        this.reviewText = reviewText;
        this.ownerEmail = ownerEmail;
        this.sitterEmail = sitterEmail;
        this.dogs = dogs;
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
    @Column(name = "sitterEmail", length = 100)
    public String getSitterEmail() {
        return sitterEmail;
    }

    public void setSitterEmail(String sitterEmail) {
        this.sitterEmail = sitterEmail;
    }

    @Basic
    @Column(name = "ownerEmail", length = 100)
    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    @Basic
    @Column(name = "endDate")
    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Basic
    @Column(name = "startDate")
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Basic
    @Column(name = "rating")
    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    @Basic
    @Column(name = "reviewText")
    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    @Basic
    @Column(name = "dogs")
    public String getDogs() { return dogs; }

    public void setDogs(String dogs) { this.dogs = dogs; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Review review = (Review) o;
        return id == review.id &&
                Objects.equals(sitterEmail, review.sitterEmail) &&
                Objects.equals(ownerEmail, review.ownerEmail) &&
                Objects.equals(endDate, review.endDate) &&
                Objects.equals(startDate, review.startDate) &&
                Objects.equals(rating, review.rating) &&
                Objects.equals(reviewText, review.reviewText) &&
                Objects.equals(dogs, review.dogs);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, sitterEmail, ownerEmail, endDate, startDate, rating, reviewText, dogs);
    }
}
