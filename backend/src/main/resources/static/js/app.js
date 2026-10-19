const searchForm = document.getElementById("searchForm");
const destinationInput = document.getElementById("destinationInput");
const destinationResult = document.getElementById("destinationResult");


searchForm.addEventListener("submit", async function(event) {
    event.preventDefault();

    const query = destinationInput.value.trim();

    if (!query) {
        return;
    }

    destinationResult.textContent = "Searching...";
    document.getElementById("attractionsContainer").replaceChildren();
    try {
        const response = await fetch(
            "/api/destinations/search?query=" +
            encodeURIComponent(query)
        );

        if (!response.ok) {
            throw new Error("Destination search failed");
        }

        const data = await response.json();

        destinationResult.replaceChildren();

        const heading = document.createElement("h3");
        heading.textContent = data.name;

        const description = document.createElement("p");
        description.textContent = data.description || "No description available.";

        destinationResult.append(heading, description);
        const attractionResponse = await fetch(
            "/api/destinations/attractions?query=" +
            encodeURIComponent(query)
        );
        if (!attractionResponse.ok) {
            throw new Error("Attraction search failed");
        }
        const attractions = await attractionResponse.json();
        const attractionHeading = document.querySelector(
            "#attractionsSection h2"
        );
        attractionHeading.textContent =
            `Places to Explore (${attractions.length} found)`;
        const container = document.getElementById("attractionsContainer");
        container.replaceChildren();
        if (attractions.length < 4) {
            container.textContent =
                "Try a more specific destination, such as Panaji or Calangute for Goa.";
        }
        attractions.forEach(attraction => {
            const card = document.createElement("div");
            card.className = "attraction-card";

            const label = document.createElement("label");
            label.className = "attraction-option";

            const checkbox = document.createElement("input");
            checkbox.type = "checkbox";
            checkbox.className = "attraction-checkbox";
            checkbox.value = attraction.name;

            const details = document.createElement("div");

            const name = document.createElement("h3");
            name.textContent = attraction.name;

            const category = document.createElement("p");
            category.textContent = attraction.category;

            details.append(name, category);
            label.append(checkbox, details);
            card.appendChild(label);
            container.appendChild(card);
        });


    } catch (error) {
        destinationResult.textContent =
            "Couldn't load destination details. Please try again.";
        console.error(error);
    }
});
document.querySelectorAll(".destination-card").forEach(card => {
    card.addEventListener("click", () => {
        destinationInput.value = card.dataset.destination;
        searchForm.requestSubmit();
    });
});
const planTripBtn = document.getElementById("planTripBtn");

planTripBtn.addEventListener("click", () => {

    const selectedAttractions = Array.from(
        document.querySelectorAll(".attraction-checkbox:checked")
    ).map(checkbox => checkbox.value);

    const result = document.getElementById("selectionResult");

    if (selectedAttractions.length === 0) {
        result.textContent = "Please select at least one attraction.";
        return;
    }

    result.textContent =
        "Selected attractions: " + selectedAttractions.join(", ");

    console.log("Selected attractions:", selectedAttractions);
    const travelers = Number(document.getElementById("travelers").value);
    const startDate = document.getElementById("startDate").value;
    const endDate = document.getElementById("endDate").value;
    const start = new Date(startDate + "T00:00:00");
    const end = new Date(endDate + "T00:00:00");
    const days = Math.round(
        (end - start) / (1000 * 60 * 60 * 24)
    ) + 1;
    const hotelCategory =
        document.getElementById("hotelCategory").value;
    const foodPreference =
        document.getElementById("foodPreference").value;
    const totalBudget =
        Number(document.getElementById("totalBudget").value);
    const tripData = {
        destination: destinationInput.value.trim(),
        days,
        travelers,
        startDate,
        endDate,
        selectedAttractions,
        hotelCategory,
        foodPreference,
        totalBudget
    };

    console.log("Trip data:", tripData);
});