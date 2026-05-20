import { Link } from "react-router-dom";

export default function HomePage() {
  return (
    <div style={styles.container}>
      <h1 style={styles.headline}>📼 Deine digitale Kassette</h1>
      <p style={styles.sub}>
        Erstelle Mixtapes mit deinen Lieblingssongs, teile sie mit Freunden
        und entdecke die Playlisten anderer.
      </p>
      <div style={styles.actions}>
        <Link to="/mixtapes/new" style={styles.btnPrimary}>
          Mixtape erstellen
        </Link>
        <Link to="/mixtapes" style={styles.btnSecondary}>
          Alle Mixtapes
        </Link>
      </div>
    </div>
  );
}

const styles = {
  container: { textAlign: "center", paddingTop: "4rem" },
  headline: { fontSize: "3rem", color: "#e94560", marginBottom: "1rem" },
  sub: {
    fontSize: "1.1rem",
    color: "#aaa",
    maxWidth: "500px",
    margin: "0 auto 2.5rem",
    lineHeight: "1.6",
  },
  actions: { display: "flex", gap: "1rem", justifyContent: "center" },
  btnPrimary: {
    backgroundColor: "#e94560",
    color: "#fff",
    padding: "0.75rem 1.75rem",
    borderRadius: "8px",
    textDecoration: "none",
    fontWeight: "bold",
    fontSize: "1rem",
  },
  btnSecondary: {
    backgroundColor: "transparent",
    color: "#e94560",
    padding: "0.75rem 1.75rem",
    borderRadius: "8px",
    textDecoration: "none",
    fontWeight: "bold",
    fontSize: "1rem",
    border: "2px solid #e94560",
  },
};
