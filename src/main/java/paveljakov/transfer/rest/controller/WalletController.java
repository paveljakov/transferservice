package paveljakov.transfer.rest.controller;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import paveljakov.transfer.dto.EntityIdResponseDto;
import paveljakov.transfer.dto.wallet.CreateWalletDto;
import paveljakov.transfer.dto.wallet.WalletDto;
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
        service.get("/wallets/:id", this::getWallet, jsonTransformer);
        service.get("/accounts/:accountId/wallets", this::getWalletsForUser, jsonTransformer);
        service.put("/accounts/:accountId/wallets", this::insertWallet, jsonTransformer);
    }

    private WalletDto getWallet(final Request request, final Response response) {
        return walletRepository.find(request.params("id"))
                .orElseThrow();
    }

    private List<WalletDto> getWalletsForUser(final Request request, final Response response) {
        return walletRepository.findByAccount(request.params("accountId"));
    }

    private EntityIdResponseDto insertWallet(final Request request, final Response response) {
        final CreateWalletDto dto = jsonTransformer.deserialize(request.body(), CreateWalletDto.class);

        return walletRepository.insert(dto, request.params("accountId"))
                .orElseThrow(IllegalStateException::new);
    }

}
