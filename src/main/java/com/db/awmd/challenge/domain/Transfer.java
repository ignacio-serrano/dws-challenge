package com.db.awmd.challenge.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class Transfer {

  @NotBlank
  private String accountFromId;

  @NotBlank
  private String accountToId;

  @NotNull
  @Min(value = 0, message = "Amount to transfer must be positive.")
  private BigDecimal amount;

}
