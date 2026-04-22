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
