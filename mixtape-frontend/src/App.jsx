import { BrowserRouter, Routes, Route } from "react-router-dom";
import Layout from "./components/layout/Layout";
import HomePage from "./pages/HomePage";
import MixtapeListPage from "./pages/MixtapeListPage";
import MixtapeDetailPage from "./pages/MixtapeDetailPage";
import NotFoundPage from "./pages/NotFoundPage";
import MixtapeFormPage from './pages/MixtapeFormPage';


export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Layout />}>
          <Route index element={<HomePage />} />
          <Route path="mixtapes" element={<MixtapeListPage />} />
          <Route path="mixtapes/:id" element={<MixtapeDetailPage />} />
          <Route path="*" element={<NotFoundPage />} />
          <Route path="mixtapes/new" element={<MixtapeFormPage />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}
