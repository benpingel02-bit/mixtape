import { useState } from "react";
import apiClient from "../api/client";

export default function SpotifySearch({ onAddTrack }) {
    const [query, setQuery] = useState("");
    const [results, setResults] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    async function handleSearch(e) {
        e.preventDefault();
        if (!query.trim()) return;

        setLoading(true);
        setError(null);

        try {
            const res = await apiClient.get("/spotify/search", {
                params: { q: query, limit: 8 },
            });
            setResults(res.data);
        } catch {
            setError("Spotify-Suche fehlgeschlagen.");
        } finally {
            setLoading(false);
        }
    }

    return (
        <div>
            <form onSubmit={handleSearch} style={styles.form}>
                <input
                    value={query}
                    onChange={(e) => setQuery(e.target.value)}
                    placeholder="Song oder Artist suchen..."
                    style={styles.input}
                />
                <button type="submit" style={styles.searchBtn} disabled={loading}>
                    {loading ? "..." : "Suchen"}
                </button>
            </form>

            {error && <p style={{ color: "#e94560", fontSize: "0.85rem" }}>{error}</p>}

            {results.length > 0 && (
                <ul style={styles.results}>
                    {results.map((track) => (
                        <li key={track.spotifyTrackId} style={styles.resultItem}>
                            {track.albumCoverUrl && (
                                <img src={track.albumCoverUrl} alt="" style={styles.cover} />
                            )}
                            <div style={styles.trackInfo}>
                                <div style={styles.trackTitle}>{track.title}</div>
                                <div style={styles.trackArtist}>
                                    {track.artist} · {track.albumName}
                                </div>
                            </div>
                            <span style={styles.duration}>
                {Math.floor(track.durationSeconds / 60)}:
                                {String(track.durationSeconds % 60).padStart(2, "0")}
              </span>
                            <button
                                type="button"
                                onClick={() => onAddTrack(track)}
                                style={styles.addBtn}
                            >
                                +
                            </button>
                        </li>
                    ))}
                </ul>
            )}
        </div>
    );
}

const styles = {
    form: { display: "flex", gap: "0.5rem", marginBottom: "1rem" },
    input: {
        flex: 1,
        backgroundColor: "#0f0f1a",
        border: "1px solid #2a2a4e",
        borderRadius: "8px",
        padding: "0.6rem 0.8rem",
        color: "#fff",
        fontSize: "1rem",
        outline: "none",
    },
    searchBtn: {
        backgroundColor: "#4a90e2",
        color: "#fff",
        border: "none",
        borderRadius: "8px",
        padding: "0.6rem 1.2rem",
        cursor: "pointer",
        fontWeight: "bold",
    },
    results: { listStyle: "none", padding: 0, margin: 0, display: "flex", flexDirection: "column", gap: "0.4rem" },
    resultItem: {
        display: "flex", alignItems: "center", gap: "0.75rem",
        backgroundColor: "#0f0f1a", borderRadius: "8px", padding: "0.6rem 0.8rem",
    },
    cover: { width: "36px", height: "36px", borderRadius: "4px", objectFit: "cover" },
    trackInfo: { flex: 1 },
    trackTitle: { color: "#fff", fontSize: "0.9rem", fontWeight: "bold" },
    trackArtist: { color: "#888", fontSize: "0.8rem" },
    duration: { color: "#888", fontSize: "0.8rem" },
    addBtn: {
        backgroundColor: "#e94560", color: "#fff", border: "none",
        borderRadius: "6px", width: "28px", height: "28px",
        cursor: "pointer", fontSize: "1.2rem", lineHeight: 1,
    },
};