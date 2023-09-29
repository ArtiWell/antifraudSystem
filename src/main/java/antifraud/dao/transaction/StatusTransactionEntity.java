package antifraud.dao.transaction;


import antifraud.enums.TransactionEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "Status")
@Getter
@Setter
@ToString
public class StatusTransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private TransactionEnum status;

    @Column(name = "price")
    private Long price;
}
