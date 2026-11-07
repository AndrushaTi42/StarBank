package com.star.bank.rules;

import com.star.bank.dto.RecommendationDto;
import com.star.bank.dto.UserStats;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class TopSavingRule implements RecommendationRuleSet {
    private final static UUID ID = UUID.fromString("59efc529-2fff-41af-baff-90ccd7402925");
    private final static String NAME = "Top Saving";
    private final static String TEXT = "Откройте свою собственную «Копилку» с нашим банком! «Копилка» — это уникальный банковский " +
            "инструмент, который поможет вам легко и удобно накапливать деньги на важные цели. Больше " +
            "никаких забытых чеков и потерянных квитанций — всё под контролем! Преимущества «Копилки»: " +
            "Наполнение средств на конкретные цели. Установите лимит и срок накопления, и банк будет " +
            "автоматически переводить определенную сумму на ваш счет. Прозрачность и контроль. " +
            "Отслеживайте свои доходы и расходы, контролируйте процесс накопления и корректируйте " +
            "стратегию при необходимости. Безопасность и надежность. Ваши средства находятся под " +
            "защитой банка, а доступ к ним возможен только через мобильное приложение или " +
            "интернет-банкинг. Начните использовать «Копилку» уже сегодня и станьте ближе к своим " +
            "финансовым целям!";

    @Override
    public Optional<RecommendationDto> check(UUID userId, UserStats stats) {
        boolean hasDebit = stats.debitCount() > 0;
        boolean depositCondition = (stats.debitDeposit() >= 50_000 || stats.savingDeposit() >= 50_000);
        boolean positiveBalance = stats.debitDeposit() > stats.debitWithdraw();
        if (hasDebit && depositCondition && positiveBalance) {
            return Optional.of(new RecommendationDto(ID, NAME, TEXT));
        }
        return Optional.empty();
    }
}
