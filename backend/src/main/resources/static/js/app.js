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
        description.textContent = "Great choice! "+data.description || "No description available.";

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
            checkbox.dataset.category = attraction.category;
            console.log(attraction.name, "Category:", attraction.category);
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

/*planTripBtn.addEventListener("click", () => {

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
    if (start<end){
        result.textContent = "Start date cannot be before end date";
        return;
    }
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
});*/

const planTripBtn = document.getElementById("planTripBtn");

planTripBtn.addEventListener("click", async () => {
    const result = document.getElementById("selectionResult");
    const itinerarySection = document.getElementById("itinerarySection");
    const itineraryResult = document.getElementById("itineraryResult");

    const selectedAttractions = Array.from(
        document.querySelectorAll(".attraction-checkbox:checked")
    ).map(checkbox => ({
        name: checkbox.value,
        category: checkbox.dataset.category
    }));

    const destination = destinationInput.value.trim();
    const startDate = document.getElementById("startDate").value;
    const endDate = document.getElementById("endDate").value;
    const budget = Number(document.getElementById("totalBudget").value);
    const hotelCategory = document.getElementById("hotelCategory").value;

    if (!destination || selectedAttractions.length === 0) {
        result.textContent = "Please select a destination and attractions.";
        return;
    }

    if (!startDate || !endDate || endDate < startDate) {
        result.textContent = "Please enter valid travel dates.";
        return;
    }

    if (!Number.isFinite(budget) || budget <= 0) {
        result.textContent = "Please enter a valid budget.";
        return;
    }

    const days = Math.round(
        (new Date(endDate + "T00:00:00") -
         new Date(startDate + "T00:00:00")) / 86400000
    ) + 1;

    const tripData = {
        title: `Trip to ${destination}`,
        destination: destination,
        startDate: startDate,
        endDate: endDate,
        budget: budget,
        description: `${days}-day trip. Accommodation: ${hotelCategory}.` 
    }
    planTripBtn.disabled = true;
    result.textContent = "Generating your itinerary...";
    itinerarySection.hidden = true;
    itineraryResult.replaceChildren();

    try {
        // Step 1: Save trip
        const tripResponse = await fetch("/api/trips", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(tripData)
        });

        if (!tripResponse.ok) {
            throw new Error("Could not save trip: " + tripResponse.status);
        }

        const savedTrip = await tripResponse.json();

        // Step 2: Generate itinerary
        const itineraryResponse = await fetch(
            `/api/trips/${savedTrip.id}/itinerary/generate`,
            {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(selectedAttractions)
            }
        );

        if (!itineraryResponse.ok) {
            throw new Error(
                "Could not generate itinerary: " + itineraryResponse.status
            );
        }

        const itinerary = await itineraryResponse.json();

        // Step 3: Display itinerary
        result.textContent = "Your itinerary is ready!";
        itinerarySection.hidden = false;

        if (!itinerary.items || itinerary.items.length === 0) {
            itineraryResult.textContent = "No itinerary items generated.";
            return;
        }

        const grouped = {};

        itinerary.items.forEach(item => {
            if (!grouped[item.date]) {
                grouped[item.date] = [];
            }
            grouped[item.date].push(item);
        });

        Object.keys(grouped).sort().forEach((date, index) => {
            const daySection = document.createElement("div");
            daySection.className = "itinerary-day";

            const heading = document.createElement("h3");
            heading.textContent = `Day ${index + 1} - ${date}`;
            daySection.appendChild(heading);

            grouped[date].forEach(item => {
                const card = document.createElement("div");
                card.className = "itinerary-card";

                const name = document.createElement("h4");
                name.textContent = item.attractionName;

                const time = document.createElement("p");
                time.textContent =
                    `${item.startTime || ""} - ${item.endTime || ""}`;

                const description = document.createElement("p");
                description.textContent = item.description || "";

                card.append(name, time, description);
                daySection.appendChild(card);
            });

            itineraryResult.appendChild(daySection);
        });

    } catch (error) {
        console.error(error);
        result.textContent = "Error: " + error.message;
    } finally {
        planTripBtn.disabled = false;
    }
});
/*document.getElementById("findHotelsBtn").addEventListener("click", () => {
    const destination = destinationInput.value.trim();

    if (!destination) {
        alert("Please select a destination first.");
        return;
    }

    const url = "https://www.booking.com/searchresults.html?ss="
        + encodeURIComponent(destination);

    window.open(url, "_blank", "noopener,noreferrer");
});*/
document.getElementById("findHotelsBtn").addEventListener("click", () => {
    const destination = destinationInput.value.trim();
    const checkin = document.getElementById("startDate").value;
    const checkout = document.getElementById("endDate").value;

    if (!destination || !checkin || !checkout || checkout <= checkin) {
        alert("Please enter a destination and valid dates.");
        return;
    }

    const params = new URLSearchParams({
        ss: destination,
        checkin: checkin,
        checkout: checkout,
        group_adults: 1,
        no_rooms: 1,
        group_children: 0
    });

    window.open(
        "https://www.booking.com/searchresults.html?" + params.toString(),
        "_blank",
        "noopener,noreferrer"
    );
});