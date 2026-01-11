package com.lbgconnect.web.form;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class JobApplicationForm {

    @NotBlank
    @Size(min = 10, max = 1000)
    private String coverLetter;

    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal expectedRate;
}
