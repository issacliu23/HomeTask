package com.example.homework.account;

import com.example.homework.customexceptions.InvalidAPIProviderException;
import com.example.homework.configuration.ProviderConfiguration;
import com.example.homework.provider.ProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@EnableConfigurationProperties(value = ProviderConfiguration.class)
public class AccountService {
    private final ProviderService providerService;
    private final ProviderConfiguration providerConfiguration;
    @Autowired
    public AccountService(ProviderService providerService, ProviderConfiguration providerConfiguration) {
        this.providerService = providerService;
        this.providerConfiguration = providerConfiguration;
    }


    public AccountValidationResponseDTO validateAccount(AccountValidationRequestDTO accountValidationRequestDTO) throws InvalidAPIProviderException {
        if(accountValidationRequestDTO == null || accountValidationRequestDTO.getAccountNumber() == null) {
            return null;
        }
        AccountValidationResponseDTO accountValidationResponseDTO = new AccountValidationResponseDTO();
        List<AccountValidationResponseDTO.AccountValidationResponse> result = new ArrayList<AccountValidationResponseDTO.AccountValidationResponse>();
        if(accountValidationRequestDTO.getProviders() != null && accountValidationRequestDTO.getProviders().size() != 0) {
            for(String p: accountValidationRequestDTO.getProviders()) {
                Boolean isValid = this.isAccountNumberValid(p, accountValidationRequestDTO.getAccountNumber());
                result.add(new AccountValidationResponseDTO.AccountValidationResponse(p, isValid));
            }
        }
        else {
            for(ProviderConfiguration.Provider p: providerConfiguration.getProviders()) {
                Boolean isValid = this.isAccountNumberValid(p.getName(), accountValidationRequestDTO.getAccountNumber());
                result.add(new AccountValidationResponseDTO.AccountValidationResponse(p.getName(),isValid));
            }
        }
        accountValidationResponseDTO.setResult(result);
        return accountValidationResponseDTO;
    }

    private Boolean isAccountNumberValid(String provider, Integer accountNumber) throws InvalidAPIProviderException {
        Optional<ProviderConfiguration.Provider> apiProvider = providerConfiguration.getProviders().stream().
                filter(p -> p.getName().equals(provider)).
                findFirst();
        if(apiProvider.isPresent()) {
            return providerService.isAccountNumberValid(apiProvider.get().getUrl(), accountNumber);
        }
        else {
            throw new InvalidAPIProviderException(provider);
        }
    }
}
