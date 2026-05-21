import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import apiClient from "../api/client";

export default function LoginPage() {
    const { login } = useAuth();
    const navigate = useNavigate();
    const [form, setForm] = useState({ username: "", password: "" });
    const [error, setError] = useState(null);
    const [loading, setLoading] = useState(false);

    function handleChange(e) {
        setForm({ ...form, [e.target.name]: e.target.value });
    }

    async function handleSubmit(e) {
        e.preventDefault();
        setError(null);
        setLoading(true);
        try {
            const res = await apiClient.post("/auth/login", form);
            login(res.data.token, { username: res.data.username, userId: res.data.userId });
            navigate("/");
        } catch (err) {
            setError("Ungültiger Benutzername oder Passwort");
        } finally {
            setLoading(false);
        }
    }

    return (
        <div style={{ maxWidth: 400, margin: "80px auto", padding: "2rem" }}>
            <h2>Login</h2>
            <form onSubmit={handleSubmit}>
                <div style={{ marginBottom: "1rem" }}>
                    <label>Username</label><br />
                    <input
                        name="username"
                        value={form.username}
                        onChange={handleChange}
                        required
                        style={{ width: "100%", padding: "0.5rem" }}
                    />
                </div>
                <div style={{ marginBottom: "1rem" }}>
                    <label>Passwort</label><br />
                    <input
                        type="password"
                        name="password"
                        value={form.password}
                        onChange={handleChange}
                        required
                        style={{ width: "100%", padding: "0.5rem" }}
                    />
                </div>
                {error && <p style={{ color: "red" }}>{error}</p>}
                <button type="submit" disabled={loading} style={{ width: "100%", padding: "0.5rem" }}>
                    {loading ? "Lädt..." : "Einloggen"}
                </button>
            </form>
            <p style={{ marginTop: "1rem" }}>
                Noch kein Konto? <Link to="/register">Registrieren</Link>
            </p>
        </div>
    );
}