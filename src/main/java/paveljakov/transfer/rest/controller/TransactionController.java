package paveljakov.transfer.rest.controller;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import paveljakov.transfer.common.CommonConstants;
import paveljakov.transfer.dto.EntityIdResponseDto;
import paveljakov.transfer.dto.transaction.TransactionCreateDto;
import paveljakov.transfer.dto.transaction.TransactionDto;
import paveljakov.transfer.repository.transaction.TransactionRepository;
import paveljakov.transfer.rest.transform.JsonTransformer;
import spark.Request;
import spark.Response;
import spark.Spark;

@Singleton
class TransactionController implements RestController {

    private final TransactionRepository transactionRepository;

    private final JsonTransformer jsonTransformer;

    @Inject
    public TransactionController(final TransactionRepository transactionRepository, final JsonTransformer jsonTransformer) {
        this.transactionRepository = transactionRepository;
        this.jsonTransformer = jsonTransformer;
    }

    @Override
    public void configureRoutes() {
        Spark.put("/transactions", CommonConstants.JSON_TYPE, this::newTransaction, jsonTransformer);
        Spark.get("/transactions/:id", this::getTransaction, jsonTransformer);
        Spark.get("/wallets/:walletId/transactions", this::getTransactionForWallet, jsonTransformer);
        Spark.get("/accounts/:accountId/transactions", this::getTransactionForAccount, jsonTransformer);
    }

    private EntityIdResponseDto newTransaction(final Request request, final Response response) {
        final TransactionCreateDto dto = jsonTransformer.deserialize(request.body(), TransactionCreateDto.class);

        final EntityIdResponseDto transactionId = transactionRepository.create(dto)
                .orElseThrow();

        transactionRepository.authorize(transactionId.getId());

        transactionRepository.capture(transactionId.getId());

        return transactionId;
    }

    private TransactionDto getTransaction(final Request request, final Response response) {
        return transactionRepository.find(request.params("id"))
                .orElseThrow();
    }

    private List<TransactionDto> getTransactionForWallet(final Request request, final Response response) {
        return transactionRepository.findByWallet(request.params("walletId"));
    }

    private List<TransactionDto> getTransactionForAccount(final Request request, final Response response) {
        return transactionRepository.findByAccount(request.params("accountId"));
    }

}
