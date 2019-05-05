package paveljakov.transfer.service.transaction;

import java.util.List;
import java.util.Optional;

import paveljakov.transfer.dto.EntityIdResponseDto;
import paveljakov.transfer.dto.transaction.TransactionCreateDto;
import paveljakov.transfer.dto.transaction.TransactionDto;

public interface TransactionService {

    Optional<TransactionDto> find(String id);

    List<TransactionDto> findByWallet(String walletId);

    List<TransactionDto> findByAccount(String accountId);

    Optional<EntityIdResponseDto> transfer(TransactionCreateDto dto);

    Optional<EntityIdResponseDto> create(TransactionCreateDto dto);

    void authorize(String id);

    void capture(String id);

    void cancel(final String id);

}
