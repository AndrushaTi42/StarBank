package com.star.bank.rules;

import com.star.bank.dto.RecommendationDto;
import com.star.bank.dto.UserStats;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class Invest500Rule implements RecommendationRuleSet {
    private static final UUID ID = UUID.fromString("147f6a0f-3b91-413b-ab99-87f081d60d5a");
    private static final String NAME = "Invest 500";
    private static final String TEXT = "Откройте свой путь к успеху с индивидуальным инвестиционным счетом (ИИС)" +
            " от нашего банка! Воспользуйтесь налоговыми льготами и начните инвестировать с умом." +
            " Пополните счет до конца года и получите выгоду в виде вычета на взнос в следующем налоговом периоде." +
            " Не упустите возможность разнообразить свой портфель, снизить риски и следить за актуальными рыночными" +
            " тенденциями. Откройте ИИС сегодня и станьте ближе к финансовой независимости!";

    @Override
    public Optional<RecommendationDto> check(UUID userId, UserStats stats) {
        boolean hasDebit = stats.debitCount() > 0;
        boolean lackOfInvest = stats.investCount() == 0;
        boolean savingDepositLevel = stats.savingDeposit() > 1000;
        if (hasDebit && lackOfInvest && savingDepositLevel) {
            return Optional.of(new RecommendationDto(ID, NAME, TEXT));
        }
        return Optional.empty();
    }
}
