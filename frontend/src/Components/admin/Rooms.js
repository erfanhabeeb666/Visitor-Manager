import React, { useEffect, useState } from 'react';
import api from '../../lib/api';

export default function Rooms() {
  const [buildings, setBuildings] = useState([]);
  const [floors, setFloors] = useState([]);
  const [rooms, setRooms] = useState([]);
  const [form, setForm] = useState({ buildingId: '', floorId: '', roomNumber: '' });
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

  const loadRooms = async (floorId) => {
    if (!floorId) { setRooms([]); return; }
    try {
      const res = await api.get(`/api/admin/rooms/by-floor/${floorId}`);
      setRooms(res.data || []);
    } catch { setRooms([]); }
  };

  useEffect(() => { loadBuildings(); }, []);

  const onChangeBuilding = async (buildingId) => {
    setForm((f) => ({ ...f, buildingId, floorId: '' }));
    setRooms([]);
    await loadFloors(buildingId);
  };

  const onChangeFloor = async (floorId) => {
    setForm((f) => ({ ...f, floorId }));
    await loadRooms(floorId);
  };

  const onSubmit = async (e) => {
    e.preventDefault();
    if (!form.floorId || !form.roomNumber) { setError('Floor and room number required'); return; }
    try {
      setLoading(true);
      setError('');
      await api.post(`/api/admin/rooms`, { floorId: Number(form.floorId), roomNumber: form.roomNumber });
      setForm({ ...form, roomNumber: '' });
      await loadRooms(form.floorId);
    } catch (e) {
      setError(e.response?.data?.message || 'Save failed');
    } finally { setLoading(false); }
  };

  return (
    <div className="space-y-6">
      <h2 className="text-xl font-semibold">Rooms</h2>

      <form onSubmit={onSubmit} className="bg-white shadow rounded p-4 grid grid-cols-1 md:grid-cols-4 gap-4">
        <div>
          <label className="block text-sm font-medium mb-1">Building</label>
          <select className="w-full rounded border border-gray-300 px-3 py-2" value={form.buildingId} onChange={(e)=>onChangeBuilding(e.target.value)}>
            <option value="">Select</option>
            {buildings.map(b => <option key={b.id} value={b.id}>{b.name}</option>)}
          </select>
        </div>
        <div>
          <label className="block text-sm font-medium mb-1">Floor</label>
          <select className="w-full rounded border border-gray-300 px-3 py-2" value={form.floorId} onChange={(e)=>onChangeFloor(e.target.value)} disabled={!form.buildingId}>
            <option value="">Select</option>
            {floors.map(f => <option key={f.id} value={f.id}>Floor {f.floorNumber}</option>)}
          </select>
        </div>
        <div>
          <label className="block text-sm font-medium mb-1">Room Number</label>
          <input className="w-full rounded border border-gray-300 px-3 py-2" value={form.roomNumber} onChange={(e)=>setForm({...form, roomNumber: e.target.value})} />
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
              <th className="px-4 py-3">Room Number</th>
            </tr>
          </thead>
          <tbody>
            {rooms.map(r => (
              <tr key={r.id} className="border-t">
                <td className="px-4 py-3">{r.id}</td>
                <td className="px-4 py-3">{r.roomNumber}</td>
              </tr>
            ))}
            {rooms.length === 0 && (
              <tr><td className="px-4 py-3 text-gray-500" colSpan={2}>No rooms found.</td></tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
