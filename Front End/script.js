let map, marker;
// Elements for displaying location and speed
const locationElement = document.getElementById("location");
const speedElement = document.getElementById("speed");

// Initialize the map and marker
function initMap() {
    map = L.map("map").setView([51.505, -0.09], 13); // Default to a generic location

    // Load OpenStreetMap tiles
    L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
        attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
    }).addTo(map);

    // Add an initial marker
    marker = L.marker([51.505, -0.09]).addTo(map);
}

// Function to update real-time location
function updateLocation(position) {
    const { latitude, longitude, speed } = position.coords;
    
    // Update the marker's position on the map
    marker.setLatLng([latitude, longitude]);
    map.setView([latitude, longitude], map.getZoom());

    // Update location details on the UI
    locationElement.innerHTML = `Latitude: ${latitude.toFixed(4)}, Longitude: ${longitude.toFixed(4)}`;
    speedElement.innerHTML = speed ? `${(speed * 3.6).toFixed(1)} km/h` : "Speed unavailable";
}

// Track real-time location
function trackLocation() {
    if (navigator.geolocation) {
        navigator.geolocation.watchPosition(
            updateLocation,
            error => console.error("Error watching position:", error.message),
            { enableHighAccuracy: true }
        );
    } else {
        locationElement.innerHTML = "Geolocation is not supported by this browser.";
    }
}

// Call initialization functions
window.onload = () => {
    initMap();
    trackLocation();
};
