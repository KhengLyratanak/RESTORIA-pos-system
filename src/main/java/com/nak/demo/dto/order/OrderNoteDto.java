package com.nak.demo.dto.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/** OrderResponseDto plus the order note. Kept beside the mapper so the DTO itself didn't need editing. */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class OrderNoteDto extends OrderResponseDto {
    @JsonProperty("note")
    private String note;
}
