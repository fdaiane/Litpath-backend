package com.litpath.litpath.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "user_genre_preferences",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "genre_id"})
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class UserGenrePreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "genre_id", nullable = false)
    private Genre genre;
}
