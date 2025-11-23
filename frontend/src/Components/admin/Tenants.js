import React, { useEffect, useMemo, useState } from 'react';
import api from '../../lib/api';
import { toast } from '../../lib/toast';

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
        toast.success('Tenant updated');
      } else {
        await api.post(`/api/admin/tenants`, payload);
        toast.success('Tenant created');
      }
      setForm({ id: null, tenantName: '', phoneNumber: '', buildingId: '', floorId: '', roomId: '' });
      await loadTenants();
    } catch (e) {
      setError(e.response?.data?.message || 'Save failed');
      toast.error('Save failed');
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
      toast.success('Tenant deleted');
    } catch (e) { setError(e.response?.data?.message || 'Delete failed'); toast.error('Delete failed'); }
    finally { setLoading(false); }
  };

  return (
    <div className="space-y-6">
      <h2 className="text-xl font-semibold">Tenants</h2>

      <form onSubmit={onSubmit} className="card">
        <div className="card-body grid grid-cols-1 md:grid-cols-5 gap-4">
          <div>
            <label>Name</label>
            <input className="input" value={form.tenantName} onChange={(e)=>setForm({...form, tenantName:e.target.value})} required/>
          </div>
          <div>
            <label>Phone</label>
            <input className="input" value={form.phoneNumber} onChange={(e)=>setForm({...form, phoneNumber:e.target.value})} placeholder="Digits only" required/>
          </div>
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
            <label>Room</label>
            <select className="select" value={form.roomId} onChange={(e)=>setForm({...form, roomId:e.target.value})} disabled={!form.floorId}>
              <option value="">Select</option>
              {rooms.map(r => <option key={r.id} value={r.id}>{r.roomNumber}</option>)}
            </select>
          </div>
          <div className="md:col-span-5 flex items-end gap-2">
            <button type="submit" className="btn btn-primary" disabled={loading}>{loading ? 'Saving...' : (isEditing? 'Update':'Create')}</button>
            {isEditing && (
              <button type="button" className="btn btn-outline" onClick={()=>setForm({ id: null, tenantName: '', phoneNumber: '', buildingId: '', floorId: '', roomId: '' })} disabled={loading}>Cancel</button>
            )}
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
                <th>Name</th>
                <th>Phone</th>
                <th>Room</th>
                <th>Floor</th>
                <th>Building</th>
                <th className="text-right">Actions</th>
              </tr>
            </thead>
            <tbody>
              {items.map(t => (
                <tr key={t.id} className="table-row">
                  <td>{t.id}</td>
                  <td>{t.tenantName}</td>
                  <td>{t.phoneNumber}</td>
                  <td>{t.roomNumber}</td>
                  <td>{t.floorNumber}</td>
                  <td>{t.buildingName}</td>
                  <td className="text-right space-x-2">
                    <button className="btn btn-outline" onClick={()=>onEdit(t)}>Edit</button>
                    <button className="btn btn-danger" onClick={()=>onDelete(t.id)}>Delete</button>
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
    </div>
  );
}
