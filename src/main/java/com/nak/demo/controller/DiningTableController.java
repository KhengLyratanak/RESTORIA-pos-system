package com.nak.demo.controller;

import com.nak.demo.dto.base.Response;
import com.nak.demo.dto.table.TableResponseDto;
import com.nak.demo.service.DiningTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tables")
public class DiningTableController {

    @Autowired
    private DiningTableService diningTableService;

    /** Every table with FREE / BUSY and, when busy, its open order. */
    @GetMapping
    public ResponseEntity<Response> listTables() {
        List<TableResponseDto> tables = diningTableService.listTables();
        return ResponseEntity.status(HttpStatus.OK)
                .body(Response.success("200", "success", "successfully retrieved tables", tables));
    }
}
