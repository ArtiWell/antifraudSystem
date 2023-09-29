package antifraud.dao.antifraud;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Antifraud")
@Getter
@Setter
public class IpAntifraudEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "antifraud_id")
    private Long id;

    @Column(name = "ip")
    private String ip;

}
