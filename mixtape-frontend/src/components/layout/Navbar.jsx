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
