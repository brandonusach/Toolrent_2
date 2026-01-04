package com.toolrent.msloans.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ToolInstanceDTO {
    private Long id;
    private Long toolId;
    private String status;
}

