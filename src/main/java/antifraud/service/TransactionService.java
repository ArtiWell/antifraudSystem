package antifraud.service;

import antifraud.controller.transaction.*;
import antifraud.dao.antifraud.IpAntifraudEntity;
import antifraud.dao.antifraud.IpAntifraudRepository;
import antifraud.dao.antifraud.StolenCardEntity;
import antifraud.dao.antifraud.StolenCardRepository;
import antifraud.dao.transaction.StatusTransactionEntity;
import antifraud.dao.transaction.StatusTransactionRepository;
import antifraud.dao.transaction.TransactionEntity;
import antifraud.dao.transaction.TransactionRepository;
import antifraud.enums.TransactionEnum;
import antifraud.exception.ConflictException;
import antifraud.exception.UnprocessableException;
import antifraud.exception.WrongDataException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;

import static antifraud.enums.TransactionEnum.MANUAL_PROCESSING;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final IpAntifraudRepository ipAntifraudRepository;
    private final StolenCardRepository stolenCardRepository;
    private final TransactionRepository transactionRepository;
    private final StatusTransactionRepository statusTransactionRepository;

    public TransactionResponse checkTransactionAmount(TransactionAmount amount) {
        TransactionEnum transactionEnum;
        if (
                amount.getNumber() == null
                        || isInvalidLuhn(amount.getNumber())
                        || isInvalidIp(amount.getIp())
                        || amount.getAmount() == null
                        || amount.getAmount() <= 0
        ) {
            throw new WrongDataException();
        }

        List<String> answer = new ArrayList<>();

        if (amount.getAmount() <= statusTransactionRepository.findByStatus(TransactionEnum.ALLOWED).getPrice()) {
            transactionEnum = TransactionEnum.ALLOWED;
        } else if (amount.getAmount() <= statusTransactionRepository.findByStatus(MANUAL_PROCESSING).getPrice()) {
            transactionEnum = MANUAL_PROCESSING;
            answer.add("amount");
        } else {
            transactionEnum = TransactionEnum.PROHIBITED;
            answer.add("amount");
        }

        if (stolenCardRepository.findByNumber(amount.getNumber()).isPresent()) {
            if (transactionEnum == MANUAL_PROCESSING) {
                answer = new ArrayList<>();
            }
            answer.add("card-number");
            transactionEnum = TransactionEnum.PROHIBITED;

        }

        if (ipAntifraudRepository.findByIp(amount.getIp()).isPresent()) {
            if (transactionEnum == MANUAL_PROCESSING) {
                answer = new ArrayList<>();
            }
            answer.add("ip");
            transactionEnum = TransactionEnum.PROHIBITED;

        }
        Long ipCorrelation = transactionRepository.checkIpCorrelation(amount.getNumber(),
                amount.getDate().minusHours(1L), amount.getDate(), amount.getIp());
        if (ipCorrelation >= 2) {
            if (transactionEnum == MANUAL_PROCESSING) {
                answer = new ArrayList<>();
            }
            answer.add("ip-correlation");
            if (ipCorrelation == 2 && transactionEnum != TransactionEnum.PROHIBITED) {
                transactionEnum = MANUAL_PROCESSING;
            } else {
                transactionEnum = TransactionEnum.PROHIBITED;
            }
        }

        Long regionCorrelation = transactionRepository.checkRegionCorrelation(amount.getNumber(),
                amount.getDate().minusHours(1L), amount.getDate(), amount.getRegion().toString());
        if (regionCorrelation >= 2) {
            if (transactionEnum == MANUAL_PROCESSING) {
                answer = new ArrayList<>();
            }
            answer.add("region-correlation");
            if (regionCorrelation == 2 && transactionEnum != TransactionEnum.PROHIBITED) {
                transactionEnum = MANUAL_PROCESSING;
            } else {
                transactionEnum = TransactionEnum.PROHIBITED;
            }
        }

        TransactionEntity transactionEntity = createdEntity(amount, transactionEnum, String.join(", ", answer));
        transactionRepository.save(transactionEntity);

        return new TransactionResponse(transactionEntity.getResult(), transactionEntity.getInfo());

    }

    private TransactionEntity createdEntity(TransactionAmount amount, TransactionEnum transactionEnum, String answer) {
        TransactionEntity transactionEntity = new TransactionEntity();
        transactionEntity.setIp(amount.getIp());
        transactionEntity.setAmount(amount.getAmount());
        transactionEntity.setNumber(amount.getNumber());
        transactionEntity.setRegion(amount.getRegion());
        transactionEntity.setDate(amount.getDate());
        transactionEntity.setResult(transactionEnum);
        if (transactionEnum.equals(TransactionEnum.ALLOWED)) {
            transactionEntity.setInfo("none");
        } else {
            transactionEntity.setInfo(answer);
        }
        return transactionEntity;
    }

    public IpAntifraudResponse ipAntifraud(IpAntifraudAmount amount) {

        if (ipAntifraudRepository.findByIp(amount.ip()).isPresent()) {
            throw new ConflictException();
        }
        if (isInvalidIp(amount.ip())) {
            throw new WrongDataException();
        }

        IpAntifraudEntity ipAntifraudEntity = new IpAntifraudEntity();
        ipAntifraudEntity.setIp(amount.ip());
        ipAntifraudRepository.save(ipAntifraudEntity);
        return new IpAntifraudResponse(ipAntifraudEntity.getId(), ipAntifraudEntity.getIp());
    }

    private boolean isInvalidIp(String ip) {

        try {
            String[] groups = ip.split("\\.");
            return Arrays.stream(groups)
                    .map(Integer::parseInt)
                    .filter(i -> (i >= 0 && i <= 255))
                    .count() != 4;
        } catch (Exception e) {
            return true;
        }
    }

    public DeleteIpResponse deleteIp(String ip) {
        if (isInvalidIp(ip)) {
            throw new WrongDataException();
        }
        if (ipAntifraudRepository.findByIp(ip).isEmpty()) {
            throw new EntityNotFoundException();
        }
        ipAntifraudRepository.delete(ipAntifraudRepository.findByIp(ip).get());
        return new DeleteIpResponse(String.format("IP %s successfully removed!", ip));
    }

    public List<IpAntifraudResponse> getListIp() {
        return ipAntifraudRepository.findAll(Sort.by("id")).stream().map(
                a -> new IpAntifraudResponse(a.getId(), a.getIp())
        ).toList();
    }

    public StolencardResponse saveStolencard(StolencardAmount amount) {
        if (stolenCardRepository.findByNumber(amount.number()).isPresent()) {
            throw new ConflictException();
        }
        if (isInvalidLuhn(amount.number())) {
            throw new WrongDataException();
        }
        StolenCardEntity entity = new StolenCardEntity();
        entity.setNumber(amount.number());
        stolenCardRepository.save(entity);
        return new StolencardResponse(entity.getId(), entity.getNumber());
    }

    private boolean isInvalidLuhn(String number) {
        int sum = Character.getNumericValue(number.charAt(number.length() - 1));
        int parity = number.length() % 2;
        for (int i = number.length() - 2; i >= 0; i--) {
            int summand = Character.getNumericValue(number.charAt(i));
            if (i % 2 == parity) {
                int product = summand * 2;
                summand = (product > 9) ? (product - 9) : product;
            }
            sum += summand;
        }
        return !((sum % 10) == 0);
    }

    public DeleteCardByStolenCard deleteCard(String number) {
        if (isInvalidLuhn(number)) {
            throw new WrongDataException();
        }
        if (stolenCardRepository.findByNumber(number).isEmpty()) {
            throw new EntityNotFoundException();
        }
        stolenCardRepository.delete(stolenCardRepository.findByNumber(number).get());
        return new DeleteCardByStolenCard(String.format("Card %s successfully removed!", number));
    }

    public List<StolencardResponse> getListCards() {
        return stolenCardRepository.findAll(Sort.by("id")).stream().map(
                a -> new StolencardResponse(a.getId(), a.getNumber())
        ).toList();
    }

    @Transactional
    public FeedbackResponse feedback(FeedbackRequest request) {
        TransactionEntity entity = transactionRepository.findById(request.transactionId())
                .orElseThrow(EntityNotFoundException::new);
        Long amount = entity.getAmount();

        if (entity.getFeedback() != null) {
            throw new ConflictException();
        }
        if (request.feedback() == entity.getResult()) {
            throw new UnprocessableException();
        }

        StatusTransactionEntity entityStatus = statusTransactionRepository.findByStatus(request.feedback());
        StatusTransactionEntity entityResult = statusTransactionRepository.findByStatus(entity.getResult());
        StatusTransactionEntity manualProcessingStatus = statusTransactionRepository.findByStatus(MANUAL_PROCESSING);

        long change = entityResult.getId() - entityStatus.getId();
        if (change == 2) {
            entityStatus.setPrice(calculateByFormula(entityStatus.getPrice(), amount, Double::sum));
            manualProcessingStatus.setPrice(calculateByFormula(manualProcessingStatus.getPrice(), amount, Double::sum));
        } else if (change == 1) {
            entityStatus.setPrice(calculateByFormula(entityStatus.getPrice(), amount, Double::sum));
        } else if (change == -2) {
            entityResult.setPrice(calculateByFormula(entityResult.getPrice(), amount, (a, b) -> a - b));
            manualProcessingStatus.setPrice(calculateByFormula(manualProcessingStatus.getPrice(), amount, (a, b) -> a - b));
        } else if (change == -1) {
            entityResult.setPrice(calculateByFormula(entityResult.getPrice(), amount, (a, b) -> a - b));
        }

        entity.setFeedback(request.feedback());
        return mapperEntityByFeedbackResponse(entity);
    }

    private long calculateByFormula(Long price, Long amount, BiFunction<Double, Double, Double> function) {
        return (long) Math.ceil(function.apply(0.8 * price, 0.2 * amount));
    }

    private FeedbackResponse mapperEntityByFeedbackResponse(TransactionEntity entity) {
        return new FeedbackResponse(
                entity.getId(), entity.getAmount(), entity.getIp(), entity.getNumber(), entity.getRegion(),
                entity.getDate(), entity.getResult(), entity.getFeedback() == null ? "" : entity.getFeedback().name()
        );
    }

    public List<FeedbackResponse> getHistory() {
        return transactionRepository.findAll(Sort.by("id")).stream().map(
                this::mapperEntityByFeedbackResponse).toList();
    }

    public List<FeedbackResponse> getHistoryByNumber(String number) {
        if (number == null || isInvalidLuhn(number)) {
            throw new WrongDataException();
        }
        List<TransactionEntity> entities = transactionRepository.findByNumber(number);
        if (entities.size() == 0) {
            throw new EntityNotFoundException();
        }
        return entities.stream().map(this::mapperEntityByFeedbackResponse).toList();

    }
}
