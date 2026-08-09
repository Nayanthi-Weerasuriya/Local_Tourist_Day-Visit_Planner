import { getJSON, postJSON } from "./api.js";
import { applyButtonLoading, showToast } from "./common.js";

const loginForm = document.getElementById("login-form");
const submitButton = document.getElementById("login-submit");

async function redirectIfAlreadyLoggedIn() {
    try {
        await getJSON("/api/admin/auth/me");
        window.location.href = "/admin-dashboard.html";
    } catch (error) {
        if (error.status !== 401) {
            showToast(error.message, "error");
        }
    }
}

loginForm.addEventListener("submit", async (event) => {
    event.preventDefault();

    const username = document.getElementById("username").value.trim();
    const password = document.getElementById("password").value;

    if (!username || !password) {
        showToast("Username and password are required.", "error");
        return;
    }

    applyButtonLoading(submitButton, true, "Logging in...");

    try {
        await postJSON("/api/admin/auth/login", { username, password });
        showToast("Login successful.", "success");
        window.location.href = "/admin-dashboard.html";
    } catch (error) {
        showToast(error.message, "error");
    } finally {
        applyButtonLoading(submitButton, false);
    }
});

redirectIfAlreadyLoggedIn();
