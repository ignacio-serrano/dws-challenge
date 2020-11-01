package com.db.awmd.challenge.web;

import com.db.awmd.challenge.domain.Transfer;
import com.db.awmd.challenge.service.TransfersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1/transfers")
@Slf4j
public class TransfersController {

    private final TransfersService transfersService;

    @Autowired
    public TransfersController(TransfersService transfersService) {
        this.transfersService = transfersService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void makeTransfer(@RequestBody @Valid Transfer transfer) {
        log.info("Processing transfer {}", transfer);

        transfersService.transfer(transfer.getAccountFromId(), transfer.getAccountToId(), transfer.getAmount());

        log.info("Transfer {} successfully processed.", transfer);
    }
}
