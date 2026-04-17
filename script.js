// ── BOOT ANIMATION ──
let bootPct = 0;
const bootBar = document.getElementById('boot-bar-inner');
const bootScreen = document.getElementById('boot-screen');
const bootText = document.getElementById('boot-text');
const msgs = [
  'Загрузка Windows 95...',
  'Инициализация устройств...',
  'Загрузка профиля...',
  'Добро пожаловать, Essense!'
];
let msgIdx = 0;

const bootInterval = setInterval(() => {
  bootPct += Math.random() * 8 + 2;
  if (bootPct > 100) bootPct = 100;
  bootBar.style.width = bootPct + '%';
  if (bootPct > 30 && msgIdx < 1) { bootText.textContent = msgs[1]; msgIdx = 1; }
  if (bootPct > 60 && msgIdx < 2) { bootText.textContent = msgs[2]; msgIdx = 2; }
  if (bootPct > 85 && msgIdx < 3) { bootText.textContent = msgs[3]; msgIdx = 3; }
  if (bootPct >= 100) {
    clearInterval(bootInterval);
    setTimeout(() => {
      bootScreen.style.transition = 'opacity 0.4s';
      bootScreen.style.opacity = '0';
      setTimeout(() => {
        bootScreen.style.display = 'none';
        openWindow('win-about');
        setTimeout(() => openWindow('win-projects'), 300);
        setTimeout(() => openWindow('win-contacts'), 600);
        // Animate skill bars after windows open
        setTimeout(animateSkillBars, 1200);
      }, 400);
    }, 600);
  }
}, 60);

// ── CLOCK ──
function updateClock() {
  const now = new Date();
  const h = String(now.getHours()).padStart(2, '0');
  const m = String(now.getMinutes()).padStart(2, '0');
  document.getElementById('taskbar-clock').textContent = h + ':' + m;
}
updateClock();
setInterval(updateClock, 10000);

// ── WINDOW MANAGEMENT ──
const windows = {};
let zTop = 100;

function openWindow(id) {
  const el = document.getElementById(id);
  if (!el) return;

  el.style.display = 'block';
  el.classList.add('active');
  el.style.zIndex = ++zTop;

  // Trigger open animation
  el.classList.remove('win-opening');
  void el.offsetWidth; // reflow to restart animation
  el.classList.add('win-opening');
  setTimeout(() => el.classList.remove('win-opening'), 220);

  if (!windows[id]) {
    windows[id] = { minimized: false };
    const offset = Object.keys(windows).length * 20;
    if (!el.style.top || el.style.top === '0px') {
      el.style.top = (60 + offset) + 'px';
      el.style.left = (100 + offset) + 'px';
    }
  }
  windows[id].minimized = false;

  // Animate skill bars when skills window opens
  if (id === 'win-skills') {
    setTimeout(animateSkillBars, 100);
  }

  updateTaskbar();
}

function closeWindow(id) {
  const el = document.getElementById(id);
  if (el) {
    el.classList.remove('active');
    el.style.display = 'none';
  }
  delete windows[id];
  updateTaskbar();
}

function minimizeWindow(id) {
  const el = document.getElementById(id);
  if (el) el.style.display = 'none';
  if (windows[id]) windows[id].minimized = true;
  updateTaskbar();
}

function maximizeWindow(id) {
  const el = document.getElementById(id);
  if (!el) return;
  if (el.dataset.maximized === '1') {
    el.style.top = el.dataset.prevTop;
    el.style.left = el.dataset.prevLeft;
    el.style.width = el.dataset.prevWidth;
    el.style.height = '';
    el.dataset.maximized = '0';
  } else {
    el.dataset.prevTop = el.style.top;
    el.dataset.prevLeft = el.style.left;
    el.dataset.prevWidth = el.style.width || '';
    el.style.top = '0';
    el.style.left = '0';
    el.style.width = '100vw';
    el.style.height = 'calc(100vh - 28px)';
    el.dataset.maximized = '1';
  }
}

function updateTaskbar() {
  const bar = document.getElementById('taskbar-buttons');
  bar.innerHTML = '';
  const icons  = { 'win-about': '👤', 'win-projects': '📁', 'win-skills': '📊', 'win-contacts': '✉️' };
  const labels = { 'win-about': 'Обо мне', 'win-projects': 'Проекты', 'win-skills': 'Навыки', 'win-contacts': 'Контакты' };

  for (const id in windows) {
    const btn = document.createElement('div');
    btn.className = 'taskbar-btn';
    const el = document.getElementById(id);
    if (el && el.style.display !== 'none') btn.classList.add('pressed');
    btn.textContent = (icons[id] || '🗂') + ' ' + (labels[id] || id);
    btn.onclick = () => {
      const w = document.getElementById(id);
      if (!w) return;
      if (w.style.display === 'none') {
        w.style.display = 'block';
        w.style.zIndex = ++zTop;
        if (windows[id]) windows[id].minimized = false;
        if (id === 'win-skills') setTimeout(animateSkillBars, 100);
      } else if (parseInt(w.style.zIndex) < zTop) {
        w.style.zIndex = ++zTop;
      } else {
        minimizeWindow(id);
      }
      updateTaskbar();
    };
    bar.appendChild(btn);
  }
}

