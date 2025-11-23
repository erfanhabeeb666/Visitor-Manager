import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Login from "./Components/Login";
import Admin from "./Components/Admin";
import Security from "./Components/Security";
import ProtectedRoute from "./Components/ProtectedRoute";
import Toaster from "./Components/Toaster";

const App = () => {
    return (
        <Router>
            <Toaster />
            <Routes>
                <Route path="/" element={<Login />} />
                <Route
                  path="/admin"
                  element={
                    <ProtectedRoute requireRole="ADMIN">
                      <Admin />
                    </ProtectedRoute>
                  }
                />
                <Route
                  path="/security"
                  element={
                    <ProtectedRoute requireRole="SECURITY">
                      <Security />
                    </ProtectedRoute>
                  }
                />
            </Routes>
        </Router>
    );
};

export default App;
