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
