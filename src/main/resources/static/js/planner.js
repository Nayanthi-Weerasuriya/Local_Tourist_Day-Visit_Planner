import { deleteJSON, getJSON, postJSON, putJSON } from "./api.js";
import {
    clearStoredPlanId,
    formatRouteText,
    getStoredPlanId,
    renderEmpty,
    renderError,
    renderLoading,
    showToast
} from "./common.js";

const plannerCode = document.getElementById("planner-code");
const plannerCount = document.getElementById("planner-count");
const plannerRouteText = document.getElementById("planner-route-text");
const plannerStops = document.getElementById("planner-stops");
const plannerList = document.getElementById("planner-list");
const generatePlanButton = document.getElementById("generate-plan-btn");
const downloadPdfButton = document.getElementById("download-pdf-btn");

function updateSummary(plan) {
    plannerCode.textContent = plan ? plan.plannerCode : "No active plan";
    plannerCount.textContent = plan ? String(plan.totalPlaces) : "0";
    plannerRouteText.textContent = plan ? formatRouteText(plan.totalPlaces) : "No stops yet";

    if (!plan || !plan.places.length) {
        plannerStops.innerHTML = renderEmpty("No stops selected yet.");
        return;
    }

    plannerStops.innerHTML = plan.places.map((place) => `
        <div class="stop-chip">
            <strong>Stop ${place.visitOrder}</strong>
            <div>${place.name}</div>
        </div>
    `).join("");
}

function renderPlannerItems(plan) {
    if (!plan || !plan.places.length) {
        plannerList.innerHTML = renderEmpty(
            "Your day planner is empty. Add places from the places list or place details page.",
            '<a class="button primary" href="/places.html">Browse places</a>'
        );
        return;
    }

    plannerList.innerHTML = plan.places.map((place, index) => `
        <article class="planner-item">
            <img src="${place.imageUrl}" alt="${place.name}" onerror="this.onerror=null; this.src='/images/default-place.png';">
            <div>
                <span class="badge ${place.category ? place.category.toLowerCase() : ""}" style="margin-bottom: 0.5rem;">${place.category}</span>
                <div class="card-topline">
                    <h3>Stop ${place.visitOrder}: ${place.name}</h3>
                </div>
                <p class="status-text">${place.distanceKm} km from Rajagiriya</p>
                <p class="status-text">${place.address}</p>
            </div>
            <div class="action-group">
                <button class="button secondary small move-up-btn" type="button" data-index="${index}" ${index === 0 ? "disabled" : ""}>Move up</button>
                <button class="button secondary small move-down-btn" type="button" data-index="${index}" ${index === plan.places.length - 1 ? "disabled" : ""}>Move down</button>
                <button class="button danger small remove-btn" type="button" data-place-id="${place.placeId}">Remove</button>
            </div>
        </article>
    `).join("");
}

function swapItems(items, firstIndex, secondIndex) {
    const nextItems = [...items];
    [nextItems[firstIndex], nextItems[secondIndex]] = [nextItems[secondIndex], nextItems[firstIndex]];
    return nextItems;
}

async function fetchPlan() {
    const planId = getStoredPlanId();
    if (!planId) {
        updateSummary(null);
        renderPlannerItems(null);
        return null;
    }

    plannerList.innerHTML = renderLoading("Loading your day plan...");

    try {
        const plan = await getJSON(`/api/day-plans/${planId}`);
        updateSummary(plan);
        renderPlannerItems(plan);
        return plan;
    } catch (error) {
        if (error.status === 404) {
            clearStoredPlanId();
            updateSummary(null);
            renderPlannerItems(null);
        } else {
            plannerList.innerHTML = renderError("Unable to load your planner right now.");
            showToast(error.message, "error");
        }
        return null;
    }
}

async function reorderPlan(nextPlaces) {
    const planId = getStoredPlanId();
    await putJSON(`/api/day-plans/${planId}/places/reorder`, {
        placeIds: nextPlaces.map((place) => place.placeId)
    });
}

