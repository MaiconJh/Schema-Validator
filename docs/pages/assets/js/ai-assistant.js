// AI Assistant Widget for Schema-Validator Documentation
(function () {
  'use strict';

  const CONFIG = {
    apiEndpoint: (document.getElementById('ai-assistant-chat') && document.getElementById('ai-assistant-chat').dataset.apiEndpoint)
      || 'http://localhost:3001/chat/completions',
    turnstileSiteKey: (document.getElementById('ai-assistant-chat') && document.getElementById('ai-assistant-chat').dataset.turnstileSiteKey) || '',
    maxHistoryMessages: 8,
    maxDocsMatches: 4
  };

  const DEFAULT_LAYOUT = {
    width: 440,
    height: 640,
    minWidth: 360,
    minHeight: 380,
    margin: 20,
    mobileMargin: 16,
    maximizeMargin: 20,
    mobileMaximizeMargin: 12,
    gapFromToggle: 16
  };

  const PERSISTENCE = {
    stateKey: 'schema-validator-ai-chat-state-v2',
    sessionTokenKey: 'schema-validator-ai-chat-session-v2',
    legacySessionTokenKey: 'schema-validator-ai-chat-session',
    maxAgeMs: 12 * 60 * 60 * 1000,
    maxRenderedMessages: 24,
    maxMessageChars: 6000,
    maxDraftChars: 2000,
    debounceMs: 120
  };

  const STOP_WORDS = new Set([
    'a', 'about', 'after', 'again', 'all', 'also', 'an', 'and', 'any', 'are', 'as', 'at',
    'be', 'been', 'but', 'by', 'can', 'com', 'como', 'da', 'das', 'de', 'do', 'dos', 'em',
    'for', 'from', 'get', 'has', 'help', 'how', 'isso', 'isto', 'its', 'let', 'me', 'more',
    'na', 'nas', 'need', 'nos', 'not', 'num', 'numa', 'o', 'of', 'on', 'or', 'os', 'para',
    'por', 'que', 'se', 'sem', 'ser', 'sobre', 'some', 'the', 'this', 'to', 'uma', 'um',
    'with', 'you'
  ]);

  const DOMAIN_HINTS = [
    'schema-validator',
    'schema validator',
    'json schema',
    'schema keyword',
    'validation',
    'validator',
    'skript',
    'draft 2020-12',
    'draft-2020-12',
    'plugin',
    'minecraft'
  ];

  const ICONS = {
    maximize: '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="15 3 21 3 21 9"></polyline><polyline points="9 21 3 21 3 15"></polyline><line x1="21" y1="3" x2="14" y2="10"></line><line x1="3" y1="21" x2="10" y2="14"></line></svg>',
    restore: '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="8" y="8" width="13" height="13" rx="2"></rect><path d="M16 8V5a2 2 0 0 0-2-2H5a2 2 0 0 0-2 2v9a2 2 0 0 0 2 2h3"></path></svg>',
    newChat: '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.1" stroke-linecap="round" stroke-linejoin="round"><path d="M21 15a2 2 0 0 1-2 2H8l-5 4V5a2 2 0 0 1 2-2h7"></path><path d="M16 3h5"></path><path d="M18.5 0.5v5"></path></svg>',
    close: '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>',
    copy: '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="9" y="9" width="13" height="13" rx="2"></rect><path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1"></path></svg>',
    check: '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><polyline points="20 6 9 17 4 12"></polyline></svg>',
    edit: '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 20h9"></path><path d="M16.5 3.5a2.1 2.1 0 0 1 3 3L7 19l-4 1 1-4Z"></path></svg>',
    save: '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><path d="M19 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11l5 5v11a2 2 0 0 1-2 2Z"></path><polyline points="17 21 17 13 7 13 7 21"></polyline><polyline points="7 3 7 8 15 8"></polyline></svg>',
    mention: '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.1" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="4"></circle><path d="M16 8v5a2 2 0 1 0 4 0v-1a8 8 0 1 0-4 6.93"></path></svg>',
    scope: '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.1" stroke-linecap="round" stroke-linejoin="round"><path d="M3 11h18"></path><path d="M5 7h14"></path><path d="M7 15h10"></path><path d="M9 19h6"></path></svg>'
  };

  const toggleButton = document.getElementById('ai-assistant-toggle');
  const chatContainer = document.getElementById('ai-assistant-chat');
  const closeButton = document.getElementById('ai-assistant-close');
  const maximizeButton = document.getElementById('ai-assistant-maximize');
  const resetButton = document.getElementById('ai-assistant-reset');
  const messagesContainer = document.getElementById('ai-assistant-messages');
  const threadContainer = document.getElementById('ai-assistant-thread');
  const userInput = document.getElementById('ai-user-input');
  const sendButton = document.getElementById('ai-send-button');
  const currentPageElement = document.getElementById('ai-current-page');
  const pageContextElement = document.getElementById('ai-assistant-page-context');
  const visibleContextElement = document.getElementById('ai-assistant-visible-context');
  const activeMentionsContainer = document.getElementById('ai-assistant-active-mentions');
  const mentionMenu = document.getElementById('ai-assistant-mention-menu');
  const mentionList = document.getElementById('ai-assistant-mention-list');
  const resetConfirmPanel = document.getElementById('ai-assistant-reset-panel');
  const resetCancelButton = document.getElementById('ai-assistant-reset-cancel');
  const resetConfirmButton = document.getElementById('ai-assistant-reset-confirm');
  const challengeContainer = document.getElementById('ai-assistant-challenge');
  const challengeCopy = document.getElementById('ai-assistant-challenge-copy');
  const turnstileContainer = document.getElementById('ai-assistant-turnstile');
  const resizeHandle = chatContainer ? chatContainer.querySelector('.ai-resize-handle') : null;
  const header = chatContainer ? chatContainer.querySelector('.ai-assistant-header') : null;

  let isOpen = false;
  let isLoading = false;
  let isDragging = false;
  let isResizing = false;
  let isMaximized = false;
  let lastNormalRect = null;
  let conversationHistory = [];
  let docsIndex = [];
  let pageContext = null;
  let currentVisibleSections = [];
  let visibleSectionsTicking = false;
  let mentionState = { isOpen: false, start: -1, end: -1, items: [], activeIndex: 0 };
  let turnstileState = { loadingPromise: null, widgetId: null, token: '', pendingSubmission: null, retryInFlight: false };
  let sessionToken = '';
  let renderedMessages = [];
  let persistTimer = null;

  let dragOffset = { x: 0, y: 0 };
  let resizeState = { startX: 0, startY: 0, startWidth: 0, startHeight: 0, left: 0, top: 0 };

  function init() {
    if (
      !toggleButton ||
      !chatContainer ||
      !closeButton ||
      !maximizeButton ||
      !resetButton ||
      !messagesContainer ||
      !threadContainer ||
      !userInput ||
      !sendButton ||
      !header ||
      !resizeHandle ||
      !mentionMenu ||
      !mentionList ||
      !resetConfirmPanel ||
      !resetCancelButton ||
      !resetConfirmButton ||
      !activeMentionsContainer ||
      !challengeContainer ||
      !challengeCopy ||
      !turnstileContainer
    ) {
      console.error('AI Assistant: Required elements not found');
      return;
    }

    resetButton.innerHTML = ICONS.newChat;
    maximizeButton.innerHTML = ICONS.maximize;
    closeButton.innerHTML = ICONS.close;

    bindEvents();
    sessionToken = loadStoredSessionToken();
    pageContext = collectPageContext();
    hydrateStaticContext();
    updateVisibleSections();
    renderActiveMentionChips();
    hideChallenge();
    restorePersistedState();
    syncChatLayout();
    updateHeaderControls();
    updateResetControlState();
    autoResizeComposer();
    enhanceCodeBlocks(threadContainer);
    loadDocsIndex();
  }

  function bindEvents() {
    toggleButton.addEventListener('click', toggleChat);
    resetButton.addEventListener('click', requestChatReset);
    resetCancelButton.addEventListener('click', hideResetConfirmation);
    resetConfirmButton.addEventListener('click', performChatReset);
    closeButton.addEventListener('click', closeChat);
    maximizeButton.addEventListener('click', toggleMaximize);
    sendButton.addEventListener('click', sendMessage);

    userInput.addEventListener('input', handleComposerInput);
    userInput.addEventListener('keydown', handleComposerKeyDown);
    userInput.addEventListener('click', handleComposerCursorMove);
    userInput.addEventListener('keyup', handleComposerCursorMove);

    mentionList.addEventListener('click', handleMentionListClick);
    threadContainer.addEventListener('click', handleThreadClick);
    threadContainer.addEventListener('keydown', handleThreadKeyDown);

    header.addEventListener('mousedown', startDrag);
    resizeHandle.addEventListener('mousedown', startResize);
    document.addEventListener('mousemove', handlePointerMove);
    document.addEventListener('mouseup', stopInteractions);
    document.addEventListener('keydown', handleGlobalKeyDown);
    document.addEventListener('click', handleDocumentClick);
    document.addEventListener('visibilitychange', handleVisibilityChange);
    window.addEventListener('resize', handleViewportChange);
    window.addEventListener('scroll', scheduleVisibleSectionRefresh, { passive: true });
    window.addEventListener('pagehide', persistAssistantState);
  }

  function hydrateStaticContext() {
    if (pageContextElement) {
      pageContextElement.textContent = pageContext.title || 'Current page';
    }

    if (currentPageElement) {
      currentPageElement.textContent = pageContext.title || 'Current page';
    }
  }

  function collectPageContext() {
    const articleBody = document.getElementById('article-body');
    const articleTitle = document.querySelector('.article-title');
    const articleLead = document.querySelector('.article-lead');
    const breadcrumbs = Array.from(document.querySelectorAll('.breadcrumbs-link, .breadcrumbs-current'))
      .map((element) => cleanText(element.textContent))
      .filter(Boolean);
    const pageTitle = cleanText(chatContainer.dataset.pageTitle || (articleTitle ? articleTitle.textContent : '') || (currentPageElement ? currentPageElement.textContent : 'Schema-Validator documentation'));
    const pageDescription = cleanText(chatContainer.dataset.pageDescription || (articleLead ? articleLead.textContent : ''));
    const pageUrl = chatContainer.dataset.pageUrl || window.location.pathname;
    const sections = Array.from(document.querySelectorAll('#article-body h2, #article-body h3')).map((heading, index) => {
      if (!heading.id) {
        heading.id = slugify(heading.textContent || `section-${index + 1}`);
      }

      return {
        id: heading.id,
        title: cleanText(heading.textContent),
        level: heading.tagName.toLowerCase(),
        excerpt: getSectionExcerpt(heading),
        element: heading
      };
    });

    return {
      articleBody,
      title: pageTitle,
      description: pageDescription,
      url: normalizeUrl(pageUrl),
      breadcrumbs,
      sections,
      pageExcerpt: excerptText(cleanText(articleBody ? articleBody.textContent : pageDescription), 420)
    };
  }

  function getSectionExcerpt(heading) {
    const chunks = [];
    let next = heading.nextElementSibling;

    while (next && !/^H[23]$/.test(next.tagName)) {
      if (!next.matches('pre, .code-block, table, nav')) {
        const text = cleanText(next.textContent);
        if (text) {
          chunks.push(text);
        }
      }

      if (chunks.join(' ').length > 360) {
        break;
      }

      next = next.nextElementSibling;
    }

    return excerptText(chunks.join(' '), 320);
  }

  function toggleChat() {
    if (isOpen) {
      closeChat();
      return;
    }

    openChat();
  }

  function openChat() {
    setChatOpenState(true, { focus: true });
    syncChatLayout();
    autoResizeComposer();
  }

  function closeChat() {
    setChatOpenState(false);
  }

  function setChatOpenState(nextOpen, options) {
    const settings = options || {};

    isOpen = Boolean(nextOpen);
    if (!isOpen) {
      stopInteractions();
      closeMentionMenu();
      hideResetConfirmation();
    }

    chatContainer.style.display = isOpen ? 'flex' : 'none';
    chatContainer.setAttribute('aria-hidden', isOpen ? 'false' : 'true');
    toggleButton.setAttribute('aria-expanded', isOpen ? 'true' : 'false');

    if (isOpen && settings.focus) {
      userInput.focus();
    }

    if (settings.persist !== false) {
      schedulePersistState();
    }
  }

  function handleGlobalKeyDown(event) {
    if (event.key !== 'Escape' || !isOpen) {
      return;
    }

    if (!resetConfirmPanel.hidden) {
      hideResetConfirmation();
      return;
    }

    if (mentionState.isOpen) {
      closeMentionMenu();
      return;
    }

    if (isDragging || isResizing) {
      stopInteractions();
      return;
    }

    if (isMaximized) {
      toggleMaximize();
      return;
    }

    closeChat();
    toggleButton.focus();
  }

  function handleDocumentClick(event) {
    if (!resetConfirmPanel.hidden) {
      if (resetConfirmPanel.contains(event.target) || resetButton.contains(event.target)) {
        return;
      }

      hideResetConfirmation();
    }

    if (!mentionState.isOpen) {
      return;
    }

    if (mentionMenu.contains(event.target) || userInput.contains(event.target)) {
      return;
    }

    closeMentionMenu();
  }

  function handleViewportChange() {
    syncChatLayout();
    autoResizeComposer();
    scheduleVisibleSectionRefresh();
    schedulePersistState();
  }

  function handleVisibilityChange() {
    if (document.visibilityState === 'hidden') {
      hideResetConfirmation();
      persistAssistantState();
    }
  }

  function startDrag(event) {
    if (!isOpen || isMaximized || event.button !== 0) {
      return;
    }

    if (event.target.closest('.ai-assistant-action, a, button, input, textarea, select')) {
      return;
    }

    const rect = chatContainer.getBoundingClientRect();
    isDragging = true;
    dragOffset = {
      x: event.clientX - rect.left,
      y: event.clientY - rect.top
    };

    chatContainer.classList.add('is-dragging');
    document.body.classList.add('ai-assistant-no-select');
    event.preventDefault();
  }

  function startResize(event) {
    if (!isOpen || isMaximized || event.button !== 0) {
      return;
    }

    const rect = chatContainer.getBoundingClientRect();
    isResizing = true;
    resizeState = {
      startX: event.clientX,
      startY: event.clientY,
      startWidth: rect.width,
      startHeight: rect.height,
      left: rect.left,
      top: rect.top
    };

    chatContainer.classList.add('is-resizing');
    document.body.classList.add('ai-assistant-no-select');
    event.preventDefault();
    event.stopPropagation();
  }

  function handlePointerMove(event) {
    if (isDragging) {
      const rect = clampRect({
        left: event.clientX - dragOffset.x,
        top: event.clientY - dragOffset.y,
        width: chatContainer.offsetWidth,
        height: chatContainer.offsetHeight
      });

      applyRect(rect, true);
      return;
    }

    if (!isResizing) {
      return;
    }

    const rect = clampRect({
      left: resizeState.left,
      top: resizeState.top,
      width: resizeState.startWidth + (event.clientX - resizeState.startX),
      height: resizeState.startHeight + (event.clientY - resizeState.startY)
    });

    applyRect(rect, true);
  }

  function stopInteractions() {
    if (!isDragging && !isResizing) {
      return;
    }

    isDragging = false;
    isResizing = false;
    chatContainer.classList.remove('is-dragging', 'is-resizing');
    document.body.classList.remove('ai-assistant-no-select');
  }

  function toggleMaximize() {
    if (!isOpen) {
      return;
    }

    stopInteractions();

    if (isMaximized) {
      isMaximized = false;
      chatContainer.classList.remove('is-maximized');
      syncChatLayout();
      updateHeaderControls();
      schedulePersistState();
      return;
    }

    const rect = chatContainer.getBoundingClientRect();
    lastNormalRect = {
      left: rect.left,
      top: rect.top,
      width: rect.width,
      height: rect.height
    };

    isMaximized = true;
    chatContainer.classList.add('is-maximized');
    applyRect(getMaximizedRect(), false);
    updateHeaderControls();
    schedulePersistState();
  }

  function updateHeaderControls() {
    maximizeButton.innerHTML = isMaximized ? ICONS.restore : ICONS.maximize;
    maximizeButton.setAttribute('aria-label', isMaximized ? 'Restore chat size' : 'Maximize chat');
    maximizeButton.setAttribute('title', isMaximized ? 'Restore chat size' : 'Maximize chat');
  }

  function updateResetControlState() {
    const disabled = isLoading || !hasResettableContent();
    resetButton.disabled = disabled;
    resetButton.setAttribute('aria-disabled', disabled ? 'true' : 'false');
    resetButton.setAttribute('title', disabled ? 'No conversation to clear' : 'Start new chat');
  }

  function syncChatLayout() {
    if (isMaximized) {
      applyRect(getMaximizedRect(), false);
      return;
    }

    const rect = clampRect(lastNormalRect || getDefaultRect());
    applyRect(rect, true);
  }

  function getDefaultRect() {
    const margin = getViewportMargin();
    const maxWidth = Math.max(0, window.innerWidth - margin * 2);
    const maxHeight = Math.max(0, window.innerHeight - margin * 2);
    const width = clamp(
      window.innerWidth <= 720 ? maxWidth : DEFAULT_LAYOUT.width,
      Math.min(DEFAULT_LAYOUT.minWidth, maxWidth),
      maxWidth
    );
    const height = clamp(
      DEFAULT_LAYOUT.height,
      Math.min(DEFAULT_LAYOUT.minHeight, maxHeight),
      maxHeight
    );
    const toggleRect = toggleButton.getBoundingClientRect();
    const maxTop = Math.max(margin, window.innerHeight - margin - height);

    return {
      left: Math.max(margin, window.innerWidth - margin - width),
      top: clamp(toggleRect.top - DEFAULT_LAYOUT.gapFromToggle - height, margin, maxTop),
      width,
      height
    };
  }

  function getMaximizedRect() {
    const margin = window.innerWidth <= 720
      ? DEFAULT_LAYOUT.mobileMaximizeMargin
      : DEFAULT_LAYOUT.maximizeMargin;

    return {
      left: margin,
      top: margin,
      width: Math.max(0, window.innerWidth - margin * 2),
      height: Math.max(0, window.innerHeight - margin * 2)
    };
  }

  function clampRect(rect) {
    const margin = getViewportMargin();
    const maxWidth = Math.max(0, window.innerWidth - margin * 2);
    const maxHeight = Math.max(0, window.innerHeight - margin * 2);
    const width = clamp(
      rect.width || DEFAULT_LAYOUT.width,
      Math.min(DEFAULT_LAYOUT.minWidth, maxWidth),
      maxWidth
    );
    const height = clamp(
      rect.height || DEFAULT_LAYOUT.height,
      Math.min(DEFAULT_LAYOUT.minHeight, maxHeight),
      maxHeight
    );
    const maxLeft = Math.max(margin, window.innerWidth - margin - width);
    const maxTop = Math.max(margin, window.innerHeight - margin - height);

    return {
      left: clamp(rect.left == null ? maxLeft : rect.left, margin, maxLeft),
      top: clamp(rect.top == null ? maxTop : rect.top, margin, maxTop),
      width,
      height
    };
  }

  function applyRect(rect, rememberRect) {
    chatContainer.style.left = `${rect.left}px`;
    chatContainer.style.top = `${rect.top}px`;
    chatContainer.style.right = 'auto';
    chatContainer.style.bottom = 'auto';
    chatContainer.style.width = `${rect.width}px`;
    chatContainer.style.height = `${rect.height}px`;

    if (rememberRect) {
      lastNormalRect = { ...rect };
      schedulePersistState();
    }
  }

  function getViewportMargin() {
    return window.innerWidth <= 720 ? DEFAULT_LAYOUT.mobileMargin : DEFAULT_LAYOUT.margin;
  }

  function handleComposerInput() {
    autoResizeComposer();
    updateMentionSuggestions();
    renderActiveMentionChips();
    updateResetControlState();
    schedulePersistState();
  }

  function handleComposerCursorMove(event) {
    if (
      mentionState.isOpen &&
      event &&
      event.type === 'keyup' &&
      ['ArrowDown', 'ArrowUp', 'Enter', 'Tab', 'Escape'].includes(event.key)
    ) {
      return;
    }

    updateMentionSuggestions();
  }

  function handleComposerKeyDown(event) {
    if (mentionState.isOpen) {
      if (event.key === 'ArrowDown') {
        event.preventDefault();
        moveMentionSelection(1);
        return;
      }

      if (event.key === 'ArrowUp') {
        event.preventDefault();
        moveMentionSelection(-1);
        return;
      }

      if (event.key === 'Enter' || event.key === 'Tab') {
        event.preventDefault();
        selectMention(mentionState.items[mentionState.activeIndex]);
        return;
      }

      if (event.key === 'Escape') {
        event.preventDefault();
        closeMentionMenu();
        return;
      }
    }

    if (event.key === 'Escape' && !resetConfirmPanel.hidden) {
      event.preventDefault();
      hideResetConfirmation();
      return;
    }

    if (event.key === 'Enter' && !event.shiftKey) {
      event.preventDefault();
      sendMessage();
    }
  }

  function requestChatReset() {
    if (isLoading) {
      return;
    }

    if (!hasResettableContent()) {
      userInput.focus();
      return;
    }

    closeMentionMenu();
    resetConfirmPanel.hidden = false;
    resetConfirmButton.focus();
  }

  function hideResetConfirmation() {
    resetConfirmPanel.hidden = true;
  }

  function performChatReset() {
    if (isLoading) {
      return;
    }

    conversationHistory = [];
    renderedMessages = [];
    mentionState = { isOpen: false, start: -1, end: -1, items: [], activeIndex: 0 };
    hideResetConfirmation();
    closeMentionMenu();
    hideChallenge();
    turnstileState.pendingSubmission = null;
    turnstileState.retryInFlight = false;
    turnstileState.token = '';
    if (window.turnstile && turnstileState.widgetId !== null) {
      window.turnstile.reset(turnstileState.widgetId);
    }

    userInput.value = '';
    autoResizeComposer();
    renderActiveMentionChips();
    renderPersistedMessages();
    updateResetControlState();
    persistAssistantState();
    userInput.focus();
  }

  function hasResettableContent() {
    return Boolean(
      renderedMessages.length ||
      conversationHistory.length ||
      cleanText(userInput.value)
    );
  }

  function handleMentionListClick(event) {
    const button = event.target.closest('.ai-assistant-mention-item');
    if (!button) {
      return;
    }

    const item = mentionState.items.find((entry) => entry.key === button.dataset.key);
    if (item) {
      selectMention(item);
    }
  }

  function updateMentionSuggestions() {
    const queryInfo = getMentionQueryInfo(userInput.value, userInput.selectionStart);
    if (!queryInfo) {
      closeMentionMenu();
      return;
    }

    const items = getMentionEntries(queryInfo.query);
    if (!items.length) {
      closeMentionMenu();
      return;
    }

    mentionState = {
      isOpen: true,
      start: queryInfo.start,
      end: queryInfo.end,
      items,
      activeIndex: 0
    };

    renderMentionMenu();
  }

  function getMentionQueryInfo(text, cursor) {
    const beforeCursor = text.slice(0, cursor);
    const atIndex = beforeCursor.lastIndexOf('@');
    if (atIndex === -1) {
      return null;
    }

    const previousChar = atIndex === 0 ? '' : beforeCursor.charAt(atIndex - 1);
    if (previousChar && !/[\s([{]/.test(previousChar)) {
      return null;
    }

    const query = beforeCursor.slice(atIndex + 1);
    if (!query.length) {
      return { start: atIndex, end: cursor, query: '' };
    }

    if (/[\]\n]/.test(query) || /\s/.test(query)) {
      return null;
    }

    return { start: atIndex, end: cursor, query };
  }

  function getMentionEntries(query) {
    const normalizedQuery = normalizeText(query || '');
    const visibleTitles = currentVisibleSections.map((section) => section.title).join(', ');
    const entries = [
      {
        key: 'page',
        label: 'Current page',
        meta: pageContext.title || 'Schema-Validator documentation'
      },
      {
        key: 'visible',
        label: 'Visible sections',
        meta: visibleTitles || 'Current section in view'
      }
    ];

    pageContext.sections.forEach((section) => {
      entries.push({
        key: `section:${section.id}`,
        label: section.title,
        meta: section.excerpt || (section.level === 'h2' ? 'Main section' : 'Subsection')
      });
    });

    return entries
      .filter((entry) => {
        if (!normalizedQuery) {
          return true;
        }

        const haystack = normalizeText(`${entry.label} ${entry.meta}`);
        return haystack.includes(normalizedQuery);
      })
      .sort((left, right) => scoreMentionEntry(right, normalizedQuery) - scoreMentionEntry(left, normalizedQuery))
      .slice(0, 7);
  }

  function scoreMentionEntry(entry, normalizedQuery) {
    if (!normalizedQuery) {
      if (entry.key === 'visible') {
        return 50;
      }

      if (entry.key === 'page') {
        return 40;
      }

      return 20;
    }

    const label = normalizeText(entry.label);
    const meta = normalizeText(entry.meta);
    let score = 0;

    if (label.startsWith(normalizedQuery)) {
      score += 10;
    }

    if (label.includes(normalizedQuery)) {
      score += 6;
    }

    if (meta.includes(normalizedQuery)) {
      score += 2;
    }

    return score;
  }

  function renderMentionMenu() {
    mentionList.innerHTML = '';

    mentionState.items.forEach((item, index) => {
      const button = document.createElement('button');
      button.type = 'button';
      button.className = `ai-assistant-mention-item${index === mentionState.activeIndex ? ' is-active' : ''}`;
      button.dataset.key = item.key;
      button.setAttribute('role', 'option');
      button.setAttribute('aria-selected', index === mentionState.activeIndex ? 'true' : 'false');

      const title = document.createElement('span');
      title.className = 'ai-assistant-mention-item-title';
      title.textContent = `@[${item.label}]`;

      const meta = document.createElement('span');
      meta.className = 'ai-assistant-mention-item-meta';
      meta.textContent = item.meta;

      button.appendChild(title);
      button.appendChild(meta);
      mentionList.appendChild(button);
    });

    mentionMenu.hidden = false;
  }

  function closeMentionMenu() {
    mentionState = { isOpen: false, start: -1, end: -1, items: [], activeIndex: 0 };
    mentionMenu.hidden = true;
    mentionList.innerHTML = '';
  }

  function moveMentionSelection(direction) {
    if (!mentionState.items.length) {
      return;
    }

    mentionState.activeIndex = (mentionState.activeIndex + direction + mentionState.items.length) % mentionState.items.length;
    renderMentionMenu();
  }

  function selectMention(item) {
    if (!item) {
      return;
    }

    const replacement = `@[${item.label}] `;
    const selectionEnd = userInput.selectionStart;
    userInput.value = `${userInput.value.slice(0, mentionState.start)}${replacement}${userInput.value.slice(selectionEnd)}`;

    const nextCursor = mentionState.start + replacement.length;
    userInput.setSelectionRange(nextCursor, nextCursor);
    closeMentionMenu();
    autoResizeComposer();
    renderActiveMentionChips();
    userInput.focus();
  }

  function renderActiveMentionChips() {
    const fragment = document.createDocumentFragment();
    fragment.appendChild(buildMentionChip(`Page: ${pageContext.title}`, true));

    if (currentVisibleSections.length) {
      fragment.appendChild(buildMentionChip(
        `Visible: ${currentVisibleSections.slice(0, 2).map((section) => section.title).join(' / ')}`,
        true
      ));
    }

    resolveMentionReferences(extractMentionLabels(userInput.value)).forEach((mention) => {
      fragment.appendChild(buildMentionChip(`Mentioned: ${mention.title}`, false));
    });

    activeMentionsContainer.innerHTML = '';
    activeMentionsContainer.appendChild(fragment);
  }

  function buildMentionChip(text, isAuto) {
    const chip = document.createElement('span');
    chip.className = `ai-assistant-mention-chip${isAuto ? ' ai-assistant-mention-chip--auto' : ''}`;
    chip.innerHTML = `${ICONS.mention}<span>${escapeHtml(text)}</span>`;
    return chip;
  }

  function autoResizeComposer() {
    userInput.style.height = 'auto';
    userInput.style.height = `${Math.min(userInput.scrollHeight, 180)}px`;
  }

  function scheduleVisibleSectionRefresh() {
    if (visibleSectionsTicking) {
      return;
    }

    visibleSectionsTicking = true;
    window.requestAnimationFrame(() => {
      updateVisibleSections();
      visibleSectionsTicking = false;
    });
  }

  function updateVisibleSections() {
    if (!pageContext.sections.length) {
      currentVisibleSections = [];
      updateContextBar();
      renderActiveMentionChips();
      return;
    }

    const ranked = pageContext.sections
      .map((section) => {
        const rect = section.element.getBoundingClientRect();
        return {
          section,
          inViewport: rect.bottom > 96 && rect.top < window.innerHeight * 0.7,
          distance: Math.abs(rect.top - 140)
        };
      })
      .filter((item) => item.inViewport)
      .sort((left, right) => left.distance - right.distance);

    let nextVisible = ranked.slice(0, 3).map((item) => item.section);
    if (!nextVisible.length) {
      const fallback = [...pageContext.sections]
        .reverse()
        .find((section) => section.element.getBoundingClientRect().top <= 140) || pageContext.sections[0];
      nextVisible = fallback ? [fallback] : [];
    }

    const currentSignature = currentVisibleSections.map((section) => section.id).join('|');
    const nextSignature = nextVisible.map((section) => section.id).join('|');
    if (currentSignature === nextSignature) {
      return;
    }

    currentVisibleSections = nextVisible;
    updateContextBar();
    renderActiveMentionChips();
  }

  function updateContextBar() {
    if (pageContextElement) {
      pageContextElement.textContent = pageContext.title || 'Current page';
    }

    if (visibleContextElement) {
      visibleContextElement.textContent = currentVisibleSections.length
        ? currentVisibleSections.slice(0, 2).map((section) => section.title).join(' / ')
        : 'Current section';
    }
  }

  async function loadDocsIndex() {
    try {
      const response = await fetch(buildPath('/search.json'), {
        headers: {
          'Accept': 'application/json'
        }
      });
      const rawText = await response.text();

      if (!response.ok || !rawText.trim() || rawText.includes('{%') || rawText.startsWith('---')) {
        throw new Error('search.json is not available in rendered form');
      }

      const parsed = JSON.parse(rawText);
      docsIndex = parsed.map((item) => ({
        title: cleanText(item.title),
        url: normalizeUrl(item.url),
        description: cleanText(item.description),
        content: cleanText(item.content)
      }));
    } catch (error) {
      console.warn('AI Assistant: search index unavailable, using current page only.', error);
      docsIndex = [];
    }
  }

  function buildPath(path) {
    const base = window.site && window.site.baseurl ? String(window.site.baseurl).replace(/\/$/, '') : '';
    const normalized = path.startsWith('/') ? path : `/${path}`;
    return `${base}${normalized}`;
  }

  async function sendMessage() {
    const rawMessage = userInput.value.trim();
    if (!rawMessage || isLoading) {
      return;
    }

    if (/^\/(?:clear|new)\s*$/i.test(rawMessage)) {
      performChatReset();
      return;
    }

    if (!CONFIG.apiEndpoint) {
      addMessage('Configure `ai_assistant_worker_url` before enabling the documentation chat service.', 'assistant');
      return;
    }

    const mentionReferences = resolveMentionReferences(extractMentionLabels(rawMessage));
    const userQuestion = stripMentionLabels(rawMessage).trim();
    if (!userQuestion) {
      addMessage(getLocalizedCopy(rawMessage, {
        en: 'Add a documentation question after the mention. Example: `@[Current page] What does this section mean?`',
        pt: 'Adicione uma pergunta sobre a documentacao depois da mencao. Exemplo: `@[Current page] O que esta secao significa?`'
      }), 'assistant');
      userInput.focus();
      return;
    }

    const docsMatches = retrieveRelevantDocs(userQuestion, mentionReferences, CONFIG.maxDocsMatches);
    if (isOutOfScopeQuestion(userQuestion, mentionReferences, docsMatches)) {
      addMessage(getLocalizedCopy(userQuestion, {
        en: 'This assistant is limited to Schema-Validator documentation. Ask about validation keywords, configuration, examples, architecture, or mention a section with `@`.',
        pt: 'Este assistente e limitado a documentacao do Schema-Validator. Pergunte sobre keywords de validacao, configuracao, exemplos, arquitetura ou mencione uma secao com `@`.'
      }), 'assistant');
      userInput.value = '';
      autoResizeComposer();
      closeMentionMenu();
      renderActiveMentionChips();
      userInput.focus();
      return;
    }

    const submission = {
      rawMessage,
      userQuestion,
      mentionReferences,
      docsMatches
    };

    addMessage(rawMessage, 'user');
    userInput.value = '';
    autoResizeComposer();
    closeMentionMenu();
    renderActiveMentionChips();
    hideChallenge();

    await dispatchChatSubmission(submission, { appendUserMessage: false });
  }

  async function dispatchChatSubmission(submission, options) {
    const locale = detectQuestionLanguage(submission.userQuestion);
    isLoading = true;
    sendButton.disabled = true;
    updateResetControlState();
    const loadingMessage = addMessage(getLocalizedCopy(submission.userQuestion, {
      en: 'Grounding answer in Schema-Validator docs...',
      pt: 'Buscando contexto na documentacao do Schema-Validator...'
    }), 'assistant', true);

    try {
      const response = await fetch(CONFIG.apiEndpoint, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json',
          ...(sessionToken ? { 'X-Chat-Session': sessionToken } : {})
        },
        body: JSON.stringify(buildChatRequestPayload(submission, options && options.challengeToken)),
        mode: 'cors',
        credentials: 'include'
      });

      persistSessionTokenFromResponse(response);

      const data = await response.json().catch(() => ({}));
      if (!response.ok) {
        const handled = await handleChatErrorResponse(response, data, submission, loadingMessage);
        if (handled) {
          return;
        }
        throw new Error(`API error: ${response.status}`);
      }

      const assistantMessage = data && data.reply
        ? data.reply
        : data && data.choices && data.choices[0] && data.choices[0].message
          ? data.choices[0].message.content
          : '';

      if (!assistantMessage) {
        throw new Error('Invalid response format');
      }

      loadingMessage.remove();
      addMessage(assistantMessage, 'assistant');
      rememberConversationTurn(submission.userQuestion, assistantMessage);
      resetTurnstileState();
    } catch (error) {
      console.error('AI Assistant Error:', error);
      loadingMessage.remove();

      let errorMessage = getLocalizedCopy(submission.userQuestion, {
        en: 'Sorry, I could not reach the Schema-Validator assistant service.',
        pt: 'Nao consegui acessar o servico do assistente do Schema-Validator.'
      });
      if (error.message.includes('Failed to fetch') || error.message.includes('NETWORK_ERR')) {
        errorMessage += ` ${getServiceEndpointHint(locale)}`;
      } else {
        errorMessage += ` ${escapeInlineText(error.message)}.`;
      }

      addMessage(errorMessage, 'assistant');
    } finally {
      turnstileState.retryInFlight = false;
      isLoading = false;
      sendButton.disabled = false;
      updateResetControlState();
      userInput.focus();
    }
  }

  function buildChatRequestPayload(submission, challengeToken) {
    return {
      question: submission.userQuestion,
      locale: detectQuestionLanguage(submission.userQuestion),
      challengeToken: challengeToken || turnstileState.token || '',
      history: conversationHistory.slice(-CONFIG.maxHistoryMessages),
      context: {
        pageUrl: pageContext ? pageContext.url : '',
        pageTitle: pageContext ? pageContext.title : '',
        pageDescription: pageContext ? pageContext.description : '',
        pageExcerpt: pageContext ? pageContext.pageExcerpt : '',
        breadcrumbs: pageContext ? pageContext.breadcrumbs : [],
        visibleSections: currentVisibleSections.slice(0, 3).map((section) => ({
          id: section.id,
          title: section.title,
          excerpt: section.excerpt
        })),
        mentionedSections: submission.mentionReferences.slice(0, 4).map((section) => ({
          id: section.id,
          title: section.title,
          excerpt: section.excerpt
        }))
      }
    };
  }

  async function handleChatErrorResponse(response, data, submission, loadingMessage) {
    const error = data && data.error ? data.error : {};
    const locale = detectQuestionLanguage(submission.userQuestion);

    if (response.status === 403 && error.code === 'challenge_required') {
      loadingMessage.remove();
      turnstileState.pendingSubmission = submission;

      const verificationMessage = error.message || getLocalizedCopy(submission.userQuestion, {
        en: 'Verification is required before continuing.',
        pt: 'Uma verificacao e necessaria antes de continuar.'
      });
      addMessage(verificationMessage, 'assistant');

      const challengeReady = await showChallenge(verificationMessage);
      if (!challengeReady) {
        addMessage(getLocalizedCopy(submission.userQuestion, {
          en: 'Turnstile is not configured in the docs yet. Configure `ai_assistant_turnstile_site_key` before enforcing challenge mode.',
          pt: 'O Turnstile ainda nao esta configurado na documentacao. Configure `ai_assistant_turnstile_site_key` antes de exigir verificacao.'
        }), 'assistant');
      }
      return true;
    }

    if (response.status === 429) {
      loadingMessage.remove();
      const retryAfter = error.retryAfter || response.headers.get('Retry-After');
      const message = retryAfter
        ? `${error.message || localizedRateLimitCopy(locale)} ${getLocalizedCopy(submission.userQuestion, {
          en: `Retry after ${retryAfter} seconds.`,
          pt: `Tente novamente em ${retryAfter} segundos.`
        })}`
        : (error.message || localizedRateLimitCopy(locale));
      addMessage(message, 'assistant');
      return true;
    }

    if (error && error.message) {
      throw new Error(error.message);
    }

    return false;
  }

  function hideChallenge() {
    challengeContainer.hidden = true;
    challengeCopy.textContent = 'Complete a quick verification to continue using the chat.';
  }

  async function showChallenge(message) {
    challengeCopy.textContent = message || 'Complete a quick verification to continue using the chat.';
    challengeContainer.hidden = false;

    if (!CONFIG.turnstileSiteKey) {
      return false;
    }

    const turnstileApi = await loadTurnstileScript();
    if (!turnstileApi) {
      return false;
    }

    if (turnstileState.widgetId !== null) {
      turnstileApi.reset(turnstileState.widgetId);
      return true;
    }

    turnstileState.widgetId = turnstileApi.render(turnstileContainer, {
      sitekey: CONFIG.turnstileSiteKey,
      theme: 'auto',
      callback: handleTurnstileSolved,
      'expired-callback': handleTurnstileExpired,
      'error-callback': handleTurnstileError
    });

    return true;
  }

  function resetTurnstileState() {
    turnstileState.token = '';
    turnstileState.pendingSubmission = null;
    turnstileState.retryInFlight = false;
    if (window.turnstile && turnstileState.widgetId !== null) {
      window.turnstile.reset(turnstileState.widgetId);
    }
    hideChallenge();
  }

  async function loadTurnstileScript() {
    if (window.turnstile) {
      return window.turnstile;
    }

    if (turnstileState.loadingPromise) {
      return turnstileState.loadingPromise;
    }

    turnstileState.loadingPromise = new Promise((resolve) => {
      const script = document.createElement('script');
      script.src = 'https://challenges.cloudflare.com/turnstile/v0/api.js?render=explicit';
      script.async = true;
      script.defer = true;
      script.onload = () => resolve(window.turnstile || null);
      script.onerror = () => resolve(null);
      document.head.appendChild(script);
    });

    return turnstileState.loadingPromise;
  }

  async function handleTurnstileSolved(token) {
    turnstileState.token = token || '';
    if (!turnstileState.pendingSubmission || turnstileState.retryInFlight) {
      return;
    }

    turnstileState.retryInFlight = true;
    const pending = turnstileState.pendingSubmission;
    await dispatchChatSubmission(pending, { challengeToken: turnstileState.token, appendUserMessage: false });
  }

  function handleTurnstileExpired() {
    turnstileState.token = '';
  }

  function handleTurnstileError() {
    turnstileState.token = '';
    addMessage(getLocalizedCopy(userInput.value || 'turnstile', {
      en: 'The verification widget could not be loaded. Try again in a moment.',
      pt: 'O widget de verificacao nao conseguiu carregar. Tente novamente em instantes.'
    }), 'assistant');
  }

  function localizedRateLimitCopy(locale) {
    return locale === 'pt'
      ? 'O chat esta em modo de protecao temporaria.'
      : 'The chat is temporarily protected.';
  }

  function getServiceEndpointHint(locale) {
    if (CONFIG.apiEndpoint.indexOf('localhost:3001') !== -1) {
      return locale === 'pt'
        ? 'Verifique se o proxy local esta em execucao: `node docs/pages/assets/js/cors-proxy.js`.'
        : 'Make sure the local proxy is running: `node docs/pages/assets/js/cors-proxy.js`.';
    }

    return locale === 'pt'
      ? 'Verifique se `ai_assistant_worker_url` aponta para o Worker publicado e se o CORS permite este dominio.'
      : 'Make sure `ai_assistant_worker_url` points to the deployed Worker and CORS allows this origin.';
  }

  function loadStoredSessionToken() {
    try {
      const storage = window.sessionStorage;
      const current = storage.getItem(PERSISTENCE.sessionTokenKey) || '';
      if (current) {
        return current;
      }

      const legacy = window.localStorage.getItem(PERSISTENCE.legacySessionTokenKey) || '';
      if (legacy) {
        storage.setItem(PERSISTENCE.sessionTokenKey, legacy);
        window.localStorage.removeItem(PERSISTENCE.legacySessionTokenKey);
        return legacy;
      }

      return '';
    } catch (error) {
      return '';
    }
  }

  function persistSessionTokenFromResponse(response) {
    const nextToken = response.headers.get('X-Chat-Session');
    if (!nextToken) {
      return;
    }

    sessionToken = nextToken;
    try {
      window.sessionStorage.setItem(PERSISTENCE.sessionTokenKey, nextToken);
      window.localStorage.removeItem(PERSISTENCE.legacySessionTokenKey);
    } catch (error) {
      // Ignore storage errors and continue with in-memory session state.
    }

    schedulePersistState();
  }

  function buildPayloadMessages(userQuestion, mentionReferences, docsMatches) {
    return [
      { role: 'system', content: buildBaseSystemPrompt() },
      { role: 'system', content: buildDynamicContext(userQuestion, mentionReferences, docsMatches) },
      ...conversationHistory.slice(-CONFIG.maxHistoryMessages),
      { role: 'user', content: `Documentation question: ${userQuestion}` }
    ];
  }

  function buildBaseSystemPrompt() {
    return [
      'You are the Schema-Validator documentation assistant.',
      'Answer only with grounded Schema-Validator documentation context.',
      'Do not answer unrelated general questions, requests outside the project, or topics unsupported by the provided docs context.',
      'If the documentation context is insufficient, say so clearly and ask the user to mention a page section or ask about Schema-Validator docs.',
      'Keep answers concise, technical, and grounded in the documentation.',
      'Respond in the same language as the user when possible.',
      'End answers with a "References" list naming the pages or sections you used.'
    ].join(' ');
  }

  function buildDynamicContext(userQuestion, mentionReferences, docsMatches) {
    const visibleSections = currentVisibleSections.slice(0, 3);
    const sectionLines = dedupeById([...mentionReferences, ...visibleSections], (item) => item.id || item.title)
      .slice(0, 4)
      .map((section) => `- ${section.title}: ${section.excerpt || 'No excerpt available.'}`)
      .join('\n') || '- None';

    const docsLines = docsMatches
      .map((match, index) => `${index + 1}. ${match.title} (${match.url})\n   Description: ${match.description || 'No description.'}\n   Excerpt: ${match.excerpt || 'No excerpt.'}`)
      .join('\n') || 'None';

    return [
      '[Schema-Validator documentation context]',
      `Current page: ${pageContext.title}`,
      `Current URL: ${pageContext.url}`,
      `Breadcrumbs: ${pageContext.breadcrumbs.join(' > ') || 'Docs'}`,
      `Page description: ${pageContext.description || 'No page description available.'}`,
      `Current page excerpt: ${pageContext.pageExcerpt || 'No page excerpt available.'}`,
      `Visible sections:\n${visibleSections.map((section) => `- ${section.title}: ${section.excerpt || 'No excerpt available.'}`).join('\n') || '- None'}`,
      `Mentioned sections:\n${sectionLines}`,
      `Related documentation matches:\n${docsLines}`,
      '[Question prefix]',
      buildPrefixedQuestionContext(userQuestion, mentionReferences, docsMatches),
      '[Restrictions]',
      '- Remain inside Schema-Validator documentation scope only.',
      '- If the question is unrelated, refuse politely.',
      '- Prefer examples and terminology that appear in the grounded snippets.'
    ].join('\n');
  }

  function buildPrefixedQuestionContext(userQuestion, mentionReferences, docsMatches) {
    const mentionTitles = mentionReferences.length
      ? mentionReferences.map((mention) => mention.title).join(', ')
      : 'None';
    const relatedTitles = docsMatches.length
      ? docsMatches.slice(0, 3).map((match) => match.title).join(', ')
      : 'None';

    return `Question="${userQuestion}" | CurrentPage="${pageContext.title}" | Mentioned="${mentionTitles}" | Visible="${currentVisibleSections.map((section) => section.title).join(', ') || 'None'}" | RelatedDocs="${relatedTitles}"`;
  }

  function retrieveRelevantDocs(userQuestion, mentionReferences, limit) {
    const queryTokens = tokenizeText(`${userQuestion} ${mentionReferences.map((mention) => mention.title).join(' ')}`);
    return dedupeById([buildCurrentPageDocument()].concat(docsIndex), (entry) => entry.url || entry.title)
      .map((entry) => scoreDocument(entry, queryTokens))
      .filter((entry) => entry.score > 0)
      .sort((left, right) => right.score - left.score)
      .slice(0, limit);
  }

  function buildCurrentPageDocument() {
    return {
      title: pageContext.title,
      url: pageContext.url,
      description: pageContext.description,
      content: cleanText(pageContext.articleBody ? pageContext.articleBody.textContent : pageContext.pageExcerpt)
    };
  }

  function scoreDocument(entry, queryTokens) {
    let score = 0;
    [...new Set(queryTokens)].forEach((token) => {
      if (normalizeText(entry.title).includes(token)) {
        score += 6;
      }

      if (normalizeText(entry.description).includes(token)) {
        score += 3;
      }

      if (normalizeText(entry.content).includes(token)) {
        score += 1;
      }
    });

    if (entry.url === pageContext.url) {
      score += 1;
    }

    return {
      ...entry,
      score,
      excerpt: buildExcerpt(entry.content || entry.description, queryTokens)
    };
  }

  function buildExcerpt(text, queryTokens) {
    const clean = cleanText(text);
    if (!clean) {
      return '';
    }

    const normalized = normalizeText(clean);
    const token = queryTokens.find((item) => normalized.includes(item));
    if (!token) {
      return excerptText(clean, 220);
    }

    const index = normalized.indexOf(token);
    return excerptText(clean.slice(Math.max(0, index - 90), Math.min(clean.length, index + 180)), 240);
  }

  function isOutOfScopeQuestion(userQuestion, mentionReferences, docsMatches) {
    const normalizedQuestion = normalizeText(userQuestion);
    const questionTokens = tokenizeText(userQuestion);
    const contextualHints = ['this', 'that', 'current page', 'section', 'page', 'isso', 'esta', 'secao', 'pagina'];

    if (!questionTokens.length || mentionReferences.length || questionTokens.length <= 2) {
      return false;
    }

    if (contextualHints.some((hint) => normalizedQuestion.includes(hint))) {
      return false;
    }

    if (docsMatches.length && docsMatches[0].score >= 4) {
      return false;
    }

    if (DOMAIN_HINTS.some((hint) => normalizedQuestion.includes(normalizeText(hint)))) {
      return false;
    }

    const nearbyContext = normalizeText(
      `${pageContext.title} ${pageContext.description} ${currentVisibleSections.map((section) => section.title).join(' ')}`
    );
    return !questionTokens.some((token) => nearbyContext.includes(token));
  }

  function rememberConversationTurn(userQuestion, assistantMessage) {
    conversationHistory.push(
      { role: 'user', content: userQuestion },
      { role: 'assistant', content: assistantMessage }
    );

    if (conversationHistory.length > CONFIG.maxHistoryMessages * 2) {
      conversationHistory = conversationHistory.slice(-CONFIG.maxHistoryMessages * 2);
    }

    updateResetControlState();
    schedulePersistState();
  }

  function addMessage(content, type, isMessageLoading, options) {
    const settings = options || {};
    const messageDiv = document.createElement('div');
    const baseClass = type === 'user' ? 'user-message' : 'ai-message';
    messageDiv.className = `${baseClass}${isMessageLoading ? ' loading' : ''}`;
    messageDiv.innerHTML = parseMarkdown(content);

    threadContainer.appendChild(messageDiv);
    enhanceCodeBlocks(messageDiv);
    messagesContainer.scrollTop = messagesContainer.scrollHeight;

    if (!isMessageLoading && !settings.skipPersist) {
      renderedMessages.push({
        role: type === 'user' ? 'user' : 'assistant',
        content: sanitizeStoredText(content, PERSISTENCE.maxMessageChars)
      });

      if (renderedMessages.length > PERSISTENCE.maxRenderedMessages) {
        renderedMessages = renderedMessages.slice(-PERSISTENCE.maxRenderedMessages);
      }

      schedulePersistState();
    }

    updateResetControlState();

    return messageDiv;
  }

  function restorePersistedState() {
    const persisted = readPersistedState();
    if (!persisted) {
      setChatOpenState(false, { persist: false });
      return;
    }

    conversationHistory = sanitizePersistedConversationHistory(persisted.conversationHistory);
    renderedMessages = sanitizePersistedRenderedMessages(persisted.renderedMessages);
    sessionToken = persisted.sessionToken || sessionToken;
    lastNormalRect = sanitizePersistedRect(persisted.lastNormalRect);
    isMaximized = Boolean(persisted.isMaximized);
    chatContainer.classList.toggle('is-maximized', isMaximized);

    if (persisted.draft) {
      userInput.value = sanitizeStoredText(persisted.draft, PERSISTENCE.maxDraftChars);
      renderActiveMentionChips();
    }

    renderPersistedMessages();
    setChatOpenState(Boolean(persisted.isOpen), { focus: false, persist: false });
    updateResetControlState();
  }

  function renderPersistedMessages() {
    Array.from(threadContainer.children).forEach((child) => {
      if (!child.classList.contains('ai-message--intro')) {
        child.remove();
      }
    });

    renderedMessages.forEach((entry) => {
      addMessage(entry.content, entry.role, false, { skipPersist: true });
    });

    messagesContainer.scrollTop = messagesContainer.scrollHeight;
  }

  function schedulePersistState() {
    if (persistTimer) {
      window.clearTimeout(persistTimer);
    }

    persistTimer = window.setTimeout(() => {
      persistTimer = null;
      persistAssistantState();
    }, PERSISTENCE.debounceMs);
  }

  function persistAssistantState() {
    if (persistTimer) {
      window.clearTimeout(persistTimer);
      persistTimer = null;
    }

    try {
      window.sessionStorage.setItem(PERSISTENCE.stateKey, JSON.stringify({
        version: 2,
        updatedAt: Date.now(),
        isOpen,
        isMaximized,
        lastNormalRect: sanitizePersistedRect(lastNormalRect),
        sessionToken: sessionToken || '',
        draft: sanitizeStoredText(userInput.value, PERSISTENCE.maxDraftChars),
        conversationHistory: sanitizePersistedConversationHistory(conversationHistory),
        renderedMessages: sanitizePersistedRenderedMessages(renderedMessages)
      }));
    } catch (error) {
      // Ignore storage errors and keep runtime state only.
    }
  }

  function readPersistedState() {
    try {
      const raw = window.sessionStorage.getItem(PERSISTENCE.stateKey);
      if (!raw) {
        return null;
      }

      const parsed = JSON.parse(raw);
      if (!parsed || parsed.version !== 2) {
        window.sessionStorage.removeItem(PERSISTENCE.stateKey);
        return null;
      }

      if (!parsed.updatedAt || Date.now() - parsed.updatedAt > PERSISTENCE.maxAgeMs) {
        window.sessionStorage.removeItem(PERSISTENCE.stateKey);
        return null;
      }

      return parsed;
    } catch (error) {
      return null;
    }
  }

  function sanitizePersistedConversationHistory(entries) {
    if (!Array.isArray(entries)) {
      return [];
    }

    return entries
      .filter((entry) => entry && (entry.role === 'user' || entry.role === 'assistant'))
      .slice(-CONFIG.maxHistoryMessages * 2)
      .map((entry) => ({
        role: entry.role,
        content: sanitizeStoredText(entry.content, PERSISTENCE.maxMessageChars)
      }))
      .filter((entry) => entry.content);
  }

  function sanitizePersistedRenderedMessages(entries) {
    if (!Array.isArray(entries)) {
      return [];
    }

    return entries
      .filter((entry) => entry && (entry.role === 'user' || entry.role === 'assistant'))
      .slice(-PERSISTENCE.maxRenderedMessages)
      .map((entry) => ({
        role: entry.role,
        content: sanitizeStoredText(entry.content, PERSISTENCE.maxMessageChars)
      }))
      .filter((entry) => entry.content);
  }

  function sanitizePersistedRect(rect) {
    if (!rect || typeof rect !== 'object') {
      return null;
    }

    const left = Number(rect.left);
    const top = Number(rect.top);
    const width = Number(rect.width);
    const height = Number(rect.height);
    if (![left, top, width, height].every((value) => Number.isFinite(value))) {
      return null;
    }

    return { left, top, width, height };
  }

  function sanitizeStoredText(value, maxLength) {
    const normalized = String(value || '')
      .replace(/\u0000/g, '')
      .replace(/\r\n?/g, '\n');

    return normalized.length > maxLength
      ? normalized.slice(0, maxLength)
      : normalized;
  }

  function parseMarkdown(text) {
    const container = document.createElement('div');
    const lines = String(text || '').replace(/\r\n?/g, '\n').split('\n');
    let index = 0;

    while (index < lines.length) {
      const line = lines[index];

      if (!line.trim()) {
        index += 1;
        continue;
      }

      if (isCodeFence(line)) {
        index = appendCodeBlock(container, lines, index);
        continue;
      }

      if (isHorizontalRule(line)) {
        container.appendChild(document.createElement('hr'));
        index += 1;
        continue;
      }

      if (isHeading(line)) {
        appendHeading(container, line);
        index += 1;
        continue;
      }

      if (isTableStart(lines, index)) {
        index = appendTable(container, lines, index);
        continue;
      }

      if (isUnorderedListItem(line)) {
        index = appendList(container, lines, index, false);
        continue;
      }

      if (isOrderedListItem(line)) {
        index = appendList(container, lines, index, true);
        continue;
      }

      if (isBlockquote(line)) {
        index = appendBlockquote(container, lines, index);
        continue;
      }

      index = appendParagraph(container, lines, index);
    }

    return container.innerHTML;
  }

  function appendHeading(container, line) {
    const match = line.match(/^(#{1,4})\s+(.+)$/);
    if (!match) {
      return;
    }

    const level = Math.min(match[1].length + 1, 4);
    const heading = document.createElement(`h${level}`);
    heading.innerHTML = parseInline(match[2].trim());
    container.appendChild(heading);
  }

  function appendCodeBlock(container, lines, startIndex) {
    const match = lines[startIndex].trim().match(/^```([\w#+-]+)?\s*$/);
    const language = normalizeLanguage(match && match[1] ? match[1].toLowerCase() : '');
    const codeLines = [];
    let index = startIndex + 1;

    while (index < lines.length && !/^```/.test(lines[index].trim())) {
      codeLines.push(lines[index]);
      index += 1;
    }

    if (index < lines.length) {
      index += 1;
    }

    const pre = document.createElement('pre');
    const code = document.createElement('code');
    code.className = `language-${language}`;
    code.textContent = codeLines.join('\n');
    pre.appendChild(code);
    container.appendChild(pre);

    return index;
  }

  function appendList(container, lines, startIndex, ordered) {
    const list = document.createElement(ordered ? 'ol' : 'ul');
    const pattern = ordered
      ? /^\s*\d+\.\s+(.+)$/
      : /^\s*[-*+]\s+(.+)$/;
    let index = startIndex;

    while (index < lines.length) {
      const match = lines[index].match(pattern);
      if (!match) {
        break;
      }

      const item = document.createElement('li');
      item.innerHTML = parseInline(match[1].trim());
      list.appendChild(item);
      index += 1;
    }

    container.appendChild(list);
    return index;
  }

  function appendTable(container, lines, startIndex) {
    const rows = [];
    let index = startIndex;

    while (index < lines.length && isPipeRow(lines[index])) {
      rows.push(lines[index].trim());
      index += 1;
    }

    const hasSeparator = rows.length > 1 && isTableSeparator(rows[1]);
    const headerCells = splitTableRow(rows[0]);
    const bodyRows = rows.slice(hasSeparator ? 2 : 1);
    const columnCount = Math.max(
      headerCells.length,
      ...bodyRows.map((row) => splitTableRow(row).length),
      0
    );

    const wrapper = document.createElement('div');
    wrapper.className = 'ai-assistant-table-wrap';

    const table = document.createElement('table');
    const thead = document.createElement('thead');
    const headerRow = document.createElement('tr');

    normalizeTableCells(headerCells, columnCount).forEach((cell) => {
      const th = document.createElement('th');
      th.innerHTML = parseInline(cell);
      headerRow.appendChild(th);
    });

    thead.appendChild(headerRow);
    table.appendChild(thead);

    if (bodyRows.length) {
      const tbody = document.createElement('tbody');

      bodyRows.forEach((row) => {
        const tr = document.createElement('tr');

        normalizeTableCells(splitTableRow(row), columnCount).forEach((cell) => {
          const td = document.createElement('td');
          td.innerHTML = parseInline(cell);
          tr.appendChild(td);
        });

        tbody.appendChild(tr);
      });

      table.appendChild(tbody);
    }

    wrapper.appendChild(table);
    container.appendChild(wrapper);

    return index;
  }

  function appendBlockquote(container, lines, startIndex) {
    const quoteLines = [];
    let index = startIndex;

    while (index < lines.length && isBlockquote(lines[index])) {
      quoteLines.push(lines[index].replace(/^\s*>\s?/, ''));
      index += 1;
    }

    const blockquote = document.createElement('blockquote');
    blockquote.innerHTML = parseMarkdown(quoteLines.join('\n'));
    container.appendChild(blockquote);

    return index;
  }

  function appendParagraph(container, lines, startIndex) {
    const paragraphLines = [];
    let index = startIndex;

    while (index < lines.length) {
      const line = lines[index];

      if (!line.trim()) {
        break;
      }

      if (index !== startIndex && isBlockStart(lines, index)) {
        break;
      }

      paragraphLines.push(line.trim());
      index += 1;
    }

    const paragraph = document.createElement('p');
    paragraph.innerHTML = paragraphLines.map(parseInline).join('<br>');
    container.appendChild(paragraph);

    return index;
  }

  function parseInline(text) {
    const codeSegments = [];
    const markdownLinkSegments = [];
    const withCodeTokens = String(text || '').replace(/`([^`]+)`/g, function (_, code) {
      const token = `@@AI_CODE_${codeSegments.length}@@`;
      codeSegments.push(`<code>${escapeHtml(code)}</code>`);
      return token;
    });

    const withLinkTokens = withCodeTokens.replace(
      /\[([^\]]+)\]\((https?:\/\/[^\s)]+)\)/g,
      function (_, label, url) {
        const token = `@@AI_LINK_${markdownLinkSegments.length}@@`;
        markdownLinkSegments.push(buildLinkHtml(url, escapeHtml(label)));
        return token;
      }
    );

    let html = escapeHtml(withLinkTokens);
    html = linkifyPlainUrls(html);
    html = html.replace(/\*\*([^*]+)\*\*/g, '<strong>$1</strong>');
    html = html.replace(/\*([^*\n]+)\*/g, '<em>$1</em>');

    markdownLinkSegments.forEach(function (segment, index) {
      html = html.replace(`@@AI_LINK_${index}@@`, segment);
    });

    codeSegments.forEach(function (segment, index) {
      html = html.replace(`@@AI_CODE_${index}@@`, segment);
    });

    return html;
  }

  function linkifyPlainUrls(text) {
    return String(text || '').replace(/\bhttps?:\/\/[^\s<]+/g, function (match) {
      const split = splitTrailingUrlPunctuation(match);
      return `${buildLinkHtml(split.url, escapeHtml(split.url))}${split.trailing}`;
    });
  }

  function splitTrailingUrlPunctuation(url) {
    let core = String(url || '');
    let trailing = '';

    while (core) {
      const lastChar = core.charAt(core.length - 1);
      if (/[.,!?;:]/.test(lastChar)) {
        trailing = lastChar + trailing;
        core = core.slice(0, -1);
        continue;
      }

      if (lastChar === ')' && hasUnmatchedClosingParen(core)) {
        trailing = lastChar + trailing;
        core = core.slice(0, -1);
        continue;
      }

      break;
    }

    return {
      url: core,
      trailing
    };
  }

  function hasUnmatchedClosingParen(value) {
    let balance = 0;

    for (let index = 0; index < value.length; index += 1) {
      const char = value.charAt(index);
      if (char === '(') {
        balance += 1;
      } else if (char === ')') {
        if (balance === 0) {
          return true;
        }
        balance -= 1;
      }
    }

    return balance < 0;
  }

  function buildLinkHtml(url, label) {
    const normalizedHref = resolveChatLinkHref(url);
    const safeHref = escapeHtml(normalizedHref.href);
    const safeLabel = label || safeHref;

    if (normalizedHref.isInternalDocumentationLink) {
      return `<a href="${safeHref}" data-chat-link="internal">${safeLabel}</a>`;
    }

    return `<a href="${safeHref}" target="_blank" rel="noreferrer noopener" data-chat-link="external">${safeLabel}</a>`;
  }

  function resolveChatLinkHref(url) {
    const rawUrl = String(url || '').trim();
    if (!rawUrl) {
      return {
        href: '#',
        isInternalDocumentationLink: false
      };
    }

    try {
      const parsed = new URL(rawUrl, window.location.href);
      if (isDocumentationLink(parsed)) {
        return {
          href: rebaseDocumentationUrl(parsed),
          isInternalDocumentationLink: true
        };
      }

      return {
        href: parsed.toString(),
        isInternalDocumentationLink: false
      };
    } catch (error) {
      return {
        href: rawUrl,
        isInternalDocumentationLink: false
      };
    }
  }

  function isDocumentationLink(parsedUrl) {
    if (!parsedUrl || !/^https?:$/.test(parsedUrl.protocol)) {
      return false;
    }

    const docsPath = getDocumentationBasePath();
    const hostname = parsedUrl.hostname.toLowerCase();
    const currentHostname = window.location.hostname.toLowerCase();
    const knownDocsHostnames = new Set([
      currentHostname,
      'maiconjh.github.io'
    ]);

    if (!knownDocsHostnames.has(hostname)) {
      return false;
    }

    const pathname = parsedUrl.pathname.replace(/\/index\.html$/, '/');
    return docsPath === '/'
      ? pathname.startsWith('/')
      : pathname === docsPath || pathname.startsWith(`${docsPath}/`);
  }

  function rebaseDocumentationUrl(parsedUrl) {
    const docsPath = getDocumentationBasePath();
    const normalizedPathname = parsedUrl.pathname.replace(/\/index\.html$/, '/');
    let relativePath = normalizedPathname;

    if (docsPath !== '/' && relativePath.startsWith(docsPath)) {
      relativePath = relativePath.slice(docsPath.length) || '/';
    }

    const base = window.location.origin + (docsPath === '/' ? '' : docsPath);
    return `${base}${relativePath.startsWith('/') ? relativePath : `/${relativePath}`}${parsedUrl.search}${parsedUrl.hash}`;
  }

  function getDocumentationBasePath() {
    const configuredBase = window.site && window.site.baseurl
      ? String(window.site.baseurl).trim()
      : '';

    if (configuredBase) {
      return configuredBase === '/' ? '/' : configuredBase.replace(/\/$/, '');
    }

    const path = window.location.pathname || '/';
    const knownSegment = '/Schema-Validator';
    const segmentIndex = path.indexOf(`${knownSegment}/`);
    if (segmentIndex >= 0) {
      return knownSegment;
    }

    return '/';
  }

  function enhanceCodeBlocks(container) {
    Array.from(container.querySelectorAll('pre')).forEach((pre) => {
      if (pre.closest('.code-block')) {
        return;
      }

      const code = pre.querySelector('code');
      const rawCode = code ? code.textContent : pre.textContent;
      const language = normalizeLanguage(extractLanguage(code, pre));
      const block = createInteractiveCodeBlock(rawCode, language);
      pre.replaceWith(block);
    });
  }

  function createInteractiveCodeBlock(rawCode, language) {
    const block = document.createElement('div');
    block.className = 'code-block';
    block.dataset.lang = language;
    block.__rawCode = rawCode;
    block.__language = language;

    const headerElement = document.createElement('div');
    headerElement.className = 'code-block-header';

    const languagePill = document.createElement('span');
    languagePill.className = 'code-block-lang';
    languagePill.innerHTML = `${ICONS.scope}<span>${formatLanguageLabel(language)}</span>`;

    const actions = document.createElement('div');
    actions.className = 'ai-code-block-actions';
    actions.appendChild(createCodeActionButton('code-block-copy', 'Copy', ICONS.copy));
    actions.appendChild(createCodeActionButton('code-block-edit', 'Edit', ICONS.edit));

    headerElement.appendChild(languagePill);
    headerElement.appendChild(actions);

    const pre = document.createElement('pre');
    pre.className = 'highlight';

    const code = document.createElement('code');
    code.className = `language-${language}`;
    code.innerHTML = highlightCode(rawCode, language);
    pre.appendChild(code);

    const editorPanel = document.createElement('div');
    editorPanel.className = 'code-block-editor-panel';

    const editor = document.createElement('textarea');
    editor.className = 'code-block-editor';
    editor.spellcheck = false;
    editor.value = rawCode;

    const editorFooter = document.createElement('div');
    editorFooter.className = 'code-block-editor-footer';

    const editorHelp = document.createElement('span');
    editorHelp.className = 'code-block-editor-help';
    editorHelp.textContent = 'Tab indents, Ctrl/Cmd+Enter saves, Esc cancels.';

    const editorActions = document.createElement('div');
    editorActions.className = 'code-block-editor-actions';
    editorActions.appendChild(createCodeActionButton('code-block-save', 'Save', ICONS.save));
    editorActions.appendChild(createCodeActionButton('code-block-cancel', 'Cancel', ICONS.close));

    editorFooter.appendChild(editorHelp);
    editorFooter.appendChild(editorActions);
    editorPanel.appendChild(editor);
    editorPanel.appendChild(editorFooter);

    block.appendChild(headerElement);
    block.appendChild(pre);
    block.appendChild(editorPanel);

    return block;
  }

  function createCodeActionButton(className, label, iconMarkup) {
    const button = document.createElement('button');
    button.type = 'button';
    button.className = className;
    button.innerHTML = `${iconMarkup}<span>${label}</span>`;
    button.dataset.defaultLabel = label;
    return button;
  }

  function handleThreadClick(event) {
    const copyButton = event.target.closest('.code-block-copy');
    if (copyButton) {
      copyCodeBlock(copyButton.closest('.code-block'), copyButton);
      return;
    }

    const editButton = event.target.closest('.code-block-edit');
    if (editButton) {
      openCodeEditor(editButton.closest('.code-block'));
      return;
    }

    const saveButton = event.target.closest('.code-block-save');
    if (saveButton) {
      saveCodeEditor(saveButton.closest('.code-block'), saveButton);
      return;
    }

    const cancelButton = event.target.closest('.code-block-cancel');
    if (cancelButton) {
      closeCodeEditor(cancelButton.closest('.code-block'), true);
    }
  }

  function handleThreadKeyDown(event) {
    const editor = event.target.closest('.code-block-editor');
    if (!editor) {
      return;
    }

    if (event.key === 'Tab') {
      event.preventDefault();
      const start = editor.selectionStart;
      const end = editor.selectionEnd;
      editor.value = `${editor.value.slice(0, start)}  ${editor.value.slice(end)}`;
      editor.selectionStart = editor.selectionEnd = start + 2;
      return;
    }

    if ((event.ctrlKey || event.metaKey) && event.key === 'Enter') {
      event.preventDefault();
      saveCodeEditor(editor.closest('.code-block'));
      return;
    }

    if (event.key === 'Escape') {
      event.preventDefault();
      closeCodeEditor(editor.closest('.code-block'), true);
    }
  }

  async function copyCodeBlock(block, button) {
    if (!block) {
      return;
    }

    const editor = block.querySelector('.code-block-editor');
    const text = block.classList.contains('is-editing') && editor ? editor.value : (block.__rawCode || '');
    await writeClipboardText(text);
    setTransientButtonState(button, 'Copied', ICONS.check);
  }

  function openCodeEditor(block) {
    if (!block) {
      return;
    }

    const editor = block.querySelector('.code-block-editor');
    if (!editor) {
      return;
    }

    editor.value = block.__rawCode || '';
    block.classList.add('is-editing');
    editor.focus();
    editor.setSelectionRange(editor.value.length, editor.value.length);
  }

  function saveCodeEditor(block, button) {
    if (!block) {
      return;
    }

    const editor = block.querySelector('.code-block-editor');
    const codeElement = block.querySelector('pre code');
    if (!editor || !codeElement) {
      return;
    }

    block.__rawCode = editor.value;
    codeElement.innerHTML = highlightCode(block.__rawCode, block.__language || 'plaintext');
    closeCodeEditor(block, false);

    if (button) {
      setTransientButtonState(button, 'Saved', ICONS.check);
    }
  }

  function closeCodeEditor(block, resetEditor) {
    if (!block) {
      return;
    }

    const editor = block.querySelector('.code-block-editor');
    if (editor && resetEditor) {
      editor.value = block.__rawCode || '';
    }

    block.classList.remove('is-editing');
  }

  async function writeClipboardText(text) {
    try {
      await navigator.clipboard.writeText(text);
      return;
    } catch (error) {
      const textarea = document.createElement('textarea');
      textarea.value = text;
      textarea.setAttribute('readonly', 'true');
      textarea.style.position = 'absolute';
      textarea.style.left = '-9999px';
      document.body.appendChild(textarea);
      textarea.select();
      document.execCommand('copy');
      textarea.remove();
    }
  }

  function setTransientButtonState(button, label, iconMarkup) {
    const defaultLabel = button.dataset.defaultLabel || 'Copy';
    const defaultIcon = button.classList.contains('code-block-save')
      ? ICONS.save
      : (button.classList.contains('code-block-edit') ? ICONS.edit : (button.classList.contains('code-block-cancel') ? ICONS.close : ICONS.copy));

    button.innerHTML = `${iconMarkup}<span>${label}</span>`;
    window.setTimeout(() => {
      button.innerHTML = `${defaultIcon}<span>${defaultLabel}</span>`;
    }, 1800);
  }

  function highlightCode(rawCode, language) {
    switch (normalizeLanguage(language)) {
      case 'json':
        return highlightJson(rawCode);
      case 'yaml':
      case 'yml':
        return highlightYaml(rawCode);
      case 'javascript':
      case 'typescript':
      case 'java':
      case 'python':
        return highlightScriptLike(rawCode, normalizeLanguage(language));
      case 'bash':
      case 'powershell':
        return highlightShell(rawCode);
      case 'html':
      case 'xml':
        return highlightMarkup(rawCode);
      case 'css':
        return highlightCss(rawCode);
      case 'sql':
        return highlightSql(rawCode);
      case 'markdown':
        return highlightMarkdown(rawCode);
      default:
        return escapeHtml(rawCode);
    }
  }

  function highlightJson(rawCode) {
    return rawCode.split('\n').map((line) => {
      let html = escapeHtml(line);
      html = html.replace(/^(\s*)(&quot;(?:[^"\\]|\\.)*&quot;)(\s*:)/, '$1<span class="na">$2</span><span class="p">$3</span>');
      html = html.replace(/(&quot;(?:[^"\\]|\\.)*&quot;)(?!\s*:)/g, '<span class="s2">$1</span>');
      html = html.replace(/\b(-?(?:0|[1-9]\d*)(?:\.\d+)?)\b/g, '<span class="mi">$1</span>');
      html = html.replace(/\b(true|false|null)\b/g, '<span class="kc">$1</span>');
      html = html.replace(/([{}\[\],])/g, '<span class="p">$1</span>');
      return html;
    }).join('\n');
  }

  function highlightYaml(rawCode) {
    return rawCode.split('\n').map((line) => {
      const commentIndex = line.indexOf('#');
      const content = commentIndex >= 0 ? line.slice(0, commentIndex) : line;
      const comment = commentIndex >= 0 ? line.slice(commentIndex) : '';
      let html = escapeHtml(content);

      html = html.replace(/^(\s*-?\s*)([\w.-]+)(\s*:)/, '$1<span class="na">$2</span><span class="p">$3</span>');
      html = html.replace(/(&quot;[^"]*&quot;|&#39;[^']*&#39;)/g, '<span class="s2">$1</span>');
      html = html.replace(/\b(true|false|null|yes|no|on|off)\b/gi, '<span class="kc">$1</span>');
      html = html.replace(/\b(-?(?:0|[1-9]\d*)(?:\.\d+)?)\b/g, '<span class="mi">$1</span>');

      if (comment) {
        html += `<span class="c1">${escapeHtml(comment)}</span>`;
      }

      return html;
    }).join('\n');
  }

  function highlightScriptLike(rawCode, language) {
    const keywordPatterns = {
      javascript: /\b(const|let|var|function|return|if|else|for|while|await|async|new|class|import|export|from|try|catch|throw)\b/g,
      typescript: /\b(const|let|var|function|return|if|else|for|while|await|async|new|class|import|export|from|interface|type|implements|extends|public|private|readonly)\b/g,
      java: /\b(public|private|protected|class|interface|extends|implements|static|final|void|new|return|if|else|for|while|try|catch|throw|package|import)\b/g,
      python: /\b(def|return|if|elif|else|for|while|try|except|class|import|from|with|as|lambda|yield|pass|raise)\b/g
    };

    const protectedFragments = [];
    let working = rawCode;
    working = protectPattern(working, /`(?:\\.|[^`])*`|"(?:\\.|[^"])*"|'(?:\\.|[^'])*'/g, 's2', protectedFragments);
    working = protectPattern(working, /\/\*[\s\S]*?\*\/|\/\/.*$/gm, 'c1', protectedFragments);
    if (language === 'python') {
      working = protectPattern(working, /#.*$/gm, 'c1', protectedFragments);
    }

    let html = escapeHtml(working);
    html = html.replace(keywordPatterns[language], '<span class="k">$1</span>');
    html = html.replace(/\b(true|false|null|None)\b/g, '<span class="kc">$1</span>');
    html = html.replace(/\b(-?(?:0|[1-9]\d*)(?:\.\d+)?)\b/g, '<span class="mi">$1</span>');
    html = html.replace(/\b([A-Za-z_][A-Za-z0-9_]*)\s*(?=\()/g, '<span class="nf">$1</span>');
    html = html.replace(/\$[A-Za-z_][A-Za-z0-9_:.-]*/g, '<span class="nv">$&</span>');
    html = html.replace(/([{}()[\].,;:])/g, '<span class="p">$1</span>');
    return restoreProtectedFragments(html, protectedFragments);
  }

  function highlightShell(rawCode) {
    const protectedFragments = [];
    let working = rawCode;
    working = protectPattern(working, /"(?:\\.|[^"])*"|'(?:\\.|[^'])*'/g, 's2', protectedFragments);
    working = protectPattern(working, /#.*$/gm, 'c1', protectedFragments);

    let html = escapeHtml(working);
    html = html.replace(/\b(if|then|else|fi|for|do|done|case|esac|function|param|foreach|switch)\b/g, '<span class="k">$1</span>');
    html = html.replace(/\$[A-Za-z_][A-Za-z0-9_:.-]*/g, '<span class="nv">$&</span>');
    html = html.replace(/(^|\s)(--?[A-Za-z0-9_-]+)/g, '$1<span class="na">$2</span>');
    return restoreProtectedFragments(html, protectedFragments);
  }

  function highlightMarkup(rawCode) {
    let html = escapeHtml(rawCode);
    html = html.replace(/(&lt;\/?)([A-Za-z0-9:-]+)/g, '$1<span class="nt">$2</span>');
    html = html.replace(/([A-Za-z:-]+)=(&quot;.*?&quot;|&#39;.*?&#39;)/g, '<span class="na">$1</span><span class="p">=</span><span class="s2">$2</span>');
    html = html.replace(/(&lt;|&gt;|\/&gt;)/g, '<span class="p">$1</span>');
    return html;
  }

  function highlightCss(rawCode) {
    return rawCode.split('\n').map((line) => {
      let html = escapeHtml(line);
      html = html.replace(/\/\*.*?\*\//g, '<span class="c1">$&</span>');
      html = html.replace(/^(\s*)([^{}]+)(\s*\{)/, '$1<span class="nf">$2</span>$3');
      html = html.replace(/([A-Za-z-]+)(\s*:)/g, '<span class="na">$1</span><span class="p">$2</span>');
      html = html.replace(/(&quot;.*?&quot;|&#39;.*?&#39;)/g, '<span class="s2">$1</span>');
      html = html.replace(/#[0-9a-fA-F]{3,8}\b/g, '<span class="mi">$&</span>');
      html = html.replace(/\b(-?(?:0|[1-9]\d*)(?:\.\d+)?(?:px|rem|em|%|vh|vw)?)\b/g, '<span class="mi">$1</span>');
      html = html.replace(/([{}();:,])/g, '<span class="p">$1</span>');
      return html;
    }).join('\n');
  }

  function highlightSql(rawCode) {
    const protectedFragments = [];
    let working = rawCode;
    working = protectPattern(working, /"(?:\\.|[^"])*"|'(?:\\.|[^'])*'/g, 's2', protectedFragments);
    working = protectPattern(working, /--.*$/gm, 'c1', protectedFragments);

    let html = escapeHtml(working);
    html = html.replace(/\b(SELECT|FROM|WHERE|JOIN|LEFT|RIGHT|INNER|OUTER|GROUP|BY|ORDER|INSERT|INTO|VALUES|UPDATE|SET|DELETE|LIMIT|OFFSET|AS|AND|OR|NOT|IN|ON)\b/gi, '<span class="k">$1</span>');
    html = html.replace(/\b(-?(?:0|[1-9]\d*)(?:\.\d+)?)\b/g, '<span class="mi">$1</span>');
    return restoreProtectedFragments(html, protectedFragments);
  }

  function highlightMarkdown(rawCode) {
    return rawCode.split('\n').map((line) => {
      let html = escapeHtml(line);
      html = html.replace(/^(#{1,6}\s.*)$/g, '<span class="k">$1</span>');
      html = html.replace(/^(\s*[-*+]\s)/g, '<span class="p">$1</span>');
      html = html.replace(/`[^`]+`/g, '<span class="s2">$&</span>');
      html = html.replace(/\[([^\]]+)\]\(([^)]+)\)/g, '<span class="na">[$1]</span><span class="p">(</span><span class="s2">$2</span><span class="p">)</span>');
      return html;
    }).join('\n');
  }

  function protectPattern(text, pattern, tokenClass, protectedFragments) {
    return text.replace(pattern, (match) => {
      const token = `@@AI_TOKEN_${protectedFragments.length}@@`;
      protectedFragments.push(`<span class="${tokenClass}">${escapeHtml(match)}</span>`);
      return token;
    });
  }

  function restoreProtectedFragments(text, protectedFragments) {
    return protectedFragments.reduce((accumulator, fragment, index) => {
      return accumulator.replaceAll(`@@AI_TOKEN_${index}@@`, fragment);
    }, text);
  }

  function extractLanguage() {
    const nodes = Array.from(arguments);

    for (const node of nodes) {
      if (!node || !node.className || typeof node.className !== 'string') {
        continue;
      }

      const match = node.className.match(/language-([a-z0-9#+-]+)/i);
      if (match && match[1]) {
        return match[1];
      }
    }

    return 'plaintext';
  }

  function normalizeLanguage(language) {
    const raw = String(language || 'plaintext').toLowerCase();
    const aliases = {
      js: 'javascript',
      ts: 'typescript',
      sh: 'bash',
      shell: 'bash',
      pwsh: 'powershell',
      ps1: 'powershell',
      yml: 'yaml',
      text: 'plaintext',
      txt: 'plaintext',
      md: 'markdown'
    };

    return aliases[raw] || raw;
  }

  function formatLanguageLabel(language) {
    const labels = {
      javascript: 'JavaScript',
      typescript: 'TypeScript',
      powershell: 'PowerShell',
      plaintext: 'Text',
      yaml: 'YAML',
      json: 'JSON',
      html: 'HTML',
      xml: 'XML',
      css: 'CSS',
      bash: 'Bash',
      sql: 'SQL',
      markdown: 'Markdown',
      java: 'Java',
      python: 'Python'
    };

    return labels[language] || language.charAt(0).toUpperCase() + language.slice(1);
  }

  function stripMentionLabels(text) {
    return cleanText(String(text || '').replace(/@\[[^\]]+\]/g, ' '));
  }

  function extractMentionLabels(text) {
    return [...String(text || '').matchAll(/@\[(.+?)\]/g)]
      .map((match) => cleanText(match[1]))
      .filter(Boolean);
  }

  function resolveMentionReferences(labels) {
    const mentions = [];

    labels.forEach((label) => {
      const normalizedLabel = normalizeText(label);

      if (normalizedLabel === 'current page') {
        mentions.push({
          id: 'current-page',
          title: pageContext.title,
          excerpt: pageContext.pageExcerpt
        });
        return;
      }

      if (normalizedLabel === 'visible sections' || normalizedLabel === 'visible section') {
        currentVisibleSections.forEach((section) => {
          mentions.push({
            id: section.id,
            title: section.title,
            excerpt: section.excerpt
          });
        });
        return;
      }

      const section = pageContext.sections.find((entry) => normalizeText(entry.title) === normalizedLabel)
        || pageContext.sections.find((entry) => normalizeText(entry.title).includes(normalizedLabel));

      if (section) {
        mentions.push({
          id: section.id,
          title: section.title,
          excerpt: section.excerpt
        });
      }
    });

    return dedupeById(mentions, (mention) => mention.id);
  }

  function escapeHtml(value) {
    return String(value).replace(/[&<>"']/g, function (char) {
      const replacements = {
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        "'": '&#39;'
      };

      return replacements[char];
    });
  }

  function escapeInlineText(value) {
    return String(value || '').replace(/[<>]/g, '');
  }

  function cleanText(text) {
    return String(text || '').replace(/\s+/g, ' ').trim();
  }

  function normalizeText(text) {
    return String(text || '')
      .toLowerCase()
      .normalize('NFD')
      .replace(/[\u0300-\u036f]/g, '')
      .replace(/\s+/g, ' ')
      .trim();
  }

  function excerptText(text, maxLength) {
    const clean = cleanText(text);
    if (!clean || clean.length <= maxLength) {
      return clean;
    }

    return `${clean.slice(0, Math.max(0, maxLength - 1)).trim()}...`;
  }

  function normalizeUrl(path) {
    return String(path || '')
      .replace(window.location.origin, '')
      .replace(/\/index\.html$/, '/')
      .replace(/\/+$/, '') || '/';
  }

  function tokenizeText(text) {
    return normalizeText(text)
      .split(/[^a-z0-9-]+/)
      .filter((token) => token.length > 2 && !STOP_WORDS.has(token));
  }

  function slugify(text) {
    return normalizeText(text).replace(/[^a-z0-9\s-]/g, '').replace(/\s+/g, '-');
  }

  function dedupeById(items, keyResolver) {
    const seen = new Set();
    return items.filter((item) => {
      const key = keyResolver(item);
      if (!key || seen.has(key)) {
        return false;
      }

      seen.add(key);
      return true;
    });
  }

  function getLocalizedCopy(question, copies) {
    return detectQuestionLanguage(question) === 'pt' ? copies.pt : copies.en;
  }

  function detectQuestionLanguage(text) {
    const normalized = normalizeText(text);
    const portugueseHints = ['como', 'posso', 'preciso', 'ajuda', 'documentacao', 'configuracao', 'validacao', 'secao', 'pagina'];
    return portugueseHints.some((hint) => normalized.includes(hint)) ? 'pt' : 'en';
  }

  function isBlockStart(lines, index) {
    const line = lines[index];
    return (
      isCodeFence(line) ||
      isHorizontalRule(line) ||
      isHeading(line) ||
      isUnorderedListItem(line) ||
      isOrderedListItem(line) ||
      isBlockquote(line) ||
      isTableStart(lines, index)
    );
  }

  function isCodeFence(line) {
    return /^```/.test(line.trim());
  }

  function isHorizontalRule(line) {
    return /^\s*(?:-{3,}|\*{3,}|_{3,})\s*$/.test(line);
  }

  function isHeading(line) {
    return /^(#{1,4})\s+.+/.test(line);
  }

  function isUnorderedListItem(line) {
    return /^\s*[-*+]\s+/.test(line);
  }

  function isOrderedListItem(line) {
    return /^\s*\d+\.\s+/.test(line);
  }

  function isBlockquote(line) {
    return /^\s*>\s?/.test(line);
  }

  function isPipeRow(line) {
    const trimmed = line.trim();
    return trimmed.startsWith('|') && trimmed.endsWith('|') && trimmed.length > 2;
  }

  function isTableSeparator(line) {
    return /^\|?[\s:-|]+\|?$/.test(line.trim());
  }

  function isTableStart(lines, index) {
    if (!isPipeRow(lines[index])) {
      return false;
    }

    if (index + 1 >= lines.length) {
      return false;
    }

    return isPipeRow(lines[index + 1]) || isTableSeparator(lines[index + 1]);
  }

  function splitTableRow(row) {
    return row
      .trim()
      .replace(/^\|/, '')
      .replace(/\|$/, '')
      .split('|')
      .map(function (cell) {
        return cell.trim();
      });
  }

  function normalizeTableCells(cells, columnCount) {
    const normalized = cells.slice(0, columnCount);

    while (normalized.length < columnCount) {
      normalized.push('');
    }

    return normalized;
  }

  function clamp(value, min, max) {
    return Math.min(Math.max(value, min), max);
  }

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', init);
  } else {
    init();
  }
})();
