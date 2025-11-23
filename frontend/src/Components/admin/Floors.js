import React, { useEffect, useState } from 'react';
import api from '../../lib/api';

export default function Floors() {
  const [buildings, setBuildings] = useState([]);
  const [floors, setFloors] = useState([]);
  const [form, setForm] = useState({ buildingId: '', floorNumber: '' });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const loadBuildings = async () => {
    try {
      const res = await api.get(`/api/admin/buildings`, { params: { page: 0, size: 100 } });
      setBuildings(res.data?.content || []);
    } catch {}
  };

  const loadFloors = async (buildingId) => {
    if (!buildingId) { setFloors([]); return; }
    try {
      const res = await api.get(`/api/admin/floors/by-building/${buildingId}`);
      setFloors(res.data || []);
    } catch { setFloors([]); }
  };

  useEffect(() => { loadBuildings(); }, []);

  const onSubmit = async (e) => {
    e.preventDefault();
    if (!form.buildingId || !form.floorNumber) { setError('Building and floor number required'); return; }
    try {
      setLoading(true);
      setError('');
      await api.post(`/api/admin/floors`, { buildingId: Number(form.buildingId), floorNumber: Number(form.floorNumber) });
      setForm({ buildingId: form.buildingId, floorNumber: '' });
      await loadFloors(form.buildingId);
    } catch (e) {
      setError(e.response?.data?.message || 'Save failed');
    } finally { setLoading(false); }
  };

  return (
    <div className="space-y-6">
      <h2 className="text-xl font-semibold">Floors</h2>

      <form onSubmit={onSubmit} className="bg-white shadow rounded p-4 grid grid-cols-1 md:grid-cols-3 gap-4">
        <div>
          <label className="block text-sm font-medium mb-1">Building</label>
          <select className="w-full rounded border border-gray-300 px-3 py-2" value={form.buildingId} onChange={(e)=>{ setForm({ ...form, buildingId: e.target.value }); loadFloors(e.target.value); }}>
            <option value="">Select</option>
            {buildings.map(b => <option key={b.id} value={b.id}>{b.name}</option>)}
          </select>
        </div>
        <div>
          <label className="block text-sm font-medium mb-1">Floor Number</label>
          <input type="number" className="w-full rounded border border-gray-300 px-3 py-2" value={form.floorNumber} onChange={(e)=>setForm({ ...form, floorNumber: e.target.value })} min={0} />
        </div>
        <div className="flex items-end">
          <button type="submit" className="rounded bg-primary-600 hover:bg-primary-700 text-white px-4 py-2" disabled={loading}>Create</button>
        </div>
      </form>

      {error && <div className="rounded bg-red-50 text-red-700 px-3 py-2 text-sm">{error}</div>}

      <div className="bg-white shadow rounded overflow-hidden">
        <table className="min-w-full">
          <thead className="bg-gray-50 text-left text-sm text-gray-600">
            <tr>
              <th className="px-4 py-3">ID</th>
              <th className="px-4 py-3">Floor Number</th>
            </tr>
          </thead>
          <tbody>
            {floors.map(f => (
              <tr key={f.id} className="border-t">
                <td className="px-4 py-3">{f.id}</td>
                <td className="px-4 py-3">{f.floorNumber}</td>
              </tr>
            ))}
            {floors.length === 0 && (
              <tr><td className="px-4 py-3 text-gray-500" colSpan={2}>No floors found.</td></tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
