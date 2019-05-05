package paveljakov.transfer.rest.controller;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import paveljakov.transfer.common.CommonConstants;
import paveljakov.transfer.dto.EntityIdResponseDto;
import paveljakov.transfer.dto.wallet.WalletCreateDto;
import paveljakov.transfer.dto.wallet.WalletDto;
import paveljakov.transfer.dto.wallet.WalletMonetaryAmountDto;
import paveljakov.transfer.rest.transform.JsonTransformer;
import paveljakov.transfer.service.wallet.WalletService;
import spark.Request;
import spark.Response;
import spark.Spark;

@Singleton
class WalletController implements RestController {

    private final WalletService walletService;

    private final JsonTransformer jsonTransformer;

    @Inject
    public WalletController(final WalletService walletService, final JsonTransformer jsonTransformer) {
        this.walletService = walletService;
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
        return walletService.find(request.params("id"))
                .orElseThrow();
    }

    private Object addFunds(final Request request, final Response response) {
        final WalletMonetaryAmountDto dto = jsonTransformer.deserialize(request.body(), WalletMonetaryAmountDto.class);

        walletService.addAmount(request.params("id"), dto);

        return null;
    }

    private List<WalletDto> getWalletsForAccount(final Request request, final Response response) {
        return walletService.findByAccount(request.params("accountId"));
    }

    private EntityIdResponseDto insertWallet(final Request request, final Response response) {
        final WalletCreateDto dto = jsonTransformer.deserialize(request.body(), WalletCreateDto.class);

        return walletService.insert(dto, request.params("accountId"))
                .orElseThrow(IllegalStateException::new);
    }

}