// ── SKILL BAR ANIMATION ──
function animateSkillBars() {
  const bars = document.querySelectorAll('.skill-bar-inner');
  bars.forEach(bar => {
    const target = bar.dataset.width || bar.style.width;
    bar.style.width = '0';
    setTimeout(() => { bar.style.width = target; }, 50);
  });
}

// ── DRAG ──
let dragging = null, dragOffX = 0, dragOffY = 0;

function startDrag(e, id) {
  dragging = id;
  const el = document.getElementById(id);
  const rect = el.getBoundingClientRect();
  dragOffX = e.clientX - rect.left;
  dragOffY = e.clientY - rect.top;
  el.style.zIndex = ++zTop;
  e.preventDefault();
}

document.addEventListener('mousemove', e => {
  if (!dragging) return;
  const el = document.getElementById(dragging);
  if (!el) return;
  let x = e.clientX - dragOffX;
  let y = e.clientY - dragOffY;
  x = Math.max(0, Math.min(x, window.innerWidth - el.offsetWidth));
  y = Math.max(0, Math.min(y, window.innerHeight - el.offsetHeight - 28));
  el.style.left = x + 'px';
  el.style.top  = y + 'px';
});

document.addEventListener('mouseup', () => dragging = null);

// ── ICON SELECT ──
function selectIcon(el) {
  document.querySelectorAll('.desktop-icon').forEach(i => i.classList.remove('selected'));
  el.classList.add('selected');
}

document.addEventListener('click', e => {
  if (
    !e.target.closest('.desktop-icon') &&
    !e.target.closest('#start-menu') &&
    !e.target.closest('#start-btn')
  ) {
    document.querySelectorAll('.desktop-icon').forEach(i => i.classList.remove('selected'));
    closeStartMenu();
  }
});

// ── START MENU ──
function toggleStartMenu() {
  document.getElementById('start-menu').classList.toggle('open');
}
function closeStartMenu() {
  document.getElementById('start-menu').classList.remove('open');
}

// ── GITHUB ──
function goGithub() {
  window.open('https://github.com/Anonymous1artem', '_blank');
}

// ── RECYCLE BIN ──
function openRecycleBin() {
  alert('Корзина пуста.\n\nВсе баги были удалены.');
}

// ── BSOD ──
function showBSOD() {
  document.getElementById('bsod').style.display = 'block';
}

// ═══════════════════════════════════════
// DROPDOWN MENUS
// ═══════════════════════════════════════

let activeMenu = null;

function toggleMenu(menuId, e) {
  e.stopPropagation();
  const menu = document.getElementById(menuId);
  if (!menu) return;

  if (activeMenu && activeMenu !== menu) {
    activeMenu.classList.remove('open');
    document.querySelectorAll('.win-menu-item').forEach(i => i.classList.remove('menu-active'));
  }

  const isOpen = menu.classList.contains('open');
  closeAllMenus();
  if (!isOpen) {
    menu.classList.add('open');
    activeMenu = menu;
    // highlight parent item
    const wrap = menu.closest('.win-menu-wrap');
    if (wrap) wrap.querySelector('.win-menu-item').classList.add('menu-active');
  }
}

function closeAllMenus() {
  document.querySelectorAll('.win-dropdown').forEach(d => d.classList.remove('open'));
  document.querySelectorAll('.win-menu-item').forEach(i => i.classList.remove('menu-active'));
  activeMenu = null;
}

// close menus on outside click
document.addEventListener('click', (e) => {
  if (!e.target.closest('.win-menu-wrap')) closeAllMenus();
});
document.addEventListener('keydown', (e) => {
  if (e.key === 'Escape') closeAllMenus();
});

// ═══════════════════════════════════════
// THEME SWITCHER
// ═══════════════════════════════════════

const themes = ['light', 'dark', 'teal', 'amber', 'matrix'];
let currentTheme = 'light';

function setTheme(name) {
  // Remove all theme classes
  themes.forEach(t => document.body.classList.remove('theme-' + t));
  if (name !== 'light') document.body.classList.add('theme-' + name);
  currentTheme = name;

  // Update checkmarks
  themes.forEach(t => {
    const el = document.getElementById('theme-' + t);
    if (el) el.querySelector('.dd-check').textContent = (t === name) ? '✓' : ' ';
    if (el) el.classList.toggle('dd-checked', t === name);
  });

  closeAllMenus();

  // Show toast
  const names = { light: 'Светлая', dark: 'Тёмная', teal: 'Бирюзовая', amber: 'Янтарная', matrix: 'Matrix' };
  showToast('🎨 Тема: ' + names[name]);
}

