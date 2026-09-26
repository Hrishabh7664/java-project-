package com.service;

import com.dto.TransactionRequestDto;
import com.dto.TransactionResponseDto;
import com.entity.Account;
import com.entity.Transaction;
import com.repository.AccountRepository;
import com.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public TransactionService(TransactionRepository transactionRepository, AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    public List<TransactionResponseDto> getAllTransactions() {
        return transactionRepository.findAllByOrderByTimestampDesc().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<TransactionResponseDto> getTransactionsByAccountId(Long accountId) {
        return transactionRepository.findByAccountIdOrderByTimestampDesc(accountId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public TransactionResponseDto processTransfer(TransactionRequestDto requestDto) {
        Account sourceAccount = accountRepository.findByAccountNumber(requestDto.getFromAccountNumber())
                .orElseThrow(() -> new IllegalArgumentException("Source account " + requestDto.getFromAccountNumber() + " not found"));

        if (sourceAccount.getBalance().compareTo(requestDto.getAmount()) < 0) {
            throw new IllegalArgumentException("Insufficient funds in account " + sourceAccount.getAccountNumber());
        }

        // Deduct from source
        sourceAccount.setBalance(sourceAccount.getBalance().subtract(requestDto.getAmount()));
        accountRepository.save(sourceAccount);

        // Record debit transaction
        String txId = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Transaction debitTx = new Transaction(
                txId,
                "Transfer to " + requestDto.getToAccount() + ": " + requestDto.getDescription(),
                requestDto.getAmount(),
                "DEBIT",
                "COMPLETED",
                sourceAccount
        );
        Transaction saved = transactionRepository.save(debitTx);

        // If target account is an internal account, credit it as well
        accountRepository.findByAccountNumber(requestDto.getToAccount()).ifPresent(targetAccount -> {
            targetAccount.setBalance(targetAccount.getBalance().add(requestDto.getAmount()));
            accountRepository.save(targetAccount);

            String creditTxId = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            Transaction creditTx = new Transaction(
                    creditTxId,
                    "Received from " + sourceAccount.getAccountNumber() + ": " + requestDto.getDescription(),
                    requestDto.getAmount(),
                    "CREDIT",
                    "COMPLETED",
                    targetAccount
            );
            transactionRepository.save(creditTx);
        });

        return mapToDto(saved);
    }

    public TransactionResponseDto mapToDto(Transaction tx) {
        return new TransactionResponseDto(
                tx.getId(),
                tx.getTransactionId(),
                tx.getDescription(),
                tx.getAmount(),
                tx.getType(),
                tx.getStatus(),
                tx.getTimestamp(),
                tx.getAccount() != null ? tx.getAccount().getAccountNumber() : "N/A",
                tx.getAccount() != null ? tx.getAccount().getHolderName() : "N/A"
        );
    }
}
