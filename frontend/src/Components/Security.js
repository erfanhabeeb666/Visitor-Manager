import React from "react";

const Security = () => {
    return (
        <div className="min-h-screen bg-gray-100">
            <header className="bg-white shadow">
                <div className="mx-auto max-w-7xl px-4 py-4">
                    <h1 className="text-2xl font-semibold">Security Dashboard</h1>
                </div>
            </header>
            <main className="mx-auto max-w-7xl px-4 py-6 space-y-6">
                <div className="bg-white shadow rounded p-4">
                    <p className="text-gray-700">Welcome to the Security Dashboard. Here you can mscan qr and view active visits</p>
                </div>
            </main>
        </div>
    );
};

export default Security;
