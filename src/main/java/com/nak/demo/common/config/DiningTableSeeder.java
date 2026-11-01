package com.nak.demo.common.config;

import com.nak.demo.service.DiningTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/** Runs once at startup: makes sure Table 1–10 and Take Away 01–10 exist. */
@Component
public class DiningTableSeeder implements CommandLineRunner {

    @Autowired
    private DiningTableService diningTableService;

    @Override
    public void run(String... args) {
        diningTableService.createDefaultTables();
    }
}
