const API = {
    login: "/api/auth/login",
    signup: "/api/auth/signup",
    logout: "/api/auth/logout",
    forgot: "/api/auth/forgot-password",
    verifyOtp: "/api/auth/verify-otp",
    resetPassword: "/api/auth/reset-password",
    soil: "/api/soil",
    recommendations: "/api/recommendations",
    recent: "/api/recommendations/recent",
    stats: "/api/dashboard/stats",
    crops: "/api/crops"
};

const CROP_IMAGES = {
    'wheat': '/images/crops/wheat.svg',
    'rice': '/images/crops/rice.svg',
    'corn': '/images/crops/maize.svg',
    'maize': '/images/crops/maize.svg',
    'sugarcane': '/images/crops/sugarcane.svg',
    'cotton': '/images/crops/cotton.svg',
    'tomato': '/images/crops/tomato.svg',
    'potato': '/images/crops/potato.svg',
    'onion': '/images/crops/onion.svg',
    'chilli': '/images/crops/chilli.svg',
    'brinjal': '/images/crops/brinjal.svg',
    'cabbage': '/images/crops/cabbage.svg',
    'barley': '/images/crops/barley.svg',
    'ragi': '/images/crops/ragi.svg',
    'groundnut': '/images/crops/groundnut.svg',
    'sunflower': '/images/crops/sunflower.svg',
    'banana': '/images/crops/banana.svg',
    'mango': '/images/crops/mango.svg',
    'default': '/images/crops/generic.svg'
};

const FERTILIZER_IMAGES = {
    'Urea': '/images/fertilizers/urea.svg',
    'DAP': '/images/fertilizers/dap.svg',
    'MOP': '/images/fertilizers/mop.svg',
    'Compost': '/images/fertilizers/compost.svg',
    'default': '/images/fertilizers/urea.svg'
};

const state = {
    user: JSON.parse(localStorage.getItem("fertilizerUser") || "null"),
    token: localStorage.getItem("fertilizerToken"),
    crops: [
        { name: "Rice", imageUrl: "/images/crops/rice.svg", description: "Water-rich cereal crop" },
        { name: "Wheat", imageUrl: "/images/crops/wheat.svg", description: "Cool-season grain crop" },
        { name: "Maize", imageUrl: "/images/crops/maize.svg", description: "High-growth cereal crop" },
        { name: "Ragi", imageUrl: "/images/crops/ragi.svg", description: "Hardy millet crop" },
        { name: "Barley", imageUrl: "/images/crops/barley.svg", description: "Cool-season grain crop" },
        { name: "Tomato", imageUrl: "/images/crops/tomato.svg", description: "Nutrient-sensitive vegetable crop" },
        { name: "Potato", imageUrl: "/images/crops/potato.svg", description: "Potassium-loving tuber crop" },
        { name: "Onion", imageUrl: "/images/crops/onion.svg", description: "Bulb vegetable crop" },
        { name: "Chilli", imageUrl: "/images/crops/chilli.svg", description: "Spice vegetable crop" },
        { name: "Brinjal", imageUrl: "/images/crops/brinjal.svg", description: "Fruit vegetable crop" },
        { name: "Cabbage", imageUrl: "/images/crops/cabbage.svg", description: "Leafy vegetable crop" },
        { name: "Sugarcane", imageUrl: "/images/crops/sugarcane.svg", description: "Long-duration commercial crop" },
        { name: "Cotton", imageUrl: "/images/crops/cotton.svg", description: "Fiber crop" },
        { name: "Groundnut", imageUrl: "/images/crops/groundnut.svg", description: "Oilseed legume crop" },
        { name: "Sunflower", imageUrl: "/images/crops/sunflower.svg", description: "Oilseed crop" },
        { name: "Banana", imageUrl: "/images/crops/banana.svg", description: "Fruit crop" },
        { name: "Mango", imageUrl: "/images/crops/mango.svg", description: "Orchard fruit crop" }
    ]
};

// Global error handlers to help surface runtime issues during debugging
window.addEventListener('error', (ev) => {
    try { console.error('Runtime error:', ev.error || ev.message || ev); } catch (e) {}
    try { toast((ev.error && ev.error.message) || ev.message || 'An error occurred', 'error'); } catch (e) {}
});
window.addEventListener('unhandledrejection', (ev) => {
    try { console.error('Unhandled rejection:', ev.reason); } catch (e) {}
    try { toast((ev.reason && ev.reason.message) || String(ev.reason) || 'Unhandled rejection', 'error'); } catch (e) {}
});

document.addEventListener("DOMContentLoaded", () => {
    const tasks = [
        wireNavigation,
        wirePasswordToggles,
        wireAuthForms,
        wireSoilForm,
        renderDashboard,
        renderResult,
        renderHistory,
        renderProfile,
        setupCropAutocomplete
    ];

    tasks.forEach((fn) => {
        try {
            const result = fn();
            // If the function returns a promise, attach a catch handler so async errors don't bubble
            if (result && typeof result.catch === 'function') {
                result.catch((err) => {
                    console.error('Init task error (async):', err);
                    try { toast(err.message || String(err), 'error'); } catch (e) { /* ignore */ }
                });
            }
        } catch (err) {
            console.error('Init task error:', err);
            try { toast(err.message || String(err), 'error'); } catch (e) { /* ignore */ }
        }
    });
});

