package com.example.homework.account;

import com.example.homework.configuration.ProviderConfiguration;
import com.example.homework.customexceptions.InvalidAPIProviderException;
import com.example.homework.provider.ProviderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@EnableConfigurationProperties(value = ProviderConfiguration.class)
@SpringBootTest
public class AccountServiceTest {
    @Autowired
    private ProviderConfiguration providerConfiguration;
    @Autowired
    private AccountService accountService;
    @MockBean
    private ProviderService providerService;

    @Test
    public void whenNullProvidersAreProvidedThenValidateWithAllProviders() {
        AccountValidationRequestDTO request = new AccountValidationRequestDTO(12345678);
        try {
            when(providerService.isAccountNumberValid(any(),any())).thenReturn(true);
            AccountValidationResponseDTO response = accountService.validateAccount(request);
            for(AccountValidationResponseDTO.AccountValidationResponse r : response.getResult()) {
                if(providerConfiguration.getProviders().stream().noneMatch(p -> p.getName().equals(r.getProvider()))) {
                    fail();
                }
            }
        }
        catch(InvalidAPIProviderException e) {
            fail();
        }
    }
    @Test
    public void whenEmptyProvidersAreProvidedThenValidateWithAllProviders() {
        AccountValidationRequestDTO request = new AccountValidationRequestDTO(12345678, new ArrayList<>());
        try {
            when(providerService.isAccountNumberValid(any(),any())).thenReturn(true);
            AccountValidationResponseDTO response = accountService.validateAccount(request);
            for(AccountValidationResponseDTO.AccountValidationResponse r : response.getResult()) {
                if(providerConfiguration.getProviders().stream().noneMatch(p -> p.getName().equals(r.getProvider()))) {
                    fail();
                }
            }
        }
        catch(InvalidAPIProviderException e) {
            fail();
        }
    }

    @Test
    public void whenProvidersAreProvidedThenValidateWithGivenProviders()  {
        List<String> providerList = new ArrayList<>();
        providerList.add(providerConfiguration.getProviders().get(0).getName());
        AccountValidationRequestDTO request = new AccountValidationRequestDTO(12345678, providerList);
        try {
            when(providerService.isAccountNumberValid(any(),any())).thenReturn(true);
            AccountValidationResponseDTO response = accountService.validateAccount(request);
            for(AccountValidationResponseDTO.AccountValidationResponse r : response.getResult()) {
                if(providerList.stream().noneMatch(p -> p.equals(r.getProvider()))) {
                    fail();
                }
            }
        }
        catch(InvalidAPIProviderException e) {
            fail();
        }
    }

    @Test
    public void whenRequestIsNullThenReturnNull() {
        try {
            AccountValidationResponseDTO response =  accountService.validateAccount(null);
            assertNull(response);
        }
        catch (InvalidAPIProviderException e) {
            fail();
        }
    }

    @Test
    public void whenAccountNumberIsNullThenReturnNull() {
        try {
            AccountValidationResponseDTO response =  accountService.validateAccount(new AccountValidationRequestDTO(null));
            assertNull(response);
        }
        catch (InvalidAPIProviderException e) {
            fail();
        }
    }

    @Test
    public void whenInvalidProvidersAreProvidedThenThrowInvalidAPIProviderException() throws InvalidAPIProviderException {
        when(providerService.isAccountNumberValid(any(),any())).thenThrow(InvalidAPIProviderException.class);
        assertThrows(InvalidAPIProviderException.class, () -> {
            String invalidProvider = "invalidProvider";
            accountService.validateAccount(new AccountValidationRequestDTO(12345678, new ArrayList<String>(Arrays.asList(invalidProvider))));
        });
    }
}
