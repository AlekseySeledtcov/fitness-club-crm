package ru.seledcov.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.seledcov.entity.Tariff;

public interface TariffRepository extends JpaRepository<Tariff, Long> {

    boolean existsByFingerprint(String fingerprint);

    boolean existsByFingerprintAndIdNot(String fingerprint, Long id);

}
