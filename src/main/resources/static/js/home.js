import { getJSON } from "./api.js";
import { createPlaceCard, handleAddToPlan, renderError, renderLoading, showToast } from "./common.js";

const featuredPlacesContainer = document.getElementById("featured-places");
const heroSearchForm = document.getElementById("hero-search-form");

featuredPlacesContainer.innerHTML = renderLoading("Loading featured places...");

async function loadFeaturedPlaces() {
    try {
        const places = await getJSON("/api/places");
        const featured = places.slice(0, 6);

        featuredPlacesContainer.innerHTML = featured.map(createPlaceCard).join("");
    } catch (error) {
        featuredPlacesContainer.innerHTML = renderError("Unable to load featured places right now.");
        showToast(error.message, "error");
    }
}

heroSearchForm.addEventListener("submit", (event) => {
    event.preventDefault();

    const keyword = document.getElementById("hero-keyword").value.trim();
    const category = document.getElementById("hero-category").value;
    const params = new URLSearchParams();

    if (keyword) {
        params.set("keyword", keyword);
    }
    if (category && category !== "All") {
        params.set("category", category);
    }

    window.location.href = `/places.html${params.toString() ? `?${params.toString()}` : ""}`;
});

featuredPlacesContainer.addEventListener("click", handleAddToPlan);
loadFeaturedPlaces();
