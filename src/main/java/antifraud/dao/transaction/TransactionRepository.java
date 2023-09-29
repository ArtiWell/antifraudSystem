package antifraud.dao.transaction;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {
    List<TransactionEntity> findByNumber(String number);

    Optional<TransactionEntity> findById(Long id);

    List<TransactionEntity> findAll(Sort sort);

    @Query(value = "SELECT count (distinct t.ip) FROM Transaction t WHERE t.number = :numberCard and " +
            "t.ip != :ip and" +
            "(t.date >= :first and t.date < :second)", nativeQuery = true)
    Long checkIpCorrelation(String numberCard, LocalDateTime first, LocalDateTime second, String ip);

    @Query(value = "SELECT count (distinct t.region) FROM Transaction t WHERE t.number = :numberCard and " +
            "t.region != :region and" +
            "(t.date >= :first and t.date < :second)", nativeQuery = true)
    Long checkRegionCorrelation(String numberCard, LocalDateTime first, LocalDateTime second, String region);


}
