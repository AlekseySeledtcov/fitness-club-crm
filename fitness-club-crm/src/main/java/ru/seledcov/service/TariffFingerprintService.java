package ru.seledcov.service;

import org.springframework.stereotype.Service;
import ru.seledcov.dto.TariffRequestDto;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@Service
public class TariffFingerprintService {

    public String calculateFingerprint(TariffRequestDto dto) {

        String canonicalString = createCanonicalString(dto);

        return sha256(canonicalString);
    }


    private String createCanonicalString(TariffRequestDto dto) {

        String normalisedName = dto.name()
                .strip()
                .replaceAll("\\s+", " ")
                .toLowerCase();

        String normalisedTotalVisits = dto.totalVisits() == null ? "NULL" : dto.totalVisits().toString();

        String normalisedDurationDays = dto.durationDays() == null ? "NULL" : dto.durationDays().toString();

        String normalisedAccessTime = dto.accessTime() == null ? "NULL" : dto.accessTime().getName();

        BigDecimal normalisedPrice = dto.price().setScale(2);

        return "name=" + normalisedName + ";" +
                "type=" + dto.type().getName() + ";" +
                "clientCategory=" + dto.clientCategory().getName() + ";" +
                "unlimited=" + dto.unlimited() + ";" +
                "totalVisits=" + normalisedTotalVisits + ";" +
                "durationDays=" + normalisedDurationDays + ";" +
                "accessTime=" + normalisedAccessTime + ";" +
                "price=" + normalisedPrice;
    }

    private static String sha256(String value) {

        try {
            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] input =
                    value.getBytes(StandardCharsets.UTF_8);

            byte[] hash =
                    digest.digest(input);

            String result =
                    HexFormat.of().formatHex(hash);

            return result;

        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(
                    "SHA-256 is not available", e
            );
        }
    }
}
