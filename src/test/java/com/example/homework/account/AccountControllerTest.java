package com.example.homework.account;

import com.example.homework.configuration.ProviderConfiguration;
import com.example.homework.customexceptions.InvalidAPIProviderException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@EnableConfigurationProperties(value = ProviderConfiguration.class)
@WebMvcTest(AccountController.class)
public class AccountControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ProviderConfiguration providerConfiguration;
    @MockBean
    private AccountService accountService;

    @Test
    public void whenRequestIsNullThenReturnBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/account/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(null))
                        .characterEncoding("utf-8")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
    @Test
    public void whenRequestHasNoAccountNumberThenReturnBadRequest() throws Exception {
        AccountValidationRequestDTO request = new AccountValidationRequestDTO();
        List<String> providerList = new ArrayList<String>();
        providerList.add("Test");
        request.setProviders(providerList);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/account/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request))
                        .characterEncoding("utf-8")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
    @Test
    public void whenRequestHasNullProvidersThenValidateWithAllProviders() throws Exception {
        AccountValidationRequestDTO requestWithNullProviders = new AccountValidationRequestDTO(12345678);
        List<AccountValidationResponseDTO.AccountValidationResponse> result = new ArrayList<AccountValidationResponseDTO.AccountValidationResponse>();
        for(ProviderConfiguration.Provider p: providerConfiguration.getProviders()) {
            result.add(new AccountValidationResponseDTO.AccountValidationResponse(p.getName(), true));
        }
        AccountValidationResponseDTO response = new AccountValidationResponseDTO(result);
        when(accountService.validateAccount(any())).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/account/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(requestWithNullProviders))
                        .characterEncoding("utf-8")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.length()").value(providerConfiguration.getProviders().size()));
    }
    @Test
    public void whenRequestHasEmptyProvidersThenValidateWithAllProviders() throws Exception {
        AccountValidationRequestDTO requestWithEmptyProviders = new AccountValidationRequestDTO(12345678, new ArrayList<>());
        List<AccountValidationResponseDTO.AccountValidationResponse> result = new ArrayList<AccountValidationResponseDTO.AccountValidationResponse>();
        for(ProviderConfiguration.Provider p: providerConfiguration.getProviders()) {
            result.add(new AccountValidationResponseDTO.AccountValidationResponse(p.getName(), true));
        }
        AccountValidationResponseDTO response = new AccountValidationResponseDTO(result);
        when(accountService.validateAccount(any())).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/account/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(requestWithEmptyProviders))
                        .characterEncoding("utf-8")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.length()").value(providerConfiguration.getProviders().size()));
    }
    @Test
    public void whenRequestHasProvidersThenValidateWithGivenProviders() throws Exception {
        AccountValidationRequestDTO request = new AccountValidationRequestDTO(12345678, new ArrayList<String>(Arrays.asList("provider1")));
        List<AccountValidationResponseDTO.AccountValidationResponse> result = new ArrayList<AccountValidationResponseDTO.AccountValidationResponse>();
        result.add(new AccountValidationResponseDTO.AccountValidationResponse("provider1",true));
        AccountValidationResponseDTO response = new AccountValidationResponseDTO(result);
        when(accountService.validateAccount(any())).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/account/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request))
                        .characterEncoding("utf-8")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.length()").value(1));
    }
    @Test
    public void whenRequestHasInvalidProviderThenReturnBadRequestWithMessage() throws Exception {
        String invalidProvider = "invalidProvider";
        String message = "No such provider: " + invalidProvider;
        AccountValidationRequestDTO request = new AccountValidationRequestDTO(12345678, new ArrayList<String>(Arrays.asList(invalidProvider)));
        when(accountService.validateAccount(any())).thenThrow(new InvalidAPIProviderException(invalidProvider));
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/account/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request))
                        .characterEncoding("utf-8")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(message));
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
