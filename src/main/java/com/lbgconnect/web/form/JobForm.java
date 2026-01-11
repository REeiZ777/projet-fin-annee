package com.lbgconnect.web.form;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class JobForm {

    @NotBlank
    @Size(max = 160)
    private String title;

    @NotBlank
    @Size(min = 20, max = 2000)
    private String description;

    @NotBlank
    @Size(max = 80)
    private String category;

    @NotBlank
    @Size(max = 120)
    private String location;

    @NotBlank
    @Size(max = 60)
    private String contractType;

    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal salaryMin;

    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal salaryMax;

    @Size(max = 200)
    private String tags;
}
