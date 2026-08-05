package com.ordertracking.user.service.location;

import com.ordertracking.user.dto.AddressValidationRequest;
import com.ordertracking.user.dto.AddressValidationResponse;
import com.ordertracking.user.dto.GeoapifyProperties;
import com.ordertracking.user.dto.GeoapifyResponse;
import com.ordertracking.user.exception.InvalidAddressException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GeoapifyLocationValidationServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private GeoapifyLocationValidationService service;

    @BeforeEach
    void setUp() {

        ReflectionTestUtils.setField(
                service,
                "apiKey",
                "test-api-key"
        );

        ReflectionTestUtils.setField(
                service,
                "baseUrl",
                "https://api.geoapify.com/v1/geocode/search"
        );
    }

    @Test
    void shouldReturnValidatedAddressWhenGeoapifyReturnsResult() {

        AddressValidationRequest request =
                AddressValidationRequest.builder()
                        .doorNoOrBuildingName("25")
                        .street("MG Road")
                        .city("Bengaluru")
                        .state("Karnataka")
                        .country("India")
                        .postalCode("560001")
                        .build();

        GeoapifyProperties properties =
                new GeoapifyProperties();

        properties.setCity("Bengaluru");
        properties.setState("Karnataka");
        properties.setCountry("India");
        properties.setPostcode("560001");
        properties.setLat(12.976657);
        properties.setLon(77.602260);
        properties.setDisplayName(
                "25, MG Road, Bengaluru, Karnataka, 560001, India"
        );

        GeoapifyResponse response =
                new GeoapifyResponse();

        response.setResults(List.of(properties));

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(GeoapifyResponse.class)
        )).thenReturn(ResponseEntity.ok(response));

        AddressValidationResponse result =
                service.validate(request);

        assertTrue(result.isValid());
        assertEquals("Bengaluru", result.getCity());
        assertEquals("Karnataka", result.getState());
        assertEquals("India", result.getCountry());
        assertEquals("560001", result.getPostalCode());

        assertEquals(12.976657, result.getLatitude());
        assertEquals(77.602260, result.getLongitude());

        assertNotNull(result.getDisplayName());

        verify(restTemplate, times(1))
                .exchange(
                        anyString(),
                        eq(HttpMethod.GET),
                        any(HttpEntity.class),
                        eq(GeoapifyResponse.class)
                );
    }

    @Test
    void shouldRejectAddressWhenGeoapifyReturnsNoResults() {

        AddressValidationRequest request =
                AddressValidationRequest.builder()
                        .doorNoOrBuildingName("999999")
                        .street("Nonexistent Road")
                        .city("Bengaluru")
                        .state("Karnataka")
                        .country("India")
                        .postalCode("560001")
                        .build();

        GeoapifyResponse response =
                new GeoapifyResponse();

        response.setResults(List.of());

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(GeoapifyResponse.class)
        )).thenReturn(ResponseEntity.ok(response));

        assertThrows(
                InvalidAddressException.class,
                () -> service.validate(request)
        );
    }

    @Test
    void shouldRejectAddressWhenGeoapifyResponseIsNull() {

        AddressValidationRequest request =
                AddressValidationRequest.builder()
                        .doorNoOrBuildingName("25")
                        .street("MG Road")
                        .city("Bengaluru")
                        .state("Karnataka")
                        .country("India")
                        .postalCode("560001")
                        .build();

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(GeoapifyResponse.class)
        )).thenReturn(ResponseEntity.ok(null));

        assertThrows(
                InvalidAddressException.class,
                () -> service.validate(request)
        );
    }
}