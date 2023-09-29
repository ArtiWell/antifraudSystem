package antifraud.controller.transaction;

import antifraud.enums.RegionsEnum;
import antifraud.enums.TransactionEnum;

import java.time.LocalDateTime;

public record FeedbackResponse(Long transactionId,
                               Long amount,
                               String ip,
                               String number,
                               RegionsEnum region,
                               LocalDateTime date,
                               TransactionEnum result,
                               String feedback) {
}
