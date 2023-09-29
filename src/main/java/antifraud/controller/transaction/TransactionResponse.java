package antifraud.controller.transaction;

import antifraud.enums.TransactionEnum;

public record TransactionResponse(TransactionEnum result, String info) {

}
