import React from 'react';

export interface User {
  id: string | number;
  email?: string;
  username?: string;
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
}

export declare function useAuth(): AuthContextType;
export declare function AuthProvider({ children }: { children: React.ReactNode }): React.JSX.Element;