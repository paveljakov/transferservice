package paveljakov.transfer.rest.controller;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import paveljakov.transfer.dto.AccountDto;
import paveljakov.transfer.dto.WalletDto;
import paveljakov.transfer.repository.wallet.WalletRepository;
import paveljakov.transfer.rest.transform.JsonTransformer;
import spark.Request;
import spark.Response;
import spark.Service;

@Singleton
public class WalletController implements RestController {

    private final WalletRepository walletRepository;

    private final JsonTransformer jsonTransformer;

    @Inject
    public WalletController(final WalletRepository walletRepository, final JsonTransformer jsonTransformer) {
        this.walletRepository = walletRepository;
        this.jsonTransformer = jsonTransformer;
    }

    @Override
    public void configureRoutes(final Service service) {
        service.get("/wallet/:id", this::getWallet, jsonTransformer);
        service.get("/accounts/:accountId/wallets", this::getWalletsForUser, jsonTransformer);
    }

    private WalletDto getWallet(final Request request, final Response response) {
        return walletRepository.find(request.params("id"))
                .orElseThrow();
    }

    private List<WalletDto> getWalletsForUser(final Request request, final Response response) {
        return walletRepository.findByAccount(request.params("accountId"));
    }

}