function authHeaders() {
    return {
        "Content-Type": "application/json",
        "X-Auth-Token": state.token || ""
    };
}

async function request(url, options = {}) {
    const response = await fetch(url, options);
    const data = await response.json().catch(() => ({}));
    if (!response.ok) {
        throw new Error(data.message || "Request failed");
    }
    return data;
}

function wireNavigation() {
    document.querySelectorAll("[data-current]").forEach((el) => {
        const page = document.body.dataset.page;
        if (el.dataset.current === page) el.classList.add("active");
    });

    document.querySelectorAll("[data-auth-only]").forEach((el) => {
        el.style.display = state.token ? "" : "none";
    });
    document.querySelectorAll("[data-guest-only]").forEach((el) => {
        el.style.display = state.token ? "none" : "";
    });
    document.querySelectorAll("[data-username]").forEach((el) => {
        el.textContent = state.user?.username || "Farmer";
    });

    document.querySelector("[data-nav-toggle]")?.addEventListener("click", () => {
        document.querySelector(".nav-links")?.classList.toggle("open");
    });

    document.querySelector("[data-profile-trigger]")?.addEventListener("click", () => {
        document.querySelector(".dropdown")?.classList.toggle("open");
    });


    document.querySelectorAll("[data-logout]").forEach((button) => {
        button.addEventListener("click", async () => {
            try {
                if (state.token) {
                    await request(API.logout, { method: "POST", headers: authHeaders() });
                }
            } catch (error) {
                console.warn(error.message);
            }
            localStorage.removeItem("fertilizerToken");
            localStorage.removeItem("fertilizerUser");
            localStorage.removeItem("lastRecommendation");
            location.href = "/login.html";
        });
    });

    document.querySelectorAll(".nav-links a").forEach((link) => {
        link.addEventListener("click", () => {
            document.querySelector(".nav-links")?.classList.remove("open");
        });
    });
}


function wirePasswordToggles() {
    document.querySelectorAll("[data-show-password]").forEach((button) => {
        button.addEventListener("click", () => {
            const input = button.closest(".password-field").querySelector("input");
            input.type = input.type === "password" ? "text" : "password";
            button.textContent = input.type === "password" ? "Show" : "Hide";
        });
    });
}

function wireAuthForms() {
    const loginForm = document.querySelector("#loginForm");
    loginForm?.addEventListener("submit", async (event) => {
        event.preventDefault();
        clearError(loginForm);
        const payload = Object.fromEntries(new FormData(loginForm));
        try {
            const data = await request(API.login, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(payload)
            });
            saveSession(data);
            toast("Welcome back, " + data.username);
            setTimeout(() => location.href = "/dashboard.html", 500);
        } catch (error) {
            showError(loginForm, error.message);
            toast(error.message, "error");
        }
    });

    const signupForm = document.querySelector("#signupForm");
    signupForm?.addEventListener("submit", async (event) => {
        event.preventDefault();
        clearError(signupForm);
        const payload = Object.fromEntries(new FormData(signupForm));
        if (payload.password !== payload.confirmPassword) {
            showError(signupForm, "Passwords do not match");
            return;
        }
        delete payload.confirmPassword;
        try {
            const data = await request(API.signup, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(payload)
            });
            saveSession(data);
            toast("Account created successfully");
            setTimeout(() => location.href = "/dashboard.html", 500);
        } catch (error) {
            showError(signupForm, error.message);
            toast(error.message, "error");
        }
    });

    const forgotForm = document.querySelector("#forgotForm");
    const forgotState = { email: "", resetToken: "" };

    forgotForm?.addEventListener("submit", async (event) => {
        event.preventDefault();
        clearError(forgotForm);
        const email = forgotForm.querySelector("#email")?.value.trim();
        try {
            const data = await request(API.forgot, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ email })
            });
            forgotState.email = data.email;
            showResetStep(forgotForm, "otp");
            toast("Verification code sent successfully", "success", {
                detail: "Your OTP: " + data.otp,
                duration: 30000,
                otp: true
            });
        } catch (error) {
            showError(forgotForm, error.message);
            toast(error.message, "error");
        }
    });

    forgotForm?.querySelector("[data-verify-otp]")?.addEventListener("click", async () => {
        clearError(forgotForm);
        const otp = forgotForm.querySelector("#otp")?.value.trim();
        try {
            const data = await request(API.verifyOtp, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ email: forgotState.email, otp })
            });
            forgotState.resetToken = data.resetToken;
            showResetStep(forgotForm, "password");
            toast(data.message);
        } catch (error) {
            showError(forgotForm, error.message);
            toast(error.message, "error");
        }
    });

    forgotForm?.querySelector("[data-reset-password]")?.addEventListener("click", async () => {
        clearError(forgotForm);
        const newPassword = forgotForm.querySelector("#newPassword")?.value || "";
        const confirmPassword = forgotForm.querySelector("#confirmPassword")?.value || "";
        if (newPassword !== confirmPassword) {
            showError(forgotForm, "Passwords do not match");
            return;
        }
        try {
            const data = await request(API.resetPassword, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    email: forgotState.email,
                    resetToken: forgotState.resetToken,
                    newPassword
                })
            });
            showResetStep(forgotForm, "done");
            toast(data.message);
        } catch (error) {
            showError(forgotForm, error.message);
            toast(error.message, "error");
        }
    });

    forgotForm?.querySelector("[data-change-email]")?.addEventListener("click", () => {
        forgotState.email = "";
        forgotState.resetToken = "";
        forgotForm.reset();
        clearError(forgotForm);
        showResetStep(forgotForm, "email");
    });
}

