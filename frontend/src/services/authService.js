import api from './api';

const USER_STORAGE_KEY = 'user';

function saveUserToStorage(user) {
  if (user && user.id != null) {
    localStorage.setItem('userId', String(user.id));
    localStorage.setItem(USER_STORAGE_KEY, JSON.stringify({
      id: user.id,
      username: user.username ?? null,
      email: user.email ?? null,
      role: user.role ?? null,
      baseCurrency: user.baseCurrency ?? 'RON',
    }));
  }
}

export const authService = {
  async login(email, password) {
    const { data } = await api.post('/auth/login', { email, password });
    localStorage.setItem('token', data.token);
    const user = {
      id: data.userId,
      email: data.email,
      username: data.username,
      role: data.role,
      baseCurrency: data.baseCurrency ?? 'RON',
    };
    saveUserToStorage(user);
    return { token: data.token, user };
  },

  async register(username, email, password) {
    const { data } = await api.post('/auth/register', { username, email, password });
    localStorage.setItem('token', data.token);
    const user = {
      id: data.userId,
      email: data.email,
      username: data.username,
      role: data.role,
      baseCurrency: data.baseCurrency ?? 'RON',
    };
    saveUserToStorage(user);
    return { token: data.token, user };
  },

  logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('userId');
    localStorage.removeItem(USER_STORAGE_KEY);
  },

  getCurrentUser() {
    const raw = localStorage.getItem(USER_STORAGE_KEY);
    if (raw) {
      try {
        const user = JSON.parse(raw);
        if (user.id != null) return user;
      } catch (_) {}
    }
    const userId = localStorage.getItem('userId');
    return userId ? { id: userId } : null;
  },

  isAuthenticated() {
    return !!localStorage.getItem('token');
  },

  /**
   * Trimite username și/sau email la PUT /auth/profile.
   * Backend returnează același format ca la login (token, userId, username, email, role).
   * Actualizăm token-ul și userul în localStorage; returnez userul pentru a actualiza contextul.
   */
  async updateProfile({ username, email }) {
    const { data } = await api.put('/auth/profile', { username, email });
    localStorage.setItem('token', data.token);
    const user = {
      id: data.userId,
      email: data.email,
      username: data.username,
      role: data.role,
      baseCurrency: data.baseCurrency ?? 'RON',
    };
    saveUserToStorage(user);
    return { user };
  },
};