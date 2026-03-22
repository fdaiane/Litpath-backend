package com.litpath.litpath.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
    name = "user_book_lists",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "list_type"})
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class UserBookList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "list_type", nullable = false)
    private ListType listType;

    @OneToMany(mappedBy = "userBookList", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserBookListItem> items = new ArrayList<>();
}