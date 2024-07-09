package br.com.github.jordihofc.withdraw.account;

import br.com.github.jordihofc.withdraw.shared.lockmanager.DistributedLockServer;
import com.amazonaws.services.dynamodbv2.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Validated
public class WithdrawService {
    private final AccountRepository accountRepository;
    private final DistributedLockServer distributedLockServer;

    public WithdrawService(AccountRepository accountRepository, DistributedLockServer distributedLockServer) {
        this.accountRepository = accountRepository;
        this.distributedLockServer = distributedLockServer;
    }

    public void withdrawWithConcunrrencyControl(UUID accountId, @Valid @Positive BigDecimal amount) {
        Optional<LockItem> possibleLock = distributedLockServer.acquireLock(accountId.toString());

        if (possibleLock.isPresent()) {
            Account account = accountRepository.findById(accountId).orElseThrow(
                    () -> new IllegalArgumentException("Not Exist Account for accountId")
            );
            BigDecimal balance = account.getBalance();

            if (balance.compareTo(amount) < 0) {//if balance less than amount
                throw new IllegalStateException("there's not enough balance");
            }

            BigDecimal newBalance = balance.subtract(amount);
            account.setBalance(newBalance);

            accountRepository.save(account);
            distributedLockServer.releaseLock(possibleLock.get());
        }

    }

    public void withdraw(UUID accountId, @Valid @Positive BigDecimal amount) {
        Account account = accountRepository.findById(accountId).orElseThrow(
                () -> new IllegalArgumentException("Not Exist Account for accountId")
        );

        BigDecimal balance = account.getBalance();
        if (balance.compareTo(amount) < 0) {//if balance less than amount
            throw new IllegalStateException("there's not enough balance");
        }

        BigDecimal newBalance = balance.subtract(amount);
        account.setBalance(newBalance);

        accountRepository.save(account);
    }


}
