# Setup MixTape Frontend Structure
$frontendPath = "C:\mixtape\mixtape-frontend\src"
$backendPath = "C:\mixtape\mixtape-backend\src"

Write-Host "Cleaning up wrongly placed files in backend..." -ForegroundColor Yellow
@("api", "components", "pages") | ForEach-Object {
    $path = "$backendPath\$_"
    if (Test-Path $path) {
        Remove-Item -Recurse -Force $path
        Write-Host "  Removed $path" -ForegroundColor Red
    }
}

Write-Host "Creating folder structure in frontend..." -ForegroundColor Yellow
@("api", "components\layout", "pages") | ForEach-Object {
    New-Item -ItemType Directory -Force -Path "$frontendPath\$_" | Out-Null
    Write-Host "  Created $_" -ForegroundColor Green
}

# ── client.js ──────────────────────────────────────────────────────────────────
Set-Content -Path "$frontendPath\api\client.js" -Value @'
import axios from 'axios';

const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    console.error('API Error:', error.response?.status, error.response?.data);
    return Promise.reject(error);
  }
);

export default apiClient;
'@

# ── Navbar.jsx ─────────────────────────────────────────────────────────────────
Set-Content -Path "$frontendPath\components\layout\Navbar.jsx" -Value @'
import { Link, NavLink } from "react-router-dom";

export default function Navbar() {
  return (
    <nav style={styles.nav}>
      <Link to="/" style={styles.brand}>
        📼 MixTape
      </Link>
      <div style={styles.links}>
        <NavLink
          to="/mixtapes"
          style={({ isActive }) => ({ ...styles.link, ...(isActive ? styles.activeLink : {}) })}
        >
          Alle Mixtapes
        </NavLink>
        <NavLink
          to="/mixtapes/new"
          style={({ isActive }) => ({ ...styles.link, ...(isActive ? styles.activeLink : {}) })}
        >
          + Neues Mixtape
        </NavLink>
      </div>
    </nav>
  );
}

const styles = {
  nav: {
    display: "flex",
    alignItems: "center",
    justifyContent: "space-between",
    padding: "1rem 2rem",
    backgroundColor: "#1a1a2e",
    borderBottom: "2px solid #e94560",
  },
  brand: {
    color: "#e94560",
    textDecoration: "none",
    fontSize: "1.5rem",
    fontWeight: "bold",
    letterSpacing: "0.05em",
  },
  links: { display: "flex", gap: "1.5rem" },
  link: { color: "#ccc", textDecoration: "none", fontSize: "0.95rem" },
  activeLink: { color: "#e94560", fontWeight: "bold" },
};
'@

# ── Layout.jsx ─────────────────────────────────────────────────────────────────
Set-Content -Path "$frontendPath\components\layout\Layout.jsx" -Value @'
import { Outlet } from "react-router-dom";
import Navbar from "./Navbar";

export default function Layout() {
  return (
    <div style={styles.wrapper}>
      <Navbar />
      <main style={styles.main}>
        <Outlet />
      </main>
    </div>
  );
}

const styles = {
  wrapper: {
    minHeight: "100vh",
    backgroundColor: "#0f0f1a",
    color: "#f0f0f0",
    fontFamily: "'Segoe UI', sans-serif",
  },
  main: {
    maxWidth: "1100px",
    margin: "0 auto",
    padding: "2rem 1rem",
  },
};
'@

# ── HomePage.jsx ───────────────────────────────────────────────────────────────
Set-Content -Path "$frontendPath\pages\HomePage.jsx" -Value @'
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
'@

# ── MixtapeListPage.jsx ────────────────────────────────────────────────────────
Set-Content -Path "$frontendPath\pages\MixtapeListPage.jsx" -Value @'
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
'@

# ── MixtapeDetailPage.jsx ──────────────────────────────────────────────────────
Set-Content -Path "$frontendPath\pages\MixtapeDetailPage.jsx" -Value @'
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
'@

# ── NotFoundPage.jsx ───────────────────────────────────────────────────────────
Set-Content -Path "$frontendPath\pages\NotFoundPage.jsx" -Value @'
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
'@

# ── App.jsx ────────────────────────────────────────────────────────────────────
Set-Content -Path "$frontendPath\App.jsx" -Value @'
import { BrowserRouter, Routes, Route } from "react-router-dom";
import Layout from "./components/layout/Layout";
import HomePage from "./pages/HomePage";
import MixtapeListPage from "./pages/MixtapeListPage";
import MixtapeDetailPage from "./pages/MixtapeDetailPage";
import NotFoundPage from "./pages/NotFoundPage";

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Layout />}>
          <Route index element={<HomePage />} />
          <Route path="mixtapes" element={<MixtapeListPage />} />
          <Route path="mixtapes/:id" element={<MixtapeDetailPage />} />
          <Route path="*" element={<NotFoundPage />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}
'@

# ── main.jsx ───────────────────────────────────────────────────────────────────
Set-Content -Path "$frontendPath\main.jsx" -Value @'
import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import "./index.css";
import App from "./App";

createRoot(document.getElementById("root")).render(
  <StrictMode>
    <App />
  </StrictMode>
);
'@

# ── index.css ──────────────────────────────────────────────────────────────────
Set-Content -Path "$frontendPath\index.css" -Value @'
*, *::before, *::after {
  box-sizing: border-box;
}

body {
  margin: 0;
  background-color: #0f0f1a;
  color: #f0f0f0;
}
'@

# ── .env ───────────────────────────────────────────────────────────────────────
Set-Content -Path "C:\mixtape\mixtape-frontend\.env" -Value @'
VITE_API_BASE_URL=http://localhost:8080/api
'@

Write-Host ""
Write-Host "Done! All files created successfully." -ForegroundColor Green
Write-Host "Now run: npm run dev" -ForegroundColor Cyan
