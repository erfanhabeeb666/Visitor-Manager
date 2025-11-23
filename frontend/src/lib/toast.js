// Simple event-based toast utility
export function showToast(type, message) {
  try {
    window.dispatchEvent(new CustomEvent('app:toast', { detail: { type, message } }));
  } catch (e) {
    // noop
  }
}

export const toast = {
  success: (msg) => showToast('success', msg),
  error: (msg) => showToast('error', msg),
  info: (msg) => showToast('info', msg),
};