plannerList.addEventListener("click", async (event) => {
    const planId = getStoredPlanId();
    if (!planId) {
        return;
    }

    const currentPlan = await getJSON(`/api/day-plans/${planId}`);

    if (event.target.classList.contains("remove-btn")) {
        const placeId = event.target.dataset.placeId;
        await deleteJSON(`/api/day-plans/${planId}/places/${placeId}`);
        showToast("Place removed from the day plan.", "success");
        const updatedPlan = await fetchPlan();
        if (updatedPlan && !updatedPlan.totalPlaces) {
            plannerRouteText.textContent = "No stops yet";
        }
        return;
    }

    if (event.target.classList.contains("move-up-btn")) {
        const index = Number(event.target.dataset.index);
        const reordered = swapItems(currentPlan.places, index, index - 1);
        await reorderPlan(reordered);
        await fetchPlan();
        return;
    }

    if (event.target.classList.contains("move-down-btn")) {
        const index = Number(event.target.dataset.index);
        const reordered = swapItems(currentPlan.places, index, index + 1);
        await reorderPlan(reordered);
        await fetchPlan();
    }
});

generatePlanButton.addEventListener("click", async () => {
    const planId = getStoredPlanId();
    if (!planId) {
        showToast("Start by adding places to your planner.", "info");
        return;
    }

    try {
        await postJSON(`/api/day-plans/${planId}/generate`);
        showToast("Suggested route order generated.", "success");
        await fetchPlan();
    } catch (error) {
        showToast(error.message, "error");
    }
});

async function downloadPlanAsPDF() {
    const planId = getStoredPlanId();
    if (!planId) {
        showToast("No active plan to download.", "info");
        return;
    }

    try {
        const plan = await getJSON(`/api/day-plans/${planId}`);
        if (!plan || !plan.places.length) {
            showToast("Your plan is empty. Add some places first.", "info");
            return;
        }

        const { jsPDF } = window.jspdf;
        const doc = new jsPDF();

        // Add Header
        doc.setFontSize(22);
        doc.setTextColor(40, 40, 40);
        doc.text("Tourist Planner Itinerary", 14, 20);
        
        doc.setFontSize(10);
        doc.setTextColor(100);
        doc.text(`Plan Code: ${plan.plannerCode}`, 14, 30);
        doc.text(`Generated on: ${new Date().toLocaleString()}`, 14, 35);
        doc.text(`Total stops: ${plan.totalPlaces}`, 14, 40);

        // Define Table
        const tableColumn = ["Stop", "Location", "Category", "Address", "Directions"];
        const tableRows = [];

        plan.places.forEach((place) => {
            const placeData = [
                place.visitOrder,
                place.name,
                place.category,
                place.address,
                "Get Directions" // Placeholder
            ];
            tableRows.push(placeData);
        });

        doc.autoTable({
            head: [tableColumn],
            body: tableRows,
            startY: 50,
            theme: 'grid',
            headStyles: { fillColor: [30, 41, 59], textColor: [255, 255, 255] },
            columnStyles: {
                4: { textColor: [37, 99, 235], fontStyle: 'bold' } // Directions column
            },
            didDrawCell: (data) => {
                if (data.section === 'body' && data.column.index === 4) {
                    const place = plan.places[data.row.index];
                    const mapsUrl = `https://www.google.com/maps/dir/?api=1&destination=${encodeURIComponent(place.name + ', ' + place.address)}`;
                    // This area is clickable
                    doc.link(data.cell.x, data.cell.y, data.cell.width, data.cell.height, { url: mapsUrl });
                }
            }
        });

        // Footer
        const pageCount = doc.internal.getNumberOfPages();
        for (let i = 1; i <= pageCount; i++) {
            doc.setPage(i);
            doc.setFontSize(10);
            doc.setTextColor(150);
            doc.text(`Page ${i} of ${pageCount} | Local Tourist Planner`, 14, doc.internal.pageSize.height - 10);
        }

        doc.save(`my-tourist-plan-${plan.plannerCode}.pdf`);
        showToast("PDF downloaded! Check your downloads folder.", "success");
    } catch (error) {
        console.error("PDF generation error:", error);
        showToast("Could not generate PDF. Please try again.", "error");
    }
}

downloadPdfButton.addEventListener("click", downloadPlanAsPDF);

fetchPlan();
