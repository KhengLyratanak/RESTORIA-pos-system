package com.nak.demo.controller;

import com.nak.demo.dto.response.BakongHealthResponse;
import com.nak.demo.service.BakongService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/bakong")
public class BakongController {

    private final BakongService bakongService;

    public BakongController(BakongService bakongService) {
        this.bakongService = bakongService;
    }

    /** Quick check from the browser or Postman: is Bakong reachable and is the token accepted? */
    @GetMapping("/health")
    public BakongHealthResponse health() {
        return bakongService.health();
    }
}