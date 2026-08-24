package ru.seledcov.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.seledcov.entity.Client;

public interface ClientRepository extends JpaRepository<Client, Long> {
}
