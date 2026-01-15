package com.musti.codereviewer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodeReviewRequest {

    @NotBlank(message = "Code cannot be empty")
    @Size(max = 50000, message = "Code cannot exceed 50000 characters")
    private String code;

    @Size(max = 50, message = "Language cannot exceed 50 characters")
    private String language; // java, python, etc.

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description; // Optional context about the code
}
