package com.litpath.litpath.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "review_comment_reactions",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "comment_id"})
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class ReviewCommentReaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    private ReviewComment comment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReactionType reactionType;
}
