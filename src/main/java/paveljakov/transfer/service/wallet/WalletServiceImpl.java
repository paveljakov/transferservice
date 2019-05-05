package paveljakov.transfer.service.wallet;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.lang3.StringUtils;
import org.codejargon.fluentjdbc.api.query.Query;

import paveljakov.transfer.dto.EntityIdResponseDto;
import paveljakov.transfer.dto.wallet.WalletCreateDto;
import paveljakov.transfer.dto.wallet.WalletDto;
import paveljakov.transfer.dto.wallet.WalletMonetaryAmountDto;
import paveljakov.transfer.dto.wallet.WalletUpdateDto;
import paveljakov.transfer.repository.WalletQueries;
import paveljakov.transfer.repository.wallet.WalletOperationException;
import paveljakov.transfer.repository.wallet.WalletRepository;

@Singleton
public class WalletServiceImpl implements WalletService {

    private final Query jdbc;

    private final WalletRepository walletRepository;

    @Inject
    public WalletServiceImpl(final Query jdbc, final WalletRepository walletRepository) {
        this.jdbc = jdbc;
        this.walletRepository = walletRepository;
    }

    @Override
    public Optional<WalletDto> find(final String id) {
        if (StringUtils.isBlank(id)) {
            throw new IllegalArgumentException("Parameter id is mandatory!");
        }

        return walletRepository.find(id);
    }

    @Override
    public List<WalletDto> findByAccount(final String accountId) {
        if (StringUtils.isBlank(accountId)) {
            throw new IllegalArgumentException("Parameter accountId is mandatory!");
        }

        return walletRepository.findByAccount(accountId);
    }

    @Override
    public Optional<EntityIdResponseDto> insert(final WalletCreateDto dto, final String accountId) {
        if (dto == null) {
            throw new IllegalArgumentException("Parameter dto is mandatory!");
        }
        if (StringUtils.isBlank(accountId)) {
            throw new IllegalArgumentException("Parameter accountId is mandatory!");
        }

        return walletRepository.insert(dto, accountId);
    }

    @Override
    public void addAmount(final String id, final WalletMonetaryAmountDto dto) {
        if (StringUtils.isBlank(id)) {
            throw new IllegalArgumentException("Parameter id is mandatory!");
        }

        validateMonetaryAmountDto(dto);

        jdbc.transaction().inNoResult(() -> {
            final WalletDto wallet = walletRepository.lock(id);

            final WalletUpdateDto updateDto = new WalletUpdateDto(
                    id,
                    wallet.getBalance().add(dto.getAmount()),
                    wallet.getBalanceAvailable().add(dto.getAmount())
            );

            update(updateDto);
        });
    }

    @Override
    public void authorizeAmount(final String id, final WalletMonetaryAmountDto dto) {
        if (StringUtils.isBlank(id)) {
            throw new IllegalArgumentException("Parameter id is mandatory!");
        }

        validateMonetaryAmountDto(dto);

        jdbc.transaction().inNoResult(() -> {
            final WalletDto wallet = walletRepository.lock(id);

            final WalletUpdateDto updateDto = new WalletUpdateDto(
                    id,
                    wallet.getBalance(),
                    wallet.getBalanceAvailable().subtract(dto.getAmount())
            );

            update(updateDto);
        });
    }

    @Override
    public void unauthorizeAmount(final String id, final WalletMonetaryAmountDto dto) {
        if (StringUtils.isBlank(id)) {
            throw new IllegalArgumentException("Parameter id is mandatory!");
        }

        validateMonetaryAmountDto(dto);

        jdbc.transaction().inNoResult(() -> {
            final WalletDto wallet = walletRepository.lock(id);

            final WalletUpdateDto updateDto = new WalletUpdateDto(
                    id,
                    wallet.getBalance(),
                    wallet.getBalanceAvailable().add(dto.getAmount())
            );

            update(updateDto);
        });
    }

    @Override
    public void captureAmount(final String id, final WalletMonetaryAmountDto dto) {
        if (StringUtils.isBlank(id)) {
            throw new IllegalArgumentException("Parameter id is mandatory!");
        }

        validateMonetaryAmountDto(dto);

        jdbc.transaction().inNoResult(() -> {
            final WalletDto wallet = walletRepository.lock(id);

            final WalletUpdateDto updateDto = new WalletUpdateDto(
                    id,
                    wallet.getBalance().subtract(dto.getAmount()),
                    wallet.getBalanceAvailable()
            );

            update(updateDto);
        });
    }

    private void update(final WalletUpdateDto dto) {
        validateWalletUpdateDto(dto);

        walletRepository.update(dto);
    }

    private void validateWalletUpdateDto(final WalletUpdateDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Parameter dto is mandatory!");
        }
        if (StringUtils.isBlank(dto.getId())) {
            throw new IllegalArgumentException("Parameter dto.id is mandatory!");
        }
        if (dto.getNewBalance().compareTo(BigDecimal.ZERO) < 0) {
            throw new WalletOperationException("Non-sufficient funds!");
        }
        if (dto.getNewBalanceAvailable().compareTo(BigDecimal.ZERO) < 0) {
            throw new WalletOperationException("Non-sufficient funds!");
        }
    }

    private void validateMonetaryAmountDto(final WalletMonetaryAmountDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Parameter dto is mandatory!");
        }
        if (dto.getAmount() == null) {
            throw new IllegalArgumentException("Parameter dto.amount is mandatory!");
        }
        if (dto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Parameter dto.amount must be positive!");
        }
    }

}