function saveSession(data) {
    state.token = data.token;
    state.user = {
        userId: data.userId,
        username: data.username,
        email: data.email,
        phone: data.phone
    };
    localStorage.setItem("fertilizerToken", data.token);
    localStorage.setItem("fertilizerUser", JSON.stringify(state.user));
}

function showResetStep(form, stepName) {
    form.querySelectorAll("[data-reset-step]").forEach((step) => {
        const active = step.dataset.resetStep === stepName;
        step.hidden = !active;
        step.classList.toggle("active", active);
    });
}

function wireSoilForm() {
    const form = document.querySelector("#soilForm");
    form?.addEventListener("submit", async (event) => {
        event.preventDefault();
        if (!requireAuth()) return;
        clearError(form);
        const values = Object.fromEntries(new FormData(form));
        const payload = {
            nitrogen: Number(values.nitrogen),
            phosphorus: Number(values.phosphorus),
            potassium: Number(values.potassium),
            phValue: Number(values.phValue),
            cropType: values.cropType
        };
        if ([payload.nitrogen, payload.phosphorus, payload.potassium, payload.phValue].some(Number.isNaN)) {
            showError(form, "Enter valid numeric soil values");
            return;
        }
        const validationMessage = validateSoilPayload(payload);
        if (validationMessage) {
            showError(form, validationMessage);
            return;
        }
        if (!payload.cropType) {
            showError(form, "Choose a crop from the suggestions");
            return;
        }
        showLoader(true);
        try {
            const data = await request(API.soil, {
                method: "POST",
                headers: authHeaders(),
                body: JSON.stringify(payload)
            });
            localStorage.setItem("lastRecommendation", JSON.stringify(data));
            toast("Recommendation generated");
            setTimeout(() => location.href = "/result.html", 600);
        } catch (error) {
            showError(form, error.message);
            toast(error.message, "error");
        } finally {
            showLoader(false);
        }
    });
}

function validateSoilPayload(payload) {
    const validations = [
        { value: payload.nitrogen, min: 0, max: 300, label: "Nitrogen" },
        { value: payload.phosphorus, min: 0, max: 300, label: "Phosphorus" },
        { value: payload.potassium, min: 0, max: 300, label: "Potassium" },
        { value: payload.phValue, min: 0, max: 14, label: "pH" }
    ];

    for (const item of validations) {
        if (item.value < item.min || item.value > item.max) {
            return `${item.label} must be between ${item.min} and ${item.max}`;
        }
    }
    return "";
}

async function loadCrops() {
    try {
        state.crops = await request(API.crops);
    } catch (error) {
        // Use hardcoded fallback crops
    }
}

async function setupCropAutocomplete() {
    const root = document.querySelector("[data-crop-selector]");
    if (!root) return;

    await loadCrops();

    const searchInput = root.querySelector("#cropSearch");
    const hiddenInput = root.querySelector("#cropType");
    const suggestions = root.querySelector("[data-crop-suggestions]");
    const selectedCard = root.querySelector("[data-selected-crop]");
    const spotlight = document.querySelector("[data-analysis-crop]");
    let activeIndex = -1;
    let currentMatches = [];

    const closeSuggestions = () => {
        suggestions.classList.remove("open");
        searchInput.setAttribute("aria-expanded", "false");
        activeIndex = -1;
    };

    const renderSuggestions = (query) => {
        const trimmed = query.trim().toLowerCase();
        hiddenInput.value = "";
        root.classList.remove("selected");

        if (!trimmed) {
            suggestions.innerHTML = "";
            closeSuggestions();
            return;
        }

        currentMatches = state.crops.filter((crop) => crop.name.toLowerCase().includes(trimmed));
        if (!currentMatches.length) {
            suggestions.innerHTML = `<div class="crop-option" role="option"><span>No matching crop found</span></div>`;
            suggestions.classList.add("open");
            searchInput.setAttribute("aria-expanded", "true");
            return;
        }

        suggestions.innerHTML = currentMatches.map((crop, index) => `
            <button type="button" class="crop-option" role="option" data-crop-index="${index}">
                <img src="${escapeHtml(crop.imageUrl)}" alt="${escapeHtml(crop.name)}">
                <div>
                    <strong>${escapeHtml(crop.name)}</strong>
                    <span>${escapeHtml([crop.category, crop.description || "Crop insight profile"].filter(Boolean).join(" - "))}</span>
                </div>
            </button>
        `).join("");
        suggestions.classList.add("open");
        searchInput.setAttribute("aria-expanded", "true");
    };

    const selectCrop = (crop) => {
        hiddenInput.value = crop.name;
        searchInput.value = crop.name;
        root.classList.add("selected");
        closeSuggestions();
        updateCropPreview(selectedCard, crop, "Selected Crop");
        updateCropPreview(spotlight, crop, "Crop Insight Ready");
    };

    searchInput.addEventListener("input", () => {
        renderSuggestions(searchInput.value);
        const exact = state.crops.find((crop) => crop.name.toLowerCase() === searchInput.value.trim().toLowerCase());
        if (exact) {
            selectCrop(exact);
        } else if (selectedCard) {
            selectedCard.hidden = true;
        }
    });

    searchInput.addEventListener("keydown", (event) => {
        if (!suggestions.classList.contains("open") || !currentMatches.length) return;
        if (event.key === "ArrowDown") {
            event.preventDefault();
            activeIndex = Math.min(activeIndex + 1, currentMatches.length - 1);
            markActiveSuggestion(suggestions, activeIndex);
        }
        if (event.key === "ArrowUp") {
            event.preventDefault();
            activeIndex = Math.max(activeIndex - 1, 0);
            markActiveSuggestion(suggestions, activeIndex);
        }
        if (event.key === "Enter" && activeIndex >= 0) {
            event.preventDefault();
            selectCrop(currentMatches[activeIndex]);
        }
        if (event.key === "Escape") {
            closeSuggestions();
        }
    });

    suggestions.addEventListener("click", (event) => {
        const option = event.target.closest("[data-crop-index]");
        if (!option) return;
        selectCrop(currentMatches[Number(option.dataset.cropIndex)]);
    });

    document.addEventListener("click", (event) => {
        if (!root.contains(event.target)) closeSuggestions();
    });
}

