import React from "react";
import { useNavigate } from "react-router-dom";
import Buildings from "./admin/Buildings";
import Tenants from "./admin/Tenants";
import Floors from "./admin/Floors";
import Rooms from "./admin/Rooms";
import SecurityUsers from "./admin/SecurityUsers";
import { jwtDecode } from "jwt-decode";

const sections = [
  { key: "buildings", label: "Buildings" },
  { key: "tenants", label: "Tenants" },
  { key: "floors", label: "Floors" },
  { key: "rooms", label: "Rooms" },
  { key: "security-users", label: "Security Users" },
];

const Admin = () => {
  const navigate = useNavigate();
  const [active, setActive] = React.useState("buildings");
  const [userName, setUserName] = React.useState("");

  React.useEffect(() => {
    try {
      const token = localStorage.getItem("jwtToken");
      if (token) {
        const decoded = jwtDecode(token);
        if (decoded && decoded.name) setUserName(decoded.name);
      }
    } catch (_) {}
  }, []);

  const renderSection = () => {
    switch (active) {
      case "buildings":
        return <Buildings />;
      case "tenants":
        return <Tenants />;
      case "floors":
        return <Floors />;
      case "rooms":
        return <Rooms />;
      case "security-users":
        return <SecurityUsers />;
      default:
        return null;
    }
  };

  return (
    <div className="min-h-screen">
      <header className="bg-white shadow">
        <div className="container-app py-4 flex items-center justify-between">
          <h1 className="text-2xl font-semibold">Admin Dashboard</h1>
          <div className="flex items-center gap-4">
            <span className="text-gray-600">Hello, {userName || "Admin"}</span>
            <button
              onClick={() => {
                localStorage.removeItem("jwtToken");
                navigate("/");
              }}
              className="btn btn-neutral"
            >
              Logout
            </button>
          </div>
        </div>
      </header>
      <div className="container-app py-6 grid grid-cols-1 md:grid-cols-4 gap-6">
        <aside className="md:col-span-1 card">
          <div className="card-body p-3">
            <nav className="space-y-2">
              {sections.map((s) => (
                <button
                  key={s.key}
                  onClick={() => setActive(s.key)}
                  className={`w-full text-left btn ${
                    active === s.key ? 'btn-primary' : 'btn-outline'
                  }`}
                >
                  {s.label}
                </button>
              ))}
            </nav>
          </div>
        </aside>
        <main className="md:col-span-3 space-y-6">{renderSection()}</main>
      </div>
    </div>
  );
};

export default Admin;

