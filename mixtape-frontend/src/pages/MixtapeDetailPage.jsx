import { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import apiClient from "../api/client";

export default function MixtapeDetailPage() {
  const { id } = useParams();
  const [mixtape, setMixtape] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [copied, setCopied] = useState(false);

  useEffect(() => {
    apiClient
        .get(`/mixtapes/${id}`)
        .then((res) => setMixtape(res.data))
        .catch(() => setError("Mixtape nicht gefunden."))
        .finally(() => setLoading(false));
  }, [id]);

  if (loading) return <p>Lade Mixtape...</p>;
  if (error) return <p style={{ color: "#e94560" }}>{error}</p>;

  const tracks = mixtape.tracks || [];
  const totalMinutes = Math.floor(
      tracks.reduce((sum, t) => sum + t.durationSeconds, 0) / 60
  );

  // Spotify-URIs aus den gespeicherten Track-Daten bauen, kein erneuter API-Call nötig
  const spotifyUris = tracks.map((t) => `spotify:track:${t.spotifyTrackId}`);
  const spotifyWebLinks = tracks.map((t) => `https://open.spotify.com/track/${t.spotifyTrackId}`);

  function handleCopyUris() {
    navigator.clipboard.writeText(spotifyUris.join("\n"));
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  }

  return (
      <div>
        <Link to="/mixtapes" style={styles.back}>← Zurück</Link>

        <div style={styles.header}>
          <h1 style={styles.title}>{mixtape.title}</h1>
          <span style={styles.badge}>{mixtape.cassetteType}</span>
        </div>

        {mixtape.description && <p style={styles.description}>{mixtape.description}</p>}

        <p style={styles.meta}>
          🎵 {tracks.length} Tracks · ⏱ {totalMinutes} Minuten · 👤 {mixtape.username}
        </p>

        {/* Spotify Export — einfacher Deeplink, kein Login nötig */}
        {tracks.length > 0 && (
            <div style={styles.exportBox}>
              <p style={styles.exportLabel}>🎵 In Spotify öffnen</p>
              <div style={styles.exportButtons}>
                <button onClick={handleCopyUris} style={styles.spotifyBtn}>
                  {copied ? "✓ Kopiert!" : "Spotify-URIs kopieren"}
                </button>
                {tracks[0] && (
                    <a
                        href={spotifyWebLinks[0]}
                        target="_blank"
                        rel="noreferrer"
                        style={styles.spotifyLink}
                    >
                      Ersten Track öffnen
                    </a>
                )}
              </div>
                <p style={styles.exportHint}>
                    Öffne die Tracks einzeln über die Links unten, oder füge die kopierten URIs
                    in Spotify per Suchleiste oder Rechtsklick → "Songs hinzufügen" in eine Playlist ein.
                </p>
            </div>
        )}

        <h2 style={styles.tracksHeading}>Tracks</h2>
        {tracks.length === 0 ? (
            <p style={{ color: "#888" }}>Noch keine Tracks.</p>
        ) : (
            <ul style={styles.trackList}>
              {tracks.map((track, i) => (
                  <li key={track.id} style={styles.trackItem}>
                    {track.albumCoverUrl && (
                        <img src={track.albumCoverUrl} alt={track.albumName} style={styles.cover} />
                    )}
                    <div style={{ flex: 1 }}>
                      <div style={styles.trackTitle}>{track.position}. {track.title}</div>
                      <div style={styles.trackMeta}>{track.artist} · {track.albumName}</div>
                    </div>
                    <a
                        href={spotifyWebLinks[i]}
                        target="_blank"
                        rel="noreferrer"
                        style={styles.trackLink}
                    >
                      ↗ Spotify
                    </a>
                  </li>
              ))}
            </ul>
        )}
      </div>
  );
}

const styles = {
  back: { color: "#e94560", textDecoration: "none", fontSize: "0.9rem" },
  header: { display: "flex", alignItems: "center", gap: "1rem", margin: "1rem 0" },
  title: { margin: 0, fontSize: "2rem" },
  badge: {
    background: "#e94560", color: "#fff",
    padding: "0.2rem 0.6rem", borderRadius: "4px", fontSize: "0.85rem"
  },
  description: { color: "#aaa", marginBottom: "0.5rem" },
  meta: { color: "#888", marginBottom: "1.5rem" },
  exportBox: {
    background: "#1a1a2e", border: "1px solid #1DB954",
    borderRadius: "8px", padding: "1rem", marginBottom: "1.5rem"
  },
  exportLabel: { margin: "0 0 0.75rem 0", fontWeight: "bold" },
  exportButtons: { display: "flex", gap: "0.75rem", flexWrap: "wrap" },
  spotifyBtn: {
    background: "#1DB954", color: "#fff", border: "none",
    padding: "0.5rem 1rem", borderRadius: "20px",
    cursor: "pointer", fontSize: "0.9rem", fontWeight: "bold"
  },
  spotifyLink: {
    background: "transparent", color: "#1DB954", border: "1px solid #1DB954",
    padding: "0.5rem 1rem", borderRadius: "20px",
    textDecoration: "none", fontSize: "0.9rem", fontWeight: "bold"
  },
  exportHint: { color: "#888", fontSize: "0.8rem", margin: "0.75rem 0 0 0" },
  tracksHeading: { borderBottom: "1px solid #333", paddingBottom: "0.5rem" },
  trackList: { listStyle: "none", padding: 0, margin: 0 },
  trackItem: {
    display: "flex", alignItems: "center", gap: "1rem",
    padding: "0.75rem 0", borderBottom: "1px solid #1a1a2e"
  },
  cover: { width: 48, height: 48, borderRadius: "4px", objectFit: "cover" },
  trackTitle: { fontWeight: "bold" },
  trackMeta: { color: "#888", fontSize: "0.85rem" },
  trackLink: { color: "#1DB954", textDecoration: "none", fontSize: "0.85rem", whiteSpace: "nowrap" },
};