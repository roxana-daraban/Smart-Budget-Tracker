/**
 * Declarații de tip pentru authService.js
 * updateProfile acceptă username și email opționale (backend le tratează ca atare).
 */
export interface UpdateProfileData {
  username?: string;
  email?: string;
}

export declare const authService: {
  login(email: string, password: string): Promise<{ token: string; user: { id: number; email?: string; username?: string; role?: string } }>;
  register(username: string, email: string, password: string): Promise<{ token: string; user: { id: number; email?: string; username?: string; role?: string } }>;
  logout(): void;
  getCurrentUser(): { id: string | number; email?: string; username?: string; role?: string } | null;
  isAuthenticated(): boolean;
  updateProfile(data: UpdateProfileData): Promise<{ user: { id: number; email?: string; username?: string; role?: string } }>;
};
