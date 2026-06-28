import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

export default function SpotifyConnectedPage() {
    const navigate = useNavigate();
    const [countdown, setCountdown] = useState(3);

    useEffect(() => {
        const interval = setInterval(() => {
            setCountdown(c => c - 1);
        }, 1000);

        const timeout = setTimeout(() => {
            navigate("/mixtapes");
        }, 3000);

        return () => {
            clearInterval(interval);
            clearTimeout(timeout);
        };
    }, [navigate]);

    return (
        <div style={{ textAlign: "center", marginTop: "100px" }}>
            <h2 style={{ color: "#1DB954" }}>✅ Spotify verbunden!</h2>
            <p>Du wirst in {countdown} Sekunden weitergeleitet...</p>
        </div>
    );
}