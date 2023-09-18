package by.vlad.elibrary.repository;

import by.vlad.elibrary.model.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    Integer countByEmail(String email);

    Optional<Client> findByEmail(String email);
}
