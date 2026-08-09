import { getJSON, postJSON } from "./api.js";
import { ensurePlanId, getQueryParam, renderError, renderLoading, showToast } from "./common.js";

const detailRoot = document.getElementById("place-detail-root");
const placeId = getQueryParam("id");

detailRoot.innerHTML = renderLoading("Loading place details...");

function renderDetail(place) {
    return `
        <section class="detail-card">
            <div class="detail-hero">
                <img class="detail-image" src="${place.imageUrl}" alt="${place.name}" onerror="this.onerror=null; this.src='/images/default-place.png';">
                <div class="detail-body">
                    <div class="card-topline">
                        <span class="badge ${place.category ? place.category.toLowerCase() : ""}">${place.category}</span>
                        <span class="status-text">${place.distanceKm} km from Rajagiriya</span>
                    </div>
                    <h1>${place.name}</h1>
                    <p>${place.description}</p>
                    <div class="detail-grid">
                        <div class="detail-info">
                            <h3>Visit information</h3>
                            <p><strong>Opening time:</strong> ${place.openingTime}</p>
                            <p><strong>Closing time:</strong> ${place.closingTime}</p>
                            <p><strong>Address:</strong> ${place.address}</p>
                            <p><strong>Travel tips:</strong> ${place.travelTips}</p>
                        </div>
                        <div class="detail-info">
                            <h3>Planner action</h3>
                            <p>Add this location to your one-day plan and then arrange the visit order on the planner page.</p>
                            <div class="action-group">
                                <button id="add-to-plan-btn" class="button primary" type="button">Add to Day Plan</button>
                                <a class="button secondary" href="/planner.html">Open planner</a>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </section>
        <section class="detail-layout">
            <article class="detail-card map-card">
                <div class="panel-head">
                    <div>
                        <p class="eyebrow">Map location</p>
                        <h3 style="margin: 0;">${place.name}</h3>
                    </div>
                    <a class="button secondary small" href="https://www.google.com/maps/dir/?api=1&destination=${encodeURIComponent(place.name + ', ' + place.address)}" target="_blank">Get Directions</a>
                </div>
                <div id="place-map"></div>
            </article>
            <article class="detail-card detail-info">
                <p class="eyebrow">Quick plan idea</p>
                <h3>Suggested use in a day route</h3>
                <p>Use this place as one stop in a simple Rajagiriya-centered day visit. Combine nearby heritage, park, or leisure spots to keep travel time low.</p>
                <p><strong>Recommended sequence:</strong> add it to your planner, then generate a suggested order based on distance.</p>
            </article>
        </section>
    `;
}

function initializeMap(place) {
    const map = L.map("place-map").setView([place.latitude, place.longitude], 14);

    L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
        maxZoom: 19,
        attribution: "&copy; OpenStreetMap contributors"
    }).addTo(map);

    L.marker([place.latitude, place.longitude])
        .addTo(map)
        .bindPopup(`<strong>${place.name}</strong><br>${place.address}`)
        .openPopup();
}

async function addToPlan(place) {
    try {
        const planId = await ensurePlanId();
        await postJSON(`/api/day-plans/${planId}/places`, { placeId: place.id });
        showToast(`${place.name} added to your day plan.`, "success");
    } catch (error) {
        showToast(error.message, "error");
    }
}

async function loadPlace() {
    if (!placeId || Number.isNaN(Number(placeId))) {
        detailRoot.innerHTML = renderError("Invalid place requested. Please return to the places page and choose a valid location.");
        return;
    }

    try {
        const place = await getJSON(`/api/places/${placeId}`);
        detailRoot.innerHTML = renderDetail(place);
        initializeMap(place);

        document.getElementById("add-to-plan-btn").addEventListener("click", () => addToPlan(place));
    } catch (error) {
        if (error.status === 404) {
            detailRoot.innerHTML = renderError("Place not found. It may have been removed or the link is incorrect.");
        } else {
            detailRoot.innerHTML = renderError("Unable to load place details right now.");
        }
        showToast(error.message, "error");
    }
}

loadPlace();
