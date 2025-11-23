import React from "react";
import api from "../lib/api";
import { toast } from "../lib/toast";
import Spinner from "./Spinner";
import { Html5QrcodeScanner } from "html5-qrcode";
import { jwtDecode } from "jwt-decode";
import { useNavigate } from "react-router-dom";

const Security = () => {
  const [visitId, setVisitId] = React.useState("");
  const [loading, setLoading] = React.useState(false);
  const [visits, setVisits] = React.useState([]);
  const [userName, setUserName] = React.useState("");
  const navigate = useNavigate();

  const fetchActive = async () => {
    try {
      setLoading(true);
      const res = await api.get("/api/security/active-visits");
      setVisits(Array.isArray(res.data) ? res.data : []);
    } catch (e) {
      toast.error("Failed to load active visits");
    } finally {
      setLoading(false);
    }
  };

  React.useEffect(() => { fetchActive(); }, []);

  // QR Scanner
  const scannerRef = React.useRef(null);
  const [scanning, setScanning] = React.useState(false);

  const startScanner = () => {
    try {
      if (scannerRef.current) return; // already running
      const scanner = new Html5QrcodeScanner("qr-reader", { fps: 10, qrbox: 250 });
      scanner.render(
        (decodedText) => {
          setVisitId(decodedText);
          toast.success("QR scanned");
        },
        () => {}
      );
      scannerRef.current = scanner;
      setScanning(true);
    } catch (e) {
      toast.error("Camera access failed");
    }
  };
  const stopScanner = async () => {
    try {
      if (scannerRef.current) {
        await scannerRef.current.clear();
        scannerRef.current = null;
      }
    } catch {}
    setScanning(false);
  };
  React.useEffect(() => {
    return () => { // cleanup
      if (scannerRef.current) {
        scannerRef.current.clear().catch(()=>{});
        scannerRef.current = null;
      }
    };
  }, []);

  const withAction = async (fn, successMsg) => {
    try {
      setLoading(true);
      await fn();
      toast.success(successMsg);
      await fetchActive();
    } catch (e) {
      const msg = e.response?.data?.message || "Action failed";
      toast.error(msg);
    } finally {
      setLoading(false);
    }
  };

  const handleScan = () => {
    if (!visitId) return toast.info("Enter visit ID");
    return withAction(() => api.post(`/api/security/scan/${visitId}`), "Scan processed");
  };
  const handleCheckIn = () => {
    if (!visitId) return toast.info("Enter visit ID");
    return withAction(() => api.post(`/api/security/check-in/${visitId}`), "Checked in");
  };
  const handleCheckOut = () => {
    if (!visitId) return toast.info("Enter visit ID");
    return withAction(() => api.post(`/api/security/check-out/${visitId}`), "Checked out");
  };

  const handleRowCheckOut = (id) => withAction(() => api.post(`/api/security/check-out/${id}`), "Checked out");
  const handleRowScan = (id) => withAction(() => api.post(`/api/security/scan/${id}`), "Scan processed");

  React.useEffect(() => {
    // greet from JWT
    try {
      const token = localStorage.getItem("jwtToken");
      if (token) {
        const decoded = jwtDecode(token);
        if (decoded && decoded.name) setUserName(decoded.name);
      }
    } catch (_) {}
  }, []);

  return (
    <div className="min-h-screen">
      <header className="bg-white shadow">
        <div className="container-app py-4 flex items-center justify-between">
          <h1 className="text-2xl font-semibold">Security Dashboard</h1>
          <div className="flex items-center gap-4">
            <div className="text-gray-600">Hello, {userName || "Security"}</div>
            <button
              className="btn btn-neutral"
              onClick={() => {
                localStorage.removeItem("jwtToken");
                navigate("/");
              }}
            >
              Logout
            </button>
          </div>
        </div>
      </header>
      <main className="container-app py-6 space-y-6">
        <div className="card">
          <div className="bg-white shadow rounded p-4 space-y-4">
            <p className="text-gray-700">Scan QR by entering Visit ID or use quick check-in/out.</p>
            <div className="grid grid-cols-1 md:grid-cols-5 gap-3">
              <input
                className="md:col-span-2 w-full rounded border border-gray-300 px-3 py-2 focus:ring-2 focus:ring-primary-500"
                placeholder="Visit ID"
                value={visitId}
                onChange={(e) => setVisitId(e.target.value)}
              />
              <button onClick={handleScan} disabled={loading} className="rounded bg-gray-900 text-white px-4 py-2 inline-flex items-center gap-2 justify-center">
                {loading && <Spinner size={16} />}<span>Scan</span>
              </button>
              <button onClick={handleCheckIn} disabled={loading} className="rounded bg-green-600 hover:bg-green-700 text-white px-4 py-2 inline-flex items-center gap-2 justify-center">
                {loading && <Spinner size={16} />}<span>Check-In</span>
              </button>
              <button onClick={handleCheckOut} disabled={loading} className="rounded bg-red-600 hover:bg-red-700 text-white px-4 py-2 inline-flex items-center gap-2 justify-center">
                {loading && <Spinner size={16} />}<span>Check-Out</span>
              </button>
            </div>
            <div className="mt-4">
              <div className="flex items-center gap-2 mb-2">
                {!scanning ? (
                  <button onClick={startScanner} className="btn btn-primary">Start Camera</button>
                ) : (
                  <button onClick={stopScanner} className="btn btn-outline">Stop Camera</button>
                )}
              </div>
              <div id="qr-reader" className="w-full max-w-md"></div>
            </div>
          </div>
        </div>

        <div className="card overflow-hidden">
          <div className="flex items-center justify-between p-3 border-b">
            <h2 className="font-medium">Active Visits</h2>
            <button onClick={fetchActive} disabled={loading} className="rounded border border-gray-300 px-3 py-1 inline-flex items-center gap-2">
              {loading && <Spinner size={16} />}<span>Refresh</span>
            </button>
          </div>
          <div className="card-body p-0">
            <table className="table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Visitor</th>
                  <th>Status</th>
                  <th>Tenant</th>
                  <th>Building</th>
                  <th>Floor</th>
                  <th>Room</th>
                  <th>Visit Time</th>
                  <th className="text-right">Actions</th>
                </tr>
              </thead>
              <tbody>
                {visits.map(v => (
                  <tr key={v.id} className="table-row">
                    <td>{v.id}</td>
                    <td>{v.visitorName}</td>
                    <td>{v.status}</td>
                    <td>{v.tenant?.tenantName || '-'}</td>
                    <td>{v.building?.name || '-'}</td>
                    <td>{v.floor?.floorNumber ?? '-'}</td>
                    <td>{v.room?.roomNumber || '-'}</td>
                    <td>{v.visitDateTime || '-'}</td>
                    <td className="text-right space-x-2">
                      <button className="btn btn-outline" onClick={() => handleRowScan(v.id)}>Scan</button>
                      <button className="btn btn-danger" onClick={() => handleRowCheckOut(v.id)}>Check-Out</button>
                    </td>
                  </tr>
                ))}
                {visits.length === 0 && (
                  <tr><td className="px-4 py-3 text-gray-500" colSpan={9}>No active visits.</td></tr>
                )}
              </tbody>
            </table>
          </div>
        </div>
      </main>
    </div>
  );
};

export default Security;
