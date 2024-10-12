import './App.css';
import {BrowserRouter, Route, Routes} from "react-router-dom";
import Auth from "./pages/Auth";
import Main from "./pages/Main";

export const BACKEND_HOST_URL = 'http://localhost:8080/web_lab4_1042_war'
export const COOKIE_TOKEN = "token";


function App() {
    return (
        <Routes>
            <Route path="*" element={Auth()}/>
            <Route path="/main" element={Main()}/>
        </Routes>
    );
}

export default App;
