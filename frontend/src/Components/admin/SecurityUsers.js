import React, { useEffect, useMemo, useState } from 'react';
import api from '../../lib/api';
import { toast } from '../../lib/toast';

export default function SecurityUsers() {
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [search, setSearch] = useState('');
  const [page, setPage] = useState(0);
  const pageSize = 10;

  const [form, setForm] = useState({ id: null, name: '', email: '', password: '', adharUid: '' });
  const isEditing = useMemo(() => form.id !== null, [form.id]);
  const canSubmit = useMemo(() => form.name && form.email && form.adharUid && (!isEditing ? form.password : true), [form, isEditing]);

  const loadItems = async () => {
    try {
      setLoading(true);
      setError('');
      const res = await api.get('/api/admin/security-users');
      setItems(Array.isArray(res.data) ? res.data : []);
    } catch (e) {
      setError(e.response?.data?.message || 'Failed to load users');
    } finally { setLoading(false); }
  };

  useEffect(() => { loadItems(); }, []);

  const onSubmit = async (e) => {
    e.preventDefault();
    if (!canSubmit) return;
    try {
      setLoading(true);
      setError('');
      if (isEditing) {
        await api.put(`/api/admin/security-users/${form.id}`, {
          name: form.name,
          email: form.email,
          password: form.password, // optional on backend; can be blank to keep existing
          adharUid: form.adharUid,
        });
        toast.success('Security user updated');
      } else {
        await api.post('/api/admin/security-users', {
          name: form.name,
          email: form.email,
          password: form.password,
          adharUid: form.adharUid,
        });
        toast.success('Security user created');
      }
      setForm({ id: null, name: '', email: '', password: '', adharUid: '' });
      await loadItems();
    } catch (e) {
      setError(e.response?.data?.message || 'Save failed');
    } finally { setLoading(false); }
  };

  const onEdit = (u) => {
    setForm({ id: u.id, name: u.name || '', email: u.email || '', password: '', adharUid: u.adharUid || '' });
  };

  const onCancel = () => setForm({ id: null, name: '', email: '', password: '', adharUid: '' });

  const onDelete = async (id) => {
    if (!window.confirm('Delete this security user?')) return;
    try {
      setLoading(true);
      setError('');
      await api.delete(`/api/admin/security-users/${id}`);
      await loadItems();
      toast.success('Security user deleted');
    } catch (e) { setError(e.response?.data?.message || 'Delete failed'); }
    finally { setLoading(false); }
  };

  const filtered = useMemo(() => {
    const q = search.trim().toLowerCase();
    if (!q) return items;
    return items.filter(u =>
      (u.name || '').toLowerCase().includes(q) ||
      (u.email || '').toLowerCase().includes(q) ||
      (u.adharUid || '').toLowerCase().includes(q)
    );
  }, [items, search]);

  const totalPages = Math.max(1, Math.ceil(filtered.length / pageSize));
  const currentPage = Math.min(page, totalPages - 1);
  const pageItems = useMemo(() => filtered.slice(currentPage * pageSize, (currentPage + 1) * pageSize), [filtered, currentPage]);

  return (
    <div className="space-y-6">
      <h2 className="text-xl font-semibold">Security Users</h2>

      <form onSubmit={onSubmit} className="card">
        <div className="card-body grid grid-cols-1 md:grid-cols-5 gap-4">
          <div>
            <label>Name</label>
            <input className="input" value={form.name} onChange={(e)=>setForm({...form, name:e.target.value})} required/>
          </div>
          <div>
            <label>Email</label>
            <input type="email" className="input" value={form.email} onChange={(e)=>setForm({...form, email:e.target.value})} required/>
          </div>
          <div>
            <label>Password</label>
            <input type="password" className="input" value={form.password} onChange={(e)=>setForm({...form, password:e.target.value})} placeholder={isEditing ? '(leave blank to keep unchanged)' : ''} required={!isEditing}/>
          </div>
          <div>
            <label>Aadhaar UID</label>
            <input className="input" value={form.adharUid} onChange={(e)=>setForm({...form, adharUid:e.target.value.replace(/\D/g,'')})} pattern="\d{12}" title="Aadhaar must be exactly 12 digits" maxLength={12} required/>
          </div>
          <div className="md:col-span-5 flex items-end gap-2">
            <button type="submit" className="btn btn-primary" disabled={loading || !canSubmit}>{isEditing ? 'Update' : 'Create'}</button>
            {isEditing && (
              <button type="button" className="btn btn-outline" onClick={onCancel} disabled={loading}>Cancel</button>
            )}
          </div>
        </div>
      </form>

      {error && <div className="rounded bg-red-50 text-red-700 px-3 py-2 text-sm">{error}</div>}

      <div className="card overflow-hidden">
        <div className="card-body">
          <div className="flex items-center justify-between mb-3">
            <input
              className="input max-w-sm"
              placeholder="Search by name, email, Aadhaar"
              value={search}
              onChange={(e)=>{ setSearch(e.target.value); setPage(0); }}
            />
            <div className="text-sm text-gray-600">{filtered.length} result(s)</div>
          </div>
          <div className="overflow-x-auto">
          <table className="table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Name</th>
                <th>Email</th>
                <th>Aadhaar UID</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
            {pageItems.map(u => (
              <tr key={u.id} className="table-row">
                <td>{u.id}</td>
                <td>{u.name}</td>
                <td>{u.email}</td>
                <td>{u.adharUid}</td>
                <td>
                  <div className="flex items-center gap-3">
                    <span>{u.status}</span>
                    <button className="btn btn-outline" onClick={() => onEdit(u)}>Edit</button>
                    <button className="btn btn-danger" onClick={() => onDelete(u.id)}>Delete</button>
                  </div>
                </td>
              </tr>
            ))}
            {pageItems.length === 0 && !loading && (
              <tr><td className="px-4 py-3 text-gray-500" colSpan={5}>No security users found.</td></tr>
            )}
            </tbody>
          </table>
          </div>
          <div className="flex items-center justify-between mt-3 text-sm">
            <div>Page {currentPage + 1} of {totalPages}</div>
            <div className="space-x-2">
              <button className="btn btn-outline" disabled={currentPage === 0} onClick={()=>setPage(p=>Math.max(0,p-1))}>Previous</button>
              <button className="btn btn-outline" disabled={currentPage >= totalPages - 1} onClick={()=>setPage(p=>Math.min(totalPages-1,p+1))}>Next</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
