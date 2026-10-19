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

        const container = document.getElementById("attractionsContainer");
        container.replaceChildren();

        attractions.forEach(attraction => {
            const card = document.createElement("div");
            card.className = "attraction-card";

            const name = document.createElement("h3");
            name.textContent = attraction.name;

            const category = document.createElement("p");
            category.textContent = attraction.category;

            card.append(name, category);
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
