package antifraud.controller.transaction;

import antifraud.exception.UnauthorizedUserException;
import antifraud.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/antifraud")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("transaction")
    public TransactionResponse checkTransactionAmount(@RequestBody TransactionAmount amount) {
        return transactionService.checkTransactionAmount(amount);
    }

    @PutMapping("transaction")
    public FeedbackResponse feedback(@RequestBody FeedbackRequest request) {
        return transactionService.feedback(request);
    }

    @GetMapping("history")
    public List<FeedbackResponse> history() {
        return transactionService.getHistory();
    }

    @GetMapping("history/{number}")
    public List<FeedbackResponse> historyByNumber(@PathVariable String number) {
        return transactionService.getHistoryByNumber(number);
    }


    @PostMapping("suspicious-ip")
    public IpAntifraudResponse ipAntifraud(@RequestBody IpAntifraudAmount amount) {
        return transactionService.ipAntifraud(amount);
    }

    @DeleteMapping("suspicious-ip/{ip}")
    public DeleteIpResponse deleteIp(@PathVariable String ip) {
        return transactionService.deleteIp(ip);
    }

    @GetMapping("suspicious-ip")
    public List<IpAntifraudResponse> listIp() {
        return transactionService.getListIp();
    }

    @PostMapping("stolencard")
    public StolencardResponse saveStolenCard(@RequestBody StolencardAmount amount) {
        return transactionService.saveStolencard(amount);
    }

    @DeleteMapping("stolencard/{number}")
    public DeleteCardByStolenCard deleteCard(@PathVariable String number) {
        return transactionService.deleteCard(number);
    }

    @GetMapping("stolencard")
    public List<StolencardResponse> listCard() {
        return transactionService.getListCards();
    }

    @ExceptionHandler({UnauthorizedUserException.class})
    public ResponseEntity<Object> responseEntity(UnauthorizedUserException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }

}
