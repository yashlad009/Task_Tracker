window.APP_CONFIG = window.APP_CONFIG || {};

window.getApiBaseUrl = function getApiBaseUrl() {
    if (Object.prototype.hasOwnProperty.call(window.APP_CONFIG, "API_BASE_URL")) {
        return window.APP_CONFIG.API_BASE_URL;
    }

    const isLocal =
        window.location.protocol === "file:" ||
        window.location.hostname === "localhost" ||
        window.location.hostname === "127.0.0.1";

    return isLocal ? "http://localhost:8080" : "";
};

window.readStoredSession = function readStoredSession(storageKey) {
    const raw = localStorage.getItem(storageKey);
    if (!raw) {
        return null;
    }

    try {
        return JSON.parse(raw);
    } catch (error) {
        localStorage.removeItem(storageKey);
        return null;
    }
};

window.isValidUserSession = function isValidUserSession(session) {
    if (!session || typeof session !== "object") {
        return false;
    }

    const email = typeof session.email === "string" ? session.email.trim() : "";
    const userId = session.id || session._id;
    return email.length > 0 && typeof userId === "string" && userId.trim().length > 0;
};

window.clearAuthSessions = function clearAuthSessions() {
    localStorage.removeItem("user_session");
    localStorage.removeItem("admin_session");
};

window.redirectToLogin = function redirectToLogin() {
    window.clearAuthSessions();
    window.location.href = "login.html";
};

window.requireUserSession = function requireUserSession() {
    const session = window.readStoredSession("user_session");
    if (!window.isValidUserSession(session)) {
        window.redirectToLogin();
        return null;
    }

    return session;
};

window.requireAdminSession = function requireAdminSession() {
    const session = window.readStoredSession("admin_session");
    if (!window.isValidUserSession(session) || session.role !== "ADMIN") {
        window.redirectToLogin();
        return null;
    }

    return session;
};