function markActiveSuggestion(container, activeIndex) {
    container.querySelectorAll(".crop-option").forEach((option, index) => {
        option.classList.toggle("active", index === activeIndex);
    });
}

function updateCropPreview(container, crop, label) {
    if (!container || !crop) return;
    const image = container.querySelector("img");
    const span = container.querySelector("span");
    const strong = container.querySelector("strong");
    if (image) {
        const cropKey = crop.name.toLowerCase();
        image.src = CROP_IMAGES[cropKey] || CROP_IMAGES['default'];
        image.alt = crop.name;
    }
    if (span) span.textContent = label;
    if (strong) strong.textContent = crop.name;
    container.hidden = false;
}

async function renderDashboard() {
    if (document.body.dataset.page !== "dashboard") return;
    if (!requireAuth()) return;
    try {
        const [stats, recent] = await Promise.all([
            request(API.stats, { headers: authHeaders() }),
            request(API.recent, { headers: authHeaders() })
        ]);
        // Defensive defaults if API returned empty/invalid payloads
        const safeStats = stats || {};
        safeStats.tests = Math.max(0, Number(safeStats.tests) || 0);
        safeStats.recommendations = Math.max(0, Number(safeStats.recommendations) || 0);
        safeStats.healthy = Math.max(0, Number(safeStats.healthy) || 0);
        safeStats.medium = Math.max(0, Number(safeStats.medium) || 0);
        safeStats.poor = Math.max(0, Number(safeStats.poor) || 0);
        const safeRecent = Array.isArray(recent) ? recent : [];
        setText("testsCount", stats.tests);
        setText("recommendationCount", safeStats.recommendations);
        setText("healthyCount", safeStats.healthy);
        setText("attentionCount", safeStats.medium + safeStats.poor);
        renderRecent(safeRecent);
        
        // Render Charts if Chart.js is loaded
        if (window.Chart) {
            renderDashboardCharts(safeStats, safeRecent);
        }
    } catch (error) {
        toast(error.message, "error");
    }
}

function renderDashboardCharts(stats, recent) {
    if (typeof Chart === 'undefined') return;
    const trendsCtx = document.getElementById('trendsChart');
    const healthCtx = document.getElementById('healthChart');
    
    // Ensure values are valid numbers
    stats.tests = Math.max(0, Number(stats.tests) || 0);
    stats.healthy = Math.max(0, Number(stats.healthy) || 0);
    stats.medium = Math.max(0, Number(stats.medium) || 0);
    stats.poor = Math.max(0, Number(stats.poor) || 0);
    
    // Get CSS vars for premium charting colors
    const style = getComputedStyle(document.body);
    const brandColor = style.getPropertyValue('--brand').trim() || '#10B981';
    const warningColor = style.getPropertyValue('--warning').trim() || '#F59E0B';
    const dangerColor = style.getPropertyValue('--danger').trim() || '#EF4444';
    const textColor = style.getPropertyValue('--muted').trim() || '#64748B';
    const gridColor = style.getPropertyValue('--line').trim() || '#E2E8F0';
    
    if (trendsCtx) {
        const ctx = trendsCtx.getContext('2d');
        const gradient = ctx.createLinearGradient(0, 0, 0, 260);
        gradient.addColorStop(0, `${brandColor}40`); // 25% opacity
        gradient.addColorStop(1, `${brandColor}00`); // 0% opacity

        const data = [Math.max(1, stats.tests - 4), Math.max(2, stats.tests - 2), stats.tests];
        new Chart(trendsCtx, {
            type: 'line',
            data: {
                labels: ['Last Month', 'Last Week', 'This Week'],
                datasets: [{
                    label: 'Analyses',
                    data: data,
                    borderColor: brandColor,
                    backgroundColor: gradient,
                    borderWidth: 3,
                    pointBackgroundColor: '#fff',
                    pointBorderColor: brandColor,
                    pointBorderWidth: 2,
                    pointRadius: 4,
                    pointHoverRadius: 6,
                    tension: 0.4,
                    fill: true
                }]
            },
            options: { 
                responsive: true, 
                maintainAspectRatio: false, 
                plugins: { 
                    legend: { display: false },
                    tooltip: {
                        backgroundColor: style.getPropertyValue('--surface').trim(),
                        titleColor: style.getPropertyValue('--text').trim(),
                        bodyColor: style.getPropertyValue('--muted').trim(),
                        borderColor: gridColor,
                        borderWidth: 1,
                        padding: 12,
                        boxPadding: 6,
                        usePointStyle: true,
                    }
                },
                scales: {
                    x: {
                        grid: { display: false, drawBorder: false },
                        ticks: { color: textColor, font: { family: 'Inter' } }
                    },
                    y: {
                        grid: { color: gridColor, drawBorder: false, borderDash: [5, 5] },
                        ticks: { color: textColor, font: { family: 'Inter' }, stepSize: 1 }
                    }
                }
            }
        });
    }

    if (healthCtx) {
        new Chart(healthCtx, {
            type: 'doughnut',
            data: {
                labels: ['Healthy', 'Moderate', 'Critical'],
                datasets: [{
                    data: [stats.healthy || 1, stats.medium || 0, stats.poor || 0],
                    backgroundColor: [brandColor, warningColor, dangerColor],
                    borderWidth: 0,
                    hoverOffset: 4
                }]
            },
            options: { 
                responsive: true, 
                maintainAspectRatio: false, 
                cutout: '75%',
                plugins: {
                    legend: {
                        position: 'bottom',
                        labels: {
                            color: textColor,
                            usePointStyle: true,
                            padding: 20,
                            font: { family: 'Inter', size: 13 }
                        }
                    },
                    tooltip: {
                        backgroundColor: style.getPropertyValue('--surface').trim(),
                        titleColor: style.getPropertyValue('--text').trim(),
                        bodyColor: style.getPropertyValue('--muted').trim(),
                        borderColor: gridColor,
                        borderWidth: 1,
                        padding: 12
                    }
                }
            }
        });
    }
}

