import React from 'react';
import { Navigate } from 'react-router-dom';
import { jwtDecode } from 'jwt-decode';

export default function ProtectedRoute({ children, requireRole }) {
  const token = localStorage.getItem('jwtToken');
  if (!token) return <Navigate to="/" replace />;
  try {
    const decoded = jwtDecode(token);
    const role = decoded?.userType;
    if (requireRole && role !== requireRole) return <Navigate to="/" replace />;
  } catch (e) {
    return <Navigate to="/" replace />;
  }
  return children;
}
