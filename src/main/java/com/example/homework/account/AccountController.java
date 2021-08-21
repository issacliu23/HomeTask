package com.example.homework.account;

import com.example.homework.customexceptions.InvalidAPIProviderException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "api/v1/account")
public class AccountController {
    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validateAccount(@Valid @RequestBody AccountValidationRequestDTO accountValidationRequestDTO) {
        try {
            AccountValidationResponseDTO response = accountService.validateAccount(accountValidationRequestDTO);
            if (response != null && response.getResult().size() != 0) {
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        catch(InvalidAPIProviderException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
