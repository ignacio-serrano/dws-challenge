package com.db.awmd.challenge.service;

import com.db.awmd.challenge.exception.TransferAccountNotFound;
import com.db.awmd.challenge.repository.AccountsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class TransfersServiceImpl implements TransfersService {

    private final AccountsRepository accountsRepository;
    private final NotificationService notificationService;

    @Autowired
    public TransfersServiceImpl(AccountsRepository accountsRepository, NotificationService notificationService) {
        this.accountsRepository = accountsRepository;
        this.notificationService = notificationService;
    }

    @Override
    public void transfer(String accountFromId, String accountToId, BigDecimal amount) {
        validateAccountExistence(accountFromId);
        validateAccountExistence(accountToId);

        accountsRepository.transfer(accountFromId, accountToId, amount);

        notificationService.notifyAboutTransfer(accountsRepository.getAccount(accountFromId), "Sent " + amount + " to " + accountToId);
        notificationService.notifyAboutTransfer(accountsRepository.getAccount(accountToId), "Received " + amount + " from " + accountFromId);
    }

    private void validateAccountExistence(String accountId) {
        if (accountsRepository.getAccount(accountId) == null) {
            throw new TransferAccountNotFound("Account " + accountId + " not found.");
        }
    }
}
