import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import { jwtDecode } from "jwt-decode";
import './Styles/Login.css';
const Login = () => {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState(null);
    const navigate = useNavigate();

    const handleLogin = async (e) => {
        const apiUrl = process.env.REACT_APP_API_URL;
        e.preventDefault();
        setError(null);
        try {
            const response = await axios.post(`${apiUrl}auth/authenticate`, {
                email,
                password,
            });

            const token = response.data.token;
            localStorage.setItem("jwtToken", token);

            const decodedToken = jwtDecode(token);
            const role = decodedToken.userType;

            if (role === "ADMIN") {
                navigate("/admin");
            } else if (role === "SECURITY") {
                navigate("/security");
            } else {
                setError("Invalid role");
            }
        } catch (err) {
            setError("Invalid credentials");
        }
    };

    return (
        <div className="min-h-screen flex items-center justify-center px-4">
            <div className="w-full max-w-md">
                <div className="card">
                    <div className="card-body">
                        <h2 className="text-2xl font-semibold mb-6 text-center">Login</h2>
                        <form onSubmit={handleLogin} className="space-y-4">
                            <div>
                                <label>Email</label>
                                <input
                                    type="email"
                                    placeholder="you@example.com"
                                    value={email}
                                    onChange={(e) => setEmail(e.target.value)}
                                    required
                                    className="input"
                                />
                            </div>
                            <div>
                                <label>Password</label>
                                <input
                                    type="password"
                                    placeholder="••••••••"
                                    value={password}
                                    onChange={(e) => setPassword(e.target.value)}
                                    required
                                    className="input"
                                />
                            </div>
                            <button
                                type="submit"
                                className="btn btn-neutral w-full"
                            >
                                Login
                            </button>
                        </form>
                        {error && (
                            <p className="mt-4 rounded bg-red-50 text-red-700 px-3 py-2 text-sm">{error}</p>
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Login;
