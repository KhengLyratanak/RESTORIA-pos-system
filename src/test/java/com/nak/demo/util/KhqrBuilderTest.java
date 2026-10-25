package com.nak.demo.util;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

class KhqrBuilderTest {

    @Test
    void crcMatchesStandardCcittFalseVector() {
        assertEquals("29B1", KhqrBuilder.crc16("123456789"));
    }

    @Test
    void qrIsWellFormed() {
        var r = KhqrBuilder.build(new KhqrBuilder.Params(
                "shop@bank", "Test Shop", "Phnom Penh", false, null, null,
                "KHR", new BigDecimal("10000"), "ORD-1", "POS-1", 1_700_000_000_000L, 1_700_000_180_000L));

        String qr = r.qr();
        assertTrue(qr.startsWith("000201010212"));
        assertTrue(qr.contains("5303116"));        // KHR
        assertTrue(qr.contains("540510000"));      // amount
        String body = qr.substring(0, qr.length() - 4);
        assertTrue(body.endsWith("6304"));
        assertEquals(KhqrBuilder.crc16(body), qr.substring(qr.length() - 4));
        assertEquals(32, r.md5().length());
    }

    @Test
    void usdAmountHasTwoDecimals() {
        var r = KhqrBuilder.build(new KhqrBuilder.Params(
                "shop@bank", "Test Shop", "Phnom Penh", false, null, null,
                "USD", new BigDecimal("4.5"), "ORD-2", "POS-1", 1L, 2L));
        assertTrue(r.qr().contains("54044.50"));
        assertTrue(r.qr().contains("5303840"));
    }
}

