import { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import apiClient from "../api/client";
import { useAuth } from "../context/AuthContext";

export default function MixtapeDetailPage() {
  const { id } = useParams();
  const { user, isLoggedIn } = useAuth();
  const [mixtape, setMixtape] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [exporting, setExporting] = useState(false);
  const [playlistUrl, setPlaylistUrl] = useState(null);
  const [exportError, setExportError] = useState(null);

  useEffect(() => {
    apiClient
        .get(`/mixtapes/${id}`)
        .then((res) => setMixtape(res.data))
        .catch(() => setError("Mixtape nicht gefunden."))
        .finally(() => setLoading(false));
  }, [id]);

  async function handleSpotifyExport() {
    setExporting(true);
    setExportError(null);
    try {
      // Prüfen ob User bereits mit Spotify verbunden
      const connectedRes = await apiClient.get(`/spotify/connected?userId=${user.userId}`);
      if (!connectedRes.data.connected) {
        // Spotify Auth starten
        const authRes = await apiClient.get(`/spotify/auth-url?userId=${user.userId}`);
        window.location.href = authRes.data.url;
        return;
      }
      // Playlist erstellen
      const exportRes = await apiClient.post(`/spotify/export/${id}?userId=${user.userId}`);
      setPlaylistUrl(exportRes.data.playlistUrl);
    } catch (err) {
      setExportError("Export fehlgeschlagen. Bitte versuche es erneut.");
    } finally {
      setExporting(false);
    }
  }

  if (loading) return <p>Lade Mixtape...</p>;
  if (error) return <p style={{ color: "#e94560" }}>{error}</p>;

  const totalMinutes = Math.floor(
      (mixtape.tracks || []).reduce((sum, t) => sum + t.durationSeconds, 0) / 60
  );

  return (
      <div>
        <Link to="/mixtapes" style={styles.back}>← Zurück</Link>

        <div style={styles.header}>
          <h1 style={styles.title}>{mixtape.title}</h1>
          <span style={styles.badge}>{mixtape.cassetteType}</span>
        </div>

        {mixtape.description && <p style={styles.description}>{mixtape.description}</p>}

        <p style={styles.meta}>
          🎵 {(mixtape.tracks || []).length} Tracks · ⏱ {totalMinutes} Minuten · 👤 {mixtape.username}
        </p>

        {/* Spotify Export Button */}
        {isLoggedIn && (
            <div style={{ marginBottom: "1.5rem" }}>
              {playlistUrl ? (
                  <a href={playlistUrl} target="_blank" rel="noreferrer" style={styles.spotifyLink}>
                    🎧 Playlist in Spotify öffnen
                  </a>
              ) : (
                  <button
                      onClick={handleSpotifyExport}
                      disabled={exporting}
                      style={styles.spotifyBtn}
                  >
                    {exporting ? "Exportiere..." : "🎵 Als Spotify Playlist exportieren"}
                  </button>
              )}
              {exportError && <p style={{ color: "#e94560", marginTop: "0.5rem" }}>{exportError}</p>}
            </div>
        )}

        <h2 style={styles.tracksHeading}>Tracks</h2>
        {(mixtape.tracks || []).length === 0 ? (
            <p style={{ color: "#888" }}>Noch keine Tracks.</p>
        ) : (
            <ul style={styles.trackList}>
              {(mixtape.tracks || []).map((track) => (
                  <li key={track.id} style={styles.trackItem}>
                    {track.albumCoverUrl && (
                        <img src={track.albumCoverUrl} alt={track.albumName} style={styles.cover} />
                    )}
                    <div>
                      <div style={styles.trackTitle}>{track.position}. {track.title}</div>
                      <div style={styles.trackMeta}>{track.artist} · {track.albumName}</div>
                    </div>
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
  spotifyBtn: {
    background: "#1DB954", color: "#fff", border: "none",
    padding: "0.6rem 1.2rem", borderRadius: "20px",
    cursor: "pointer", fontSize: "0.95rem", fontWeight: "bold"
  },
  spotifyLink: {
    background: "#1DB954", color: "#fff",
    padding: "0.6rem 1.2rem", borderRadius: "20px",
    textDecoration: "none", fontSize: "0.95rem", fontWeight: "bold"
  },
  tracksHeading: { borderBottom: "1px solid #333", paddingBottom: "0.5rem" },
  trackList: { listStyle: "none", padding: 0, margin: 0 },
  trackItem: {
    display: "flex", alignItems: "center", gap: "1rem",
    padding: "0.75rem 0", borderBottom: "1px solid #1a1a2e"
  },
  cover: { width: 48, height: 48, borderRadius: "4px", objectFit: "cover" },
  trackTitle: { fontWeight: "bold" },
  trackMeta: { color: "#888", fontSize: "0.85rem" },
};