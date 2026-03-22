package com.litpath.litpath.service;

import com.litpath.litpath.dto.ActivityFeedItemDTO;
import com.litpath.litpath.exception.ResourceNotFoundException;
import com.litpath.litpath.model.User;
import com.litpath.litpath.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class ActivityFeedService {

    private final UserRepository userRepository;
    private final BookReviewRepository bookReviewRepository;
    private final UserFavoriteBookRepository favoriteBookRepository;
    private final UserFavoriteAuthorRepository favoriteAuthorRepository;
    private final UserBookListRepository userBookListRepository;
    private final UserFollowRepository userFollowRepository;
    private final ReviewReactionRepository reviewReactionRepository;
    private final ReviewCommentRepository reviewCommentRepository;

    public ActivityFeedService(UserRepository userRepository,
                                BookReviewRepository bookReviewRepository,
                                UserFavoriteBookRepository favoriteBookRepository,
                                UserFavoriteAuthorRepository favoriteAuthorRepository,
                                UserBookListRepository userBookListRepository,
                                UserFollowRepository userFollowRepository,
                                ReviewReactionRepository reviewReactionRepository,
                                ReviewCommentRepository reviewCommentRepository) {
        this.userRepository = userRepository;
        this.bookReviewRepository = bookReviewRepository;
        this.favoriteBookRepository = favoriteBookRepository;
        this.favoriteAuthorRepository = favoriteAuthorRepository;
        this.userBookListRepository = userBookListRepository;
        this.userFollowRepository = userFollowRepository;
        this.reviewReactionRepository = reviewReactionRepository;
        this.reviewCommentRepository = reviewCommentRepository;
    }


    @Transactional(readOnly = true)
    public List<ActivityFeedItemDTO> getMyFeed(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado!"));

        List<ActivityFeedItemDTO> items = new ArrayList<>();

       
        bookReviewRepository.findByUserId(user.getId()).forEach(review -> {
            ActivityFeedItemDTO item = new ActivityFeedItemDTO();
            item.setType("REVIEW");
            item.setCreatedAt(review.getCreatedAt());
            item.setBookId(review.getBook().getId());
            item.setBookTitle(review.getBook().getTitle());
            item.setRating(review.getRating());
            item.setComment(review.getComment());
            items.add(item);
        });

        
        favoriteBookRepository.findByUserId(user.getId()).forEach(fav -> {
            ActivityFeedItemDTO item = new ActivityFeedItemDTO();
            item.setType("FAVORITE_BOOK");
            item.setCreatedAt(fav.getCreatedAt());
            item.setBookId(fav.getBook().getId());
            item.setBookTitle(fav.getBook().getTitle());
            items.add(item);
        });

        
        favoriteAuthorRepository.findByUserId(user.getId()).forEach(fav -> {
            ActivityFeedItemDTO item = new ActivityFeedItemDTO();
            item.setType("FAVORITE_AUTHOR");
            item.setCreatedAt(fav.getCreatedAt());
            item.setAuthorId(fav.getAuthor().getId());
            item.setAuthorName(fav.getAuthor().getName());
            items.add(item);
        });

        
        userBookListRepository.findByUserId(user.getId()).forEach(list ->
            list.getItems().forEach(listItem -> {
                ActivityFeedItemDTO item = new ActivityFeedItemDTO();
                item.setType("JA_LI".equals(list.getListType().name()) ? "READ_BOOK" : "WANT_BOOK");
                item.setCreatedAt(listItem.getCreatedAt());
                item.setBookId(listItem.getBook().getId());
                item.setBookTitle(listItem.getBook().getTitle());
                items.add(item);
            })
        );

        
        userFollowRepository.findByFollowerId(user.getId()).forEach(follow -> {
            ActivityFeedItemDTO item = new ActivityFeedItemDTO();
            item.setType("FOLLOW");
            item.setCreatedAt(follow.getCreatedAt());
            item.setFollowedUserId(follow.getFollowing().getId());
            item.setFollowedUsername(follow.getFollowing().getUsername());
            items.add(item);
        });

        
        reviewReactionRepository.findByUserId(user.getId()).forEach(reaction -> {
            ActivityFeedItemDTO item = new ActivityFeedItemDTO();
            item.setType("REVIEW_REACTION");
            item.setCreatedAt(reaction.getCreatedAt());
            item.setReactionType(reaction.getReactionType().name());
            item.setBookId(reaction.getReview().getBook().getId());
            item.setReviewBookTitle(reaction.getReview().getBook().getTitle());
            items.add(item);
        });

       
        reviewCommentRepository.findByUserId(user.getId()).forEach(comment -> {
            ActivityFeedItemDTO item = new ActivityFeedItemDTO();
            item.setType("REVIEW_COMMENT");
            item.setCreatedAt(comment.getCreatedAt());
            item.setBookId(comment.getReview().getBook().getId());
            item.setReviewBookTitle(comment.getReview().getBook().getTitle());
            item.setComment(comment.getContent());
            item.setFromUserId(comment.getReview().getUser().getId());
            item.setFromUsername(comment.getReview().getUser().getUsername());
            items.add(item);
        });

        items.sort(Comparator.comparing(
                ActivityFeedItemDTO::getCreatedAt,
                Comparator.nullsLast(Comparator.reverseOrder())
        ));

        return items;
    }

    @Transactional(readOnly = true)
    public List<ActivityFeedItemDTO> getReceivedFeed(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado!"));

        List<ActivityFeedItemDTO> items = new ArrayList<>();

        bookReviewRepository.findByUserId(user.getId()).forEach(review -> {

            reviewReactionRepository.findByReviewId(review.getId()).forEach(reaction -> {
                if (reaction.getUser().getId().equals(user.getId())) return;

                ActivityFeedItemDTO item = new ActivityFeedItemDTO();
                item.setType("RECEIVED_REACTION");
                item.setCreatedAt(reaction.getCreatedAt());
                item.setBookId(review.getBook().getId());
                item.setBookTitle(review.getBook().getTitle());
                item.setReactionType(reaction.getReactionType().name());
                item.setFromUserId(reaction.getUser().getId());
                item.setFromUsername(reaction.getUser().getUsername());
                items.add(item);
            });

            reviewCommentRepository.findByReviewId(review.getId()).forEach(comment -> {
                if (comment.getUser().getId().equals(user.getId())) return;

                ActivityFeedItemDTO item = new ActivityFeedItemDTO();
                item.setType("RECEIVED_COMMENT");
                item.setCreatedAt(comment.getCreatedAt());
                item.setBookId(review.getBook().getId());
                item.setBookTitle(review.getBook().getTitle());
                item.setComment(comment.getContent());
                item.setFromUserId(comment.getUser().getId());
                item.setFromUsername(comment.getUser().getUsername());
                items.add(item);
            });
        });

        userFollowRepository.findByFollowingId(user.getId()).forEach(follow -> {
            ActivityFeedItemDTO item = new ActivityFeedItemDTO();
            item.setType("NEW_FOLLOWER");
            item.setCreatedAt(follow.getCreatedAt());
            item.setFromUserId(follow.getFollower().getId());
            item.setFromUsername(follow.getFollower().getUsername());
            items.add(item);
        });

        items.sort(Comparator.comparing(
                ActivityFeedItemDTO::getCreatedAt,
                Comparator.nullsLast(Comparator.reverseOrder())
        ));

        return items;
    }
}