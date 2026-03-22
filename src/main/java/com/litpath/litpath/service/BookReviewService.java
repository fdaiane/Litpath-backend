package com.litpath.litpath.service;

import com.litpath.litpath.dto.*;
import com.litpath.litpath.exception.BusinessException;
import com.litpath.litpath.exception.ResourceNotFoundException;
import com.litpath.litpath.model.*;
import com.litpath.litpath.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookReviewService {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final BookReviewRepository bookReviewRepository;
    private final ReviewReactionRepository reviewReactionRepository;
    private final ReviewCommentRepository reviewCommentRepository;
    private final ReviewCommentReactionRepository reviewCommentReactionRepository;

    public BookReviewService(UserRepository userRepository,
                              BookRepository bookRepository,
                              BookReviewRepository bookReviewRepository,
                              ReviewReactionRepository reviewReactionRepository,
                              ReviewCommentRepository reviewCommentRepository,
                              ReviewCommentReactionRepository reviewCommentReactionRepository) {
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.bookReviewRepository = bookReviewRepository;
        this.reviewReactionRepository = reviewReactionRepository;
        this.reviewCommentRepository = reviewCommentRepository;
        this.reviewCommentReactionRepository = reviewCommentReactionRepository;
    }

    
    @Transactional
    public BookReviewResponseDTO createReview(String email, Long bookId, BookReviewRequestDTO dto) {
        User user = findUserByEmail(email);

        if (bookReviewRepository.existsByUserIdAndBookId(user.getId(), bookId)) {
            throw new BusinessException("Você já avaliou este livro!");
        }

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Livro não encontrado!"));

        BookReview review = new BookReview();
        review.setUser(user);
        review.setBook(book);
        review.setRating(dto.getRating());
        review.setComment(sanitizeComment(dto.getComment()));
        review.setCreatedAt(LocalDateTime.now());

        review = bookReviewRepository.save(review);
        return toReviewDTO(review, user.getId());
    }

    
    @Transactional
    public BookReviewResponseDTO updateReview(String email, Long reviewId, BookReviewRequestDTO dto) {
        User user = findUserByEmail(email);
        BookReview review = findReviewById(reviewId);

        if (!review.getUser().getId().equals(user.getId())) {
            throw new BusinessException("Você não tem permissão para editar esta avaliação!");
        }

        review.setRating(dto.getRating());
        review.setComment(sanitizeComment(dto.getComment()));
        review.setUpdatedAt(LocalDateTime.now());

        review = bookReviewRepository.save(review);
        return toReviewDTO(review, user.getId());
    }

    
    @Transactional
    public void deleteReview(String email, Long reviewId) {
        User user = findUserByEmail(email);
        BookReview review = findReviewById(reviewId);

        if (!review.getUser().getId().equals(user.getId())) {
            throw new BusinessException("Você não tem permissão para deletar esta avaliação!");
        }

        bookReviewRepository.delete(review);
    }

    
    public List<BookReviewResponseDTO> getReviewsByBook(Long bookId, String email) {
        User user = findUserByEmail(email);

        return bookReviewRepository.findByBookId(bookId)
                .stream()
                .map(review -> toReviewDTO(review, user.getId()))
                .collect(Collectors.toList());
    }

    
    public List<BookReviewResponseDTO> getMyReviews(String email) {
        User user = findUserByEmail(email);

        return bookReviewRepository.findByUserId(user.getId())
                .stream()
                .map(review -> toReviewDTO(review, user.getId()))
                .collect(Collectors.toList());
    }

    
    public Double getAverageRating(Long bookId) {
        if (!bookRepository.existsById(bookId)) {
            throw new ResourceNotFoundException("Livro não encontrado!");
        }
        Double avg = bookReviewRepository.findAverageRatingByBookId(bookId);
        return avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0;
    }

    
    @Transactional
    public BookReviewResponseDTO reactToReview(String email, Long reviewId, ReviewReactionRequestDTO dto) {
        User user = findUserByEmail(email);
        BookReview review = findReviewById(reviewId);

        if (review.getUser().getId().equals(user.getId())) {
            throw new BusinessException("Você não pode reagir à sua própria avaliação!");
        }

        reviewReactionRepository.findByUserIdAndReviewId(user.getId(), reviewId)
                .ifPresentOrElse(
                        existing -> {
                            existing.setReactionType(dto.getReactionType());
                            reviewReactionRepository.save(existing);
                        },
                        () -> {
                            ReviewReaction reaction = new ReviewReaction();
                            reaction.setUser(user);
                            reaction.setReview(review);
                            reaction.setReactionType(dto.getReactionType());
                            reviewReactionRepository.save(reaction);
                        }
                );

        return toReviewDTO(review, user.getId());
    }

    
    @Transactional
    public void removeReactionFromReview(String email, Long reviewId) {
        User user = findUserByEmail(email);

        ReviewReaction reaction = reviewReactionRepository
                .findByUserIdAndReviewId(user.getId(), reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Reação não encontrada!"));

        reviewReactionRepository.delete(reaction);
    }

    
    @Transactional
    public ReviewCommentResponseDTO addComment(String email, Long reviewId, ReviewCommentRequestDTO dto) {
        User user = findUserByEmail(email);
        BookReview review = findReviewById(reviewId);

        ReviewComment comment = new ReviewComment();
        comment.setUser(user);
        comment.setReview(review);
        comment.setContent(dto.getContent());
        comment.setCreatedAt(LocalDateTime.now());

        comment = reviewCommentRepository.save(comment);
        return toCommentDTO(comment, user.getId());
    }

    
    @Transactional
    public ReviewCommentResponseDTO updateComment(String email, Long commentId, ReviewCommentRequestDTO dto) {
        User user = findUserByEmail(email);
        ReviewComment comment = findCommentById(commentId);

        if (!comment.getUser().getId().equals(user.getId())) {
            throw new BusinessException("Você não tem permissão para editar este comentário!");
        }

        comment.setContent(dto.getContent());
        comment.setUpdatedAt(LocalDateTime.now());

        comment = reviewCommentRepository.save(comment);
        return toCommentDTO(comment, user.getId());
    }

    
    @Transactional
    public void deleteComment(String email, Long commentId) {
        User user = findUserByEmail(email);
        ReviewComment comment = findCommentById(commentId);

        if (!comment.getUser().getId().equals(user.getId())) {
            throw new BusinessException("Você não tem permissão para deletar este comentário!");
        }

        reviewCommentRepository.delete(comment);
    }

    
    @Transactional
    public ReviewCommentResponseDTO reactToComment(String email, Long commentId, ReviewReactionRequestDTO dto) {
        User user = findUserByEmail(email);
        ReviewComment comment = findCommentById(commentId);

        if (comment.getUser().getId().equals(user.getId())) {
            throw new BusinessException("Você não pode reagir ao seu próprio comentário!");
        }

        reviewCommentReactionRepository.findByUserIdAndCommentId(user.getId(), commentId)
                .ifPresentOrElse(
                        existing -> {
                            existing.setReactionType(dto.getReactionType());
                            reviewCommentReactionRepository.save(existing);
                        },
                        () -> {
                            ReviewCommentReaction reaction = new ReviewCommentReaction();
                            reaction.setUser(user);
                            reaction.setComment(comment);
                            reaction.setReactionType(dto.getReactionType());
                            reviewCommentReactionRepository.save(reaction);
                        }
                );

        return toCommentDTO(comment, user.getId());
    }

   
    @Transactional
    public void removeReactionFromComment(String email, Long commentId) {
        User user = findUserByEmail(email);

        ReviewCommentReaction reaction = reviewCommentReactionRepository
                .findByUserIdAndCommentId(user.getId(), commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Reação não encontrada!"));

        reviewCommentReactionRepository.delete(reaction);
    }

    
    private BookReviewResponseDTO toReviewDTO(BookReview review, Long loggedUserId) {
        BookReviewResponseDTO dto = new BookReviewResponseDTO();
        dto.setId(review.getId());
        dto.setBookId(review.getBook().getId());
        dto.setBookTitle(review.getBook().getTitle());
        dto.setUserId(review.getUser().getId());
        dto.setUsername(review.getUser().getUsername());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment()); 
        dto.setCreatedAt(review.getCreatedAt());
        dto.setUpdatedAt(review.getUpdatedAt());

        dto.setLikes((int)reviewReactionRepository.countByReviewIdAndReactionType(review.getId(), ReactionType.LIKE));
        dto.setDislikes((int)reviewReactionRepository.countByReviewIdAndReactionType(review.getId(), ReactionType.DISLIKE));

        reviewReactionRepository.findByUserIdAndReviewId(loggedUserId, review.getId())
                .ifPresent(r -> dto.setMyReaction(r.getReactionType()));

        List<ReviewCommentResponseDTO> comments = reviewCommentRepository.findByReviewId(review.getId())
                .stream()
                .map(c -> toCommentDTO(c, loggedUserId))
                .collect(Collectors.toList());
        dto.setComments(comments);

        return dto;
    }

    
    private ReviewCommentResponseDTO toCommentDTO(ReviewComment comment, Long loggedUserId) {
        ReviewCommentResponseDTO dto = new ReviewCommentResponseDTO();
        dto.setId(comment.getId());
        dto.setReviewId(comment.getReview().getId());
        dto.setUserId(comment.getUser().getId());
        dto.setUsername(comment.getUser().getUsername());
        dto.setContent(comment.getContent());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setUpdatedAt(comment.getUpdatedAt());

        dto.setLikes(reviewCommentReactionRepository.countByCommentIdAndReactionType(comment.getId(), ReactionType.LIKE));
        dto.setDislikes(reviewCommentReactionRepository.countByCommentIdAndReactionType(comment.getId(), ReactionType.DISLIKE));

        reviewCommentReactionRepository.findByUserIdAndCommentId(loggedUserId, comment.getId())
                .ifPresent(r -> dto.setMyReaction(r.getReactionType()));

        return dto;
    }

   
    private String sanitizeComment(String comment) {
        return (comment != null && !comment.isBlank()) ? comment.trim() : null;
    }

   
    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado!"));
    }

    private BookReview findReviewById(Long reviewId) {
        return bookReviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Avaliação não encontrada!"));
    }

    private ReviewComment findCommentById(Long commentId) {
        return reviewCommentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comentário não encontrado!"));
    }

    public List<BookReviewResponseDTO> getReviewsByUserId(Long targetUserId, String loggedEmail) {
        User loggedUser = findUserByEmail(loggedEmail);
        return bookReviewRepository.findByUserId(targetUserId)
                .stream()
                .map(review -> toReviewDTO(review, loggedUser.getId()))
                .collect(Collectors.toList());
    }
}