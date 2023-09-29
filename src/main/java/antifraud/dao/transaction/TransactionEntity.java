package antifraud.dao.transaction;

import antifraud.enums.RegionsEnum;
import antifraud.enums.TransactionEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "Transaction")
@Getter
@Setter
@ToString
public class TransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "ip")
    private String ip;

    @Column(name = "amount")
    private Long amount;

    @Column(name = "number")
    private String number;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "region")
    private RegionsEnum region;

    @Column(name = "date")
    private LocalDateTime date;

    @Column(name = "result")
    private TransactionEnum result;

    @Column(name = "info")
    private String info;

    @Column(name = "feedback")
    @Enumerated(EnumType.STRING)
    private TransactionEnum feedback;
}
