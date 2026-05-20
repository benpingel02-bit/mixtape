import { Link } from "react-router-dom";

export default function NotFoundPage() {
  return (
    <div style={{ textAlign: "center", paddingTop: "4rem" }}>
      <h1 style={{ fontSize: "4rem", color: "#e94560" }}>404</h1>
      <p style={{ color: "#aaa" }}>Diese Seite gibt es nicht.</p>
      <Link to="/" style={{ color: "#e94560" }}>Zurück zur Startseite</Link>
    </div>
  );
}
