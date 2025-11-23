import React from "react";
import { useNavigate } from "react-router-dom";
import Buildings from "./admin/Buildings";
import Tenants from "./admin/Tenants";
import Floors from "./admin/Floors";
import Rooms from "./admin/Rooms";

const sections = [
  { key: "buildings", label: "Buildings" },
  { key: "tenants", label: "Tenants" },
  { key: "floors", label: "Floors" },
  { key: "rooms", label: "Rooms" },
];

const Admin = () => {
  const navigate = useNavigate();
  const [active, setActive] = React.useState("buildings");

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
      default:
        return null;
    }
  };

  return (
    <div className="min-h-screen bg-gray-100">
      <header className="bg-white shadow">
        <div className="mx-auto max-w-7xl px-4 py-4 flex items-center justify-between">
          <h1 className="text-2xl font-semibold">Admin Dashboard</h1>
          <button
            onClick={() => {
              localStorage.removeItem("jwtToken");
              navigate("/");
            }}
            className="rounded bg-gray-800 text-white px-4 py-2"
          >
            Logout
          </button>
        </div>
      </header>
      <div className="mx-auto max-w-7xl px-4 py-6 grid grid-cols-1 md:grid-cols-4 gap-6">
        <aside className="md:col-span-1 bg-white shadow rounded p-3">
          <nav className="space-y-1">
            {sections.map((s) => (
              <button
                key={s.key}
                onClick={() => setActive(s.key)}
                className={`${
                  active === s.key
                    ? "bg-primary-50 text-primary-700 border-primary-200"
                    : "bg-white text-gray-700 hover:bg-gray-50"
                } w-full text-left border rounded px-3 py-2`}
              >
                {s.label}
              </button>
            ))}
          </nav>
        </aside>
        <main className="md:col-span-3 space-y-6">{renderSection()}</main>
      </div>
    </div>
  );
};

export default Admin;

