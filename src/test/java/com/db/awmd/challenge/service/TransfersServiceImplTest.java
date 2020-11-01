package com.db.awmd.challenge.service;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.TransferAccountNotFound;
import com.db.awmd.challenge.repository.AccountsRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class TransfersServiceImplTest {

    @Mock
    private AccountsRepository accountsRepositoryMock;

    @Mock
    private NotificationService notificationServiceMock;

    @InjectMocks
    private TransfersServiceImpl testObject;

    @Test
    public void transfer_OK() {
        //GIVEN
        Account accountFrom = givenAnExistingAccount("Id-123", new BigDecimal("1000"));
        Account accountTo = givenAnExistingAccount("Id-321", new BigDecimal("0"));

        //WHEN
        testObject.transfer("Id-123", "Id-321", new BigDecimal("250"));

        //THEN
        verify(accountsRepositoryMock).transfer(eq("Id-123"), eq("Id-321"), eq(new BigDecimal("250")));
        verify(notificationServiceMock).notifyAboutTransfer(
                eq(accountFrom),
                eq("Sent 250 to Id-321"));
        verify(notificationServiceMock).notifyAboutTransfer(
                eq(accountTo),
                eq("Received 250 from Id-123"));
    }

    @Test
    public void transfer_ERR_accountFromNotFound() {
        //GIVEN
        Account accountTo = givenAnExistingAccount("Id-321", new BigDecimal("0"));

        //WHEN
        try {
            testObject.transfer("Id-123", "Id-321", new BigDecimal("250"));
            //THEN
        } catch (TransferAccountNotFound e) {
            verify(accountsRepositoryMock, times(0)).transfer(anyString(), anyString(), any(BigDecimal.class));
            verify(notificationServiceMock, times(0)).notifyAboutTransfer(any(Account.class), anyString());
            return;
        }
        fail("Expected TransferAccountNotFound not thrown.");
    }

    @Test
    public void transfer_ERR_accountToNotFound() {
        //GIVEN
        Account accountFrom = givenAnExistingAccount("Id-123", new BigDecimal("1000"));

        //WHEN
        try {
            testObject.transfer("Id-123", "Id-321", new BigDecimal("250"));
            //THEN
        } catch (TransferAccountNotFound e) {
            verify(accountsRepositoryMock, times(0)).transfer(anyString(), anyString(), any(BigDecimal.class));
            verify(notificationServiceMock, times(0)).notifyAboutTransfer(any(Account.class), anyString());
            return;
        }
        fail("Expected TransferAccountNotFound not thrown.");
    }

    private Account givenAnExistingAccount(String id, BigDecimal balance) {
        Account account = new Account(id, balance);
        doReturn(account).when(accountsRepositoryMock).getAccount(eq(id));
        return account;
    }
}