function renderRecent(items) {
    const container = document.querySelector("#recentRecommendations");
    if (!container) return;
    if (!items.length) {
        container.innerHTML = `
            <div class="card empty-state">
                <div>
                    <strong>No guidance yet</strong>
                    <span>Start with a soil analysis to see personalized crop insights here.</span>
                </div>
            </div>`;
        return;
    }
    container.innerHTML = items.map(renderRecommendationCard).join("");
}

function renderResult() {
    if (document.body.dataset.page !== "result") return;
    if (!requireAuth()) return;
    const data = JSON.parse(localStorage.getItem("lastRecommendation") || "null");
    const container = document.querySelector("#resultCards");
    if (!data || !container) {
        container.innerHTML = `
            <div class="card empty-state">
                <div>
                    <strong>No soil analysis found</strong>
                    <span>Enter field readings to generate fresh fertilizer guidance.</span>
                </div>
            </div>`;
        return;
    }
    
    // Ensure valid data
    const cropType = data.cropType || "Unknown Crop";
    const nitrogen = Number(data.nitrogen) || 0;
    const phosphorus = Number(data.phosphorus) || 0;
    const potassium = Number(data.potassium) || 0;
    const phValue = Number(data.phValue) || 7;
    
    // Set basic info
    setText("resultCrop", cropType);
    setText("resultHealth", healthLabel(data.soilHealthStatus || "moderate"));
    
    // Populate stats
    setText("statN", nitrogen > 0 ? nitrogen : "—");
    setText("statP", phosphorus > 0 ? phosphorus : "—");
    setText("statK", potassium > 0 ? potassium : "—");
    setText("statPH", phValue > 0 ? phValue : "—");

    // Calculate health score percentage
    let score = 100;
    if (nitrogen < 20 || nitrogen > 80) score -= 15;
    if (phosphorus < 10 || phosphorus > 60) score -= 15;
    if (potassium < 100 || potassium > 400) score -= 15;
    if (phValue < 5.5 || phValue > 8.0) score -= 15;
    score = Math.max(0, Math.min(100, score));
    
    // Animate score
    const circle = document.getElementById("healthProgress");
    const scoreText = document.getElementById("healthScoreText");
    if (circle && scoreText) {
        setTimeout(() => {
            circle.style.strokeDasharray = `${score}, 100`;
            scoreText.textContent = `${score}%`;
        }, 100);
    }

    const resultCropImage = document.getElementById("resultCropImage");
    const cropImage = data.recommendations?.[0]?.cropImage || CROP_IMAGES[(cropType || "").toLowerCase()] || CROP_IMAGES['default'];
    if (resultCropImage) {
        resultCropImage.src = cropImage;
        resultCropImage.alt = cropType;
    }
    const dot = document.getElementById("resultHealthDot");
    if (dot) {
        dot.className = "dot " + healthColor(data.soilHealthStatus);
    }
    
    // Deficiency Detection
    const alertContainer = document.getElementById("deficiencyAlerts");
    const hasDeficiencies = nitrogen < 25 || phosphorus < 15 || potassium < 120;
    const targets = { minN: 25, minP: 15, minK: 120 };
    if (alertContainer && hasDeficiencies) {
        let alertsHtml = '<div style="margin-top: 32px; padding: 24px; border-radius: 16px; background: color-mix(in srgb, var(--danger) 10%, transparent); border: 1px solid color-mix(in srgb, var(--danger) 20%, transparent);"><div style="display: flex; align-items: center; gap: 12px; margin-bottom: 16px;"><svg width="24" height="24" fill="none" stroke="var(--danger)" stroke-width="2"><path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/><line x1="12" y1="9" x2="12" y2="13"/><line x1="12" y1="17" x2="12.01" y2="17"/></svg><h3 style="margin: 0; color: var(--danger); font-size: 1.1rem;">Nutrient Deficiencies Detected</h3></div><ul style="margin: 0; padding-left: 24px; color: var(--text); line-height: 1.6; font-size: 0.95rem;">';
        
        if (nitrogen < targets.minN && nitrogen > 0) alertsHtml += `<li><strong>Nitrogen (${nitrogen}mg/kg vs ${targets.minN}mg/kg):</strong> Increase nitrogen for better leaf growth.</li>`;
        if (phosphorus < targets.minP && phosphorus > 0) alertsHtml += `<li><strong>Phosphorus (${phosphorus}mg/kg vs ${targets.minP}mg/kg):</strong> Boost phosphorus for root development.</li>`;
        if (potassium < targets.minK && potassium > 0) alertsHtml += `<li><strong>Potassium (${potassium}mg/kg vs ${targets.minK}mg/kg):</strong> Add potassium for disease resistance.</li>`;
        
        alertsHtml += '</ul></div>';
        alertContainer.innerHTML = alertsHtml;
    }

    container.innerHTML = (data.recommendations || []).map(rec => {
        const cropKey = (cropType || "").toLowerCase();
        const imgSrc = CROP_IMAGES[cropKey] || CROP_IMAGES['default'];
        const dosage = rec.dosage || "As per recommendation";
        const explanation = rec.explanation || "Based on soil nutrient analysis.";
        const fertilizerName = rec.fertilizerName || "Fertilizer";
        return `
            <article class="card recommendation-card" style="display: flex; flex-direction: column; gap: 24px; padding: 32px; border-radius: 20px;">
                <div style="flex-shrink: 0; width: 140px; height: 140px; border-radius: 16px; overflow: hidden; background: var(--surface-2);">
                    <img src="${escapeHtml(imgSrc)}" alt="${escapeHtml(fertilizerName)}" style="width: 100%; height: 100%; object-fit: cover;">
                </div>
                <div class="recommendation-content" style="flex: 1;">
                    <div style="display: flex; justify-content: space-between; align-items: flex-start; flex-wrap: wrap; gap: 16px;">
                        <div>
                            <span class="eyebrow" style="color: var(--brand); letter-spacing: 0.05em;">RECOMMENDED FERTILIZER</span>
                            <h3 style="font-size: 1.6rem; margin: 4px 0 12px; letter-spacing: -0.01em;">${escapeHtml(fertilizerName)}</h3>
                        </div>
                        <div style="text-align: right; background: var(--surface-2); padding: 12px 20px; border-radius: 12px; border: 1px solid var(--line);">
                            <span class="eyebrow" style="display: block; margin-bottom: 4px;">APPLICATION DOSE</span>
                            <strong style="font-size: 1.3rem; color: var(--text);">${escapeHtml(dosage)}</strong>
                        </div>
                    </div>
                    
                    <div class="explanation-box" style="margin-top: 20px; padding: 20px; background: color-mix(in srgb, var(--accent) 5%, transparent); border-left: 4px solid var(--accent); border-radius: 0 12px 12px 0;">
                        <strong style="display: block; margin-bottom: 8px; color: var(--text); font-size: 1rem;">Why this recommendation?</strong>
                        <p style="margin: 0; color: var(--muted); line-height: 1.6; font-size: 0.95rem;">${escapeHtml(explanation)}</p>
                    </div>
                </div>
            </article>`;
    }).join("");
    
    // PDF Download
    const downloadBtn = document.getElementById("downloadPdfBtn");
    if (downloadBtn) {
        downloadBtn.addEventListener("click", () => downloadPDF(data));
    }
}

