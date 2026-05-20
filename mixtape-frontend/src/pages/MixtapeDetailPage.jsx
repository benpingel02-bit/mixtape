import { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import apiClient from "../api/client";

export default function MixtapeDetailPage() {
  const { id } = useParams();
  const [mixtape, setMixtape] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    apiClient
      .get(`/mixtapes/${id}`)
      .then((res) => setMixtape(res.data))
      .catch(() => setError("Mixtape nicht gefunden."))
      .finally(() => setLoading(false));
  }, [id]);

  if (loading) return <p>Lade Mixtape...</p>;
  if (error) return <p style={{ color: "#e94560" }}>{error}</p>;

  const totalMinutes = Math.floor(
    (mixtape.tracks || []).reduce((sum, t) => sum + t.durationSeconds, 0) / 60
  );

  return (
    <div>
      <Link to="/mixtapes" style={styles.back}>← Zurück</Link>
      <div style={styles.header}>
        <div style={{ ...styles.cassette, backgroundColor: mixtape.labelColor || "#e94560" }}>📼</div>
        <div>
          <h1 style={styles.title}>{mixtape.title}</h1>
          <p style={styles.meta}>
            {mixtape.cassetteType} · {totalMinutes} Min. · {mixtape.isPublic ? "Öffentlich" : "Privat"}
          </p>
          {mixtape.description && <p style={styles.desc}>{mixtape.description}</p>}
        </div>
      </div>
      <h2 style={styles.tracksHeadline}>Tracks</h2>
      {mixtape.tracks?.length === 0 ? (
        <p style={{ color: "#aaa" }}>Noch keine Tracks.</p>
      ) : (
        <ol style={styles.trackList}>
          {mixtape.tracks?.map((track) => (
            <li key={track.id} style={styles.trackItem}>
              {track.albumCoverUrl && (
                <img src={track.albumCoverUrl} alt={track.albumName} style={styles.cover} />
              )}
              <div>
                <div style={styles.trackTitle}>{track.title}</div>
                <div style={styles.trackArtist}>{track.artist}</div>
              </div>
              <div style={styles.trackDuration}>
                {Math.floor(track.durationSeconds / 60)}:
                {String(track.durationSeconds % 60).padStart(2, "0")}
              </div>
            </li>
          ))}
        </ol>
      )}
    </div>
  );
}

const styles = {
  back: { color: "#e94560", textDecoration: "none", fontSize: "0.9rem" },
  header: { display: "flex", gap: "1.5rem", alignItems: "flex-start", margin: "1.5rem 0" },
  cassette: { fontSize: "4rem", padding: "1rem", borderRadius: "12px", minWidth: "80px", textAlign: "center" },
  title: { fontSize: "2rem", margin: "0 0 0.5rem", color: "#fff" },
  meta: { color: "#888", margin: "0 0 0.5rem", fontSize: "0.95rem" },
  desc: { color: "#aaa", margin: 0, maxWidth: "600px" },
  tracksHeadline: { borderBottom: "1px solid #2a2a4e", paddingBottom: "0.5rem", marginBottom: "1rem" },
  trackList: { listStyle: "none", padding: 0, margin: 0, display: "flex", flexDirection: "column", gap: "0.5rem" },
  trackItem: {
    display: "flex", alignItems: "center", gap: "1rem",
    backgroundColor: "#1a1a2e", borderRadius: "8px", padding: "0.75rem 1rem",
  },
  cover: { width: "40px", height: "40px", borderRadius: "4px", objectFit: "cover" },
  trackTitle: { fontWeight: "bold", color: "#fff" },
  trackArtist: { color: "#888", fontSize: "0.85rem" },
  trackDuration: { marginLeft: "auto", color: "#888", fontSize: "0.85rem" },
};
