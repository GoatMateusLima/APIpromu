package com.promos.app.repository;

import com.promos.app.entity.Promocao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface PromocaoRepository extends JpaRepository<Promocao, Long> {

    @Query("SELECT p FROM Promocao p WHERE p.expiraEm > CURRENT_TIMESTAMP")
    List<Promocao> findAtivas();

    long deleteByExpiraEmBefore(LocalDateTime data);
}