function downloadPDF(data) {
    if (!window.jspdf) {
        toast("Loading PDF generator, please try again in a moment...", "warning");
        const script = document.createElement("script");
        script.src = "https://cdnjs.cloudflare.com/ajax/libs/jspdf/2.5.1/jspdf.umd.min.js";
        script.onload = () => downloadPDF(data);
        document.body.appendChild(script);
        return;
    }
    const { jsPDF } = window.jspdf;
    const doc = new jsPDF();
    
    doc.setFontSize(22);
    doc.text("FertiSmart Soil Analysis Report", 14, 20);
    
    doc.setFontSize(14);
    doc.text(`Crop: ${data.cropType}`, 14, 30);
    doc.text(`Health Status: ${healthLabel(data.soilHealthStatus)}`, 14, 38);
    
    doc.setFontSize(12);
    doc.text(`Nitrogen (N): ${data.nitrogen}`, 14, 50);
    doc.text(`Phosphorus (P): ${data.phosphorus}`, 14, 56);
    doc.text(`Potassium (K): ${data.potassium}`, 14, 62);
    doc.text(`pH Level: ${data.phValue}`, 14, 68);
    
    let yPos = 85;
    data.recommendations.forEach((rec, idx) => {
        doc.setFontSize(14);
        doc.text(`Recommendation ${idx + 1}: ${rec.fertilizerName}`, 14, yPos);
        doc.setFontSize(10);
        const splitText = doc.splitTextToSize(rec.recommendationDetails, 180);
        doc.text(splitText, 14, yPos + 6);
        yPos += 10 + (splitText.length * 5);
    });
    
    doc.save(`FertiSmart_Report_${new Date().toISOString().slice(0,10)}.pdf`);
}

