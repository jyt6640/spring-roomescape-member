import { api } from './api.js';

const themeGrid = document.getElementById('theme-grid');
const popularList = document.getElementById('popular-list');
const bookingOverlay = document.getElementById('booking-overlay');
const closeOverlayBtn = document.getElementById('close-overlay');
const dateInput = document.getElementById('reservation-date');
const timeSlotsContainer = document.getElementById('time-slots');
const nextBtn = document.getElementById('next-to-step-2');
const reserveBtn = document.getElementById('reserve-btn');
const finishBtn = document.getElementById('finish-btn');

let selectedTime = null;
let selectedTheme = null;

const goToStep = (stepNumber) => {
  document.querySelectorAll('.step-pane').forEach(p => p.classList.remove('active'));
  document.querySelectorAll('.step').forEach(s => s.classList.remove('active'));
  document.getElementById(`step-${stepNumber}-content`).classList.add('active');
  document.querySelector(`.step[data-step="${stepNumber}"]`).classList.add('active');
};

const openBooking = (theme) => {
  selectedTime = null;
  selectedTheme = theme;
  nextBtn.disabled = true;
  reserveBtn.disabled = false;
  reserveBtn.textContent = '예약하기';
  document.getElementById('selected-theme-name').textContent = theme.name;
  document.getElementById('selected-theme-desc').textContent = theme.description;

  const today = new Date().toISOString().split('T')[0];
  dateInput.value = today;
  dateInput.min = today;

  bookingOverlay.style.display = 'flex';
  document.body.style.overflow = 'hidden';

  updateTimes();
  goToStep(1);
};

const closeBooking = () => {
  bookingOverlay.style.display = 'none';
  document.body.style.overflow = 'auto';
  selectedTime = null;
  selectedTheme = null;
  goToStep(1);
};

const loadThemes = async () => {
  themeGrid.innerHTML = '<p class="grid-msg">테마를 불러오는 중...</p>';
  try {
    const themes = await api.getThemes();
    if (themes.length === 0) {
      themeGrid.innerHTML = '<p class="grid-msg">등록된 테마가 없습니다.<br>관리자 페이지에서 테마를 추가해 주세요.</p>';
      return;
    }
    themeGrid.innerHTML = themes.map(theme => `
      <div class="theme-card" data-id="${theme.id}" data-name="${theme.name}" data-desc="${theme.description}">
        <div class="theme-image" style="${theme.thumbnail
          ? `background-image: url('${theme.thumbnail}'); background-size: cover; background-position: center;`
          : 'background: linear-gradient(135deg, #1a1a2e, #16213e, #0f3460);'}">
          <div class="theme-badge">예약하기</div>
        </div>
        <div class="theme-content">
          <h3 class="theme-title">${theme.name}</h3>
          <p class="theme-description">${theme.description}</p>
        </div>
      </div>
    `).join('');
  } catch (e) {
    themeGrid.innerHTML = '<p class="grid-msg" style="color:#ff3b30;">테마를 불러오지 못했습니다.</p>';
  }
};

const loadPopularThemes = async () => {
  if (!popularList) return;
  try {
    const popular = await api.getPopularThemes();
    if (popular.length === 0) {
      popularList.innerHTML = '<p class="empty-popular">아직 집계된 데이터가 없습니다.</p>';
      return;
    }
    popularList.innerHTML = popular.map((theme, i) => `
      <div class="popular-item">
        <span class="popular-rank ${i < 3 ? 'top3' : ''}">${i + 1}</span>
        <span class="popular-name">${theme.name}</span>
        ${i < 3 ? '<span class="popular-hot">🔥</span>' : ''}
      </div>
    `).join('');
  } catch (e) {
    popularList.innerHTML = '<p class="empty-popular">랭킹 데이터를 불러오지 못했습니다.</p>';
  }
};

const updateTimes = async () => {
  if (!dateInput.value || !selectedTheme) return;

  nextBtn.disabled = true;
  timeSlotsContainer.innerHTML = '<p class="slot-msg">예약 가능한 시간을 확인하는 중...</p>';

  try {
    const times = await api.getReservableTimes(dateInput.value, selectedTheme.id);
    if (times.length === 0) {
      timeSlotsContainer.innerHTML = '<p class="slot-msg">이 날짜에 예약 가능한 시간이 없습니다.</p>';
      return;
    }
    timeSlotsContainer.innerHTML = times.map(time => `
      <div class="time-slot ${time.available ? '' : 'disabled'}"
           data-id="${time.timeId}"
           data-available="${time.available}">
        ${time.startAt}
        ${!time.available ? '<span class="slot-full">마감</span>' : ''}
      </div>
    `).join('');
  } catch (error) {
    timeSlotsContainer.innerHTML = '<p class="slot-msg" style="color:#ff3b30;">시간 정보를 불러오지 못했습니다.</p>';
  }
};

themeGrid.addEventListener('click', (e) => {
  const card = e.target.closest('.theme-card');
  if (!card) return;
  openBooking({
    id: Number(card.dataset.id),
    name: card.dataset.name,
    description: card.dataset.desc
  });
});

dateInput.addEventListener('change', updateTimes);

timeSlotsContainer.addEventListener('click', (e) => {
  const slot = e.target.closest('.time-slot');
  if (!slot || slot.classList.contains('disabled')) return;

  document.querySelectorAll('.time-slot').forEach(s => s.classList.remove('selected'));
  slot.classList.add('selected');

  selectedTime = {
    id: slot.dataset.id,
    startAt: slot.textContent.trim().replace('마감', '').trim()
  };

  nextBtn.disabled = false;
});

nextBtn.addEventListener('click', () => goToStep(2));
document.getElementById('back-to-step-1').addEventListener('click', () => goToStep(1));

reserveBtn.addEventListener('click', async () => {
  const name = document.getElementById('reservation-name').value.trim();
  if (!name) {
    alert('이름을 입력해 주세요.');
    return;
  }

  reserveBtn.disabled = true;
  reserveBtn.textContent = '처리 중...';

  try {
    await api.createReservation({
      name,
      date: dateInput.value,
      timeId: selectedTime.id,
      themeId: selectedTheme.id
    });

    document.getElementById('reservation-summary').innerHTML = `
      <div class="summary-row"><span class="summary-label">테마</span><span>${selectedTheme.name}</span></div>
      <div class="summary-row"><span class="summary-label">날짜</span><span>${dateInput.value}</span></div>
      <div class="summary-row"><span class="summary-label">시간</span><span>${selectedTime.startAt}</span></div>
      <div class="summary-row"><span class="summary-label">예약자</span><span>${name}</span></div>
    `;

    goToStep(3);
  } catch (error) {
    alert(error.message || '예약에 실패했습니다. 다시 시도해 주세요.');
    reserveBtn.disabled = false;
    reserveBtn.textContent = '예약하기';
  }
});

finishBtn.addEventListener('click', closeBooking);
closeOverlayBtn.addEventListener('click', () => {
  if (confirm('예약을 취소하시겠습니까? 입력한 정보가 사라집니다.')) closeBooking();
});

loadThemes();
loadPopularThemes();
