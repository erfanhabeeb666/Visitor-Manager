import React, { useEffect, useState } from 'react';
import api from '../../lib/api';
import { toast } from '../../lib/toast';

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
      toast.success('Room created');
    } catch (e) {
      setError(e.response?.data?.message || 'Save failed');
      toast.error('Save failed');
    } finally { setLoading(false); }
  };

  return (
    <div className="space-y-6">
      <h2 className="text-xl font-semibold">Rooms</h2>

      <form onSubmit={onSubmit} className="card">
        <div className="card-body grid grid-cols-1 md:grid-cols-4 gap-4">
          <div>
            <label>Building</label>
            <select className="select" value={form.buildingId} onChange={(e)=>onChangeBuilding(e.target.value)}>
              <option value="">Select</option>
              {buildings.map(b => <option key={b.id} value={b.id}>{b.name}</option>)}
            </select>
          </div>
          <div>
            <label>Floor</label>
            <select className="select" value={form.floorId} onChange={(e)=>onChangeFloor(e.target.value)} disabled={!form.buildingId}>
              <option value="">Select</option>
              {floors.map(f => <option key={f.id} value={f.id}>Floor {f.floorNumber}</option>)}
            </select>
          </div>
          <div>
            <label>Room Number</label>
            <input className="input" value={form.roomNumber} onChange={(e)=>setForm({...form, roomNumber: e.target.value})} />
          </div>
          <div className="flex items-end">
            <button type="submit" className="btn btn-primary" disabled={loading}>{loading ? 'Saving...' : 'Create'}</button>
          </div>
        </div>
      </form>

      {error && <div className="rounded bg-red-50 text-red-700 px-3 py-2 text-sm">{error}</div>}

      <div className="card overflow-hidden">
        <div className="card-body p-0">
        <table className="table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Room Number</th>
            </tr>
          </thead>
          <tbody>
            {rooms.map(r => (
              <tr key={r.id} className="table-row">
                <td>{r.id}</td>
                <td>{r.roomNumber}</td>
              </tr>
            ))}
            {rooms.length === 0 && (
              <tr><td className="px-4 py-3 text-gray-500" colSpan={2}>No rooms found.</td></tr>
            )}
          </tbody>
        </table>
        </div>
      </div>
    </div>
  );
}
