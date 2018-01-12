package rover_backend.model;

import java.util.List;

public class ReviewedSitter extends Sitter {
    private List<Review> reviews;
    private double overallSitterRank;
    private double ratingScore;

    public ReviewedSitter(Sitter s, List<Review> reviews) {
        super(s.getSitterEmail(),  s.getPhoneNumber(), s.getName(), s.getImageUrl());
        this.reviews = reviews;
        if (this.reviews.size() > 0)
            this.calculateReviews();
        else {
            this.ratingScore = 0;
            this.overallSitterRank = 0;
        }
    }

    private void calculateReviews() {
        int totalStays = this.reviews.size();
        double totalScore = 0.0;
        for (Review r : this.reviews) {
            totalScore += r.getRating();
        }

        if (totalStays == 0) {
            this.ratingScore = 0.0;
            this.overallSitterRank = this.getSitterScore();
        } else {
            this.ratingScore = totalScore / totalStays;
            this.overallSitterRank = (this.getSitterScore() + ratingScore) / totalStays;
        }
    }

    public double getRatingScore() {
        return this.ratingScore;
    }

    public double getOverallSitterRank() {
        return this.overallSitterRank;
    }
}
