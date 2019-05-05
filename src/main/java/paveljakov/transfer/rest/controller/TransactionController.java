package paveljakov.transfer.rest.controller;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import paveljakov.transfer.common.CommonConstants;
import paveljakov.transfer.dto.EntityIdResponseDto;
import paveljakov.transfer.dto.transaction.TransactionCreateDto;
import paveljakov.transfer.dto.transaction.TransactionDto;
import paveljakov.transfer.rest.transform.JsonTransformer;
import paveljakov.transfer.service.transaction.TransactionService;
import spark.Request;
import spark.Response;
import spark.Spark;

@Singleton
class TransactionController implements RestController {

    private final TransactionService transactionService;

    private final JsonTransformer jsonTransformer;

    @Inject
    public TransactionController(final TransactionService transactionService, final JsonTransformer jsonTransformer) {
        this.transactionService = transactionService;
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

        return transactionService.transfer(dto)
                .orElseThrow();
    }

    private TransactionDto getTransaction(final Request request, final Response response) {
        return transactionService.find(request.params("id"))
                .orElseThrow();
    }

    private List<TransactionDto> getTransactionForWallet(final Request request, final Response response) {
        return transactionService.findByWallet(request.params("walletId"));
    }

    private List<TransactionDto> getTransactionForAccount(final Request request, final Response response) {
        return transactionService.findByAccount(request.params("accountId"));
    }

}
