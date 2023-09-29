package antifraud.dao.transaction;

import antifraud.enums.TransactionEnum;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatusTransactionRepository extends JpaRepository<StatusTransactionEntity, Long> {
    StatusTransactionEntity findByStatus(TransactionEnum status);

}
