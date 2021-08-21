package com.example.homework.account;

import java.util.List;

public class AccountValidationResponseDTO {
    private List<AccountValidationResponse> result;

    public AccountValidationResponseDTO() {
    }

    public AccountValidationResponseDTO(List<AccountValidationResponse> result) {
        this.result = result;
    }

    public List<AccountValidationResponse> getResult() {
        return result;
    }

    public void setResult(List<AccountValidationResponse> result) {
        this.result = result;
    }

    public static class AccountValidationResponse {
        private String provider;
        private Boolean isValid;

        public AccountValidationResponse() {
        }

        public AccountValidationResponse(String provider, Boolean isValid) {
            this.provider = provider;
            this.isValid = isValid;
        }

        public String getProvider() {
            return provider;
        }

        public void setProvider(String provider) {
            this.provider = provider;
        }

        public Boolean getIsValid() {
            return isValid;
        }

        public void setIsValid(Boolean isValid) {
            this.isValid = isValid;
        }
    }

}