import { Link, NavLink, useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";

export default function Navbar() {
  const { isLoggedIn, user, logout } = useAuth();
  const navigate = useNavigate();

  function handleLogout() {
    logout();
    navigate("/");
  }

  return (
      <nav style={styles.nav}>
        <Link to="/" style={styles.brand}>
          🎼 MixTape
        </Link>
        <div style={styles.links}>
          <NavLink
              to="/mixtapes"
              style={({ isActive }) => ({ ...styles.link, ...(isActive ? styles.activeLink : {}) })}
          >
            Alle Mixtapes
          </NavLink>
          {isLoggedIn && (
              <NavLink
                  to="/mixtapes/new"
                  style={({ isActive }) => ({ ...styles.link, ...(isActive ? styles.activeLink : {}) })}
              >
                + Neues Mixtape
              </NavLink>
          )}
          {isLoggedIn ? (
              <>
                <span style={styles.username}>👤 {user?.username}</span>
                <button onClick={handleLogout} style={styles.logoutBtn}>
                  Logout
                </button>
              </>
          ) : (
              <>
                <NavLink
                    to="/login"
                    style={({ isActive }) => ({ ...styles.link, ...(isActive ? styles.activeLink : {}) })}
                >
                  Login
                </NavLink>
                <NavLink
                    to="/register"
                    style={({ isActive }) => ({ ...styles.link, ...(isActive ? styles.activeLink : {}) })}
                >
                  Registrieren
                </NavLink>
              </>
          )}
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
  links: { display: "flex", gap: "1.5rem", alignItems: "center" },
  link: { color: "#ccc", textDecoration: "none", fontSize: "0.95rem" },
  activeLink: { color: "#e94560", fontWeight: "bold" },
  username: { color: "#a0a0c0", fontSize: "0.95rem" },
  logoutBtn: {
    background: "none",
    border: "1px solid #e94560",
    color: "#e94560",
    cursor: "pointer",
    padding: "0.3rem 0.8rem",
    borderRadius: "4px",
    fontSize: "0.9rem",
  },
};