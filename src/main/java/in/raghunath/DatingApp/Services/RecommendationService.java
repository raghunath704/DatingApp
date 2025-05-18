package in.raghunath.DatingApp.Services;

import in.raghunath.DatingApp.Models.UserModel;
import in.raghunath.DatingApp.Repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationService {
    private final UserRepository userRepository;

    public RecommendationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserModel> recommendUsers(String userId, int topN) {
        UserModel currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<UserModel> allUsers = userRepository.findAll();
        allUsers.removeIf(u -> u.getId().equals(userId)); // Exclude self

        List<ScoredUser> scoredUsers = new ArrayList<>();
        for (UserModel other : allUsers) {
            // Exclude users of the same gender
            if (currentUser.getGender() != null && currentUser.getGender().equals(other.getGender())) {
                continue;
            }
            int score = 0;

            // Shared interests
            if (currentUser.getInterests() != null && other.getInterests() != null) {
                for (String interest : currentUser.getInterests()) {
                    if (other.getInterests().contains(interest)) {
                        score += 30; // Weight for shared interest
                    }
                }
            }

            // Age preference
            Integer otherAge = other.getAge();
            List<Integer> preferredRange = currentUser.getPreferredAgeRange();
            if (otherAge != null && preferredRange != null && preferredRange.size() == 2) {
                if (otherAge >= preferredRange.get(0) && otherAge <= preferredRange.get(1)) {
                    score += 20; // Weight for age match
                }
            }

            if (score > 0) {
                scoredUsers.add(new ScoredUser(other, score));
            }
        }

        // Sort by score descending
        scoredUsers.sort((a, b) -> Integer.compare(b.score, a.score));

        // Return top N matches
        return scoredUsers.stream()
                .limit(topN)
                .map(su -> su.user)
                .collect(Collectors.toList());
    }

    private static class ScoredUser {
        UserModel user;
        int score;
        ScoredUser(UserModel user, int score) {
            this.user = user;
            this.score = score;
        }
    }
}