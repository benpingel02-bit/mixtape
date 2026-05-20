import { useState } from "react";
import { useNavigate } from "react-router-dom";
import apiClient from "../api/client";
import SpotifySearch from "../components/SpotifySearch";

const CASSETTE_TYPES = ["C60", "C90", "C120"];
const MAX_DURATION = { C60: 3600, C90: 5400, C120: 7200 };
const LABEL_COLORS = ["#e94560", "#f5a623", "#7ed321", "#4a90e2", "#9b59b6", "#1abc9c"];

export default function MixtapeFormPage() {
    const navigate = useNavigate();

    const [form, setForm] = useState({
        title: "",
        description: "",
        cassetteType: "C90",
        labelColor: "#e94560",
        isPublic: true,
    });

    const [tracks, setTracks] = useState([]);
    const [error, setError] = useState(null);
    const [submitting, setSubmitting] = useState(false);

    const totalDuration = tracks.reduce((sum, t) => sum + t.durationSeconds, 0);
    const maxDuration = MAX_DURATION[form.cassetteType];
    const remainingSeconds = maxDuration - totalDuration;

    function handleChange(e) {
        const { name, value, type, checked } = e.target;
        setForm((prev) => ({ ...prev, [name]: type === "checkbox" ? checked : value }));
    }

    function addTrack(spotifyTrack) {
        if (tracks.find((t) => t.spotifyTrackId === spotifyTrack.spotifyTrackId)) return;
        if (totalDuration + spotifyTrack.durationSeconds > maxDuration) {
            setError(`Kassette voll! Noch ${formatDuration(remainingSeconds)} übrig.`);
            return;
        }
        setError(null);
        setTracks((prev) => [
            ...prev,
            { ...spotifyTrack, position: prev.length + 1 },
        ]);
    }

    function removeTrack(spotifyTrackId) {
        setTracks((prev) =>
            prev
                .filter((t) => t.spotifyTrackId !== spotifyTrackId)
                .map((t, i) => ({ ...t, position: i + 1 }))
        );
    }

    async function handleSubmit() {
        if (!form.title.trim()) { setError("Titel ist Pflicht."); return; }
        if (tracks.length === 0) { setError("Mindestens 1 Track erforderlich."); return; }

        setSubmitting(true);
        setError(null);

        try {
            // 1. Mixtape anlegen (userId=1 als Platzhalter bis JWT fertig ist)
            const mixtapeRes = await apiClient.post("/mixtapes", {
                ...form,
                userId: 1,
            });
            const mixtapeId = mixtapeRes.data.id;

            // 2. Tracks einzeln hinzufügen – Backend holt Metadaten selbst von Spotify
            for (const track of tracks) {
                await apiClient.post(`/mixtapes/${mixtapeId}/tracks`, {
                    spotifyTrackId: track.spotifyTrackId,
                    mixtapeId: mixtapeId,
                });
            }

            navigate(`/mixtapes/${mixtapeId}`);
        } catch (err) {
            setError(err.response?.data?.message || "Fehler beim Speichern.");
        } finally {
            setSubmitting(false);
        }
    }

    return (
        <div style={styles.container}>
            <h2 style={styles.heading}>📼 Neues Mixtape</h2>

            {/* ── Grunddaten ── */}
            <section style={styles.section}>
                <h3 style={styles.sectionTitle}>Details</h3>

                <label style={styles.label}>Titel *</label>
                <input
                    name="title"
                    value={form.title}
                    onChange={handleChange}
                    placeholder="Mein Sommer-Mix 2026"
                    style={styles.input}
                />

                <label style={styles.label}>Beschreibung</label>
                <textarea
                    name="description"
                    value={form.description}
                    onChange={handleChange}
                    placeholder="Worum geht es in diesem Mixtape?"
                    rows={3}
                    style={{ ...styles.input, resize: "vertical" }}
                />

                <label style={styles.label}>Kassettentyp</label>
                <div style={styles.row}>
                    {CASSETTE_TYPES.map((type) => (
                        <button
                            key={type}
                            type="button"
                            onClick={() => setForm((p) => ({ ...p, cassetteType: type }))}
                            style={{
                                ...styles.typeBtn,
                                ...(form.cassetteType === type ? styles.typeBtnActive : {}),
                            }}
                        >
                            {type}
                        </button>
                    ))}
                </div>

                <label style={styles.label}>Label-Farbe</label>
                <div style={styles.row}>
                    {LABEL_COLORS.map((color) => (
                        <button
                            key={color}
                            type="button"
                            onClick={() => setForm((p) => ({ ...p, labelColor: color }))}
                            style={{
                                ...styles.colorBtn,
                                backgroundColor: color,
                                outline: form.labelColor === color ? "3px solid #fff" : "none",
                            }}
                        />
                    ))}
                </div>

                <label style={styles.checkboxRow}>
                    <input
                        type="checkbox"
                        name="isPublic"
                        checked={form.isPublic}
                        onChange={handleChange}
                    />
                    <span>Öffentlich sichtbar</span>
                </label>
            </section>

            {/* ── Spotify-Suche ── */}
            <section style={styles.section}>
                <h3 style={styles.sectionTitle}>Songs hinzufügen</h3>
                <SpotifySearch onAddTrack={addTrack} />
            </section>

            {/* ── Track-Liste ── */}
            <section style={styles.section}>
                <div style={styles.trackHeader}>
                    <h3 style={styles.sectionTitle}>Tracklist ({tracks.length})</h3>
                    <span style={styles.duration}>
                        {formatDuration(totalDuration)} / {formatDuration(maxDuration)}
                    </span>
                </div>

                {/* Fortschrittsbalken */}
                <div style={styles.progressBar}>
                    <div
                        style={{
                            ...styles.progressFill,
                            width: `${Math.min((totalDuration / maxDuration) * 100, 100)}%`,
                            backgroundColor: totalDuration > maxDuration ? "#e94560" : "#4a90e2",
                        }}
                    />
                </div>

                {tracks.length === 0 ? (
                    <p style={{ color: "#888" }}>Noch keine Tracks – suche oben nach Songs.</p>
                ) : (
                    <ol style={styles.trackList}>
                        {tracks.map((track) => (
                            <li key={track.spotifyTrackId} style={styles.trackItem}>
                                {track.albumCoverUrl && (
                                    <img src={track.albumCoverUrl} alt="" style={styles.cover} />
                                )}
                                <div style={styles.trackInfo}>
                                    <div style={styles.trackTitle}>{track.title}</div>
                                    <div style={styles.trackArtist}>{track.artist}</div>
                                </div>
                                <span style={styles.trackDuration}>
                                    {formatDuration(track.durationSeconds)}
                                </span>
                                <button
                                    onClick={() => removeTrack(track.spotifyTrackId)}
                                    style={styles.removeBtn}
                                    title="Entfernen"
                                >
                                    ✕
                                </button>
                            </li>
                        ))}
                    </ol>
                )}
            </section>

            {error && <p style={styles.error}>{error}</p>}

            <button
                onClick={handleSubmit}
                disabled={submitting}
                style={{ ...styles.submitBtn, opacity: submitting ? 0.6 : 1 }}
            >
                {submitting ? "Wird gespeichert..." : "💾 Mixtape speichern"}
            </button>
        </div>
    );
}

