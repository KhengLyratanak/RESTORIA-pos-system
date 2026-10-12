package com.nak.demo.dto.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;
import java.util.List;

@JsonPropertyOrder(value = {"content","pagination"})
@JsonInclude(JsonInclude.Include.NON_NULL)
@Setter
@Getter
@AllArgsConstructor
public class PagenatedResponse<T>{

    private List<T> content;
    private Object object;

    public static <T> PagenatedResponse from(Page<T> page){
        return new PagenatedResponse(page.getContent(),page.getPageable());
    }
}
