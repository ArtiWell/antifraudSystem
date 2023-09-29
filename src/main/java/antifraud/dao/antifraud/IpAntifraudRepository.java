package antifraud.dao.antifraud;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IpAntifraudRepository extends JpaRepository<IpAntifraudEntity, Long> {

    Optional<IpAntifraudEntity> findByIp(String ip);
}
