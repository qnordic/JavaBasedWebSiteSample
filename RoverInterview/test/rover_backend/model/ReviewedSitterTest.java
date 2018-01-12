package rover_backend.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReviewedSitterTest {

    private String email = "test@test.com";
    private String phoneNumber = "5555555555";
    private String name = "abcdefghijklmnopqrstuvwxyz";
    private String imageUrl = "http://www.test.com/user";
    private double expectedSitterScore = 5.0;
    private Sitter expectedSitter = new Sitter(email, phoneNumber, name, imageUrl);

    @Test
    void verifyInitialSitterScore() {
        ReviewedSitter reviewedSitter = new ReviewedSitter(expectedSitter, new ArrayList<>());
        assertEquals((Double) expectedSitterScore, reviewedSitter.getSitterScore());
    }
}