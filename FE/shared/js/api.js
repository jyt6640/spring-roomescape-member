const BASE_URL = window.API_BASE_URL || '';

const handleResponse = async (response) => {
  if (!response.ok) {
    const contentType = response.headers.get('content-type') || '';
    if (contentType.includes('application/json')) {
      const error = await response.json().catch(() => ({ message: 'An error occurred' }));
      throw new Error(error.message || 'Network response was not ok');
    }
    const message = await response.text().catch(() => '');
    throw new Error(message || 'Network response was not ok');
  }
  if (response.status === 204) return null;
  return response.json();
};

export const api = {
  // Reservations
  async getReservations() {
    const response = await fetch(`${BASE_URL}/reservations`);
    return handleResponse(response);
  },

  async getReservableTimes(date, themeId) {
    const [available, times] = await Promise.all([
      fetch(`${BASE_URL}/reservations?date=${date}&themeId=${themeId}`).then(handleResponse),
      this.getTimes()
    ]);
    const timeMap = new Map(times.map(t => [t.id, t.startAt]));
    return available.map(slot => ({
      timeId: slot.timeId,
      startAt: timeMap.get(slot.timeId) || '',
      available: slot.available
    }));
  },

  async createReservation(data) {
    const response = await fetch(`${BASE_URL}/reservations`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        name: data.name,
        date: data.date,
        timeId: Number(data.timeId),
        themeId: Number(data.themeId)
      })
    });
    return handleResponse(response);
  },

  async deleteReservation(id) {
    const response = await fetch(`${BASE_URL}/reservations/${id}`, {
      method: 'DELETE'
    });
    return handleResponse(response);
  },

  // Times
  async getTimes() {
    const response = await fetch(`${BASE_URL}/times`);
    return handleResponse(response);
  },

  async createTime(startAt) {
    const response = await fetch(`${BASE_URL}/times`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ startAt })
    });
    return handleResponse(response);
  },

  async deleteTime(id) {
    const response = await fetch(`${BASE_URL}/times/${id}`, {
      method: 'DELETE'
    });
    return handleResponse(response);
  },

  // Themes
  async getThemes() {
    const response = await fetch(`${BASE_URL}/themes`);
    return handleResponse(response);
  },

  async getPopularThemes() {
    const fmtLocal = d =>
      `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`;
    const today = new Date();
    const to = new Date(today);
    to.setDate(to.getDate() - 1);
    const from = new Date(today);
    from.setDate(from.getDate() - 7);
    const response = await fetch(
      `${BASE_URL}/themes?sortBy=popular&from=${fmtLocal(from)}&to=${fmtLocal(to)}&limit=10`
    );
    return handleResponse(response);
  },

  async createTheme(data) {
    const response = await fetch(`${BASE_URL}/themes`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data)
    });
    return handleResponse(response);
  },

  async deleteTheme(id) {
    const response = await fetch(`${BASE_URL}/themes/${id}`, {
      method: 'DELETE'
    });
    return handleResponse(response);
  }
};
