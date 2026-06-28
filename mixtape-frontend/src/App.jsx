import { BrowserRouter, Routes, Route } from "react-router-dom";
import Layout from "./components/layout/Layout";
import HomePage from "./pages/HomePage";
import MixtapeListPage from "./pages/MixtapeListPage";
import MixtapeDetailPage from "./pages/MixtapeDetailPage";
import NotFoundPage from "./pages/NotFoundPage";
import MixtapeFormPage from './pages/MixtapeFormPage';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import SpotifyConnectedPage from './pages/SpotifyConnectedPage';

export default function App() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/login" element={<LoginPage />} />
                <Route path="/register" element={<RegisterPage />} />
                <Route path="/spotify-connected" element={<SpotifyConnectedPage />} />
                <Route path="/" element={<Layout />}>
                    <Route index element={<HomePage />} />
                    <Route path="mixtapes" element={<MixtapeListPage />} />
                    <Route path="mixtapes/new" element={<MixtapeFormPage />} />
                    <Route path="mixtapes/:id" element={<MixtapeDetailPage />} />
                    <Route path="*" element={<NotFoundPage />} />
                </Route>
            </Routes>
        </BrowserRouter>
    );
}