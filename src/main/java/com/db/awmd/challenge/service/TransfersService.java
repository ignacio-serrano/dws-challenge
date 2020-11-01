package com.db.awmd.challenge.service;

import java.math.BigDecimal;

public interface TransfersService {
    void transfer(String accountFromId, String accountToId, BigDecimal amount);
}
