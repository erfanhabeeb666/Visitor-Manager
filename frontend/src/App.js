import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Login from "./Components/Login";
import Admin from "./Components/Admin";
import Security from "./Components/Security";

const App = () => {
    return (
        <Router>
            <Routes>
                <Route path="/" element={<Login />} />
                <Route path="/admin" element={<Admin />} />
                <Route path="/security" element={<Security />} />
            </Routes>
        </Router>
    );
};

export default App;
