import React, { useEffect, useMemo, useState } from 'react';
import api from '../../lib/api';

export default function Tenants() {
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const [buildings, setBuildings] = useState([]);
  const [floors, setFloors] = useState([]);
  const [rooms, setRooms] = useState([]);

  const [form, setForm] = useState({ id: null, tenantName: '', phoneNumber: '', buildingId: '', floorId: '', roomId: '' });
  const isEditing = useMemo(() => form.id !== null, [form.id]);

  const loadTenants = async () => {
    try {
      setLoading(true);
      setError('');
      const res = await api.get(`/api/admin/tenants`);
      setItems(Array.isArray(res.data) ? res.data : []);
    } catch (e) {
      setError(e.response?.data?.message || 'Failed to load tenants');
    } finally {
      setLoading(false);
    }
  };

  const loadBuildings = async () => {
    try {
      const res = await api.get(`/api/admin/buildings`, { params: { page: 0, size: 100 } });
      setBuildings(res.data?.content || []);
    } catch (e) {
      // ignore for now
    }
  };

  useEffect(() => {
    loadTenants();
    loadBuildings();
  }, []);

  const onChangeBuilding = async (buildingId) => {
    setForm((f) => ({ ...f, buildingId, floorId: '', roomId: '' }));
    setRooms([]);
    if (!buildingId) { setFloors([]); return; }
    try {
      const res = await api.get(`/api/admin/floors/by-building/${buildingId}`);
      setFloors(res.data || []);
    } catch (e) { setFloors([]); }
  };

  const onChangeFloor = async (floorId) => {
    setForm((f) => ({ ...f, floorId, roomId: '' }));
    if (!floorId) { setRooms([]); return; }
    try {
      const res = await api.get(`/api/admin/rooms/by-floor/${floorId}`);
      setRooms(res.data || []);
    } catch (e) { setRooms([]); }
  };

  const onSubmit = async (e) => {
    e.preventDefault();
    if (!form.roomId) { setError('Please select a room'); return; }
    try {
      setLoading(true);
      setError('');
      const payload = { tenantName: form.tenantName, phoneNumber: form.phoneNumber, roomId: Number(form.roomId) };
      if (isEditing) {
        await api.put(`/api/admin/tenants/${form.id}`, payload);
      } else {
        await api.post(`/api/admin/tenants`, payload);
      }
      setForm({ id: null, tenantName: '', phoneNumber: '', buildingId: '', floorId: '', roomId: '' });
      await loadTenants();
    } catch (e) {
      setError(e.response?.data?.message || 'Save failed');
    } finally { setLoading(false); }
  };

  const onEdit = (t) => {
    setForm({ id: t.id, tenantName: t.tenantName || '', phoneNumber: t.phoneNumber || '', buildingId: '', floorId: '', roomId: t.room?.id || '' });
  };

  const onDelete = async (id) => {
    if (!window.confirm('Delete this tenant?')) return;
    try {
      setLoading(true);
      setError('');
      await api.delete(`/api/admin/tenants/${id}`);
      await loadTenants();
    } catch (e) { setError(e.response?.data?.message || 'Delete failed'); }
    finally { setLoading(false); }
  };

  return (
    <div className="space-y-6">
      <h2 className="text-xl font-semibold">Tenants</h2>

      <form onSubmit={onSubmit} className="bg-white shadow rounded p-4 grid grid-cols-1 md:grid-cols-5 gap-4">
        <div>
          <label className="block text-sm font-medium mb-1">Name</label>
          <input className="w-full rounded border border-gray-300 px-3 py-2 focus:ring-2 focus:ring-primary-500" value={form.tenantName} onChange={(e)=>setForm({...form, tenantName:e.target.value})} required/>
        </div>
        <div>
          <label className="block text-sm font-medium mb-1">Phone</label>
          <input className="w-full rounded border border-gray-300 px-3 py-2 focus:ring-2 focus:ring-primary-500" value={form.phoneNumber} onChange={(e)=>setForm({...form, phoneNumber:e.target.value})} placeholder="Digits only" required/>
        </div>
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
          <label className="block text-sm font-medium mb-1">Room</label>
          <select className="w-full rounded border border-gray-300 px-3 py-2" value={form.roomId} onChange={(e)=>setForm({...form, roomId:e.target.value})} disabled={!form.floorId}>
            <option value="">Select</option>
            {rooms.map(r => <option key={r.id} value={r.id}>{r.roomNumber}</option>)}
          </select>
        </div>
        <div className="md:col-span-5 flex items-end gap-2">
          <button type="submit" className="rounded bg-primary-600 hover:bg-primary-700 text-white px-4 py-2" disabled={loading}>{isEditing? 'Update':'Create'}</button>
          {isEditing && (
            <button type="button" className="rounded border border-gray-300 px-4 py-2" onClick={()=>setForm({ id: null, tenantName: '', phoneNumber: '', buildingId: '', floorId: '', roomId: '' })} disabled={loading}>Cancel</button>
          )}
        </div>
      </form>

      {error && <div className="rounded bg-red-50 text-red-700 px-3 py-2 text-sm">{error}</div>}

      <div className="bg-white shadow rounded overflow-hidden">
        <table className="min-w-full">
          <thead className="bg-gray-50 text-left text-sm text-gray-600">
            <tr>
              <th className="px-4 py-3">ID</th>
              <th className="px-4 py-3">Name</th>
              <th className="px-4 py-3">Phone</th>
              <th className="px-4 py-3">Room</th>
              <th className="px-4 py-3">Floor</th>
              <th className="px-4 py-3">Building</th>
              <th className="px-4 py-3 text-right">Actions</th>
            </tr>
          </thead>
          <tbody>
            {items.map(t => (
              <tr key={t.id} className="border-t">
                <td className="px-4 py-3">{t.id}</td>
                <td className="px-4 py-3">{t.tenantName}</td>
                <td className="px-4 py-3">{t.phoneNumber}</td>
                <td className="px-4 py-3">{t.roomNumber}</td>
                <td className="px-4 py-3">{t.floorNumber}</td>
                <td className="px-4 py-3">{t.buildingName}</td>
                <td className="px-4 py-3 text-right space-x-2">
                  <button className="text-primary-700 hover:underline" onClick={()=>onEdit(t)}>Edit</button>
                  <button className="text-red-600 hover:underline" onClick={()=>onDelete(t.id)}>Delete</button>
                </td>
              </tr>
            ))}
            {items.length === 0 && !loading && (
              <tr><td className="px-4 py-3 text-gray-500" colSpan={7}>No tenants found.</td></tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
