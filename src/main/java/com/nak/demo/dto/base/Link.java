package com.nak.demo.dto.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

@JsonPropertyOrder(value = {"self","first","last","previous","next"})
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class Link {
    private String self;
    private String first;
    private String last;
    private String previous;
    private String next;
}
