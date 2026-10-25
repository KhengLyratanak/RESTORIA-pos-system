package com.nak.demo.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;

/**
 * Builds a dynamic KHQR string (EMV merchant-presented QR, Bakong profile) locally.
 * No network call is needed to create a QR: it is just formatted text + a CRC.
 */
public final class KhqrBuilder {

    private KhqrBuilder() {}

    public record Params(
            String bakongAccountId,     // ABA personal: "abaakhppxxx@abaa"
            String accountInformation,  // individual QR: the account number that receives the money (USD account for USD orders)
            String merchantName,        // max 25 chars, ASCII
            String merchantCity,        // max 15 chars, ASCII
            boolean merchantAccount,    // true = merchant QR (tag 30), false = individual QR (tag 29)
            String merchantId,          // merchant QR only
            String acquiringBank,       // e.g. "ABA Bank" (used by both QR types)
            String currency,            // "KHR" or "USD"
            BigDecimal amount,
            String billNumber,          // shown in bank statements, we use the order number
            String terminalLabel,       // e.g. "POS-1"
            long createdAtMillis,
            long expiresAtMillis) {}

    /** qr = the string you render as a QR image. md5 = the key used to ask Bakong "was this paid?". */
    public record Result(String qr, String md5) {}

    public static Result build(Params p) {
        StringBuilder sb = new StringBuilder();

        sb.append(tlv("00", "01"));   // payload format indicator
        sb.append(tlv("01", "12"));   // 12 = dynamic QR (amount fixed, single use)

        String account = ascii(p.bakongAccountId(), 32, "bakongAccountId");
        if (p.merchantAccount()) {
            sb.append(tlv("30",
                    tlv("00", account)
                            + tlv("01", ascii(p.merchantId(), 32, "merchantId"))
                            + tlv("02", ascii(p.acquiringBank(), 32, "acquiringBank"))));
        } else {
            // Individual account: 00 = Bakong ID, 01 = account number, 02 = bank name.
            // ABA's shared ID (abaakhppxxx@abaa) does not say WHICH account to credit,
            // so the account number in 01 is what routes the money to you.
            StringBuilder indiv = new StringBuilder(tlv("00", account));
            if (notBlank(p.accountInformation())) {
                indiv.append(tlv("01", ascii(p.accountInformation(), 32, "accountInformation")));
            }
            if (notBlank(p.acquiringBank())) {
                indiv.append(tlv("02", ascii(p.acquiringBank(), 32, "acquiringBank")));
            }
            sb.append(tlv("29", indiv.toString()));
        }

        sb.append(tlv("52", "5999"));                              // merchant category code
        sb.append(tlv("53", currencyCode(p.currency())));          // 116 = KHR, 840 = USD
        sb.append(tlv("54", formatAmount(p.amount(), p.currency())));
        sb.append(tlv("58", "KH"));
        sb.append(tlv("59", ascii(p.merchantName(), 25, "merchantName")));
        sb.append(tlv("60", ascii(p.merchantCity(), 15, "merchantCity")));

        String additional = "";
        if (notBlank(p.billNumber()))    additional += tlv("01", ascii(p.billNumber(), 25, "billNumber"));
        if (notBlank(p.terminalLabel())) additional += tlv("07", ascii(p.terminalLabel(), 25, "terminalLabel"));
        if (!additional.isEmpty()) sb.append(tlv("62", additional));

        // KHQR timestamp template: creation + expiration (ms since epoch). The banking app refuses expired QRs.
        sb.append(tlv("99", tlv("00", Long.toString(p.createdAtMillis()))
                + tlv("01", Long.toString(p.expiresAtMillis()))));

        sb.append("6304");                                         // CRC tag + length, value appended below
        String qr = sb + crc16(sb.toString());
        return new Result(qr, md5(qr));
    }

    /** Tag(2) + length(2 digits) + value. */
    static String tlv(String tag, String value) {
        return tag + String.format("%02d", value.length()) + value;
    }

    /** CRC-16/CCITT-FALSE: poly 0x1021, init 0xFFFF, uppercase hex, 4 chars. */
    public static String crc16(String data) {
        int crc = 0xFFFF;
        for (byte b : data.getBytes(StandardCharsets.US_ASCII)) {
            crc ^= (b & 0xFF) << 8;
            for (int i = 0; i < 8; i++) {
                crc = (crc & 0x8000) != 0 ? ((crc << 1) ^ 0x1021) & 0xFFFF : (crc << 1) & 0xFFFF;
            }
        }
        return String.format("%04X", crc);
    }

    public static String md5(String s) {
        try {
            byte[] d = MessageDigest.getInstance("MD5").digest(s.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(d); // lowercase hex, as Bakong expects
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private static String currencyCode(String c) {
        return switch (c.toUpperCase()) {
            case "KHR" -> "116";
            case "USD" -> "840";
            default -> throw new IllegalArgumentException("Unsupported currency: " + c);
        };
    }

    private static String formatAmount(BigDecimal amount, String currency) {
        if (amount == null || amount.signum() <= 0) throw new IllegalArgumentException("Amount must be positive");
        return "KHR".equalsIgnoreCase(currency)
                ? amount.setScale(0, RoundingMode.HALF_UP).toPlainString()   // riel has no decimals
                : amount.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private static String ascii(String s, int max, String field) {
        if (s == null || s.isBlank()) throw new IllegalArgumentException(field + " is required");
        // An unresolved ${...} means a setting was never filled in. Fail instead of printing a fake QR.
        if (s.contains("${")) throw new IllegalStateException(field + " is not configured (still a placeholder): " + s);
        if (!s.chars().allMatch(c -> c >= 32 && c < 127)) throw new IllegalArgumentException(field + " must be ASCII");
        return s.length() > max ? s.substring(0, max) : s;
    }

    private static boolean notBlank(String s) { return s != null && !s.isBlank(); }
}