import React, { useEffect, useState } from 'react';
import api from '../../lib/api';
import { toast } from '../../lib/toast';

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
      toast.success('Floor created');
    } catch (e) {
      setError(e.response?.data?.message || 'Save failed');
      toast.error('Save failed');
    } finally { setLoading(false); }
  };

  return (
    <div className="space-y-6">
      <h2 className="text-xl font-semibold">Floors</h2>

      <form onSubmit={onSubmit} className="card">
        <div className="card-body grid grid-cols-1 md:grid-cols-3 gap-4">
          <div>
            <label>Building</label>
            <select className="select" value={form.buildingId} onChange={(e)=>{ setForm({ ...form, buildingId: e.target.value }); loadFloors(e.target.value); }}>
              <option value="">Select</option>
              {buildings.map(b => <option key={b.id} value={b.id}>{b.name}</option>)}
            </select>
          </div>
          <div>
            <label>Floor Number</label>
            <input type="number" className="input" value={form.floorNumber} onChange={(e)=>setForm({ ...form, floorNumber: e.target.value })} min={0} />
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
              <th>Floor Number</th>
            </tr>
          </thead>
          <tbody>
            {floors.map(f => (
              <tr key={f.id} className="table-row">
                <td>{f.id}</td>
                <td>{f.floorNumber}</td>
              </tr>
            ))}
            {floors.length === 0 && (
              <tr><td className="px-4 py-3 text-gray-500" colSpan={2}>No floors found.</td></tr>
            )}
          </tbody>
        </table>
        </div>
      </div>
    </div>
  );
}
