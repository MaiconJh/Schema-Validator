(function () {
  const html = document.documentElement;
  const sidebar = document.getElementById('sidebar');
  const sidebarToggle = document.getElementById('sidebar-toggle');
  const themeToggle = document.getElementById('theme-toggle');
  const toc = document.getElementById('toc');
  const body = document.getElementById('article-body');
  const searchInput = document.getElementById('search-input');

  function slugify(text) {
    return text.toLowerCase().trim().replace(/[^a-z0-9\s-]/g, '').replace(/\s+/g, '-');
  }

  if (themeToggle) {
    const saved = localStorage.getItem('docs-theme');
    if (saved) html.setAttribute('data-theme', saved);
    themeToggle.addEventListener('click', () => {
      const current = html.getAttribute('data-theme') === 'dark' ? 'light' : 'dark';
      html.setAttribute('data-theme', current);
      localStorage.setItem('docs-theme', current);
    });
  }

  if (sidebarToggle && sidebar) {
    sidebarToggle.addEventListener('click', () => {
      const open = sidebar.classList.toggle('open');
      sidebarToggle.setAttribute('aria-expanded', String(open));
    });
  }

  if (body && toc) {
    const headings = [...body.querySelectorAll('h2, h3')];
    const ul = document.createElement('ul');
    const items = [];
    headings.forEach((h) => {
      if (!h.id) h.id = slugify(h.textContent);
      const anchor = document.createElement('a');
      anchor.href = `#${h.id}`;
      anchor.textContent = '¶';
      anchor.className = 'heading-anchor';
      anchor.setAttribute('aria-label', `Permanent link to ${h.textContent}`);
      h.append(anchor);

      const li = document.createElement('li');
      if (h.tagName === 'H3') li.style.marginLeft = '0.75rem';
      const link = document.createElement('a');
      link.href = `#${h.id}`;
      link.textContent = h.textContent.replace('¶', '').trim();
      li.append(link);
      ul.append(li);
      items.push({ heading: h, link });
    });
    toc.replaceChildren(ul);

    const observer = new IntersectionObserver((entries) => {
      entries.forEach((entry) => {
        const item = items.find((x) => x.heading === entry.target);
        if (item) item.link.classList.toggle('active', entry.isIntersecting);
      });
    }, { rootMargin: '-20% 0px -70% 0px', threshold: 0.1 });

    items.forEach((item) => observer.observe(item.heading));
  }

  const navLinks = [...document.querySelectorAll('#page-index a')];
  navLinks.forEach((a) => {
    if (a.getAttribute('href') === window.location.pathname || window.location.pathname.endsWith(a.getAttribute('href'))) {
      a.setAttribute('aria-current', 'page');
    }
  });

  if (searchInput) {
    const content = (document.querySelector('.ghd-markdown')?.innerText || '').toLowerCase();
    searchInput.addEventListener('input', (e) => {
      const q = e.target.value.trim().toLowerCase();
      navLinks.forEach((link) => {
        const visible = !q || link.textContent.toLowerCase().includes(q);
        link.parentElement.classList.toggle('search-hidden', !visible);
      });
      if (q && !content.includes(q)) {
        body?.classList.add('search-hidden');
      } else {
        body?.classList.remove('search-hidden');
      }
    });
  }
})();
