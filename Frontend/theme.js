const THEME_STORAGE_KEY = "apms-theme";

function applyTheme(theme) {
    const nextTheme = theme === "light" ? "light" : "dark";
    document.documentElement.setAttribute("data-theme", nextTheme);
    localStorage.setItem(THEME_STORAGE_KEY, nextTheme);

    const toggle = document.getElementById("themeToggle");
    if (toggle) {
        toggle.setAttribute("aria-pressed", String(nextTheme === "light"));
        toggle.setAttribute("title", nextTheme === "light" ? "Switch to dark mode" : "Switch to light mode");
    }
}

function initTheme() {
    const savedTheme = localStorage.getItem(THEME_STORAGE_KEY) || "dark";
    applyTheme(savedTheme);

    const toggle = document.getElementById("themeToggle");
    if (toggle) {
        toggle.addEventListener("click", () => {
            const currentTheme = document.documentElement.getAttribute("data-theme") || "dark";
            applyTheme(currentTheme === "dark" ? "light" : "dark");
        });
    }

    document.querySelectorAll("button, .btn-primary, .btn-sm, .tab-btn, .cat-card").forEach((element) => {
        element.addEventListener("click", createRipple);
    });
}

function createRipple(event) {
    const target = event.currentTarget;
    if (!target || target.classList.contains("theme-toggle")) {
        return;
    }

    const ripple = document.createElement("span");
    const rect = target.getBoundingClientRect();
    const isCategoryCard = target.classList.contains("cat-card");
    const size = isCategoryCard
        ? Math.max(rect.width, rect.height) * 0.45
        : Math.max(rect.width, rect.height);
    ripple.className = "ripple";
    ripple.style.width = ripple.style.height = `${size}px`;
    ripple.style.left = `${event.clientX - rect.left - size / 2}px`;
    ripple.style.top = `${event.clientY - rect.top - size / 2}px`;
    if (isCategoryCard) {
        ripple.style.background = "rgba(255, 255, 255, 0.22)";
    }

    target.querySelectorAll(".ripple").forEach((existingRipple) => existingRipple.remove());
    target.appendChild(ripple);
    ripple.addEventListener("animationend", () => ripple.remove(), { once: true });
}

document.addEventListener("DOMContentLoaded", initTheme);
