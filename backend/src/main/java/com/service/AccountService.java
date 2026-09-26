package com.service;

import com.dto.AccountCreateDto;
import com.dto.AccountResponseDto;
import com.entity.Account;
import com.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public List<AccountResponseDto> getAllAccounts() {
        return accountRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public AccountResponseDto getAccountById(Long id) {
        Account account = accountRepository.findById(id).orElse(null);
        return account != null ? mapToDto(account) : null;
    }

    public AccountResponseDto getAccountByNumber(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber).orElse(null);
        return account != null ? mapToDto(account) : null;
    }

    @Transactional
    public AccountResponseDto createAccount(AccountCreateDto createDto) {
        String generatedAccountNumber = "AC" + (100000 + (long) (Math.random() * 900000));
        while (accountRepository.existsByAccountNumber(generatedAccountNumber)) {
            generatedAccountNumber = "AC" + (100000 + (long) (Math.random() * 900000));
        }

        Account account = new Account(
                generatedAccountNumber,
                createDto.getHolderName(),
                createDto.getAccountType(),
                createDto.getInitialDeposit()
        );

        Account saved = accountRepository.save(account);
        return mapToDto(saved);
    }

    public AccountResponseDto mapToDto(Account account) {
        return new AccountResponseDto(
                account.getId(),
                account.getAccountNumber(),
                account.getHolderName(),
                account.getAccountType(),
                account.getBalance(),
                account.getCreatedAt()
        );
    }
}
