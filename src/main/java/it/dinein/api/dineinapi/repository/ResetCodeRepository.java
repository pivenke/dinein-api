package it.dinein.api.dineinapi.repository;

import it.dinein.api.dineinapi.model.ResetCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResetCodeRepository extends JpaRepository<ResetCode,Long> {
    ResetCode findByCode(String code);
}
