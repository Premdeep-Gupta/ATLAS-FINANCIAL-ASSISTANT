document.addEventListener('DOMContentLoaded', () => {
    // Initialize Lucide Icons
    if (window.lucide) {
        lucide.createIcons();
    }

    const state = {
        userId: 10001,
        activeTab: 'overview',
        overviewData: null,
        watchlist: [],
        chatHistory: []
    };

    // DOM Elements
    const navItems = document.querySelectorAll('.nav-item');
    const tabContents = document.querySelectorAll('.tab-content');
    const pageTitle = document.getElementById('page-title');
    const pageSubtitle = document.getElementById('page-subtitle');
    const topNavTabs = document.querySelectorAll('.top-nav-tab');

    // Tab Titles Map
    const tabTitles = {
        overview: { title: 'Market Intelligence & Briefings', subtitle: 'Real-time financial synthesis, portfolio analytics, and automated briefings.' },
        chat: { title: 'AI Workspace Conversational Assistant', subtitle: 'Ask complex financial queries, analyze trading multiples, or manage workspace workflows.' },
        watchlist: { title: 'Watchlist & Company Research', subtitle: 'Track fundamentals, financial ratios, valuation multiples, and recent news.' },
        documents: { title: 'Financial Document Intelligence', subtitle: 'Upload 10-K filings, annual reports, or financial spreadsheets for AI summary extraction.' },
        media: { title: 'Video & Executive Earnings Call Hub', subtitle: 'Extract AI timestamp chapters, sentiment shifts, and video transcript highlights.' },
        integrations: { title: 'Productivity & Workspace Integrations', subtitle: 'Search Gmail messages, schedule Google Calendar meetings, and inspect Sheets models.' },
        settings: { title: 'Assistant Preferences & Notification Schedule', subtitle: 'Configure user role, morning briefing delivery time, and custom alert thresholds.' },
        logs: { title: 'System Monitor & Execution Logs', subtitle: 'Inspect live backend server events, database status, and Telegram update events.' }
    };

    // Navigation Switcher
    navItems.forEach(item => {
        item.addEventListener('click', () => {
            const targetTab = item.getAttribute('data-tab');
            switchTab(targetTab);
        });
    });

    // Top Feature Navbar Tabs Click Listener (Markets, Research, News, AI Insights)
    topNavTabs.forEach(btn => {
        btn.addEventListener('click', () => {
            topNavTabs.forEach(t => t.classList.remove('active'));
            btn.classList.add('active');

            const feature = btn.getAttribute('data-feature');
            if (feature === 'markets') {
                switchTab('overview');
                loadOverview();
            } else if (feature === 'research') {
                switchTab('watchlist');
                loadWatchlist();
            } else if (feature === 'news') {
                switchTab('chat');
                sendMessage('Summarize today\'s top market-moving news and relevance scores');
            } else if (feature === 'insights') {
                switchTab('chat');
                sendMessage('Give me personalized AI insights based on my saved preferences and active watchlist');
            }
        });
    });

    function switchTab(tabId) {
        navItems.forEach(i => i.classList.remove('active'));
        tabContents.forEach(c => c.classList.remove('active'));

        const activeNav = document.querySelector(`.nav-item[data-tab="${tabId}"]`);
        const activeContent = document.getElementById(`tab-${tabId}`);

        if (activeNav) activeNav.classList.add('active');
        if (activeContent) activeContent.classList.add('active');

        if (tabTitles[tabId]) {
            pageTitle.textContent = tabTitles[tabId].title;
            pageSubtitle.textContent = tabTitles[tabId].subtitle;
        }

        state.activeTab = tabId;

        // Fetch Tab Specific Data
        if (tabId === 'overview') loadOverview();
        if (tabId === 'chat') loadChatHistory();
        if (tabId === 'watchlist') loadWatchlist();
        if (tabId === 'logs') loadLogs();
    }

    // -------------------------------------------------------------
    // ADVANCED & ULTRA PRO VOICE SEARCH & UNIVERSAL SEARCH ENGINE
    // -------------------------------------------------------------
    const btnVoiceSearch = document.getElementById('btn-voice-search');
    const globalSearchInput = document.getElementById('global-search');
    const searchDropdown = document.getElementById('search-results-dropdown');
    const searchResultsBody = document.getElementById('search-results-body');
    const closeSearchBtn = document.getElementById('close-search');

    if (closeSearchBtn && searchDropdown) {
        closeSearchBtn.addEventListener('click', () => searchDropdown.classList.add('hidden'));
    }

    // Web Speech API Voice Recognition
    const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;

    if (SpeechRecognition && btnVoiceSearch) {
        const recognition = new SpeechRecognition();
        recognition.continuous = false;
        recognition.interimResults = false;
        recognition.lang = 'en-US';

        btnVoiceSearch.addEventListener('click', () => {
            if (btnVoiceSearch.classList.contains('recording')) {
                recognition.stop();
                btnVoiceSearch.classList.remove('recording');
            } else {
                try {
                    recognition.start();
                    btnVoiceSearch.classList.add('recording');
                    globalSearchInput.placeholder = 'Listening... Speak your query now!';
                } catch (e) {
                    console.error('Speech recognition error:', e);
                }
            }
        });

        recognition.onresult = (event) => {
            const transcript = event.results[0][0].transcript;
            globalSearchInput.value = transcript;
            btnVoiceSearch.classList.remove('recording');
            globalSearchInput.placeholder = 'Search ticker, news, emails, models, videos...';
            performUniversalSearch(transcript);
        };

        recognition.onerror = (event) => {
            console.error('Speech recognition error:', event.error);
            btnVoiceSearch.classList.remove('recording');
            globalSearchInput.placeholder = 'Voice search error. Try typing instead.';
        };

        recognition.onend = () => {
            btnVoiceSearch.classList.remove('recording');
        };
    } else if (btnVoiceSearch) {
        btnVoiceSearch.addEventListener('click', () => {
            alert('Voice Search Web Speech API is supported in Chrome, Edge, and Safari browsers.');
        });
    }

    // Live Search Typing Input Listener
    if (globalSearchInput) {
        globalSearchInput.addEventListener('input', (e) => {
            const val = e.target.value.trim();
            if (val.length >= 2) {
                performUniversalSearch(val);
            } else if (searchDropdown) {
                searchDropdown.classList.add('hidden');
            }
        });
    }

    function performUniversalSearch(query) {
        if (!searchDropdown || !searchResultsBody) return;

        searchDropdown.classList.remove('hidden');
        const q = query.toLowerCase();

        let html = `<div style="margin-bottom:10px;"><strong>Results for "${escapeHtml(query)}":</strong></div>`;
        let matches = 0;

        if (q.includes('aapl') || q.includes('apple')) {
            html += `<div class="search-item" style="padding:6px 0; border-bottom:1px solid rgba(255,255,255,0.05);">
                <strong>📈 AAPL (Apple Inc.)</strong> - Price: $224.50 (+1.8%) | P/E: 32.4
                <br><span style="font-size:0.75rem; color:#38bdf8; cursor:pointer;" onclick="quickQueryTicker('AAPL')">▶ Click to launch Deep-Dive AI Research</span>
            </div>`;
            matches++;
        }
        if (q.includes('nvda') || q.includes('nvidia')) {
            html += `<div class="search-item" style="padding:6px 0; border-bottom:1px solid rgba(255,255,255,0.05);">
                <strong>📈 NVDA (NVIDIA Corp.)</strong> - Price: $128.20 (+3.4%) | P/E: 45.1
                <br><span style="font-size:0.75rem; color:#38bdf8; cursor:pointer;" onclick="quickQueryTicker('NVDA')">▶ Click to launch Deep-Dive AI Research</span>
            </div>`;
            matches++;
        }
        if (q.includes('news') || q.includes('market') || q.includes('revenue')) {
            html += `<div class="search-item" style="padding:6px 0; border-bottom:1px solid rgba(255,255,255,0.05);">
                <strong>📰 Market News Highlight:</strong> Federal Reserve Signal Rate Cuts; Tech Rally Accelerates
                <br><span style="font-size:0.75rem; color:#34d399;">Relevance Score: 9.8/10</span>
            </div>`;
            matches++;
        }

        html += `<div class="search-item" style="padding:6px 0;">
            <strong>🧠 Gemini 2.5 AI Universal Agent Query:</strong>
            <br><span style="font-size:0.75rem; color:#94a3b8; cursor:pointer;" onclick="askSearchQuery('${escapeHtml(query)}')">▶ Ask Atlas AI to analyze "${escapeHtml(query)}" in workspace chat</span>
        </div>`;

        searchResultsBody.innerHTML = html;
    }

    window.askSearchQuery = (query) => {
        if (searchDropdown) searchDropdown.classList.add('hidden');
        switchTab('chat');
        sendMessage(query);
    };

    // 1. Overview API Loader
    async function loadOverview() {
        try {
            const res = await fetch(`/api/overview?userId=${state.userId}`);
            const data = await res.json();
            state.overviewData = data;

            // Render Briefing
            const briefingBox = document.getElementById('briefing-content');
            briefingBox.innerHTML = formatMarkdown(data.latestBriefing || 'No briefing available.');

            // Update user badge
            if (data.user) {
                document.getElementById('badge-user-name').textContent = data.user.firstName || 'Alex Pro';
                document.getElementById('badge-user-role').textContent = data.user.role || 'Senior Analyst';
                document.getElementById('setting-role').value = data.user.role || 'Senior Analyst';
            }

            renderOverviewChart();
        } catch (err) {
            console.error('Failed to load overview data:', err);
        }
    }

    let overviewChartInstance = null;
    function renderOverviewChart() {
        const ctx = document.getElementById('overviewMarketChart');
        if (!ctx) return;

        if (overviewChartInstance) overviewChartInstance.destroy();

        overviewChartInstance = new Chart(ctx, {
            type: 'doughnut',
            data: {
                labels: ['Technology', 'Financials', 'Healthcare', 'Consumer', 'Energy'],
                datasets: [{
                    data: [45, 20, 15, 12, 8],
                    backgroundColor: ['#38bdf8', '#3b82f6', '#a855f7', '#ec4899', '#34d399'],
                    borderWidth: 0
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        position: 'right',
                        labels: { color: '#94a3b8', font: { family: 'Inter', size: 11 } }
                    }
                }
            }
        });
    }

    // -------------------------------------------------------------
    // CHATGPT-STYLE SMOOTH TYPEWRITER & AUTO-SCROLL ENGINE
    // -------------------------------------------------------------
    const chatContainer = document.getElementById('chat-messages-container');
    const chatInput = document.getElementById('chat-input');
    const btnSend = document.getElementById('btn-send-message');

    function scrollToBottomSmooth() {
        if (!chatContainer) return;
        chatContainer.scrollTo({
            top: chatContainer.scrollHeight,
            behavior: 'smooth'
        });
    }

    async function loadChatHistory() {
        try {
            const res = await fetch(`/api/chat/history?userId=${state.userId}`);
            const history = await res.json();
            renderChatMessages(history);
        } catch (err) {
            console.error('Failed to load chat history:', err);
        }
    }

    function renderChatMessages(messages) {
        if (!messages || messages.length === 0) {
            chatContainer.innerHTML = `
                <div class="chat-bubble model">
                    Hello! I am <strong>Atlas</strong>, your AI Financial Assistant. Ask me to research companies, analyze earnings, check prices, or schedule meetings.
                </div>
            `;
            scrollToBottomSmooth();
            return;
        }

        chatContainer.innerHTML = messages.map(msg => `
            <div class="chat-bubble ${msg.role}">
                ${formatMarkdown(msg.content)}
            </div>
        `).join('');

        setTimeout(scrollToBottomSmooth, 50);
    }

    // ChatGPT-Style Word-by-Word Streamed Typewriter Animation
    function typeWriterEffect(element, fullText, onComplete) {
        element.innerHTML = '';
        const words = fullText.split(' ');
        let index = 0;

        function typeNextWord() {
            if (index < words.length) {
                const currentText = words.slice(0, index + 1).join(' ');
                element.innerHTML = formatMarkdown(currentText);
                scrollToBottomSmooth();
                index++;
                setTimeout(typeNextWord, 15); // Fast 15ms per word streaming
            } else {
                element.innerHTML = formatMarkdown(fullText);
                scrollToBottomSmooth();
                if (onComplete) onComplete();
            }
        }
        typeNextWord();
    }

    async function sendMessage(text) {
        if (!text || text.trim() === '') return;

        // Append user bubble
        chatContainer.innerHTML += `<div class="chat-bubble user">${escapeHtml(text)}</div>`;
        chatInput.value = '';
        scrollToBottomSmooth();

        // Append typing indicator bubble
        const typingId = 'typing-' + Date.now();
        chatContainer.innerHTML += `<div class="chat-bubble model" id="${typingId}"><em>Atlas is analyzing market data & generating insights...</em></div>`;
        scrollToBottomSmooth();

        try {
            const res = await fetch(`/api/chat?userId=${state.userId}`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ message: text })
            });
            const data = await res.json();

            const typingElem = document.getElementById(typingId);
            if (typingElem) {
                const responseText = data.response || 'Sorry, I could not process your query.';
                typeWriterEffect(typingElem, responseText, () => {
                    scrollToBottomSmooth();
                });
            }
        } catch (err) {
            console.error('Error sending chat message:', err);
            const typingElem = document.getElementById(typingId);
            if (typingElem) typingElem.innerHTML = 'Error communicating with backend server.';
            scrollToBottomSmooth();
        }
    }

    if (btnSend) {
        btnSend.addEventListener('click', () => sendMessage(chatInput.value));
    }
    if (chatInput) {
        chatInput.addEventListener('keypress', (e) => {
            if (e.key === 'Enter') sendMessage(chatInput.value);
        });
    }

    document.querySelectorAll('.chip-btn').forEach(btn => {
        btn.addEventListener('click', () => {
            switchTab('chat');
            const query = btn.getAttribute('data-query');
            sendMessage(query);
        });
    });

    // 3. Watchlist Module
    async function loadWatchlist() {
        try {
            const res = await fetch(`/api/watchlist?userId=${state.userId}`);
            const list = await res.json();
            renderWatchlistCards(list);
        } catch (err) {
            console.error('Failed to load watchlist:', err);
        }
    }

    function renderWatchlistCards(companies) {
        const container = document.getElementById('watchlist-cards-container');
        if (!container) return;

        if (!companies || companies.length === 0) {
            container.innerHTML = `<p class="subtext">No tickers in watchlist. Add one above!</p>`;
            return;
        }

        container.innerHTML = companies.map(comp => `
            <div class="company-card">
                <div class="company-card-header">
                    <div>
                        <h4>${comp.ticker}</h4>
                        <span>${comp.name || comp.ticker}</span>
                    </div>
                    <span class="badge ${comp.percent_change >= 0 ? 'badge-success' : 'badge-danger'}">
                        ${comp.percent_change >= 0 ? '+' : ''}${comp.percent_change || 0}%
                    </span>
                </div>
                <div class="company-metrics">
                    <div>Price: $${comp.price || 0}</div>
                    <div>P/E: ${comp.pe_ratio || 'N/A'}</div>
                    <div>Sector: ${comp.sector || 'Tech'}</div>
                    <div>Market Cap: ${comp.market_cap || 'N/A'}</div>
                </div>
                <div style="display:flex; justify-content:space-between; margin-top:8px;">
                    <button class="btn btn-sm btn-secondary" onclick="deleteTicker('${comp.ticker}')">Remove</button>
                    <button class="btn btn-sm" onclick="quickQueryTicker('${comp.ticker}')">Analyze</button>
                </div>
            </div>
        `).join('');
    }

    const btnAddTicker = document.getElementById('btn-add-ticker');
    const inputTicker = document.getElementById('input-new-ticker');
    if (btnAddTicker && inputTicker) {
        btnAddTicker.addEventListener('click', async () => {
            const val = inputTicker.value.trim();
            if (!val) return;
            await fetch(`/api/watchlist?userId=${state.userId}`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ ticker: val })
            });
            inputTicker.value = '';
            loadWatchlist();
        });
    }

    window.deleteTicker = async (ticker) => {
        await fetch(`/api/watchlist/${ticker}?userId=${state.userId}`, { method: 'DELETE' });
        loadWatchlist();
    };

    window.quickQueryTicker = (ticker) => {
        switchTab('chat');
        sendMessage(`Give me an in-depth financial analysis and key risks for ${ticker}`);
    };

    // 4. Document Processing
    const dropZone = document.getElementById('drop-zone');
    const fileInput = document.getElementById('file-input');
    const btnAnalyzeDoc = document.getElementById('btn-analyze-doc');
    const docSummaryOutput = document.getElementById('doc-summary-output');

    if (dropZone && fileInput) {
        dropZone.addEventListener('click', () => fileInput.click());
        fileInput.addEventListener('change', () => {
            if (fileInput.files.length > 0) {
                dropZone.querySelector('p').innerHTML = `Selected File: <strong>${fileInput.files[0].name}</strong>`;
            }
        });
    }

    if (btnAnalyzeDoc) {
        btnAnalyzeDoc.addEventListener('click', async () => {
            if (!fileInput.files || fileInput.files.length === 0) {
                alert('Please select a PDF or Excel/CSV document first.');
                return;
            }

            const file = fileInput.files[0];
            const query = document.getElementById('doc-custom-query').value;

            const formData = new FormData();
            formData.append('file', file);
            formData.append('query', query || 'Provide executive summary with key metrics');

            docSummaryOutput.innerHTML = '<em>Extracting text and running Gemini AI Document Analysis...</em>';

            try {
                const res = await fetch('/api/documents/upload', {
                    method: 'POST',
                    body: formData
                });
                const data = await res.json();
                docSummaryOutput.innerHTML = formatMarkdown(data.summary || 'Completed document analysis.');
            } catch (err) {
                console.error('Doc analysis error:', err);
                docSummaryOutput.innerHTML = 'Failed to analyze document.';
            }
        });
    }

    // 5. Video & Media Hub Trigger
    document.getElementById('btn-analyze-video')?.addEventListener('click', () => {
        const output = document.getElementById('video-summary-output');
        if (output) {
            output.innerHTML = `
                <p style="color:#34d399;">✓ AI Video Analysis Completed (Duration: 2:00):</p>
                <ul>
                    <li><strong>0:00 - 0:45:</strong> Q3 Revenue surge driven by AI Data Center Chip shipments (+18% YoY).</li>
                    <li><strong>0:45 - 1:30:</strong> Gross margin expansion to 74.8% due to software license mix.</li>
                    <li><strong>1:30 - 2:00:</strong> CEO confirms $10B share repurchase authorization & strong cash conversion.</li>
                </ul>
            `;
        }
    });

    // 6. Integrations Hub
    document.getElementById('btn-search-gmail')?.addEventListener('click', async () => {
        const query = document.getElementById('gmail-search-query').value;
        const res = await fetch(`/api/integrations/search-emails?query=${encodeURIComponent(query)}`);
        const emails = await res.json();

        const box = document.getElementById('gmail-results');
        box.innerHTML = emails.map(e => `
            <div style="margin-bottom:8px; border-bottom:1px solid rgba(255,255,255,0.05); padding-bottom:4px;">
                <strong>${e.subject}</strong> (${e.sender})<br>
                <span style="font-size:0.75rem; color:#94a3b8;">${e.snippet}</span>
            </div>
        `).join('');
    });

    document.getElementById('btn-schedule-calendar')?.addEventListener('click', async () => {
        const title = document.getElementById('calendar-meeting-title').value;
        const res = await fetch('/api/integrations/schedule-meeting', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ summary: title || 'Portfolio Review', startTime: 'Tomorrow 10:00 AM' })
        });
        const data = await res.json();
        document.getElementById('calendar-results').innerHTML = `<span style="color:#34d399;">✓ ${data.message}</span>`;
    });

    document.getElementById('btn-read-sheet')?.addEventListener('click', () => {
        document.getElementById('sheet-results').innerHTML = `
            <strong>Sheet KPI Matrix Loaded:</strong><br>
            • Q1 Revenue: $1,200,000 | Net Income: $550,000<br>
            • Q2 Revenue: $1,350,000 | Net Income: $600,000<br>
            • Q3 Forecast: $1,500,000 | Net Income: $730,000
        `;
    });

    // 7. Settings Saver
    document.getElementById('btn-save-settings')?.addEventListener('click', async () => {
        const role = document.getElementById('setting-role').value;
        const time = document.getElementById('setting-briefing-time').value;
        const tz = document.getElementById('setting-timezone').value;

        await fetch(`/api/settings?userId=${state.userId}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ role: role, briefingTime: time, timezone: tz })
        });

        alert('Settings saved successfully!');
        loadOverview();
    });

    // 8. System Logs
    async function loadLogs() {
        const container = document.getElementById('logs-container');
        if (!container) return;
        try {
            const res = await fetch('/api/logs');
            const logs = await res.json();
            container.textContent = logs.map(l => `[${new Date(l.timestamp).toLocaleTimeString()}] ${l.level}: ${l.message}`).join('\n');
        } catch (err) {
            container.textContent = 'Failed to fetch logs.';
        }
    }
    document.getElementById('btn-refresh-logs')?.addEventListener('click', loadLogs);

    // Quick Briefing Header Button
    document.getElementById('btn-quick-briefing')?.addEventListener('click', () => {
        switchTab('chat');
        sendMessage('Generate an instant executive briefing for my portfolio and key market movements');
    });

    // Markdown Formatter Helper
    function formatMarkdown(text) {
        if (!text) return '';
        let formatted = escapeHtml(text);
        formatted = formatted.replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>');
        formatted = formatted.replace(/\*(.*?)\*/g, '<em>$1</em>');
        formatted = formatted.replace(/`([^`]+)`/g, '<code>$1</code>');
        formatted = formatted.replace(/\n\n/g, '<br><br>');
        formatted = formatted.replace(/\n- /g, '<br>• ');
        return formatted;
    }

    function escapeHtml(str) {
        return str.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
    }

    // Initial Load
    loadOverview();
});
