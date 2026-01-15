package com.musti.codereviewer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodeReviewResponse {

    private Long id;
    private String code;
    private String language;
    private String description;
    private String review; // Review that comes from Claude
    private LocalDateTime createdAt;
}
