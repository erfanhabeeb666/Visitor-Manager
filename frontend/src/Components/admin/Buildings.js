import React, { useEffect, useMemo, useState } from 'react';
import api from '../../lib/api';

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
      } else {
        await api.post(`/api/admin/buildings`, {
          name: form.name,
          address: form.address,
        });
      }
      setForm({ id: null, name: '', address: '' });
      fetchData(0);
    } catch (e) {
      setError(e.response?.data?.message || 'Save failed');
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
      fetchData(page);
    } catch (e) {
      setError(e.response?.data?.message || 'Delete failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h2 className="text-xl font-semibold">Buildings</h2>
      </div>

      <form onSubmit={onSubmit} className="bg-white shadow rounded p-4 grid grid-cols-1 md:grid-cols-3 gap-4">
        <div>
          <label className="block text-sm font-medium mb-1">Name</label>
          <input
            className="w-full rounded border border-gray-300 px-3 py-2 focus:outline-none focus:ring-2 focus:ring-primary-500"
            value={form.name}
            onChange={(e) => setForm({ ...form, name: e.target.value })}
            required
          />
        </div>
        <div>
          <label className="block text-sm font-medium mb-1">Address</label>
          <input
            className="w-full rounded border border-gray-300 px-3 py-2 focus:outline-none focus:ring-2 focus:ring-primary-500"
            value={form.address}
            onChange={(e) => setForm({ ...form, address: e.target.value })}
          />
        </div>
        <div className="flex items-end gap-2">
          <button
            type="submit"
            className="inline-flex items-center justify-center rounded bg-primary-600 hover:bg-primary-700 text-white px-4 py-2"
            disabled={loading}
          >
            {isEditing ? 'Update' : 'Create'}
          </button>
          {isEditing && (
            <button
              type="button"
              className="inline-flex items-center justify-center rounded border border-gray-300 px-4 py-2"
              onClick={() => setForm({ id: null, name: '', address: '' })}
              disabled={loading}
            >
              Cancel
            </button>
          )}
        </div>
      </form>

      {error && (
        <div className="rounded bg-red-50 text-red-700 px-3 py-2 text-sm">{error}</div>
      )}

      <div className="bg-white shadow rounded overflow-hidden">
        <table className="min-w-full">
          <thead className="bg-gray-50 text-left text-sm text-gray-600">
            <tr>
              <th className="px-4 py-3">ID</th>
              <th className="px-4 py-3">Name</th>
              <th className="px-4 py-3">Address</th>
              <th className="px-4 py-3 text-right">Actions</th>
            </tr>
          </thead>
          <tbody>
            {items.map((b) => (
              <tr key={b.id} className="border-t">
                <td className="px-4 py-3">{b.id}</td>
                <td className="px-4 py-3">{b.name}</td>
                <td className="px-4 py-3">{b.address}</td>
                <td className="px-4 py-3 text-right space-x-2">
                  <button
                    className="text-primary-700 hover:underline"
                    onClick={() => onEdit(b)}
                  >
                    Edit
                  </button>
                  <button
                    className="text-red-600 hover:underline"
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
              className="rounded border border-gray-300 px-3 py-1 disabled:opacity-50"
              onClick={() => fetchData(Math.max(page - 1, 0))}
              disabled={page === 0 || loading}
            >
              Previous
            </button>
            <button
              className="rounded border border-gray-300 px-3 py-1 disabled:opacity-50"
              onClick={() => fetchData(Math.min(page + 1, totalPages - 1))}
              disabled={page >= totalPages - 1 || loading}
            >
              Next
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
