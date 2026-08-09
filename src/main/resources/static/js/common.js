import { getJSON, postJSON } from "./api.js";

const plannerStorageKey = "tourPlanner.planId";

export function showToast(message, type = "info") {
    const container = document.getElementById("toast-container");
    if (!container) {
        return;
    }

    const toast = document.createElement("div");
    toast.className = `toast ${type}`;
    toast.textContent = message;
    container.appendChild(toast);

    window.setTimeout(() => {
        toast.remove();
    }, 3200);
}

export function renderLoading(message = "Loading data...") {
    return `<div class="loading-state">${message}</div>`;
}

export function renderEmpty(message, actionHtml = "") {
    return `<div class="empty-state"><p>${message}</p>${actionHtml}</div>`;
}

export function renderError(message) {
    return `<div class="error-state"><p>${message}</p></div>`;
}

export function getQueryParam(name) {
    const params = new URLSearchParams(window.location.search);
    return params.get(name);
}

export function truncate(text, length = 140) {
    if (!text) {
        return "";
    }
    return text.length > length ? `${text.slice(0, length).trim()}...` : text;
}

export function createPlaceCard(place) {
    const categoryClass = place.category ? place.category.toLowerCase() : "";
    return `
        <article class="place-card">
            <img src="${place.imageUrl}" alt="${place.name}" onerror="this.onerror=null; this.src='/images/default-place.png';">
            <div class="place-card-body">
                <div class="card-topline">
                    <h3>${place.name}</h3>
                    <span class="badge ${categoryClass}">${place.category}</span>
                </div>
                <p>${truncate(place.description)}</p>
                <div class="meta-line">
                    <span>${place.distanceKm} km from Rajagiriya</span>
                    <span>${place.openingTime} - ${place.closingTime}</span>
                </div>
                <div class="inline-actions">
                    <a class="button primary small" href="/place-details.html?id=${place.id}">View details</a>
                    <button class="button secondary small add-to-plan-btn" type="button" data-place-id="${place.id}" data-place-name="${place.name}">Add to Plan</button>
                </div>
            </div>
        </article>
    `;
}

export function formatRouteText(totalPlaces) {
    if (!totalPlaces) {
        return "No stops yet";
    }
    return `${totalPlaces} planned stop${totalPlaces > 1 ? "s" : ""}`;
}

export function getStoredPlanId() {
    return window.localStorage.getItem(plannerStorageKey);
}

export function clearStoredPlanId() {
    window.localStorage.removeItem(plannerStorageKey);
}

export async function ensurePlanId() {
    let planId = getStoredPlanId();

    if (planId) {
        return planId;
    }

    const createdPlan = await postJSON("/api/day-plans", {});
    planId = String(createdPlan.id);
    window.localStorage.setItem(plannerStorageKey, planId);
    return planId;
}

export async function getActivePlan() {
    const planId = getStoredPlanId();
    if (!planId) {
        return null;
    }

    try {
        return await getJSON(`/api/day-plans/${planId}`);
    } catch (error) {
        if (error.status === 404) {
            clearStoredPlanId();
            return null;
        }
        throw error;
    }
}

export function collectFormData(formElement) {
    const formData = new FormData(formElement);
    const payload = Object.fromEntries(formData.entries());
    payload.active = formElement.elements.active ? formElement.elements.active.checked : true;
    return payload;
}

export function applyButtonLoading(button, isLoading, loadingText = "Please wait...") {
    if (!button) {
        return;
    }

    if (!button.dataset.originalText) {
        button.dataset.originalText = button.textContent;
    }

    button.disabled = isLoading;
    button.textContent = isLoading ? loadingText : button.dataset.originalText;
}

export async function handleAddToPlan(event) {
    const btn = event.target.closest(".add-to-plan-btn");
    if (!btn) {
        return;
    }

    const placeId = btn.dataset.placeId;
    const placeName = btn.dataset.placeName;
    
    try {
        const planId = await ensurePlanId();
        await postJSON(`/api/day-plans/${planId}/places`, { placeId: Number(placeId) });
        showToast(`${placeName} added to your day plan.`, "success");
    } catch (error) {
        showToast(error.message, "error");
    }
}
