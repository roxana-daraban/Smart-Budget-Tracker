import api from './api';

export const authService = {
  async login(email, password) {
    const { data } = await api.post('/auth/login', { email, password });
    localStorage.setItem('token', data.token);
    localStorage.setItem('userId', data.userId); // Backend returnează direct userId, nu data.user.id
    // Construiește obiectul user din datele primite
    return { 
      token: data.token, 
      user: {
        id: data.userId,
        email: data.email,
        username: data.username,
        role: data.role
      }
    };
  },

  logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('userId');
  },

  getCurrentUser() {
    const userId = localStorage.getItem('userId');
    return userId ? { id: userId } : null;
  },

  isAuthenticated() {
    return !!localStorage.getItem('token');
  },
};