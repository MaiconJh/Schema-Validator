/**
 * Schema Validator Docs - Modern Interactive Features
 * Handles navigation, theme switching, search, and TOC
 */

(function () {
  'use strict';

  // DOM Elements
  const html = document.documentElement;
  const body = document.body;
  const sidebar = document.getElementById('sidebar');
  const sidebarToggle = document.getElementById('sidebar-toggle');
  const themeToggle = document.getElementById('theme-toggle');
  const toc = document.getElementById('toc');
  const articleBody = document.getElementById('article-body');
  const searchInput = document.getElementById('search-input');
  const searchResults = document.getElementById('search-results');
  const pageIndex = document.getElementById('page-index');

  // Utility: Create slug from text
  function slugify(text) {
    return text.toLowerCase().trim()
      .replace(/[^a-z0-9\s-]/g, '')
      .replace(/\s+/g, '-');
  }

  // Utility: Get SVG icon
  function getIcon(name, className = '') {
    const icons = {
      'home': `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="${className}"><path d="m3 9 9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/><polyline points="9 22 9 12 15 12 15 22"/></svg>`,
      'book': `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="${className}"><path d="M4 19.5v-15A2.5 2.5 0 0 1 6.5 2H20v20H6.5a2.5 2.5 0 0 1 0-5H20"/></svg>`,
      'rocket': `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="${className}"><path d="M4.5 16.5c-1.5 1.26-2 5-2 5s3.74-.5 5-2c.71-.84.7-2.13-.09-2.91a2.18 2.18 0 0 0-2.91-.09z"/><path d="m12 15-3-3a22 22 0 0 1 2-3.95A12.88 12.88 0 0 1 22 2c0 2.72-.78 7.5-6 11a22.35 22.35 0 0 1-4 2z"/><path d="M9 12H4s.55-3.03 2-4c1.62-1.08 5 0 5 0"/><path d="M12 15v5s3.03-.55 4-2c1.08-1.62 0-5 0-5"/></svg>`,
      'settings': `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="${className}"><path d="M12.22 2h-.44a2 2 0 0 0-2 2v.18a2 2 0 0 1-1 1.73l-.43.25a2 2 0 0 1-2 0l-.15-.08a2 2 0 0 0-2.73.73l-.22.38a2 2 0 0 0 .73 2.73l.15.1a2 2 0 0 1 1 1.72v.51a2 2 0 0 1-1 1.74l-.15.09a2 2 0 0 0-.73 2.73l.22.38a2 2 0 0 0 2.73.73l.15-.08a2 2 0 0 1 2 0l.43.25a2 2 0 0 1 1 1.73V20a2 2 0 0 0 2 2h.44a2 2 0 0 0 2-2v-.18a2 2 0 0 1 1-1.73l.43-.25a2 2 0 0 1 2 0l.15.08a2 2 0 0 0 2.73-.73l.22-.39a2 2 0 0 0-.73-2.73l-.15-.08a2 2 0 0 1-1-1.74v-.5a2 2 0 0 1 1-1.74l.15-.09a2 2 0 0 0 .73-2.73l-.22-.38a2 2 0 0 0-2.73-.73l-.15.08a2 2 0 0 1-2 0l-.43-.25a2 2 0 0 1-1-1.73V4a2 2 0 0 0-2-2z"/><circle cx="12" cy="12" r="3"/></svg>`,
      'code': `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="${className}"><polyline points="16 18 22 12 16 6"/><polyline points="8 6 2 12 8 18"/></svg>`,
      'list': `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="${className}"><line x1="8" x2="21" y1="6" y2="6"/><line x1="8" x2="21" y1="12" y2="12"/><line x1="8" x2="21" y1="18" y2="18"/><line x1="3" x2="3.01" y1="6" y2="6"/><line x1="3" x2="3.01" y1="12" y2="12"/><line x1="3" x2="3.01" y1="18" y2="18"/></svg>`,
      'layers': `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="${className}"><polygon points="12 2 2 7 12 12 22 7 12 2"/><polyline points="2 17 12 22 22 17"/><polyline points="2 12 12 17 22 12"/></svg>`,
      'check-circle': `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="${className}"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/></svg>`,
      'alert-triangle': `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="${className}"><path d="m21.73 18-8-14a2 2 0 0 0-3.48 0l-8 14A2 2 0 0 0 4 21h16a2 2 0 0 0 1.73-3Z"/><line x1="12" x2="12" y1="9" y2="13"/><line x1="12" x2="12.01" y1="17" y2="17"/></svg>`,
      'info': `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="${className}"><circle cx="12" cy="12" r="10"/><line x1="12" x2="12" y1="16" y2="12"/><line x1="12" x2="12.01" y1="8" y2="8"/></svg>`,
      'search': `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="${className}"><circle cx="11" cy="11" r="8"/><line x1="21" x2="16.65" y1="21" y2="16.65"/></svg>`,
      'sun': `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="${className}"><circle cx="12" cy="12" r="4"/><path d="M12 2v2"/><path d="M12 20v2"/><path d="m4.93 4.93 1.41 1.41"/><path d="m17.66 17.66 1.41 1.41"/><path d="M2 12h2"/><path d="M20 12h2"/><path d="m6.34 17.66-1.41 1.41"/><path d="m19.07 4.93-1.41 1.41"/></svg>`,
      'moon': `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="${className}"><path d="M12 3a6 6 0 0 0 9 9 9 9 0 1 1-9-9Z"/></svg>`,
      'menu': `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="${className}"><line x1="4" x2="20" y1="12" y2="12"/><line x1="4" x2="20" y1="6" y2="6"/><line x1="4" x2="20" y1="18" y2="18"/></svg>`,
      'chevron-right': `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="${className}"><polyline points="9 18 15 12 9 6"/></svg>`,
      'chevron-left': `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="${className}"><polyline points="15 18 9 12 15 6"/></svg>`,
      'arrow-right': `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="${className}"><line x1="5" x2="19" y1="12" y2="12"/><polyline points="12 5 19 12 12 19"/></svg>`,
      'link': `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="${className}"><path d="M10 13a5 5 0 0 0 7.54.54l3-3a5 5 0 0 0-7.07-7.07l-1.72 1.71"/><path d="M14 11a5 5 0 0 0-7.54-.54l-3 3a5 5 0 0 0 7.07 7.07l1.71-1.71"/></svg>`,
      'copy': `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="${className}"><rect width="14" height="14" x="8" y="8" rx="2" ry="2"/><path d="M4 16c-1.1 0-2-.9-2-2V4c0-1.1.9-2 2-2h10c1.1 0 2 .9 2 2"/></svg>`,
      'check': `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="${className}"><polyline points="20 6 9 17 4 12"/></svg>`,
      'file-text': `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="${className}"><path d="M14.5 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V7.5L14.5 2z"/><polyline points="14 2 14 8 20 8"/><line x1="16" x2="8" y1="13" y2="13"/><line x1="16" x2="8" y1="17" y2="17"/><polyline points="10 9 9 9 8 9"/></svg>`,
      'play': `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="${className}"><polygon points="5 3 19 12 5 21 5 3"/></svg>`,
      'zap': `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="${className}"><polygon points="13 2 3 14 12 14 11 22 21 10 12 10 13 2"/></svg>`,
      'tool': `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="${className}"><path d="M14.7 6.3a1 1 0 0 0 0 1.4l1.6 1.6a1 1 0 0 0 1.4 0l3.77-3.77a6 6 0 0 1-7.94 7.94l-6.91 6.91a2.12 2.12 0 0 1-3-3l6.91-6.91a6 6 0 0 1 7.94-7.94l-3.76 3.76z"/></svg>`,
      'cpu': `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="${className}"><rect x="4" y="4" width="16" height="16" rx="2" ry="2"/><rect x="9" y="9" width="6" height="6"/><line x1="9" x2="9" y1="1" y2="4"/><line x1="15" x2="15" y1="1" y2="4"/><line x1="9" x2="9" y1="20" y2="23"/><line x1="15" x2="15" y1="20" y2="23"/><line x1="20" x2="23" y1="9" y2="9"/><line x1="20" x2="23" y1="14" y2="14"/><line x1="1" x2="4" y1="9" y2="9"/><line x1="1" x2="4" y1="14" y2="14"/></svg>`,
      'bookmark': `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="${className}"><path d="m19 21-7-4-7 4V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2v16z"/></svg>`,
      'star': `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="${className}"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/></svg>`,
      'external-link': `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="${className}"><path d="M18 13v6a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h6"/><polyline points="15 3 21 3 21 9"/><line x1="10" x2="21" y1="14" y2="3"/></svg>`,
      'help-circle': `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="${className}"><circle cx="12" cy="12" r="10"/><path d="M9.09 9a3 3 0 0 1 5.83 1c0 2-3 3-3 3"/><line x1="12" x2="12.01" y1="17" y2="17"/></svg>`,
      'hash': `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="${className}"><line x1="4" x2="20" y1="9" y2="9"/><line x1="4" x2="20" y1="15" y2="15"/><line x1="10" x2="8" y1="3" y2="21"/><line x1="16" x2="14" y1="3" y2="21"/></svg>`,
      'grid': `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="${className}"><rect x="3" y="3" width="7" height="7"/><rect x="14" y="3" width="7" height="7"/><rect x="14" y="14" width="7" height="7"/><rect x="3" y="14" width="7" height="7"/></svg>`,
      'package': `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="${className}"><path d="m16.5 9.4-9-5.19"/><path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"/><polyline points="3.27 6.96 12 12.01 20.73 6.96"/><line x1="12" x2="12" y1="22.08" y2="12"/></svg>`,
      'shield': `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="${className}"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/></svg>`,
      'arrow-down': `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="${className}"><line x1="12" x2="12" y1="5" y2="19"/><polyline points="19 12 12 19 5 12"/></svg>`,
      'folder': `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="${className}"><path d="M20 20a2 2 0 0 0 2-2V8a2 2 0 0 0-2-2h-7.9a2 2 0 0 1-1.69-.9L9.6 3.9A2 2 0 0 0 7.93 3H4a2 2 0 0 0-2 2v13a2 2 0 0 0 2 2Z"/></svg>`,
      'archive': `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="${className}"><polyline points="21 8 21 21 3 21 3 8"/><rect x="1" y="3" width="22" height="5"/><line x1="10" x2="14" y1="12" y2="12"/></svg>`,
      'doc': `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="${className}"><path d="M14.5 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V7.5L14.5 2z"/></svg>`,
      'sparkles': `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="${className}"><path d="m12 3-1.912 5.813a2 2 0 0 1-1.275 1.275L3 12l5.813 1.912a2 2 0 0 1 1.275 1.275L12 21l1.912-5.813a2 2 0 0 1 1.275-1.275L21 12l-5.813-1.912a2 2 0 0 1-1.275-1.275L12 3Z"/><path d="M5 3v4"/><path d="M19 17v4"/><path d="M3 5h4"/><path d="M17 19h4"/></svg>`,
      'puzzle': `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="${className}"><path d="M19.439 7.85c-.049.322.059.648.289.878l1.568 1.568c.47.47.706 1.087.706 1.704s-.235 1.233-.706 1.704l-1.611 1.611a.98.98 0 0 1-.837.276c-.47-.07-.802-.48-.968-.925a2.501 2.501 0 1 0-3.214 3.214c.446.166.855.497.925.968a.979.979 0 0 1-.276.837l-1.61 1.61a2.404 2.404 0 0 1-1.705.707 2.402 2.402 0 0 1-1.704-.706l-1.568-1.568a1.026 1.026 0 0 0-.877-.29c-.493.074-.84.504-1.02.968a2.5 2.5 0 1 1-3.237-3.237c.464-.18.894-.527.967-1.02a1.026 1.026 0 0 0-.289-.877l-1.568-1.568A2.402 2.402 0 0 1 1.998 12c0-.617.236-1.234.706-1.704L4.315 8.69c.24-.24.581-.353.917-.303.515.077.877.528 1.073 1.01a2.5 2.5 0 1 0 3.259-3.259c-.482-.196-.933-.558-1.01-1.073-.05-.336.062-.676.303-.917l1.61-1.611A2.404 2.404 0 0 1 12.172 2c.617 0 1.234.236 1.704.706l1.568 1.568c.23.23.556.338.877.29.493-.074.84-.504 1.02-.968a2.5 2.5 0 1 1 3.237 3.237c-.464.18-.894.527-.967 1.02Z"/></svg>`,
      'workflow': `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="${className}"><rect x="2" y="2" width="8" height="8" rx="2"/><rect x="14" y="2" width="8" height="8" rx="2"/><rect x="14" y="14" width="8" height="8" rx="2"/><rect x="2" y="14" width="8" height="8" rx="2"/><path d="M10 6h4"/><path d="M6 10v4"/><path d="M14 10v4"/><path d="M10 14h4"/></svg>`
    };
    return icons[name] || '';
  }

  // --------------------------------------------------------------------------
  // Theme Toggle
  // --------------------------------------------------------------------------
  function initTheme() {
    const savedTheme = localStorage.getItem('docs-theme');
    const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
    
    if (savedTheme) {
      html.setAttribute('data-theme', savedTheme);
    } else if (prefersDark) {
      html.setAttribute('data-theme', 'dark');
    }
    
    // Add theme toggle icon
    if (themeToggle) {
      const icon = savedTheme === 'dark' || (!savedTheme && prefersDark) ? 'sun' : 'moon';
      themeToggle.innerHTML = getIcon(icon);
      themeToggle.setAttribute('aria-label', savedTheme === 'dark' ? 'Switch to light mode' : 'Switch to dark mode');
    }
  }

  function toggleTheme() {
    const currentTheme = html.getAttribute('data-theme');
    const newTheme = currentTheme === 'dark' ? 'light' : 'dark';
    
    html.setAttribute('data-theme', newTheme);
    localStorage.setItem('docs-theme', newTheme);
    
    // Update icon
    if (themeToggle) {
      const icon = newTheme === 'dark' ? 'sun' : 'moon';
      themeToggle.innerHTML = getIcon(icon);
      themeToggle.setAttribute('aria-label', newTheme === 'dark' ? 'Switch to light mode' : 'Switch to dark mode');
    }
  }

  // --------------------------------------------------------------------------
  // Sidebar Toggle (Mobile)
  // --------------------------------------------------------------------------
  function initSidebar() {
    if (sidebarToggle && sidebar) {
      // Add hamburger icon
      sidebarToggle.innerHTML = getIcon('menu');
      
      sidebarToggle.addEventListener('click', () => {
        const isOpen = sidebar.classList.toggle('open');
        sidebarToggle.setAttribute('aria-expanded', String(isOpen));
        sidebarToggle.innerHTML = getIcon(isOpen ? 'chevron-left' : 'menu');
      });
      
      // Close sidebar when clicking outside
      document.addEventListener('click', (e) => {
        if (sidebar.classList.contains('open') && 
            !sidebar.contains(e.target) && 
            !sidebarToggle.contains(e.target)) {
          sidebar.classList.remove('open');
          sidebarToggle.setAttribute('aria-expanded', 'false');
          sidebarToggle.innerHTML = getIcon('menu');
        }
      });
    }
  }

  // --------------------------------------------------------------------------
  // Table of Contents
  // --------------------------------------------------------------------------
  function initTOC() {
    if (!articleBody || !toc) return;
    
    const headings = [...articleBody.querySelectorAll('h2, h3')];
    if (headings.length === 0) return;
    
    const ul = document.createElement('ul');
    ul.className = 'toc-list';
    
    const items = [];
    let lockedHeadingId = null;
    let lockReleaseAt = 0;
    
    headings.forEach((h) => {
      // Add ID if not present
      if (!h.id) {
        h.id = slugify(h.textContent);
      }
      
      // Add anchor link
      const anchor = document.createElement('a');
      anchor.href = `#${h.id}`;
      anchor.className = 'heading-anchor';
      anchor.innerHTML = getIcon('link', 'heading-anchor-icon');
      anchor.setAttribute('aria-label', `Link to ${h.textContent}`);
      h.appendChild(anchor);
      
      // Create TOC item
      const li = document.createElement('li');
      li.className = 'toc-item';
      
      if (h.tagName === 'H3') {
        li.classList.add('toc-item-h3');
      }
      
      const link = document.createElement('a');
      link.href = `#${h.id}`;
      link.className = h.tagName === 'H3' ? 'toc-link toc-link-h3' : 'toc-link';
      link.textContent = h.textContent;
      
      li.appendChild(link);
      ul.appendChild(li);
      
      items.push({ heading: h, link });
    });
    
    toc.replaceChildren(ul);

    function setActiveHeading(id) {
      items.forEach((item) => {
        item.link.classList.toggle('active', item.heading.id === id);
      });
    }

    function lockActiveHeading(id, durationMs = 900) {
      lockedHeadingId = id;
      lockReleaseAt = Date.now() + durationMs;
      setActiveHeading(id);
    }

    function getHeaderOffset() {
      const cssValue = getComputedStyle(document.documentElement).getPropertyValue('--header-height');
      const parsed = Number.parseFloat(cssValue);
      return Number.isFinite(parsed) ? parsed : 64;
    }

    function getActivationOffset() {
      const fallback = getHeaderOffset() + Math.max(8, Math.min(window.innerHeight * 0.04, 24));
      const sampleHeading = items[0] ? items[0].heading : null;
      if (!sampleHeading) return fallback;

      const scrollMarginTop = Number.parseFloat(getComputedStyle(sampleHeading).scrollMarginTop || '');
      if (Number.isFinite(scrollMarginTop) && scrollMarginTop > 0) {
        return scrollMarginTop;
      }

      return fallback;
    }

    function updateActiveHeading() {
      if (lockedHeadingId && Date.now() < lockReleaseAt) {
        setActiveHeading(lockedHeadingId);
        return;
      }

      if (lockedHeadingId && Date.now() >= lockReleaseAt) {
        lockedHeadingId = null;
      }

      const scrollTop = window.scrollY || window.pageYOffset || 0;
      const scrollBottom = scrollTop + window.innerHeight;
      const docHeight = Math.max(document.body.scrollHeight, document.documentElement.scrollHeight);
      const activationOffset = getActivationOffset();
      const topBoundary = scrollTop + activationOffset + 1;
      const bottomSnapThreshold = Math.max(20, Math.min(window.innerHeight * 0.06, 64));

      let activeItem = items[0];
      for (const item of items) {
        const headingTop = item.heading.getBoundingClientRect().top + scrollTop;
        if (headingTop <= topBoundary) {
          activeItem = item;
        } else {
          break;
        }
      }

      if (docHeight - scrollBottom <= bottomSnapThreshold) {
        activeItem = items[items.length - 1];
      }

      setActiveHeading(activeItem.heading.id);
    }

    let rafPending = false;
    function syncTOC() {
      if (rafPending) return;
      rafPending = true;
      window.requestAnimationFrame(() => {
        updateActiveHeading();
        rafPending = false;
      });
    }

    items.forEach((item) => {
      item.link.addEventListener('click', (event) => {
        event.preventDefault();
        event.stopPropagation();

        const scrollTop = window.scrollY || window.pageYOffset || 0;
        const headingTop = item.heading.getBoundingClientRect().top + scrollTop;
        const activationOffset = getActivationOffset();
        const targetTop = Math.max(0, headingTop - activationOffset + 1);

        lockActiveHeading(item.heading.id);
        window.history.replaceState(null, '', `#${item.heading.id}`);
        window.scrollTo({ top: targetTop, behavior: 'smooth' });
      });
    });

    window.addEventListener('scroll', syncTOC, { passive: true });
    window.addEventListener('resize', syncTOC, { passive: true });
    updateActiveHeading();
  }

  // --------------------------------------------------------------------------
  // Active Page Navigation
  // --------------------------------------------------------------------------
  function initNavigation() {
    if (!pageIndex) return;
    
    const navLinks = [...pageIndex.querySelectorAll('a')];
    const currentPath = window.location.pathname;
    const currentSlug = getHrefSlug(currentPath) || 'index.html';
    
    navLinks.forEach((a) => {
      const href = a.getAttribute('href');
      const hrefSlug = getHrefSlug(href);
      const isCurrent = currentPath === href || (href && currentPath.endsWith(href)) || (hrefSlug && hrefSlug === currentSlug);
      const icon = getPageIcon(hrefSlug);
      const label = a.textContent.trim();

      if (isCurrent) {
        a.classList.add('active');
        a.setAttribute('aria-current', 'page');
      }

      a.innerHTML = getIcon(icon) + label;
    });
  }

  function getHrefSlug(href) {
    if (!href) return '';
    const cleanHref = href.split('#')[0].split('?')[0];
    const normalized = cleanHref.endsWith('/') ? `${cleanHref}index.html` : cleanHref;
    const slashIndex = normalized.lastIndexOf('/');
    return slashIndex >= 0 ? normalized.slice(slashIndex + 1) : normalized;
  }

  // Map pages to icons
  function getPageIcon(pageSlug) {
    const icons = {
      'index.html': 'home',
      'getting-started.html': 'rocket',
      'installation.html': 'package',
      'quickstart.html': 'zap',
      'configuration.html': 'settings',
      'schema-keywords.html': 'hash',
      'validation-behavior.html': 'check-circle',
      'examples.html': 'code',
      'architecture.html': 'layers'
    };
    return icons[pageSlug] || 'file-text';
  }

  // --------------------------------------------------------------------------
  // Search Functionality
  // --------------------------------------------------------------------------
  function normalizeSearchText(text) {
    if (!text) return '';
    return text
      .toString()
      .normalize('NFD')
      .replace(/[\u0300-\u036f]/g, '')
      .toLowerCase()
      .replace(/\s+/g, ' ')
      .trim();
  }

  function escapeHtml(value) {
    return (value || '')
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;')
      .replace(/'/g, '&#39;');
  }

  function escapeRegExp(value) {
    return value.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
  }

  function getDocTypeLabel(type) {
    const labels = {
      'tutorial': 'Tutorial',
      'how-to': 'How-to',
      'reference': 'Reference',
      'explanation': 'Explanation'
    };
    return labels[type] || 'Page';
  }

  function buildSnippet(content, queryTerms) {
    const clean = (content || '').replace(/\s+/g, ' ').trim();
    if (!clean) return '';

    const lower = clean.toLowerCase();
    let matchIndex = -1;
    for (const term of queryTerms) {
      const idx = lower.indexOf(term.toLowerCase());
      if (idx >= 0 && (matchIndex < 0 || idx < matchIndex)) {
        matchIndex = idx;
      }
    }

    if (matchIndex < 0) {
      return clean.slice(0, 180) + (clean.length > 180 ? '…' : '');
    }

    const start = Math.max(0, matchIndex - 60);
    const end = Math.min(clean.length, matchIndex + 120);
    let snippet = clean.slice(start, end);
    if (start > 0) snippet = `…${snippet}`;
    if (end < clean.length) snippet = `${snippet}…`;
    return snippet;
  }

  function highlightTerms(text, terms) {
    if (!text) return '';
    let output = escapeHtml(text);
    terms
      .filter((term) => term.length >= 2)
      .forEach((term) => {
        const re = new RegExp(`(${escapeRegExp(escapeHtml(term))})`, 'ig');
        output = output.replace(re, '<mark>$1</mark>');
      });
    return output;
  }

  function scorePageMatch(page, query, queryTerms) {
    if (!query || queryTerms.length === 0) return 0;

    const titleNorm = page._titleNorm;
    const descNorm = page._descNorm;
    const contentNorm = page._contentNorm;
    const joinedNorm = page._allNorm;

    let score = 0;
    for (const term of queryTerms) {
      if (titleNorm.includes(term)) {
        score += titleNorm.startsWith(term) ? 45 : 30;
      } else if (descNorm.includes(term)) {
        score += 18;
      } else if (contentNorm.includes(term)) {
        score += 8;
      } else {
        return 0;
      }
    }

    if (titleNorm.includes(query)) score += 40;
    if (descNorm.includes(query)) score += 16;
    if (joinedNorm.includes(query)) score += 6;

    return score;
  }

  function prepareSearchEntry(item) {
    const title = (item.title || '').trim();
    const description = (item.description || '').trim();
    const content = (item.content || '').replace(/\s+/g, ' ').trim();

    return {
      title,
      description,
      url: item.url || '',
      docType: item.doc_type || '',
      content,
      _titleNorm: normalizeSearchText(title),
      _descNorm: normalizeSearchText(description),
      _contentNorm: normalizeSearchText(content),
      _allNorm: normalizeSearchText(`${title} ${description} ${content}`)
    };
  }

  function initSearch() {
    if (!searchInput || !searchResults) return;
    
    // Add search icon
    const searchWrapper = searchInput.parentElement;
    if (searchWrapper && !searchWrapper.querySelector('.header-search-icon')) {
      const searchIcon = document.createElement('span');
      searchIcon.className = 'header-search-icon';
      searchIcon.innerHTML = getIcon('search');
      searchWrapper.insertBefore(searchIcon, searchInput);
    }

    let searchData = [];
    let activeIndex = -1;

    function hideResults() {
      searchResults.hidden = true;
      searchResults.innerHTML = '';
      searchInput.setAttribute('aria-expanded', 'false');
      activeIndex = -1;
    }

    function renderResults(results, query, queryTerms) {
      searchResults.innerHTML = '';
      activeIndex = -1;

      if (!query) {
        hideResults();
        return;
      }

      if (!results.length) {
        searchResults.innerHTML = '<div class="search-empty">No results found</div>';
        searchResults.hidden = false;
        searchInput.setAttribute('aria-expanded', 'true');
        return;
      }

      const fragment = document.createDocumentFragment();

      results.forEach((item, index) => {
        const link = document.createElement('a');
        link.className = 'search-result-item';
        link.href = item.url;
        link.setAttribute('role', 'option');
        link.dataset.resultIndex = String(index);

        const title = document.createElement('span');
        title.className = 'search-result-title';
        title.innerHTML = highlightTerms(item.title, queryTerms);

        const meta = document.createElement('span');
        meta.className = 'search-result-meta';
        meta.textContent = getDocTypeLabel(item.docType);

        const snippetText = item.description || buildSnippet(item.content, queryTerms);
        if (snippetText) {
          const snippet = document.createElement('span');
          snippet.className = 'search-result-snippet';
          snippet.innerHTML = highlightTerms(snippetText, queryTerms);
          link.appendChild(snippet);
        }

        link.prepend(meta);
        link.prepend(title);
        fragment.appendChild(link);
      });

      searchResults.appendChild(fragment);
      searchResults.hidden = false;
      searchInput.setAttribute('aria-expanded', 'true');
    }

    function updateActiveResult(nextIndex) {
      const items = [...searchResults.querySelectorAll('.search-result-item')];
      if (!items.length) return;

      items.forEach((item) => item.classList.remove('active'));
      if (nextIndex < 0 || nextIndex >= items.length) {
        activeIndex = -1;
        return;
      }

      activeIndex = nextIndex;
      const activeItem = items[activeIndex];
      activeItem.classList.add('active');
      activeItem.scrollIntoView({ block: 'nearest' });
    }

    async function loadSearchData() {
      if (searchData.length > 0) return searchData;

      try {
        const response = await fetch('search.json', { cache: 'no-store' });
        if (!response.ok) throw new Error(`Search index unavailable (${response.status})`);
        const raw = await response.json();
        searchData = (Array.isArray(raw) ? raw : []).map(prepareSearchEntry).filter((item) => item.url && item.title);
      } catch (error) {
        // Fallback to sidebar pages if index is unavailable.
        const navFallback = [...(pageIndex ? pageIndex.querySelectorAll('a') : [])].map((a) => ({
          title: a.textContent.trim(),
          url: a.getAttribute('href') || '',
          description: '',
          doc_type: ''
        }));
        searchData = navFallback.map(prepareSearchEntry).filter((item) => item.url && item.title);
      }

      return searchData;
    }

    function runSearch(query) {
      const normalizedQuery = normalizeSearchText(query);
      const queryTerms = normalizedQuery.split(' ').filter(Boolean);
      if (!normalizedQuery) {
        hideResults();
        return;
      }

      const ranked = searchData
        .map((page) => ({ page, score: scorePageMatch(page, normalizedQuery, queryTerms) }))
        .filter((entry) => entry.score > 0)
        .sort((a, b) => b.score - a.score)
        .slice(0, 8)
        .map((entry) => entry.page);

      renderResults(ranked, normalizedQuery, queryTerms);
    }

    const debouncedSearch = (() => {
      let timeoutId;
      return (value) => {
        window.clearTimeout(timeoutId);
        timeoutId = window.setTimeout(() => runSearch(value), 120);
      };
    })();

    searchInput.setAttribute('aria-haspopup', 'listbox');
    searchInput.setAttribute('aria-expanded', 'false');

    searchInput.addEventListener('focus', async () => {
      await loadSearchData();
      if (searchInput.value.trim()) runSearch(searchInput.value);
    });

    searchInput.addEventListener('input', async (e) => {
      await loadSearchData();
      debouncedSearch(e.target.value || '');
    });

    searchInput.addEventListener('keydown', (e) => {
      const items = [...searchResults.querySelectorAll('.search-result-item')];
      if (!items.length || searchResults.hidden) {
        if (e.key === 'Escape') hideResults();
        return;
      }

      if (e.key === 'ArrowDown') {
        e.preventDefault();
        const next = activeIndex < items.length - 1 ? activeIndex + 1 : 0;
        updateActiveResult(next);
      } else if (e.key === 'ArrowUp') {
        e.preventDefault();
        const prev = activeIndex > 0 ? activeIndex - 1 : items.length - 1;
        updateActiveResult(prev);
      } else if (e.key === 'Enter') {
        const target = activeIndex >= 0 ? items[activeIndex] : items[0];
        if (target) {
          window.location.href = target.getAttribute('href');
        }
      } else if (e.key === 'Escape') {
        hideResults();
      }
    });

    document.addEventListener('click', (e) => {
      if (!searchWrapper || !searchWrapper.contains(e.target)) {
        hideResults();
      }
    });
  }

  // --------------------------------------------------------------------------
  // Table Enhancements
  // --------------------------------------------------------------------------
  function initTables() {
    if (!articleBody) return;

    const tables = [...articleBody.querySelectorAll('table')];
    tables.forEach((table) => {
      if (!table || table.closest('.table-wrap')) return;
      const wrapper = document.createElement('div');
      wrapper.className = 'table-wrap';
      table.parentNode.insertBefore(wrapper, table);
      wrapper.appendChild(table);
    });
  }

  // --------------------------------------------------------------------------
  // Callouts / Admonitions
  // --------------------------------------------------------------------------
  function parseCalloutMarker(text) {
    if (!text) return null;
    const match = text.trim().match(/^\[!(NOTE|TIP|IMPORTANT|WARNING|CAUTION)\]\s*(.*)$/i);
    if (!match) return null;
    return { type: match[1].toLowerCase(), rest: match[2] || '' };
  }

  function resolveCalloutConfig(type) {
    const map = {
      note: { className: 'callout-info', title: 'Note', icon: 'info' },
      tip: { className: 'callout-success', title: 'Tip', icon: 'zap' },
      important: { className: 'callout-warning', title: 'Important', icon: 'star' },
      warning: { className: 'callout-warning', title: 'Warning', icon: 'alert-triangle' },
      caution: { className: 'callout-danger', title: 'Caution', icon: 'alert-triangle' }
    };
    return map[type] || map.note;
  }

  function initCallouts() {
    if (!articleBody) return;

    const blockquotes = [...articleBody.querySelectorAll('blockquote')];
    blockquotes.forEach((quote) => {
      if (!quote || quote.closest('.callout')) return;
      const firstParagraph = quote.querySelector('p');
      if (!firstParagraph) return;

      const parsed = parseCalloutMarker(firstParagraph.textContent);
      if (!parsed) return;

      const config = resolveCalloutConfig(parsed.type);
      const callout = document.createElement('div');
      callout.className = `callout ${config.className}`;

      const icon = document.createElement('span');
      icon.className = 'callout-icon';
      icon.innerHTML = getIcon(config.icon);

      const content = document.createElement('div');
      content.className = 'callout-content';

      const title = document.createElement('strong');
      title.className = 'callout-title';
      title.textContent = config.title;
      content.appendChild(title);

      const restText = parsed.rest.trim();
      if (restText) {
        firstParagraph.textContent = restText;
      } else if (!firstParagraph.textContent.replace(/\s/g, '')) {
        firstParagraph.remove();
      } else {
        firstParagraph.textContent = '';
        firstParagraph.remove();
      }

      while (quote.firstChild) {
        content.appendChild(quote.firstChild);
      }

      callout.appendChild(icon);
      callout.appendChild(content);
      quote.replaceWith(callout);
    });
  }

  // --------------------------------------------------------------------------
  // Code Block Enhancements
  // --------------------------------------------------------------------------
  function normalizeLanguage(lang) {
    const raw = (lang || 'code').toLowerCase();
    const aliases = {
      yml: 'yaml',
      shell: 'bash',
      sh: 'bash',
      zsh: 'bash',
      console: 'bash',
      ps1: 'powershell',
      pwsh: 'powershell',
      text: 'plaintext',
      txt: 'plaintext',
      md: 'markdown',
      js: 'javascript',
      ts: 'typescript'
    };
    return aliases[raw] || raw;
  }

  function formatLanguageLabel(lang) {
    const labels = {
      javascript: 'JavaScript',
      typescript: 'TypeScript',
      plaintext: 'Text',
      powershell: 'PowerShell',
      bash: 'Bash',
      yaml: 'YAML',
      json: 'JSON',
      xml: 'XML',
      html: 'HTML',
      css: 'CSS',
      sql: 'SQL',
      toml: 'TOML',
      markdown: 'Markdown',
      java: 'Java',
      kotlin: 'Kotlin'
    };

    if (labels[lang]) return labels[lang];
    if (lang.length <= 3) return lang.toUpperCase();
    return lang.charAt(0).toUpperCase() + lang.slice(1);
  }

  function extractLanguage(...nodes) {
    for (const node of nodes) {
      if (!node || !node.className || typeof node.className !== 'string') continue;
      const match = node.className.match(/language-([a-z0-9+-]+)/i);
      if (match && match[1]) return normalizeLanguage(match[1]);
    }
    return 'plaintext';
  }

  function resolveCodeBlockContainer(pre) {
    if (!pre) return null;
    if (pre.closest('.code-block')) return pre.closest('.code-block');

    const highlighted = pre.closest('.highlighter-rouge, figure.highlight');
    if (highlighted) return highlighted;

    if (pre.parentElement && pre.parentElement.classList.contains('highlight')) {
      return pre.parentElement;
    }

    return pre;
  }

  function normalizeCodeBlocks() {
    const preNodes = [...document.querySelectorAll('.article-content pre')];

    preNodes.forEach((pre) => {
      if (pre.closest('.code-block')) return;

      const container = resolveCodeBlockContainer(pre);
      if (!container) return;

      if (container === pre) {
        const wrapper = document.createElement('div');
        wrapper.className = 'code-block';
        pre.parentNode.insertBefore(wrapper, pre);
        wrapper.appendChild(pre);
      } else {
        container.classList.add('code-block');
      }
    });

    return [...document.querySelectorAll('.article-content .code-block')];
  }

  function initCodeBlocks() {
    const codeBlocks = normalizeCodeBlocks();
    
    codeBlocks.forEach((block) => {
      const pre = block.querySelector('pre');
      if (!pre) return;
      
      // Get language from class or header
      const code = pre.querySelector('code');
      const lang = extractLanguage(code, pre, block);
      block.dataset.lang = lang;
      
      // Add header if not present
      if (!block.querySelector('.code-block-header')) {
        const header = document.createElement('div');
        header.className = 'code-block-header';
        
        const langLabel = document.createElement('span');
        langLabel.className = 'code-block-lang';
        langLabel.textContent = formatLanguageLabel(lang);
        
        const copyBtn = document.createElement('button');
        copyBtn.className = 'code-block-copy';
        copyBtn.innerHTML = getIcon('copy') + 'Copy';
        copyBtn.type = 'button';
        copyBtn.setAttribute('aria-label', 'Copy code to clipboard');
        
        copyBtn.addEventListener('click', async () => {
          const text = pre.textContent;
          try {
            await navigator.clipboard.writeText(text);
            copyBtn.innerHTML = getIcon('check') + 'Copied!';
            copyBtn.classList.add('copied');
            setTimeout(() => {
              copyBtn.innerHTML = getIcon('copy') + 'Copy';
              copyBtn.classList.remove('copied');
            }, 2000);
          } catch (err) {
            console.error('Failed to copy:', err);
          }
        });
        
        header.appendChild(langLabel);
        header.appendChild(copyBtn);
        block.insertBefore(header, block.firstElementChild);
      }
    });
  }

  // --------------------------------------------------------------------------
  // Initialize All
  // --------------------------------------------------------------------------
  function init() {
    initTheme();
    initSidebar();
    initNavigation();
    initTOC();
    initSearch();
    initTables();
    initCallouts();
    initCodeBlocks();
    
    // Add theme toggle listener
    if (themeToggle) {
      themeToggle.addEventListener('click', toggleTheme);
    }
    
    // Add smooth scroll for anchor links
    document.querySelectorAll('a[href^="#"]').forEach((anchor) => {
      anchor.addEventListener('click', (e) => {
        const target = document.querySelector(anchor.getAttribute('href'));
        if (target) {
          e.preventDefault();
          target.scrollIntoView({ behavior: 'smooth', block: 'start' });
        }
      });
    });
  }

  // Run on DOM ready
  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', init);
  } else {
    init();
  }

})();
