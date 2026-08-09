package com.rajagiriya.tourplanner.config;

import com.rajagiriya.tourplanner.entity.Admin;
import com.rajagiriya.tourplanner.entity.AdminRole;
import com.rajagiriya.tourplanner.entity.Place;
import com.rajagiriya.tourplanner.entity.PlaceCategory;
import com.rajagiriya.tourplanner.repository.AdminRepository;
import com.rajagiriya.tourplanner.repository.PlaceRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final PlaceRepository placeRepository;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(PlaceRepository placeRepository, AdminRepository adminRepository, PasswordEncoder passwordEncoder) {
        this.placeRepository = placeRepository;
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        seedAdmin();
        seedPlaces();
        refreshLocalPlaceImages();
    }

    private void refreshLocalPlaceImages() {
        refreshPlaceImage("Diyatha Uyana", "/images/diyatha-uyana.png");
        refreshPlaceImage("Parliament of Sri Lanka", "/images/parliament-of-sri-lanka.png");
        refreshPlaceImage("Arcade Independence Square", "/images/arcade-independence-square.png");
    }

    private void refreshPlaceImage(String placeName, String imageUrl) {
        placeRepository.findAll().stream()
                .filter(place -> placeName.equalsIgnoreCase(place.getName()))
                .findFirst()
                .ifPresent(place -> {
                    if (!imageUrl.equals(place.getImageUrl())) {
                        place.setImageUrl(imageUrl);
                        placeRepository.save(place);
                    }
                });
    }

    private void seedAdmin() {
        adminRepository.findByUsername("admin").ifPresentOrElse(admin -> {
        }, () -> {
            Admin admin = new Admin();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("Admin@123"));
            admin.setRole(AdminRole.ADMIN);
            adminRepository.save(admin);
        });
    }

    private void seedPlaces() {
        if (placeRepository.count() > 0) {
            return;
        }

        List<Place> places = List.of(
                createPlace(
                        "Gangaramaya Temple",
                        PlaceCategory.RELIGIOUS,
                        "A renowned Buddhist temple in Colombo that blends Sri Lankan, Thai, Indian, and Chinese architectural influences. It is a popular stop for visitors who want a calm spiritual setting with a museum-like collection of artifacts.",
                        7.0,
                        "06:00",
                        "21:00",
                        "Dress modestly, remove shoes before entering sacred areas, and visit early morning to avoid traffic and crowds.",
                        "61 Sri Jinarathana Road, Colombo 02",
                        6.9167,
                        79.8561,
                        "https://upload.wikimedia.org/wikipedia/commons/5/56/Gangaramaya_Temple_Colombo.jpg"
                ),
                createPlace(
                        "Viharamahadevi Park",
                        PlaceCategory.NATURE,
                        "The largest public park in Colombo, featuring walking paths, shaded lawns, children's play areas, and a relaxed city atmosphere for a light daytime outing.",
                        6.0,
                        "06:00",
                        "18:30",
                        "Carry water, use sunscreen in the afternoon, and pair the park visit with the museum or Independence Square nearby.",
                        "Dharmapala Mawatha, Colombo 07",
                        6.9103,
                        79.8612,
                        "https://upload.wikimedia.org/wikipedia/commons/9/98/Viharamahadevi_Park_Colombo.jpg"
                ),
                createPlace(
                        "Independence Memorial Hall",
                        PlaceCategory.HERITAGE,
                        "A national landmark built to commemorate Sri Lanka's independence, surrounded by wide open spaces that are ideal for photos, short walks, and a history-focused stop.",
                        5.0,
                        "06:00",
                        "20:00",
                        "Go in the late afternoon for softer light and combine it with Arcade Independence Square for refreshments.",
                        "Independence Avenue, Colombo 07",
                        6.9022,
                        79.8686,
                        "https://upload.wikimedia.org/wikipedia/commons/c/c0/Independence_Memorial_Hall_Colombo.jpg"
                ),
                createPlace(
                        "Colombo National Museum",
                        PlaceCategory.HERITAGE,
                        "Sri Lanka's largest museum with exhibits on ancient kingdoms, royal artifacts, carvings, and historical documents that give visitors a strong overview of the island's heritage.",
                        6.0,
                        "09:00",
                        "17:00",
                        "Keep at least 90 minutes for the visit and consider going on a weekday for a quieter experience.",
                        "Sir Marcus Fernando Mawatha, Colombo 07",
                        6.9107,
                        79.8607,
                        "https://upload.wikimedia.org/wikipedia/commons/e/e6/National_Museum_of_Colombo.jpg"
                ),
                createPlace(
                        "Diyatha Uyana",
                        PlaceCategory.NATURE,
                        "A lakeside urban park near Rajagiriya known for evening walks, food stalls, boat views, and a relaxed atmosphere that suits families and casual visitors.",
                        3.0,
                        "05:30",
                        "22:00",
                        "Best visited in the evening when the weather is cooler and the lakeside market is active.",
                        "Polduwa Road, Battaramulla",
                        6.9065,
                        79.9175,
                        "/images/diyatha-uyana.png"
                ),
                createPlace(
                        "Parliament of Sri Lanka",
                        PlaceCategory.HERITAGE,
                        "The country's striking parliament complex sits on an island in Diyawanna Lake and is one of the most recognizable modern civic landmarks near Rajagiriya.",
                        4.0,
                        "08:00",
                        "17:00",
                        "Public access is limited, so enjoy the exterior viewpoints and nearby walking areas rather than expecting a full internal tour.",
                        "Sri Jayawardenepura Kotte",
                        6.8897,
                        79.9186,
                        "/images/parliament-of-sri-lanka.png"
                ),
                createPlace(
                        "Arcade Independence Square",
                        PlaceCategory.LEISURE,
                        "A restored colonial-era shopping and dining destination that works well as a relaxed stop for snacks, coffee, and evening leisure after visiting nearby heritage sites.",
                        5.0,
                        "10:00",
                        "22:00",
                        "Good for a meal break between sightseeing stops and especially lively after sunset.",
                        "Independence Square, Colombo 07",
                        6.9045,
                        79.8670,
                        "/images/arcade-independence-square.png"
                ),
                createPlace(
                        "Beira Lake",
                        PlaceCategory.NATURE,
                        "A central Colombo waterside attraction ideal for short scenic breaks, lakeside photography, and combining with temple and city-center sightseeing.",
                        7.0,
                        "06:00",
                        "19:00",
                        "Visit in the morning or late afternoon to avoid midday heat and bring a camera for waterfront views.",
                        "Beira Lake area, Colombo 02",
                        6.9253,
                        79.8549,
                        "https://upload.wikimedia.org/wikipedia/commons/3/33/Beira_Lake_Colombo.jpg"
                ),
                createPlace(
                        "Kelaniya Raja Maha Vihara",
                        PlaceCategory.RELIGIOUS,
                        "One of Sri Lanka's most significant Buddhist temples, known for its murals, spiritual importance, and riverside setting just outside central Colombo.",
                        9.0,
                        "05:30",
                        "20:00",
                        "Dress respectfully, plan extra time during poya days, and expect a calm but active worship environment.",
                        "Peliyagoda Road, Kelaniya",
                        6.9553,
                        79.9220,
                        "https://upload.wikimedia.org/wikipedia/commons/4/49/Kelaniya_Raja_Maha_Vihara.jpg"
                ),
                createPlace(
                        "Mount Lavinia Beach",
                        PlaceCategory.NATURE,
                        "A popular beach destination for a sunset visit, sea breeze, and a relaxed seaside ending to a one-day plan centered around Colombo and Rajagiriya.",
                        15.0,
                        "06:00",
                        "22:00",
                        "Aim for late afternoon to catch the sunset, and keep a change of clothes if you plan to spend time near the water.",
                        "Mount Lavinia, Dehiwala-Mount Lavinia",
                        6.8389,
                        79.8636,
                        "https://upload.wikimedia.org/wikipedia/commons/a/a7/Mount_Lavinia_Beach.jpg"
                ),
                createPlace(
                        "Nelum Pokuna Theatre",
                        PlaceCategory.CULTURAL,
                        "A major performing arts venue in Colombo that represents the city's modern cultural scene and works well as a cultural landmark in a short urban itinerary.",
                        6.0,
                        "09:00",
                        "18:00",
                        "Check the day's event schedule before visiting and combine it with nearby heritage attractions for a balanced route.",
                        "Ananda Coomaraswamy Mawatha, Colombo 07",
                        6.9115,
                        79.8639,
                        "https://upload.wikimedia.org/wikipedia/commons/2/20/Nelum_Pokuna_Mahinda_Rajapaksa_Theatre.jpg"
                )
        );

        placeRepository.saveAll(places);
    }

    private Place createPlace(
            String name,
            PlaceCategory category,
            String description,
            double distanceKm,
            String openingTime,
            String closingTime,
            String travelTips,
            String address,
            double latitude,
            double longitude,
            String imageUrl
    ) {
        Place place = new Place();
        place.setName(name);
        place.setCategory(category);
        place.setDescription(description);
        place.setDistanceKm(distanceKm);
        place.setOpeningTime(LocalTime.parse(openingTime));
        place.setClosingTime(LocalTime.parse(closingTime));
        place.setTravelTips(travelTips);
        place.setAddress(address);
        place.setLatitude(latitude);
        place.setLongitude(longitude);
        place.setImageUrl(imageUrl);
        place.setActive(true);
        return place;
    }
}
