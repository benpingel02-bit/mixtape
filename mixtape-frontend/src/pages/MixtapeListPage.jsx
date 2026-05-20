import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import apiClient from "../api/client";

export default function MixtapeListPage() {
  const [mixtapes, setMixtapes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    apiClient
      .get("/mixtapes")
      .then((res) => setMixtapes(res.data))
      .catch(() => setError("Mixtapes konnten nicht geladen werden."))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <p>Lade Mixtapes...</p>;
  if (error) return <p style={{ color: "#e94560" }}>{error}</p>;

  return (
    <div>
      <div style={styles.header}>
        <h2>Alle Mixtapes</h2>
        <Link to="/mixtapes/new" style={styles.btnNew}>
          + Neues Mixtape
        </Link>
      </div>
      {mixtapes.length === 0 ? (
        <p style={{ color: "#aaa" }}>Noch keine Mixtapes vorhanden.</p>
      ) : (
        <div style={styles.grid}>
          {mixtapes.map((tape) => (
            <MixtapeCard key={tape.id} tape={tape} />
          ))}
        </div>
      )}
    </div>
  );
}

function MixtapeCard({ tape }) {
  return (
    <Link to={`/mixtapes/${tape.id}`} style={styles.card}>
      <div style={{ ...styles.cassette, backgroundColor: tape.labelColor || "#e94560" }}>
        📼
      </div>
      <div style={styles.cardBody}>
        <h3 style={styles.cardTitle}>{tape.title}</h3>
        <p style={styles.cardMeta}>
          {tape.cassetteType} · {tape.isPublic ? "Öffentlich" : "Privat"}
        </p>
        {tape.description && <p style={styles.cardDesc}>{tape.description}</p>}
      </div>
    </Link>
  );
}

const styles = {
  header: { display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "1.5rem" },
  btnNew: {
    backgroundColor: "#e94560", color: "#fff", padding: "0.5rem 1.25rem",
    borderRadius: "8px", textDecoration: "none", fontWeight: "bold",
  },
  grid: { display: "grid", gridTemplateColumns: "repeat(auto-fill, minmax(260px, 1fr))", gap: "1.25rem" },
  card: {
    backgroundColor: "#1a1a2e", border: "1px solid #2a2a4e", borderRadius: "12px",
    overflow: "hidden", textDecoration: "none", color: "inherit", display: "block",
  },
  cassette: { fontSize: "3rem", textAlign: "center", padding: "1.5rem" },
  cardBody: { padding: "1rem" },
  cardTitle: { margin: "0 0 0.25rem", fontSize: "1.1rem", color: "#fff" },
  cardMeta: { color: "#888", fontSize: "0.85rem", margin: "0 0 0.5rem" },
  cardDesc: { color: "#aaa", fontSize: "0.9rem", margin: 0 },
};
