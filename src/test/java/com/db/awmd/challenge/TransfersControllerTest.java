package com.db.awmd.challenge;

import com.db.awmd.challenge.exception.OverdraftException;
import com.db.awmd.challenge.exception.TransferAccountNotFound;
import com.db.awmd.challenge.repository.AccountsRepositoryInMemory;
import com.db.awmd.challenge.service.NotificationService;
import com.db.awmd.challenge.service.TransfersService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class TransfersControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private TransfersService transfersServiceMock;

    @Before
    public void prepareMockMvc() {
        this.mockMvc = webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    public void transfer_OK() throws Exception {
        //GIVEN

        //WHEN
        this.mockMvc.perform(post("/v1/transfers")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"accountFromId\":\"Id-123\",\"accountToId\":\"Id-321\",\"amount\":250}"))
        //THEN
                .andExpect(status().isCreated())
                .andDo(print());

        verify(transfersServiceMock).transfer(eq("Id-123"), eq("Id-321"), eq(new BigDecimal("250")));
        verifyNoMoreInteractions(transfersServiceMock);
    }

    @Test
    public void transfer_ERR_NegativeAmount() throws Exception {
        //GIVEN

        //WHEN
        this.mockMvc.perform(post("/v1/transfers")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"accountFromId\":\"Id-123\",\"accountToId\":\"Id-321\",\"amount\":-250}"))
        //THEN
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    public void transfer_ERR_Overdraft() throws Exception {
        //GIVEN
        doThrow(new OverdraftException(null)).when(transfersServiceMock).transfer(anyString(), anyString(), any(BigDecimal.class));

        //WHEN
        this.mockMvc.perform(post("/v1/transfers")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"accountFromId\":\"Id-123\",\"accountToId\":\"Id-321\",\"amount\":250}"))
        //THEN
                .andExpect(status().isUnprocessableEntity())
                .andExpect(status().reason("Insufficient funds."))
                .andDo(print());
    }

    @Test
    public void transfer_ERR_NonexistentAccount() throws Exception {
        //GIVEN
        doThrow(new TransferAccountNotFound(null)).when(transfersServiceMock).transfer(anyString(), anyString(), any(BigDecimal.class));

        //WHEN
        this.mockMvc.perform(post("/v1/transfers")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"accountFromId\":\"Id-123\",\"accountToId\":\"Id-321\",\"amount\":250}"))
        //THEN
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("Account from/to not found."))
                .andDo(print());
    }
}