// ═══════════════════════════════════════
// COMPACT MODE
// ═══════════════════════════════════════

let compactMode = false;
function toggleCompactMode() {
  compactMode = !compactMode;
  document.body.classList.toggle('compact-mode', compactMode);
  document.getElementById('compact-check').textContent = compactMode ? '✓' : ' ';
  closeAllMenus();
  showToast(compactMode ? '📐 Компактный вид включён' : '📐 Компактный вид выключен');
}

// ═══════════════════════════════════════
// COPY HELPERS
// ═══════════════════════════════════════

function copyContacts() {
  const text = 'GitHub: https://github.com/Anonymous1artem\nTelegram: @HiGo69\nEmail: artem.brok007@gmail.com';
  navigator.clipboard.writeText(text).then(() => showToast('📋 Контакты скопированы!')).catch(() => {});
  closeAllMenus();
}
function copyGithub() {
  navigator.clipboard.writeText('https://github.com/Anonymous1artem').then(() => showToast('📋 GitHub скопирован!')).catch(() => {});
  closeAllMenus();
}

// ═══════════════════════════════════════
// PRINT
// ═══════════════════════════════════════
function printPage() {
  closeAllMenus();
  window.print();
}

// ═══════════════════════════════════════
// ABOUT DIALOG
// ═══════════════════════════════════════
function showAboutDlg() {
  closeAllMenus();
  showToast('💾 Essense Portfolio v1.0\nWindows 95 Edition\n© 2025 Anonymous1artem', 3000);
}

// ═══════════════════════════════════════
// PROJECT SORTING
// ═══════════════════════════════════════
const sortChecks = ['sort-stars', 'sort-name', 'sort-lang'];

function sortProjects(by) {
  sortChecks.forEach(id => {
    const el = document.getElementById(id);
    if (!el) return;
    el.querySelector('.dd-check').textContent = ' ';
    el.classList.remove('dd-checked');
  });
  const active = document.getElementById('sort-' + by);
  if (active) { active.querySelector('.dd-check').textContent = '✓'; active.classList.add('dd-checked'); }

  const list = document.querySelector('.project-list');
  if (!list) { closeAllMenus(); return; }
  const items = Array.from(list.querySelectorAll('.project-item'));

  items.sort((a, b) => {
    if (by === 'stars') {
      const sa = parseInt(a.querySelector('.proj-stars').textContent.replace(/\D/g,'')) || 0;
      const sb = parseInt(b.querySelector('.proj-stars').textContent.replace(/\D/g,'')) || 0;
      return sb - sa;
    }
    if (by === 'name') {
      return a.querySelector('.proj-name').textContent.localeCompare(b.querySelector('.proj-name').textContent);
    }
    if (by === 'lang') {
      return a.querySelector('.proj-lang').textContent.localeCompare(b.querySelector('.proj-lang').textContent);
    }
    return 0;
  });
  items.forEach(i => list.appendChild(i));
  closeAllMenus();
  showToast('🔃 Отсортировано');
}

// ═══════════════════════════════════════
// PROJECT VIEW MODE
// ═══════════════════════════════════════
function setProjView(mode) {
  ['proj-view-list','proj-view-compact'].forEach(id => {
    const el = document.getElementById(id);
    if (el) { el.querySelector('.dd-check').textContent = ' '; el.classList.remove('dd-checked'); }
  });
  const active = document.getElementById('proj-view-' + mode);
  if (active) { active.querySelector('.dd-check').textContent = '✓'; active.classList.add('dd-checked'); }

  const list = document.querySelector('.project-list');
  if (list) {
    list.classList.toggle('proj-compact', mode === 'compact');
  }
  closeAllMenus();
}

// ═══════════════════════════════════════
// TOAST NOTIFICATIONS
// ═══════════════════════════════════════
let toastTimer = null;
function showToast(msg, duration = 2000) {
  let toast = document.getElementById('win95-toast');
  if (!toast) {
    toast = document.createElement('div');
    toast.id = 'win95-toast';
    toast.style.cssText = `
      position: fixed; bottom: 36px; right: 12px;
      background: var(--win-gray);
      border-top: 2px solid var(--win-white);
      border-left: 2px solid var(--win-white);
      border-right: 2px solid var(--win-darker);
      border-bottom: 2px solid var(--win-darker);
      padding: 6px 12px; font-size: 11px;
      font-family: 'MS Sans Serif', Tahoma, sans-serif;
      z-index: 9998; max-width: 240px; white-space: pre-line;
      animation: toast-in 150ms ease;
    `;
    document.body.appendChild(toast);
  }
  toast.textContent = msg;
  toast.style.display = 'block';
  toast.style.opacity = '1';
  clearTimeout(toastTimer);
  toastTimer = setTimeout(() => {
    toast.style.transition = 'opacity 300ms';
    toast.style.opacity = '0';
    setTimeout(() => { toast.style.display = 'none'; toast.style.transition = ''; }, 300);
  }, duration);
}
