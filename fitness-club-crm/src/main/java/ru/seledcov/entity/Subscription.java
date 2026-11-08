package ru.seledcov.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.seledcov.infrastructure.AccessTime;
import ru.seledcov.infrastructure.SubscriptionStatus;
import ru.seledcov.infrastructure.SubscriptionType;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long clientId;

    private String tariffName;

    @Enumerated(EnumType.STRING)
    private SubscriptionType type;

    private Long trainerId;

    private BigDecimal basePrice;

    private BigDecimal finalPrice;

    private boolean unlimited;

    private Integer totalVisits;

    private Integer remainingVisits;

    private LocalDate startDate;

    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private AccessTime accessTime;

    @Enumerated(EnumType.STRING)
    private SubscriptionStatus status;

    private boolean complimentary;
}
