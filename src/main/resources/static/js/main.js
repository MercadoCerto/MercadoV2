// ═══════════════════════════════════════════════
//  SHARED UTILITIES
// ═══════════════════════════════════════════════

const API = 'http://localhost:8080';

// ── Tipo de medida / preço por unidade ─────────
// Infere o tipoMedida quando o produto vier sem ele (compatibilidade com
// registros antigos). Retorna 'PESO', 'VOLUME', 'UNIDADE' ou null.
function inferirTipoMedida(p) {
  if (p && p.tipoMedida) return p.tipoMedida;
  if (!p) return null;
  if (p.unidade === 'kg' || p.unidade === 'g')  return 'PESO';
  if (p.unidade === 'L'  || p.unidade === 'ml') return 'VOLUME';
  if (p.unidade === 'un' || p.quantidade)       return 'UNIDADE';
  return null;
}

// Retorna { valor, unidadeBase } com o preço normalizado em R$/kg, R$/L
// ou R$/un — ou null se não houver dados suficientes.
function precoUnitBase(p) {
  if (!p || p.preco == null) return null;
  const tipo = inferirTipoMedida(p);
  const fatorParaBase = u => (u === 'g' || u === 'ml') ? 1000 : 1;
  const unidadeBaseDe = u => (u === 'kg' || u === 'g') ? 'kg'
                          : (u === 'L'  || u === 'ml') ? 'L'
                          : null;

  switch (tipo) {
    case 'PESO':
    case 'VOLUME': {
      if (!p.peso || p.peso <= 0) return null;
      const ub = unidadeBaseDe(p.unidade);
      if (!ub) return null;
      return { valor: p.preco / (p.peso / fatorParaBase(p.unidade)), unidadeBase: ub };
    }
    case 'UNIDADE': {
      const q = p.quantidade || 1;
      if (q <= 0) return null;
      return { valor: p.preco / q, unidadeBase: 'un' };
    }
    case 'PACK': {
      if (!p.peso || p.peso <= 0 || !p.quantidade || p.quantidade <= 0) return null;
      const ub = unidadeBaseDe(p.unidade);
      if (!ub) return null;
      const totalBase = (p.peso * p.quantidade) / fatorParaBase(p.unidade);
      return { valor: p.preco / totalBase, unidadeBase: ub };
    }
    default:
      return null;
  }
}

// String amigável da embalagem (ex: "5kg", "1.5L", "12 un", "6 × 500ml").
function formatarEmbalagem(p) {
  if (!p) return '';
  const tipo = inferirTipoMedida(p);
  const fmtNum = n => Number.isInteger(n) ? String(n) : String(n).replace('.', ',');
  switch (tipo) {
    case 'PESO':
    case 'VOLUME':
      return p.peso ? `${fmtNum(p.peso)}${p.unidade || ''}` : '';
    case 'UNIDADE':
      return p.quantidade ? `${p.quantidade} un` : '';
    case 'PACK':
      return (p.quantidade && p.peso)
        ? `${p.quantidade} × ${fmtNum(p.peso)}${p.unidade || ''}`
        : '';
    default:
      return p.peso ? `${fmtNum(p.peso)}${p.unidade || ''}` : '';
  }
}

// ── Auth helpers ──────────────────────────────
function getUsuarioLogado() {
  try {
    const raw = localStorage.getItem('mc_usuario');
    return raw ? JSON.parse(raw) : null;
  } catch { return null; }
}

function setUsuarioLogado(usuario) {
  localStorage.setItem('mc_usuario', JSON.stringify(usuario));
}

function logout() {
  localStorage.removeItem('mc_usuario');
  window.location = 'index.html';
}

function isComercio() {
  const u = getUsuarioLogado();
  return u && u.tipoConta === 'COMERCIO';
}

function isAdmin() {
  const u = getUsuarioLogado();
  return u && u.tipoConta === 'ADMIN';
}

function requireLogin(redirect) {
  if (!getUsuarioLogado()) {
    window.location = redirect || 'login.html';
    return false;
  }
  return true;
}

function requireComercio() {
  const u = getUsuarioLogado();
  if (!u) {
    window.location = 'login.html';
    return false;
  }
  if (u.tipoConta !== 'COMERCIO' && u.tipoConta !== 'ADMIN') {
    window.location = 'index.html';
    return false;
  }
  return true;
}

