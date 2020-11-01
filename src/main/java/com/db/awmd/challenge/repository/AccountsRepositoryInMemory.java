package com.db.awmd.challenge.repository;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.db.awmd.challenge.exception.OverdraftException;
import org.springframework.stereotype.Repository;

@Repository
public class AccountsRepositoryInMemory implements AccountsRepository {

  private final Map<String, Account> accounts = new ConcurrentHashMap<>();

  @Override
  public void createAccount(Account account) throws DuplicateAccountIdException {
    Account previousAccount = accounts.putIfAbsent(account.getAccountId(), account);
    if (previousAccount != null) {
      throw new DuplicateAccountIdException(
        "Account id " + account.getAccountId() + " already exists!");
    }
  }

  @Override
  public Account getAccount(String accountId) {
    return accounts.get(accountId);
  }

  @Override
  public void clearAccounts() {
    accounts.clear();
  }

  @Override
  public void transfer(String accountFromId, String accountToId, BigDecimal amount) {
    accounts.computeIfPresent(accountFromId, (fromId, accountFrom) -> {
      if (lesserThan(accountFrom.getBalance(), amount)) {
        throw new OverdraftException(amount + " cannot be transferred from account " + accountFromId + ": overdraft not allowed.");
      }

      accounts.computeIfPresent(accountToId, (toId, accountTo) ->
        new Account(accountToId, accountTo.getBalance().add(amount))
      );
      return new Account(accountFromId, accountFrom.getBalance().subtract(amount));
    });
  }

  private boolean lesserThan(BigDecimal first, BigDecimal second) {
      return first.compareTo(second) < 0;
  }
}