let historyDataCache = [];

async function renderHistory() {
    if (document.body.dataset.page !== "history") return;
    if (!requireAuth()) return;
    const tbody = document.querySelector("#historyTable");
    
    // Add event listeners for filters and export
    const searchInput = document.getElementById("historySearch");
    const statusFilter = document.getElementById("historyStatusFilter");
    const dateFilter = document.getElementById("historyDateFilter");
    const exportBtn = document.getElementById("exportHistoryBtn");
    
    const filterAndRender = () => {
        if (!historyDataCache.length) return;
        let filtered = historyDataCache;
        
        const search = (searchInput?.value || "").toLowerCase();
        const status = statusFilter?.value || "";
        const dateStr = dateFilter?.value || "";
        
        if (search) {
            filtered = filtered.filter(i => 
                (i.cropType || "").toLowerCase().includes(search) || 
                (i.fertilizerName || "").toLowerCase().includes(search)
            );
        }
        
        if (status) {
            filtered = filtered.filter(i => statusLabel(i.levelColor) === status);
        }
        
        if (dateStr) {
            filtered = filtered.filter(i => i.createdAt && i.createdAt.startsWith(dateStr));
        }
        
        if (!filtered.length) {
            tbody.innerHTML = `<tr><td colspan="7"><div class="empty-state"><div><strong>No matches found</strong><span>Try adjusting your search or filter criteria.</span></div></div></td></tr>`;
            return;
        }
        
        tbody.innerHTML = filtered.map((item) => {
            const cropKey = (item.cropType || 'default').toLowerCase();
            const fertKey = (item.fertilizerName || 'default');
            const cImg = CROP_IMAGES[cropKey] || CROP_IMAGES['default'];
            const fImg = FERTILIZER_IMAGES[fertKey] || FERTILIZER_IMAGES['default'];
            const pillClass = `pill-${item.levelColor || 'green'}`;
            
            return `
            <tr>
                <td style="width: 64px;"><img class="mini-img" src="${cImg}" alt="${escapeHtml(item.cropType)}"></td>
                <td><strong>${escapeHtml(item.cropType)}</strong></td>
                <td style="width: 64px;"><img class="mini-img" src="${fImg}" alt="${escapeHtml(item.fertilizerName)}"></td>
                <td><strong>${escapeHtml(item.fertilizerName)}</strong></td>
                <td><span style="opacity:0.8;">${escapeHtml(item.nutrientDeficiency)}</span></td>
                <td><span class="status-pill ${pillClass}"><span class="dot ${item.levelColor}"></span>${statusLabel(item.levelColor)}</span></td>
                <td style="font-variant-numeric: tabular-nums; white-space: nowrap;">${formatDate(item.createdAt)}</td>
            </tr>
            `;
        }).join("");
    };

    if (searchInput) searchInput.addEventListener("input", filterAndRender);
    if (statusFilter) statusFilter.addEventListener("change", filterAndRender);
    if (dateFilter) dateFilter.addEventListener("change", filterAndRender);
    
    if (exportBtn) {
        exportBtn.addEventListener("click", () => {
            if (!window.jspdf) {
                toast("Loading PDF generator, please try again in a moment...", "warning");
                const script = document.createElement("script");
                script.src = "https://cdnjs.cloudflare.com/ajax/libs/jspdf/2.5.1/jspdf.umd.min.js";
                script.onload = () => {
                    const script2 = document.createElement("script");
                    script2.src = "https://cdnjs.cloudflare.com/ajax/libs/jspdf-autotable/3.8.2/jspdf.plugin.autotable.min.js";
                    script2.onload = () => exportBtn.click();
                    document.body.appendChild(script2);
                };
                document.body.appendChild(script);
                return;
            }
            if (!window.jspdf.jsPDF.API.autoTable) return;
            const doc = new window.jspdf.jsPDF();
            doc.text("FertiSmart Recommendation History", 14, 20);
            
            const rows = Array.from(tbody.querySelectorAll("tr")).map(tr => {
                const tds = tr.querySelectorAll("td");
                if (tds.length < 7) return null;
                return [
                    tds[1].textContent.trim(),
                    tds[3].textContent.trim(),
                    tds[4].textContent.trim(),
                    tds[5].textContent.trim(),
                    tds[6].textContent.trim()
                ];
            }).filter(Boolean);
            
            doc.autoTable({
                startY: 30,
                head: [['Crop', 'Fertilizer', 'Deficiency', 'Status', 'Date']],
                body: rows,
            });
            doc.save("FertiSmart_History.pdf");
        });
    }

    try {
        const data = await request(API.recommendations, { headers: authHeaders() });
        historyDataCache = data || [];
        if (!historyDataCache.length) {
            tbody.innerHTML = `<tr><td colspan="7"><div class="empty-state"><div><strong>No records yet</strong><span>Your soil guidance history will appear after your first analysis.</span></div></div></td></tr>`;
            return;
        }
        filterAndRender();
    } catch (error) {
        toast(error.message, "error");
    }
}

function renderProfile() {
    if (document.body.dataset.page !== "profile") return;
    if (!requireAuth()) return;
    setText("profileName", state.user.username);
    setText("profileEmail", state.user.email);
    setText("profilePhone", state.user.phone);
}

