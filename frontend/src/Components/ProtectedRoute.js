import React from 'react';
import { Navigate } from 'react-router-dom';
import { jwtDecode } from 'jwt-decode';
import { toast } from '../lib/toast';

export default function ProtectedRoute({ children, requireRole }) {
  const token = localStorage.getItem('jwtToken');
  if (!token) {
    toast.error('Please login to continue');
    return <Navigate to="/" replace />;
  }
  try {
    const decoded = jwtDecode(token);
    const role = decoded?.userType;
    if (requireRole && role !== requireRole) {
      toast.error('Unauthorized');
      return <Navigate to="/" replace />;
    }
  } catch (e) {
    toast.error('Session invalid, please login');
    return <Navigate to="/" replace />;
  }
  return children;
}
