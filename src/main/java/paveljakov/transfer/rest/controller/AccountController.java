package paveljakov.transfer.rest.controller;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import paveljakov.transfer.dto.EntityIdResponseDto;
import paveljakov.transfer.dto.account.AccountDto;
import paveljakov.transfer.dto.account.CreateAccountDto;
import paveljakov.transfer.repository.account.AccountRepository;
import paveljakov.transfer.rest.transform.JsonTransformer;
import spark.Request;
import spark.Response;
import spark.Spark;

@Singleton
class AccountController implements RestController {

    private final AccountRepository accountRepository;

    private final JsonTransformer jsonTransformer;

    @Inject
    public AccountController(final AccountRepository accountRepository, final JsonTransformer jsonTransformer) {
        this.accountRepository = accountRepository;
        this.jsonTransformer = jsonTransformer;
    }

    @Override
    public void configureRoutes() {
        Spark.get("/accounts", this::getAccounts, jsonTransformer);
        Spark.put("/accounts", this::insertAccount, jsonTransformer);
        Spark.get("/accounts/:id", this::getAccount, jsonTransformer);
    }

    private List<AccountDto> getAccounts(final Request request, final Response response) {
        return accountRepository.findAll();
    }

    private AccountDto getAccount(final Request request, final Response response) {
        return accountRepository.find(request.params("id"))
                .orElseThrow();
    }

    private EntityIdResponseDto insertAccount(final Request request, final Response response) {
        final CreateAccountDto dto = jsonTransformer.deserialize(request.body(), CreateAccountDto.class);

        return accountRepository.insert(dto)
                .orElseThrow(IllegalStateException::new);
    }

}
