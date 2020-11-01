package com.db.awmd.challenge;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.fail;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.OverdraftException;
import com.db.awmd.challenge.repository.AccountsRepositoryInMemory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;

@RunWith(MockitoJUnitRunner.class)
public class AccountsRepositoryInMemoryTest {

    @InjectMocks
    private AccountsRepositoryInMemory testObject;

    @Test
    public void transfer_OK() {
        //GIVEN
        givenAnExistingAccount("Id-123", new BigDecimal("1000"));
        givenAnExistingAccount("Id-321", new BigDecimal("0"));

        //WHEN
        testObject.transfer("Id-123", "Id-321", new BigDecimal("250"));

        //THEN
        Account accountFrom = testObject.getAccount("Id-123");
        Account accountTo = testObject.getAccount("Id-321");
        assertThat(accountFrom.getBalance(), is(equalTo(new BigDecimal("750"))));
        assertThat(accountTo.getBalance(), is(equalTo(new BigDecimal("250"))));
    }

    @Test
    public void tranfer_ERR_overdraft() {
        //GIVEN
        givenAnExistingAccount("Id-123", new BigDecimal("100"));
        givenAnExistingAccount("Id-321", new BigDecimal("0"));

        //WHEN
        try {
            testObject.transfer("Id-123", "Id-321", new BigDecimal("250"));
        //THEN
        } catch (OverdraftException e) {
            Account accountFrom = testObject.getAccount("Id-123");
            assertThat(accountFrom.getBalance(), is(equalTo(new BigDecimal("100"))));
            Account accountTo = testObject.getAccount("Id-321");
            assertThat(accountTo.getBalance(), is(equalTo(new BigDecimal("0"))));
            return;
        }
        fail("Expected OverdraftException not thrown.");
    }

    private void givenAnExistingAccount(String id, BigDecimal balance) {
        testObject.createAccount(new Account(id, balance));
    }
}
