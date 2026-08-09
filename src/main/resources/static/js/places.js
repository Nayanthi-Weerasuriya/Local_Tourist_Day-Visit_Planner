import { getJSON } from "./api.js";
import { createPlaceCard, getQueryParam, handleAddToPlan, renderEmpty, renderError, renderLoading, showToast } from "./common.js";

const placesGrid = document.getElementById("places-grid");
const resultsSummary = document.getElementById("results-summary");
const searchInput = document.getElementById("search-input");
const categoryFilter = document.getElementById("category-filter");
const applyFilterButton = document.getElementById("apply-filter-btn");
const clearFilterButton = document.getElementById("clear-filter-btn");

function updateQueryParams(keyword, category) {
    const params = new URLSearchParams();
    if (keyword) {
        params.set("keyword", keyword);
    }
    if (category && category !== "All") {
        params.set("category", category);
    }
    const newUrl = `${window.location.pathname}${params.toString() ? `?${params.toString()}` : ""}`;
    window.history.replaceState({}, "", newUrl);
}

async function loadPlaces() {
    const keyword = searchInput.value.trim();
    const category = categoryFilter.value;

    placesGrid.innerHTML = renderLoading("Loading places...");

    try {
        let places;
        if (keyword) {
            places = await getJSON(`/api/places/search?keyword=${encodeURIComponent(keyword)}`);
        } else if (category !== "All") {
            places = await getJSON(`/api/places/category/${encodeURIComponent(category)}`);
        } else {
            places = await getJSON("/api/places");
        }

        if (category !== "All" && keyword) {
            places = places.filter((place) => place.category === category);
        }

        updateQueryParams(keyword, category);
        resultsSummary.innerHTML = `<strong>${places.length}</strong> place${places.length !== 1 ? "s" : ""} found.`;

        if (!places.length) {
            placesGrid.innerHTML = renderEmpty(
                "No places matched the selected filters.",
                '<a class="button secondary" href="/places.html">View all places</a>'
            );
            return;
        }

        placesGrid.innerHTML = places.map(createPlaceCard).join("");
    } catch (error) {
        placesGrid.innerHTML = renderError("Unable to load places right now.");
        showToast(error.message, "error");
    }
}

function initializeFilters() {
    const initialKeyword = getQueryParam("keyword") || "";
    const initialCategory = getQueryParam("category") || "All";
    searchInput.value = initialKeyword;
    categoryFilter.value = initialCategory;
}

applyFilterButton.addEventListener("click", loadPlaces);
placesGrid.addEventListener("click", handleAddToPlan);
clearFilterButton.addEventListener("click", () => {
    searchInput.value = "";
    categoryFilter.value = "All";
    loadPlaces();
});

initializeFilters();
loadPlaces();
