package paveljakov.transfer.repository.transaction;

import java.util.List;
import java.util.Optional;

import paveljakov.transfer.dto.EntityIdResponseDto;
import paveljakov.transfer.dto.transaction.TransactionCreateDto;
import paveljakov.transfer.dto.transaction.TransactionDto;
import paveljakov.transfer.dto.transaction.TransactionUpdateDto;

public interface TransactionRepository {

    Optional<TransactionDto> find(String id);

    List<TransactionDto> findByWallet(String walletId);

    List<TransactionDto> findByAccount(String accountId);

    Optional<EntityIdResponseDto> create(TransactionCreateDto dto);

    TransactionDto lock(String id);

    void update(TransactionUpdateDto dto);

}
