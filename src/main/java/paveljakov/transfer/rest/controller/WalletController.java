package paveljakov.transfer.rest.controller;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import paveljakov.transfer.common.CommonConstants;
import paveljakov.transfer.dto.EntityIdResponseDto;
import paveljakov.transfer.dto.wallet.WalletCreateDto;
import paveljakov.transfer.dto.wallet.WalletDto;
import paveljakov.transfer.dto.wallet.WalletMonetaryAmountDto;
import paveljakov.transfer.repository.wallet.WalletRepository;
import paveljakov.transfer.rest.transform.JsonTransformer;
import spark.Request;
import spark.Response;
import spark.Spark;

@Singleton
class WalletController implements RestController {

    private final WalletRepository walletRepository;

    private final JsonTransformer jsonTransformer;

    @Inject
    public WalletController(final WalletRepository walletRepository, final JsonTransformer jsonTransformer) {
        this.walletRepository = walletRepository;
        this.jsonTransformer = jsonTransformer;
    }

    @Override
    public void configureRoutes() {
        Spark.get("/wallets/:id", this::getWallet, jsonTransformer);
        Spark.post("/wallets/:id", CommonConstants.JSON_TYPE, this::addFunds, jsonTransformer);
        Spark.get("/accounts/:accountId/wallets", this::getWalletsForAccount, jsonTransformer);
        Spark.put("/accounts/:accountId/wallets", CommonConstants.JSON_TYPE, this::insertWallet, jsonTransformer);
    }

    private WalletDto getWallet(final Request request, final Response response) {
        return walletRepository.find(request.params("id"))
                .orElseThrow();
    }

    private Object addFunds(final Request request, final Response response) {
        final WalletMonetaryAmountDto dto = jsonTransformer.deserialize(request.body(), WalletMonetaryAmountDto.class);

        walletRepository.addAmount(request.params("id"), dto);

        return null;
    }

    private List<WalletDto> getWalletsForAccount(final Request request, final Response response) {
        return walletRepository.findByAccount(request.params("accountId"));
    }

    private EntityIdResponseDto insertWallet(final Request request, final Response response) {
        final WalletCreateDto dto = jsonTransformer.deserialize(request.body(), WalletCreateDto.class);

        return walletRepository.insert(dto, request.params("accountId"))
                .orElseThrow(IllegalStateException::new);
    }

}
