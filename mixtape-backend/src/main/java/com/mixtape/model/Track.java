package com.mixtape.model;

import jakarta.persistence.*;

@Entity
public class Track {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String spotifyTrackId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String artist;

    private String albumName;
    private String albumCoverUrl;

    @Column(nullable = false)
    private int durationSeconds;

    @Column(nullable = false)
    private int position;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "mixtape_id", nullable = false)
    private Mixtape mixtape;

    protected Track() {}

    public Track(String spotifyTrackId, String title, String artist,
                 String albumName, String albumCoverUrl,
                 int durationSeconds, int position, Mixtape mixtape) {
        this.spotifyTrackId = spotifyTrackId;
        this.title = title;
        this.artist = artist;
        this.albumName = albumName;
        this.albumCoverUrl = albumCoverUrl;
        this.durationSeconds = durationSeconds;
        this.position = position;
        this.mixtape = mixtape;
    }

    public Long getId() { return id; }
    public String getSpotifyTrackId() { return spotifyTrackId; }
    public void setSpotifyTrackId(String spotifyTrackId) { this.spotifyTrackId = spotifyTrackId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getArtist() { return artist; }
    public void setArtist(String artist) { this.artist = artist; }
    public String getAlbumName() { return albumName; }
    public void setAlbumName(String albumName) { this.albumName = albumName; }
    public String getAlbumCoverUrl() { return albumCoverUrl; }
    public void setAlbumCoverUrl(String albumCoverUrl) { this.albumCoverUrl = albumCoverUrl; }
    public int getDurationSeconds() { return durationSeconds; }
    public void setDurationSeconds(int durationSeconds) { this.durationSeconds = durationSeconds; }
    public int getPosition() { return position; }
    public void setPosition(int position) { this.position = position; }
    public Mixtape getMixtape() { return mixtape; }
    public void setMixtape(Mixtape mixtape) { this.mixtape = mixtape; }
}