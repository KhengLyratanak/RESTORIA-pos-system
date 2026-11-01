package com.nak.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

/** A dine-in table (Table 1–10) or a take-away number (Take Away 01–10). */
@Entity
@Table(name = "dining_tables")
@Data
public class DiningTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Short code, e.g. "T1" or "TA1". */
    @Column(nullable = false, unique = true, length = 10)
    private String code;

    /** Name printed on tickets, e.g. "Table 1" or "Take Away 01". */
    @Column(nullable = false, length = 30)
    private String name;

    /** DINE_IN or TAKE_AWAY */
    @Column(nullable = false, length = 10)
    private String type;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;
}
