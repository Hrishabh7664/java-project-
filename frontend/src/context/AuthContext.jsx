import React, { createContext, useContext, useState, useEffect } from 'react';
import * as apiService from '../services/apiService';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(apiService.getCurrentUser());
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const savedUser = apiService.getCurrentUser();
    if (savedUser) {
      setUser(savedUser);
    }
  }, []);

  const login = async (username, password) => {
    setLoading(true);
    try {
      const data = await apiService.login(username, password);
      const currentUser = apiService.getCurrentUser() || data?.user || { username };
      setUser(currentUser);
      return data;
    } finally {
      setLoading(false);
    }
  };

  const logout = () => {
    apiService.logout();
    setUser(null);
  };

  const value = {
    user,
    loading,
    login,
    logout,
    isAuthenticated: !!user
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

export default AuthContext;
