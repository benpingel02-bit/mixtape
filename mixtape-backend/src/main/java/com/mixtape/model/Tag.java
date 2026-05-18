package com.mixtape.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String name;

    // mappedBy zeigt auf das Feld in Mixtape – Tag ist die inverse Seite
    @ManyToMany(mappedBy = "tags")
    private Set<Mixtape> mixtapes = new HashSet<>();

    protected Tag() {}

    public Tag(String name) {
        this.name = name.toLowerCase().trim();
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Set<Mixtape> getMixtapes() { return mixtapes; }
}