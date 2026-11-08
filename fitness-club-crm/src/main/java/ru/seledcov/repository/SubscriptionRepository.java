package ru.seledcov.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.seledcov.entity.Subscription;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
}
