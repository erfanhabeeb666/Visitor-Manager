import React from 'react';

export default function Toaster() {
  const [toasts, setToasts] = React.useState([]);

  React.useEffect(() => {
    const handler = (e) => {
      const id = Math.random().toString(36).slice(2);
      const toast = { id, type: e.detail?.type || 'info', message: e.detail?.message || '' };
      setToasts((t) => [...t, toast]);
      setTimeout(() => setToasts((t) => t.filter((x) => x.id !== id)), 3000);
    };
    window.addEventListener('app:toast', handler);
    return () => window.removeEventListener('app:toast', handler);
  }, []);

  const color = (type) => {
    switch (type) {
      case 'success': return 'bg-green-600';
      case 'error': return 'bg-red-600';
      default: return 'bg-gray-800';
    }
  };

  return (
    <div className="fixed top-4 right-4 z-50 space-y-2">
      {toasts.map(t => (
        <div key={t.id} className={`text-white px-4 py-2 rounded shadow ${color(t.type)}`}>
          {t.message}
        </div>
      ))}
    </div>
  );
}
