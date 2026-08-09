import { deleteJSON, getJSON, postJSON, putJSON } from "./api.js";
import { applyButtonLoading, collectFormData, renderEmpty, showToast } from "./common.js";

const statsContainer = document.getElementById("dashboard-stats");
const placesTableBody = document.getElementById("places-table-body");
const placeForm = document.getElementById("place-form");
const formTitle = document.getElementById("form-title");
const savePlaceButton = document.getElementById("save-place-btn");
const cancelEditButton = document.getElementById("cancel-edit-btn");
const resetFormButton = document.getElementById("reset-form-btn");
const logoutButton = document.getElementById("logout-btn");

let placesCache = [];
let editingPlaceId = null;

function resetFormState() {
    editingPlaceId = null;
    formTitle.textContent = "Add New Place";
    savePlaceButton.textContent = "Save place";
    cancelEditButton.classList.add("hidden");
    placeForm.reset();
    placeForm.elements.active.checked = true;
}

function renderStats(places) {
    const activePlaces = places.filter((place) => place.active).length;
    const categories = new Set(places.map((place) => place.category));
    const farthestPlace = places.reduce((selected, place) => {
        if (!selected || place.distanceKm > selected.distanceKm) {
            return place;
        }
        return selected;
    }, null);

    const stats = [
        { label: "Total places", value: places.length },
        { label: "Active places", value: activePlaces },
        { label: "Categories covered", value: categories.size },
        { label: "Farthest stop", value: farthestPlace ? `${farthestPlace.distanceKm} km` : "0 km" }
    ];

    statsContainer.innerHTML = stats.map((stat) => `
        <article class="stat-card">
            <p class="eyebrow">${stat.label}</p>
            <strong>${stat.value}</strong>
        </article>
    `).join("");
}

function renderTable(places) {
    if (!places.length) {
        placesTableBody.innerHTML = `
            <tr>
                <td colspan="5">${renderEmpty("No places available yet.")}</td>
            </tr>
        `;
        return;
    }

    placesTableBody.innerHTML = places.map((place) => `
        <tr>
            <td>
                <strong>${place.name}</strong>
                <div class="status-text">${place.address}</div>
            </td>
            <td>${place.category}</td>
            <td>${place.distanceKm} km</td>
            <td>
                <span class="status-pill ${place.active ? "active" : "inactive"}">
                    ${place.active ? "Active" : "Inactive"}
                </span>
            </td>
            <td>
                <div class="inline-actions">
                    <button class="button secondary small edit-btn" type="button" data-id="${place.id}">Edit</button>
                    <button class="button danger small delete-btn" type="button" data-id="${place.id}">Delete</button>
                </div>
            </td>
        </tr>
    `).join("");
}

function populateForm(place) {
    editingPlaceId = place.id;
    formTitle.textContent = `Edit ${place.name}`;
    savePlaceButton.textContent = "Update place";
    cancelEditButton.classList.remove("hidden");

    placeForm.elements.name.value = place.name;
    placeForm.elements.category.value = place.category;
    placeForm.elements.distanceKm.value = place.distanceKm;
    placeForm.elements.imageUrl.value = place.imageUrl;
    placeForm.elements.openingTime.value = place.openingTime;
    placeForm.elements.closingTime.value = place.closingTime;
    placeForm.elements.latitude.value = place.latitude;
    placeForm.elements.longitude.value = place.longitude;
    placeForm.elements.address.value = place.address;
    placeForm.elements.description.value = place.description;
    placeForm.elements.travelTips.value = place.travelTips;
    placeForm.elements.active.checked = place.active;
    window.scrollTo({ top: 0, behavior: "smooth" });
}

async function loadPlaces() {
    try {
        placesCache = await getJSON("/api/admin/places");
        renderStats(placesCache);
        renderTable(placesCache);
    } catch (error) {
        if (error.status === 401) {
            window.location.href = "/management-portal.html";
            return;
        }
        showToast(error.message, "error");
    }
}

async function ensureAuthenticated() {
    try {
        await getJSON("/api/admin/auth/me");
        await loadPlaces();
    } catch (error) {
        window.location.href = "/management-portal.html";
    }
}

placeForm.addEventListener("submit", async (event) => {
    event.preventDefault();

    const payload = collectFormData(placeForm);
    payload.distanceKm = Number(payload.distanceKm);
    payload.latitude = Number(payload.latitude);
    payload.longitude = Number(payload.longitude);

    applyButtonLoading(savePlaceButton, true, editingPlaceId ? "Updating..." : "Saving...");

    try {
        if (editingPlaceId) {
            await putJSON(`/api/admin/places/${editingPlaceId}`, payload);
            showToast("Place updated successfully.", "success");
        } else {
            await postJSON("/api/admin/places", payload);
            showToast("Place added successfully.", "success");
        }

        resetFormState();
        await loadPlaces();
    } catch (error) {
        const validationMessage = Object.values(error.fieldErrors || {}).join(" ");
        showToast(validationMessage || error.message, "error");
    } finally {
        applyButtonLoading(savePlaceButton, false);
    }
});

placesTableBody.addEventListener("click", async (event) => {
    const placeId = event.target.dataset.id;
    if (!placeId) {
        return;
    }

    const selectedPlace = placesCache.find((place) => place.id === Number(placeId));

    if (event.target.classList.contains("edit-btn")) {
        populateForm(selectedPlace);
        return;
    }

    if (event.target.classList.contains("delete-btn")) {
        const confirmed = window.confirm(`Delete "${selectedPlace.name}" from the system?`);
        if (!confirmed) {
            return;
        }

        try {
            await deleteJSON(`/api/admin/places/${placeId}`);
            showToast("Place deleted successfully.", "success");
            await loadPlaces();
            if (editingPlaceId === Number(placeId)) {
                resetFormState();
            }
        } catch (error) {
            showToast(error.message, "error");
        }
    }
});

cancelEditButton.addEventListener("click", resetFormState);
resetFormButton.addEventListener("click", resetFormState);

logoutButton.addEventListener("click", async () => {
    try {
        await postJSON("/api/admin/auth/logout", {});
    } finally {
        window.location.href = "/management-portal.html";
    }
});

ensureAuthenticated();