function renderRecommendationCard(item) {
    // Mock fertilizer details for the cards based on name
    const fertDetails = getFertilizerDetails(item.fertilizerName);

    return `
        <article class="card recommendation-card enhanced-card">
            <div class="rec-header-layout">
                <div class="rec-images">
                    <div class="image-panel"><img src="${item.fertilizerImage}" alt="${escapeHtml(item.fertilizerName)} fertilizer"><span>Guidance</span></div>
                    <div class="image-panel"><img src="${item.cropImage}" alt="${escapeHtml(item.cropType)} crop"><span>${escapeHtml(item.cropType)}</span></div>
                </div>
                <div class="rec-body">
                    <div style="display: flex; gap: 8px; margin-bottom: 6px; flex-wrap: wrap;">
                        <span class="status-pill"><span class="dot ${item.levelColor}"></span>${escapeHtml(item.nutrientDeficiency)}</span>
                    </div>
                    <h3 style="margin-top: 8px;">${escapeHtml(item.fertilizerName)}</h3>
                    
                    <div class="fertilizer-info-grid">
                        <div class="info-cell">
                            <span class="info-label">Type</span>
                            <span class="info-value">${fertDetails.type}</span>
                        </div>
                        <div class="info-cell">
                            <span class="info-label">Primary Nutrients</span>
                            <span class="info-value">${fertDetails.nutrients}</span>
                        </div>
                    </div>
                    
                    <div class="why-section">
                        <h4>Why This Recommendation?</h4>
                        <p>${escapeHtml(item.recommendationDetails)}</p>
                    </div>

                    <div class="benefits-section">
                        <h4>Key Benefits</h4>
                        <ul class="benefits-list">
                            ${fertDetails.benefits.map(b => `<li><svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round" class="check-icon"><polyline points="20 6 9 17 4 12"></polyline></svg>${b}</li>`).join("")}
                        </ul>
                    </div>
                </div>
            </div>
        </article>
    `;
}

function getFertilizerDetails(name) {
    const lower = (name || "").toLowerCase();
    if (lower.includes("urea")) return { type: "Nitrogenous", nutrients: "46% Nitrogen", benefits: ["Rapid leaf growth", "Improves green color", "High solubility"] };
    if (lower.includes("dap") || lower.includes("diammonium")) return { type: "Complex", nutrients: "18% N, 46% P", benefits: ["Strong root development", "Early growth boost", "Improves resistance"] };
    if (lower.includes("mop") || lower.includes("potash")) return { type: "Potassic", nutrients: "60% Potassium", benefits: ["Enhances water retention", "Improves crop quality", "Disease resistance"] };
    if (lower.includes("npk")) return { type: "Complex NPK", nutrients: "Balanced N-P-K", benefits: ["Complete nutrient profile", "Sustained growth", "Versatile application"] };
    if (lower.includes("superphosphate") || lower.includes("ssp")) return { type: "Phosphatic", nutrients: "16% Phosphorus", benefits: ["Soil conditioning", "Root stimulation", "Contains Sulphur & Calcium"] };
    return { type: "General Purpose", nutrients: "Mixed Nutrients", benefits: ["Improves soil fertility", "Supports general growth", "Prevents deficiencies"] };
}

function requireAuth() {
    if (!state.token) {
        // Redirect to login when not authenticated. Return false so callers can abort gracefully.
        location.href = "/login.html";
        return false;
    }
    return true;
}

function showError(form, message) {
    const error = form.querySelector(".error-text");
    if (error) error.textContent = message;
}

function clearError(form) {
    const error = form.querySelector(".error-text");
    if (error) error.textContent = "";
}

function setText(id, value) {
    const el = document.getElementById(id);
    if (el) el.textContent = (value !== null && value !== undefined) ? String(value) : "—";
}

function showLoader(open) {
    document.querySelector(".loader")?.classList.toggle("open", open);
}

function toast(message, type = "success", options = {}) {
    const stack = document.querySelector(".toast-stack") || createToastStack();
    const node = document.createElement("div");
    node.className = `toast ${type}${options.otp ? " otp-toast" : ""}`;
    const duration = options.duration || 3600;
    node.innerHTML = `
        <div class="toast-icon" aria-hidden="true"></div>
        <div class="toast-content">
            <strong>${escapeHtml(message)}</strong>
            ${options.detail ? `<span>${escapeHtml(options.detail)}</span>` : ""}
        </div>
        <div class="toast-progress" style="animation-duration: ${duration}ms"></div>
    `;
    stack.appendChild(node);
    setTimeout(() => {
        node.classList.add("closing");
        setTimeout(() => node.remove(), 260);
    }, duration);
}

function createToastStack() {
    const stack = document.createElement("div");
    stack.className = "toast-stack";
    document.body.appendChild(stack);
    return stack;
}

function formatDate(value) {
    if (!value) return "-";
    try {
        // Show only the date portion on the frontend (no time)
        return new Date(value).toLocaleDateString();
    } catch (e) {
        return String(value).split('T')[0] || String(value);
    }
}

function healthColor(status) {
    if (status === "Poor") return "red";
    if (status === "Medium") return "yellow";
    return "green";
}

function healthLabel(status) {
    if (status === "Poor") return "Critical";
    if (status === "Medium") return "Moderate";
    return "Healthy";
}

function statusLabel(color) {
    if (color === "red") return "Critical";
    if (color === "yellow") return "Moderate";
    return "Healthy";
}

function escapeHtml(value) {
    return String(value ?? "")
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}