function formatDuration(seconds) {
    const m = Math.floor(seconds / 60);
    const s = seconds % 60;
    return `${m}:${String(s).padStart(2, "0")}`;
}

const styles = {
    container: { maxWidth: "700px", margin: "0 auto" },
    heading: { fontSize: "1.8rem", color: "#e94560", marginBottom: "1.5rem" },
    section: {
        backgroundColor: "#1a1a2e",
        border: "1px solid #2a2a4e",
        borderRadius: "12px",
        padding: "1.5rem",
        marginBottom: "1.5rem",
    },
    sectionTitle: { margin: "0 0 1rem", color: "#fff", fontSize: "1.1rem" },
    label: { display: "block", color: "#aaa", fontSize: "0.85rem", marginBottom: "0.4rem", marginTop: "1rem" },
    input: {
        width: "100%",
        backgroundColor: "#0f0f1a",
        border: "1px solid #2a2a4e",
        borderRadius: "8px",
        padding: "0.6rem 0.8rem",
        color: "#fff",
        fontSize: "1rem",
        outline: "none",
        boxSizing: "border-box",
    },
    row: { display: "flex", gap: "0.5rem", flexWrap: "wrap", marginTop: "0.5rem" },
    typeBtn: {
        padding: "0.4rem 1rem",
        borderRadius: "8px",
        border: "1px solid #2a2a4e",
        backgroundColor: "#0f0f1a",
        color: "#aaa",
        cursor: "pointer",
        fontSize: "0.9rem",
    },
    typeBtnActive: { backgroundColor: "#e94560", color: "#fff", border: "1px solid #e94560" },
    colorBtn: { width: "28px", height: "28px", borderRadius: "50%", border: "none", cursor: "pointer" },
    checkboxRow: { display: "flex", alignItems: "center", gap: "0.5rem", color: "#aaa", marginTop: "1rem", cursor: "pointer" },
    trackHeader: { display: "flex", justifyContent: "space-between", alignItems: "center" },
    duration: { color: "#888", fontSize: "0.85rem" },
    progressBar: { height: "6px", backgroundColor: "#2a2a4e", borderRadius: "3px", marginBottom: "1rem", overflow: "hidden" },
    progressFill: { height: "100%", borderRadius: "3px", transition: "width 0.3s" },
    trackList: { listStyle: "none", padding: 0, margin: 0, display: "flex", flexDirection: "column", gap: "0.5rem" },
    trackItem: { display: "flex", alignItems: "center", gap: "0.75rem", backgroundColor: "#0f0f1a", borderRadius: "8px", padding: "0.6rem 0.8rem" },
    cover: { width: "36px", height: "36px", borderRadius: "4px", objectFit: "cover" },
    trackInfo: { flex: 1 },
    trackTitle: { color: "#fff", fontSize: "0.9rem", fontWeight: "bold" },
    trackArtist: { color: "#888", fontSize: "0.8rem" },
    trackDuration: { color: "#888", fontSize: "0.8rem" },
    removeBtn: { background: "none", border: "none", color: "#e94560", cursor: "pointer", fontSize: "1rem", padding: "0.2rem 0.4rem" },
    error: { color: "#e94560", marginBottom: "1rem" },
    submitBtn: {
        width: "100%",
        backgroundColor: "#e94560",
        color: "#fff",
        border: "none",
        borderRadius: "8px",
        padding: "0.9rem",
        fontSize: "1rem",
        fontWeight: "bold",
        cursor: "pointer",
    },
};