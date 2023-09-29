package antifraud.controller.transaction;

import antifraud.enums.TransactionEnum;

public record FeedbackRequest(Long transactionId, TransactionEnum feedback) {
}
