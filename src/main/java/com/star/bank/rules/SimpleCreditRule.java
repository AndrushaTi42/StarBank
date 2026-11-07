package com.star.bank.rules;

import com.star.bank.dto.RecommendationDto;
import com.star.bank.dto.UserStats;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class SimpleCreditRule implements RecommendationRuleSet {
    private static final UUID ID = UUID.fromString("ab138afb-f3ba-4a93-b74f-0fcee86d447f");
    private static final String NAME = "Простой кредит";
    private static final String TEXT = "Откройте мир выгодных кредитов с нами! Ищете способ быстро и без лишних хлопот получить " +
            "нужную сумму? Тогда наш выгодный кредит — именно то, что вам нужно! Мы предлагаем низкие " +
            "процентные ставки, гибкие условия и индивидуальный подход к каждому клиенту. Почему " +
            "выбирают нас: Быстрое рассмотрение заявки. Мы ценим ваше время, поэтому процесс " +
            "рассмотрения заявки занимает всего несколько часов. Удобное оформление. Подать заявку на " +
            "кредит можно онлайн на нашем сайте или в мобильном приложении. Широкий выбор кредитных " +
            "продуктов. Мы предлагаем кредиты на различные цели: покупку недвижимости, автомобиля, " +
            "образование, лечение и многое другое. Не упустите возможность воспользоваться выгодными " +
            "условиями кредитования от нашей компании!";

    @Override
    public Optional<RecommendationDto> check(UUID userId, UserStats stats) {
        boolean lackOfCredit = stats.creditCount() == 0;
        boolean positiveBalance = stats.debitDeposit() > stats.debitWithdraw();
        boolean debitWithdrawLevel = stats.debitWithdraw() > 100_000;
        if (lackOfCredit && positiveBalance && debitWithdrawLevel) {
            return Optional.of(new RecommendationDto(ID, NAME, TEXT));
        }
        return Optional.empty();
    }
}
