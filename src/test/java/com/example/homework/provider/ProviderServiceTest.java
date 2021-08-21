package com.example.homework.provider;

import com.example.homework.customexceptions.InvalidAPIProviderException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ProviderServiceTest {
    @Autowired
    private ProviderService providerService;

    public static MockWebServer mockBackEnd;
    private String baseUrl;

    @BeforeAll
    static void setUp() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
    }
    @BeforeEach
    void initialize() {
        baseUrl = String.format("http://localhost:%s", mockBackEnd.getPort());
    }
    @AfterAll
    static void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }

    @Test
    public void whenIsAccountNumberValidApiIsCalledThenValidateAccountNumber() throws InterruptedException {
        Integer accountNumber = 12345678;
        mockBackEnd.enqueue(new MockResponse()
                .setBody(String.valueOf(true))
                .addHeader("Content-Type", "application/json"));
        try {
            Boolean isValid = providerService.isAccountNumberValid(baseUrl, accountNumber);
            assertTrue(isValid);
        }
        catch(InvalidAPIProviderException e) {
            fail();
        }
        RecordedRequest recordedRequest = mockBackEnd.takeRequest();
        assertEquals("GET", recordedRequest.getMethod());
        assertEquals("/"+accountNumber, recordedRequest.getPath());
    }
    @Test
    public void whenProviderResponseAreMoreThan1SecondAfterThenThrowIllegalStateException() {
        mockBackEnd.enqueue(new MockResponse()
                .setBody(String.valueOf(true))
                .setBodyDelay(2, TimeUnit.SECONDS)
                .addHeader("Content-Type", "application/json"));
        assertThrows(IllegalStateException.class, () -> {
            providerService.isAccountNumberValid(baseUrl, 12345678);
        });
    }
}
