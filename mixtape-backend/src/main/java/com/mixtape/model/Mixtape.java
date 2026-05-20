package com.mixtape.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class Mixtape {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CassetteType cassetteType;

    private String designTemplate;
    private String labelColor;

    @Column(nullable = false)
    private boolean isPublic = false;

    @Column(nullable = false)
    private boolean isLocked = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "mixtape", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("position ASC")
    private List<Track> tracks = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "mixtape_tags",
            joinColumns = @JoinColumn(name = "mixtape_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    public Mixtape() {}

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public CassetteType getCassetteType() { return cassetteType; }
    public void setCassetteType(CassetteType cassetteType) { this.cassetteType = cassetteType; }
    public String getDesignTemplate() { return designTemplate; }
    public void setDesignTemplate(String designTemplate) { this.designTemplate = designTemplate; }
    public String getLabelColor() { return labelColor; }
    public void setLabelColor(String labelColor) { this.labelColor = labelColor; }
    public boolean isPublic() { return isPublic; }
    public void setPublic(boolean isPublic) { this.isPublic = isPublic; }
    public boolean isLocked() { return isLocked; }
    public void setLocked(boolean isLocked) { this.isLocked = isLocked; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public List<Track> getTracks() { return tracks; }
    public Set<Tag> getTags() { return tags; }
}