package com.mixtape.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "likes",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "mixtape_id"}))
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "mixtape_id", nullable = false)
    private Mixtape mixtape;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected Like() {}

    public Like(User user, Mixtape mixtape) {
        this.user = user;
        this.mixtape = mixtape;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public User getUser() { return user; }
    public Mixtape getMixtape() { return mixtape; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}