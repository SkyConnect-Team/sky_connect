package com.skyconnect.demo.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skyconnect.demo.dto.response.FlightLiveResponse;
import com.skyconnect.demo.entity.Flight;
import com.skyconnect.demo.repository.FlightRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class AirLabsService {

    private final FlightRepository flightRepository;

    private final ObjectMapper objectMapper;

    @Value("${airlabs.api.url}")
    private String airLabsUrl;

    @Value("${airlabs.api.key}")
    private String apiKey;


    // =====================================================
    // SEARCH FLIGHT SCHEDULES
    // =====================================================

    public String getSchedules(String depIata, String arrIata) {

        RestClient restClient = RestClient.builder()
                .baseUrl(airLabsUrl)
                .build();

        String apiResponse = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/schedules")
                        .queryParam("dep_iata", depIata)
                        .queryParam("arr_iata", arrIata)
                        .queryParam("api_key", apiKey)
                        .build())
                .retrieve()
                .body(String.class);

        System.out.println("========== AIRLABS RESPONSE ==========");
        System.out.println(apiResponse);
        System.out.println("======================================");

        try {

            JsonNode root =
                    objectMapper.readTree(apiResponse);

            if (root.has("error")) {
                throw new RuntimeException(
                        "AirLabs error: " + root.toPrettyString()
                );
            }

            JsonNode response =
                    root.get("response");

            if (response == null || response.isNull()) {
                throw new RuntimeException(
                        "No schedule data found from AirLabs"
                );
            }

            // Return actual JSON, not JsonNode object properties
            return response.toPrettyString();

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to process AirLabs response: "
                            + e.getMessage(),
                    e
            );
        }
    }

    // =====================================================
    // GET LIVE FLIGHT
    // =====================================================

    public FlightLiveResponse getLiveFlight(
            Long flightId) {


        // 1. Find flight in database

        Flight flight =
                flightRepository.findById(flightId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Flight not found with ID: "
                                                + flightId
                                )
                        );


        // Example:
        // AI542

        String flightNumber =
                flight.getFlightNumber();


        // 2. Create RestClient

        RestClient restClient =
                RestClient.builder()
                        .baseUrl(airLabsUrl)
                        .build();


        // 3. Call AirLabs API

        String apiResponse =
                restClient.get()
                        .uri(uriBuilder ->
                                uriBuilder
                                        .path("/flight")
                                        .queryParam(
                                                "flight_iata",
                                                flightNumber
                                        )
                                        .queryParam(
                                                "api_key",
                                                apiKey
                                        )
                                        .build()
                        )
                        .retrieve()
                        .onStatus(
                                HttpStatusCode::isError,
                                (request, response) -> {

                                    throw new RuntimeException(
                                            "AirLabs API error. HTTP status: "
                                                    + response
                                                    .getStatusCode()
                                    );
                                }
                        )
                        .body(String.class);


        // DEBUG

        System.out.println(
                "========== AIRLABS LIVE RESPONSE =========="
        );

        System.out.println(apiResponse);

        System.out.println(
                "============================================"
        );


        try {


            // 4. Convert JSON

            JsonNode root =
                    objectMapper.readTree(apiResponse);


            // 5. Check AirLabs error

            if (root.has("error")) {

                throw new RuntimeException(
                        "AirLabs error: "
                                + root
                );
            }


            // 6. Get response

            JsonNode data =
                    root.get("response");


            if (data == null ||
                    data.isNull()) {

                throw new RuntimeException(
                        "No live flight data found for "
                                + flightNumber
                );
            }


            // 7. Convert to DTO

            return FlightLiveResponse.builder()

                    .flightId(
                            flight.getId()
                    )

                    .flightNumber(
                            getText(
                                    data,
                                    "flight_iata"
                            )
                    )

                    .airline(
                            flight.getAirline()
                    )

                    .source(
                            flight.getSource()
                    )

                    .destination(
                            flight.getDestination()
                    )

                    .status(
                            getText(
                                    data,
                                    "status"
                            )
                    )

                    .scheduledDeparture(
                            getText(
                                    data,
                                    "dep_time"
                            )
                    )

                    .estimatedDeparture(
                            getText(
                                    data,
                                    "dep_estimated"
                            )
                    )

                    .actualDeparture(
                            getText(
                                    data,
                                    "dep_actual"
                            )
                    )

                    .scheduledArrival(
                            getText(
                                    data,
                                    "arr_time"
                            )
                    )

                    .estimatedArrival(
                            getText(
                                    data,
                                    "arr_estimated"
                            )
                    )

                    .actualArrival(
                            getText(
                                    data,
                                    "arr_actual"
                            )
                    )

                    .departureDelay(
                            getInteger(
                                    data,
                                    "dep_delayed"
                            )
                    )

                    .arrivalDelay(
                            getInteger(
                                    data,
                                    "arr_delayed"
                            )
                    )

                    .latitude(
                            getDouble(
                                    data,
                                    "lat"
                            )
                    )

                    .longitude(
                            getDouble(
                                    data,
                                    "lng"
                            )
                    )

                    .altitude(
                            getDouble(
                                    data,
                                    "alt"
                            )
                    )

                    .speed(
                            getDouble(
                                    data,
                                    "speed"
                            )
                    )

                    .lastUpdated(
                            getLong(
                                    data,
                                    "updated"
                            )
                    )

                    .build();


        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to process AirLabs live response: "
                            + e.getMessage(),
                    e
            );
        }
    }


    // =====================================================
    // HELPER METHODS
    // =====================================================


    private String getText(
            JsonNode node,
            String field) {

        JsonNode value =
                node.get(field);

        if (value == null ||
                value.isNull()) {

            return null;
        }

        return value.asText();
    }


    private Integer getInteger(
            JsonNode node,
            String field) {

        JsonNode value =
                node.get(field);

        if (value == null ||
                value.isNull()) {

            return null;
        }

        return value.asInt();
    }


    private Double getDouble(
            JsonNode node,
            String field) {

        JsonNode value =
                node.get(field);

        if (value == null ||
                value.isNull()) {

            return null;
        }

        return value.asDouble();
    }


    private Long getLong(
            JsonNode node,
            String field) {

        JsonNode value =
                node.get(field);

        if (value == null ||
                value.isNull()) {

            return null;
        }

        return value.asLong();
    }
}