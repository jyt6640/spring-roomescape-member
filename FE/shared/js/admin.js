import { api } from './api.js';

const themeList = document.getElementById('theme-list');
const timeList = document.getElementById('time-list');
const reservationList = document.getElementById('reservation-list');

const loadThemes = async () => {
  themeList.innerHTML = '<p class="empty-msg">불러오는 중...</p>';
  try {
    const themes = await api.getThemes();
    if (themes.length === 0) {
      themeList.innerHTML = '<p class="empty-msg">등록된 테마가 없습니다.</p>';
      return;
    }
    themeList.innerHTML = themes.map(theme => `
      <div class="item">
        <div class="item-info">
          <span class="item-name">${theme.name}</span>
          <span class="item-meta">${theme.description}</span>
        </div>
        <button class="btn-delete" data-id="${theme.id}" data-type="theme">삭제</button>
      </div>
    `).join('');
  } catch (e) {
    themeList.innerHTML = '<p class="empty-msg error-msg">불러오기 실패</p>';
  }
};

const loadTimes = async () => {
  timeList.innerHTML = '<p class="empty-msg">불러오는 중...</p>';
  try {
    const times = await api.getTimes();
    if (times.length === 0) {
      timeList.innerHTML = '<p class="empty-msg">등록된 시간이 없습니다.</p>';
      return;
    }
    timeList.innerHTML = times.map(time => `
      <div class="item">
        <div class="item-info">
          <span class="item-name">${time.startAt}</span>
        </div>
        <button class="btn-delete" data-id="${time.id}" data-type="time">삭제</button>
      </div>
    `).join('');
  } catch (e) {
    timeList.innerHTML = '<p class="empty-msg error-msg">불러오기 실패</p>';
  }
};

const loadReservations = async () => {
  reservationList.innerHTML = '<p class="empty-msg">불러오는 중...</p>';
  try {
    const [reservations, themes, times] = await Promise.all([
      api.getReservations(),
      api.getThemes(),
      api.getTimes()
    ]);
    if (reservations.length === 0) {
      reservationList.innerHTML = '<p class="empty-msg">예약 내역이 없습니다.</p>';
      return;
    }
    const themeMap = new Map(themes.map(t => [t.id, t.name]));
    const timeMap = new Map(times.map(t => [t.id, t.startAt]));
    reservationList.innerHTML = reservations.map(r => `
      <div class="item">
        <div class="item-info">
          <span class="item-name">${r.name}</span>
          <span class="item-meta">${r.date} · ${timeMap.get(r.timeId) ?? r.timeId} · ${themeMap.get(r.themeId) ?? r.themeId}</span>
        </div>
        <button class="btn-delete" data-id="${r.id}" data-type="reservation">삭제</button>
      </div>
    `).join('');
  } catch (e) {
    reservationList.innerHTML = '<p class="empty-msg error-msg">불러오기 실패</p>';
  }
};

document.getElementById('add-theme-btn').addEventListener('click', async () => {
  const name = document.getElementById('theme-name').value.trim();
  const description = document.getElementById('theme-description').value.trim();
  const thumbnail = document.getElementById('theme-thumbnail').value.trim();

  if (!name || !description) {
    alert('테마 이름과 설명을 입력해 주세요.');
    return;
  }

  const finalThumbnail = thumbnail || 'https://images.unsplash.com/photo-1520637836862-4d197d17c38a?auto=format&fit=crop&w=800&q=80';
  try {
    await api.createTheme({ name, description, thumbnail: finalThumbnail });
    document.getElementById('theme-name').value = '';
    document.getElementById('theme-description').value = '';
    document.getElementById('theme-thumbnail').value = '';
    loadThemes();
  } catch (e) {
    alert('테마 추가 실패: ' + e.message);
  }
});

document.getElementById('add-time-btn').addEventListener('click', async () => {
  const startAt = document.getElementById('time-start').value;
  if (!startAt) {
    alert('시간을 선택해 주세요.');
    return;
  }
  try {
    await api.createTime(startAt);
    document.getElementById('time-start').value = '';
    loadTimes();
  } catch (e) {
    alert('시간 추가 실패: ' + e.message);
  }
});

document.addEventListener('click', async (e) => {
  if (!e.target.classList.contains('btn-delete')) return;

  const id = e.target.dataset.id;
  const type = e.target.dataset.type;
  const label = type === 'theme' ? '테마' : type === 'time' ? '시간' : '예약';

  if (!confirm(`이 ${label}을(를) 삭제하시겠습니까?`)) return;

  try {
    if (type === 'theme') {
      await api.deleteTheme(id);
      loadThemes();
    } else if (type === 'time') {
      await api.deleteTime(id);
      loadTimes();
    } else if (type === 'reservation') {
      await api.deleteReservation(id);
      loadReservations();
    }
  } catch (e) {
    alert('삭제 실패: ' + e.message);
  }
});

loadThemes();
loadTimes();
loadReservations();
