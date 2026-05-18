// ═══════════════════════════════════════════════
//  HEADER INJECTION — include AFTER main.js
// ═══════════════════════════════════════════════
(function () {
  const usuario = getUsuarioLogado();
  const logado = !!usuario;
  const comercio = logado && usuario.tipoConta === 'COMERCIO';

  const LINKS = [
    { href: 'index.html',            label: 'Inicio',          page: 'index.html'            },
    { href: 'mercados.html',         label: 'Mercados',        page: 'mercados.html'         },
    { href: 'lista-produtos.html',   label: 'Produtos',        page: 'lista-produtos.html'   },
    { href: 'ranking.html',          label: 'Ranking',         page: 'ranking.html'          },
    { href: 'mapa.html',             label: 'Mapa',            page: 'mapa.html'             },
    { href: 'lista-compra.html',     label: 'Lista',           page: 'lista-compra.html'     },
  ];

  // Links extras por tipo de conta
  if (logado && comercio) {
    LINKS.push({ href: 'painel-comercio.html', label: 'Painel', page: 'painel-comercio.html' });
  }
  if (logado && !comercio) {
    LINKS.push({ href: 'dashboard.html', label: 'Meu Painel', page: 'dashboard.html' });
  }

  const navHTML = LINKS.map(l =>
    `<a href="${l.href}" class="nav-link" data-page="${l.page}">${l.label}</a>`
  ).join('');

  const mobileHTML = LINKS.map(l =>
    `<a href="${l.href}" class="nav-link" data-page="${l.page}">${l.label}</a>`
  ).join('');

  // Botão de auth
  let authBtn, authBtnMobile;
  if (logado) {
    const nome = usuario.nomeUsuario || usuario.login;
    const iniciais = nome.split(' ').map(n => n[0]).join('').toUpperCase().slice(0, 2);
    authBtn = `
      <div class="user-menu">
        <button class="user-avatar-btn" id="user-menu-btn" title="${nome}">
          <span class="user-avatar">${iniciais}</span>
          <span class="user-name">${nome}</span>
          <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polyline points="6 9 12 15 18 9"/></svg>
        </button>
        <div class="user-dropdown" id="user-dropdown">
          <div class="user-dropdown-info">
            <div class="user-dropdown-name">${nome}</div>
            <div class="user-dropdown-type">${comercio ? 'Conta Comercial' : 'Consumidor'}</div>
          </div>
          <div class="user-dropdown-divider"></div>
          <a href="${comercio ? 'painel-comercio.html' : 'dashboard.html'}" class="user-dropdown-item">Meu Painel</a>
          <button class="user-dropdown-item user-dropdown-logout" onclick="logout()">Sair</button>
        </div>
      </div>`;
    authBtnMobile = `
      <div class="mobile-user-info">
        <span style="color:var(--green);font-weight:600;font-size:13px;">${nome}</span>
        <span style="font-size:11px;color:var(--ink-faint);">${comercio ? 'Comercial' : 'Consumidor'}</span>
      </div>
      <button class="nav-link nav-link-cta" onclick="logout()">Sair</button>`;
  } else {
    authBtn = `<a href="login.html" class="nav-link nav-link-cta" data-page="login.html">Entrar</a>`;
    authBtnMobile = `<a href="login.html" class="nav-link nav-link-cta" data-page="login.html">Entrar</a>`;
  }

  const header = document.getElementById('main-header');
  if (!header) return;

  header.innerHTML = `
    <a href="index.html" class="header-brand">
      <span class="brand-mercado">Mercado</span><span class="brand-certo">Certo</span>
    </a>
    <nav class="header-nav">
      ${navHTML}
      ${authBtn}
    </nav>
    <button class="hamburger" id="hamburger" aria-label="Menu">
      <span></span><span></span><span></span>
    </button>
  `;

  // Mobile nav
  let mobileNav = document.getElementById('mobile-nav');
  if (!mobileNav) {
    mobileNav = document.createElement('nav');
    mobileNav.id = 'mobile-nav';
    mobileNav.className = 'mobile-nav';
    mobileNav.innerHTML = mobileHTML + authBtnMobile;
    header.insertAdjacentElement('afterend', mobileNav);
  }

  // Botao flutuante "Voltar ao Inicio" (em todas as paginas exceto index)
  const currentPage = location.pathname.split('/').pop() || 'index.html';
  if (currentPage !== 'index.html') {
    const homeBtn = document.createElement('a');
    homeBtn.href = 'index.html';
    homeBtn.className = 'btn-home-float';
    homeBtn.title = 'Voltar ao Inicio';
    homeBtn.innerHTML = `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 9l9-7 9 7v11a2 2 0 01-2 2H5a2 2 0 01-2-2z"/><polyline points="9 22 9 12 15 12 15 22"/></svg>`;
    document.body.appendChild(homeBtn);
  }

  // User dropdown toggle
  if (logado) {
    const menuBtn = document.getElementById('user-menu-btn');
    const dropdown = document.getElementById('user-dropdown');
    if (menuBtn && dropdown) {
      menuBtn.addEventListener('click', (e) => {
        e.stopPropagation();
        dropdown.classList.toggle('open');
      });
      document.addEventListener('click', () => {
        dropdown.classList.remove('open');
      });
    }
  }
})();
