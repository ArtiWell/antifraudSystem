package antifraud.controller.transaction;


import antifraud.enums.RegionsEnum;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TransactionAmount {
    private Long amount;
    private String ip;
    private String number;
    private RegionsEnum region;
    private LocalDateTime date;
}