// ── Toast ──────────────────────────────────────
function toast(msg, type = 'info') {
  let container = document.getElementById('toast-container');
  if (!container) {
    container = document.createElement('div');
    container.id = 'toast-container';
    document.body.appendChild(container);
  }

  const icons = {
    success: `<svg class="toast-icon" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polyline points="20 6 9 17 4 12"/></svg>`,
    error:   `<svg class="toast-icon" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>`,
    info:    `<svg class="toast-icon" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>`,
  };

  const el = document.createElement('div');
  el.className = `toast toast-${type}`;
  el.innerHTML = `${icons[type] || icons.info}<span>${msg}</span>`;
  container.appendChild(el);

  setTimeout(() => {
    el.classList.add('toast-out');
    el.addEventListener('animationend', () => el.remove());
  }, 3000);
}

// ── Validation ─────────────────────────────────
function isValidEmail(email) {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
}

function isValidCnpj(cnpj) {
  cnpj = cnpj.replace(/[^\d]/g, '');
  if (cnpj.length !== 14) return false;
  if (/^(\d)\1{13}$/.test(cnpj)) return false;

  const pesos1 = [5,4,3,2,9,8,7,6,5,4,3,2];
  const pesos2 = [6,5,4,3,2,9,8,7,6,5,4,3,2];

  let soma = 0;
  for (let i = 0; i < 12; i++) soma += parseInt(cnpj[i]) * pesos1[i];
  let dig1 = soma % 11 < 2 ? 0 : 11 - (soma % 11);
  if (parseInt(cnpj[12]) !== dig1) return false;

  soma = 0;
  for (let i = 0; i < 13; i++) soma += parseInt(cnpj[i]) * pesos2[i];
  let dig2 = soma % 11 < 2 ? 0 : 11 - (soma % 11);
  return parseInt(cnpj[13]) === dig2;
}

function formatCnpj(value) {
  const digits = value.replace(/\D/g, '').slice(0, 14);
  return digits
    .replace(/^(\d{2})(\d)/, '$1.$2')
    .replace(/^(\d{2})\.(\d{3})(\d)/, '$1.$2.$3')
    .replace(/\.(\d{3})(\d)/, '.$1/$2')
    .replace(/(\d{4})(\d)/, '$1-$2');
}

// ── Spinner ────────────────────────────────────
function setLoading(btn, loading) {
  if (!btn) return;
  btn.disabled = loading;
  if (loading) {
    btn._origText = btn.innerHTML;
    btn.innerHTML = `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" style="animation:spin .7s linear infinite"><path d="M12 2v4M12 18v4M4.93 4.93l2.83 2.83M16.24 16.24l2.83 2.83M2 12h4M18 12h4M4.93 19.07l2.83-2.83M16.24 7.76l2.83-2.83"/></svg>`;
  } else {
    btn.innerHTML = btn._origText || 'Enviar';
  }
}

// ── Geocodificação (Nominatim / OpenStreetMap) ─
async function geocodificarEndereco(endereco, cidade) {
  const query = encodeURIComponent(`${endereco}, ${cidade}, Brasil`);
  const res = await fetch(
    `https://nominatim.openstreetmap.org/search?q=${query}&format=json&limit=1&countrycodes=br`,
    { headers: { 'Accept-Language': 'pt-BR,pt;q=0.9' } }
  );
  if (!res.ok) return null;
  const data = await res.json();
  if (!data.length) return null;
  return { latitude: parseFloat(data[0].lat), longitude: parseFloat(data[0].lon) };
}

// ── Stars display ──────────────────────────────
function starsDisplay(avg) {
  const filled = Math.round(avg);
  return '★'.repeat(filled) + '☆'.repeat(5 - filled);
}

// ── Nav highlight ──────────────────────────────
function highlightNav() {
  const path = location.pathname.split('/').pop() || 'index.html';
  document.querySelectorAll('.nav-link[data-page]').forEach(a => {
    a.classList.toggle('active', a.dataset.page === path);
  });
}

// ── Hamburger menu ─────────────────────────────
document.addEventListener('DOMContentLoaded', () => {
  highlightNav();

  const hamburger = document.getElementById('hamburger');
  const mobileNav = document.getElementById('mobile-nav');
  if (hamburger && mobileNav) {
    hamburger.addEventListener('click', () => {
      mobileNav.classList.toggle('open');
    });
    document.addEventListener('click', e => {
      if (!hamburger.contains(e.target) && !mobileNav.contains(e.target)) {
        mobileNav.classList.remove('open');
      }
    });
  }
});
