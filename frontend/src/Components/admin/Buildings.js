import React, { useEffect, useMemo, useState } from 'react';
import api from '../../lib/api';
import { toast } from '../../lib/toast';

const pageSize = 10;

export default function Buildings() {
  const [items, setItems] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const [form, setForm] = useState({ id: null, name: '', address: '' });
  const isEditing = useMemo(() => form.id !== null, [form.id]);

  const fetchData = async (p = page) => {
    try {
      setLoading(true);
      setError('');
      const res = await api.get(`/api/admin/buildings`, {
        params: { page: p, size: pageSize },
      });
      const data = res.data;
      setItems(data.content || []);
      setTotalPages(data.totalPages || 1);
      setPage(data.number || 0);
    } catch (e) {
      setError(e.response?.data?.message || 'Failed to load buildings');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData(0);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const onSubmit = async (e) => {
    e.preventDefault();
    try {
      setLoading(true);
      setError('');
      if (isEditing) {
        await api.put(`/api/admin/buildings/${form.id}`, {
          name: form.name,
          address: form.address,
        });
        toast.success('Building updated');
      } else {
        await api.post(`/api/admin/buildings`, {
          name: form.name,
          address: form.address,
        });
        toast.success('Building created');
      }
      setForm({ id: null, name: '', address: '' });
      fetchData(0);
    } catch (e) {
      setError(e.response?.data?.message || 'Save failed');
      toast.error('Save failed');
    } finally {
      setLoading(false);
    }
  };

  const onEdit = (item) => {
    setForm({ id: item.id, name: item.name || '', address: item.address || '' });
  };

  const onDelete = async (id) => {
    if (!window.confirm('Delete this building?')) return;
    try {
      setLoading(true);
      setError('');
      await api.delete(`/api/admin/buildings/${id}`);
      toast.success('Building deleted');
      fetchData(page);
    } catch (e) {
      setError(e.response?.data?.message || 'Delete failed');
      toast.error('Delete failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h2 className="text-xl font-semibold">Buildings</h2>
      </div>

      <form onSubmit={onSubmit} className="card">
        <div className="card-body grid grid-cols-1 md:grid-cols-3 gap-4">
          <div>
            <label>Name</label>
            <input
              className="input"
              value={form.name}
              onChange={(e) => setForm({ ...form, name: e.target.value })}
              required
            />
          </div>
          <div>
            <label>Address</label>
            <input
              className="input"
              value={form.address}
              onChange={(e) => setForm({ ...form, address: e.target.value })}
            />
          </div>
          <div className="flex items-end gap-2">
            <button
              type="submit"
              className="btn btn-primary"
              disabled={loading}
            >
              {loading ? 'Saving...' : (isEditing ? 'Update' : 'Create')}
            </button>
            {isEditing && (
              <button
                type="button"
                className="btn btn-outline"
                onClick={() => setForm({ id: null, name: '', address: '' })}
                disabled={loading}
              >
                Cancel
              </button>
            )}
          </div>
        </div>
      </form>

      {error && (
        <div className="rounded bg-red-50 text-red-700 px-3 py-2 text-sm">{error}</div>
      )}

      <div className="card overflow-hidden">
        <table className="table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Name</th>
              <th>Address</th>
              <th className="text-right">Actions</th>
            </tr>
          </thead>
          <tbody>
            {items.map((b) => (
              <tr key={b.id} className="table-row">
                <td>{b.id}</td>
                <td>{b.name}</td>
                <td>{b.address}</td>
                <td className="text-right space-x-2">
                  <button
                    className="btn btn-outline"
                    onClick={() => onEdit(b)}
                  >
                    Edit
                  </button>
                  <button
                    className="btn btn-danger"
                    onClick={() => onDelete(b.id)}
                  >
                    Delete
                  </button>
                </td>
              </tr>
            ))}
            {items.length === 0 && !loading && (
              <tr>
                <td className="px-4 py-3 text-gray-500" colSpan={4}>No buildings found.</td>
              </tr>
            )}
          </tbody>
        </table>

        <div className="flex items-center justify-between p-3 border-t bg-gray-50 text-sm">
          <div>Page {page + 1} of {Math.max(totalPages, 1)}</div>
          <div className="space-x-2">
            <button
              className="btn btn-outline px-3 py-1"
              onClick={() => fetchData(Math.max(page - 1, 0))}
              disabled={page === 0 || loading}
            >
              {loading ? '...' : 'Previous'}
            </button>
            <button
              className="btn btn-outline px-3 py-1"
              onClick={() => fetchData(Math.min(page + 1, totalPages - 1))}
              disabled={page >= totalPages - 1 || loading}
            >
              {loading ? '...' : 'Next'}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
