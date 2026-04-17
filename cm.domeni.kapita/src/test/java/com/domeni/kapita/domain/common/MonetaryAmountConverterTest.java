package com.domeni.kapita.domain.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import javax.money.MonetaryAmount;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;

class MonetaryAmountConverterTest {

  private final MonetaryAmountConverter converter = new MonetaryAmountConverter();

  @Test
  void convertToDatabaseColumnShouldSerializeCurrencyAndValueTest() {
    String result = converter.convertToDatabaseColumn(Money.of(new BigDecimal("12500.00"), "XAF"));

    assertThat(result).isEqualTo("XAF;12500");
  }

  @Test
  void convertToEntityAttributeShouldDeserializeCurrencyAndValueTest() {
    MonetaryAmount result = converter.convertToEntityAttribute("EUR;99.95");

    assertThat(result.getCurrency().getCurrencyCode()).isEqualTo("EUR");
    assertThat(result.getNumber().numberValue(BigDecimal.class)).isEqualByComparingTo("99.95");
  }

  @Test
  void convertToEntityAttributeShouldRejectInvalidPayloadTest() {
    assertThatThrownBy(() -> converter.convertToEntityAttribute("invalid-value"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("invalid monetary amount value: invalid-value");
  }
}
