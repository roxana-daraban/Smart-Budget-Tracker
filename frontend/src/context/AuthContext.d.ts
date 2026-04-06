import React from 'react';


export interface User {
  id: string | number;
  email?: string;
  username?: string;
  baseCurrency?: string;
}

export interface LoginResult {
  success: boolean;
  error?: string;
}

export interface AuthContextType {
  user: User | null;
  login: (email: string, password: string) => Promise<LoginResult>;
  logout: () => void;
  loading: boolean;
  register: (username: string, email: string, password: string) => Promise<LoginResult>;
  updateProfile: (data: { username?: string; email?: string }) => Promise<{ success: boolean; error?: string }>;
}

export declare function useAuth(): AuthContextType;
