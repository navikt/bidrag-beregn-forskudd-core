package no.nav.bidrag.beregn.forskudd.core;

import static no.nav.bidrag.beregn.felles.enums.BostatusKode.MED_ANDRE_ENN_FORELDRE;
import static no.nav.bidrag.beregn.felles.enums.BostatusKode.MED_FORELDRE;
import static no.nav.bidrag.beregn.felles.enums.SivilstandKode.ENSLIG;
import static no.nav.bidrag.beregn.felles.enums.SivilstandKode.GIFT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import no.nav.bidrag.beregn.felles.bo.Periode;
import no.nav.bidrag.beregn.felles.enums.InntektType;
import no.nav.bidrag.beregn.felles.periode.Periodiserer;
import no.nav.bidrag.beregn.forskudd.core.bo.BarnIHusstandenPeriode;
import no.nav.bidrag.beregn.forskudd.core.bo.BostatusPeriode;
import no.nav.bidrag.beregn.forskudd.core.bo.InntektPeriode;
import no.nav.bidrag.beregn.forskudd.core.bo.SivilstandPeriode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("PeriodisererTest")
class PeriodisererTest {

  private static final String INNTEKT_REFERANSE_1 = "INNTEKT_REFERANSE_1";
  private static final String INNTEKT_REFERANSE_2 = "INNTEKT_REFERANSE_2";
  private static final String INNTEKT_REFERANSE_3 = "INNTEKT_REFERANSE_3";
  private static final String SIVILSTAND_REFERANSE_GIFT = "SIVILSTAND_REFERANSE_GIFT";
  private static final String SIVILSTAND_REFERANSE_ENSLIG = "SIVILSTAND_REFERANSE_ENSLIG";
  private static final String BARN_REFERANSE_1 = "BARN_REFERANSE_1";
  private static final String BARN_REFERANSE_2 = "BARN_REFERANSE_2";
  private static final String BARN_REFERANSE_3 = "BARN_REFERANSE_3";
  private static final String BOSTATUS_REFERANSE_MED_FORELDRE_1 = "BOSTATUS_REFERANSE_MED_FORELDRE_1";
  private static final String BOSTATUS_REFERANSE_MED_FORELDRE_2 = "BOSTATUS_REFERANSE_MED_FORELDRE_2";
  private static final String BOSTATUS_REFERANSE_MED_ANDRE_ENN_FORELDRE = "BOSTATUS_REFERANSE_MED_ANDRE_ENN_FORELDRE";

  @Test
  void testPeriodiseringMedGrunnlag() {
    List<Periode> perioder = new Periodiserer()
        .addBruddpunkter(new InntektPeriode(INNTEKT_REFERANSE_1, new Periode(LocalDate.parse("2019-01-01"), LocalDate.parse("2019-03-01")),
            InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER, BigDecimal.valueOf(1000)))
        .addBruddpunkter(new SivilstandPeriode(SIVILSTAND_REFERANSE_GIFT, new Periode(LocalDate.parse("2019-02-01"), LocalDate.parse("2019-04-01")),
            GIFT))
        .finnPerioder(LocalDate.parse("2000-01-01"), LocalDate.parse("2100-01-01"));

    assertAll(
        () -> assertThat(perioder).isNotNull(),
        () -> assertThat(perioder.size()).isEqualTo(3),

        () -> assertThat(perioder.get(0).getDatoFom()).isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(perioder.get(0).getDatoTil()).isEqualTo(LocalDate.parse("2019-02-01")),

        () -> assertThat(perioder.get(1).getDatoFom()).isEqualTo(LocalDate.parse("2019-02-01")),
        () -> assertThat(perioder.get(1).getDatoTil()).isEqualTo(LocalDate.parse("2019-03-01")),

        () -> assertThat(perioder.get(2).getDatoFom()).isEqualTo(LocalDate.parse("2019-03-01")),
        () -> assertThat(perioder.get(2).getDatoTil()).isEqualTo(LocalDate.parse("2019-04-01"))
    );
  }

  @Test
  void testDuplikatePerioder() {
    List<Periode> perioder = new Periodiserer()
        .addBruddpunkter(new InntektPeriode(INNTEKT_REFERANSE_1, new Periode(LocalDate.parse("2019-01-01"), LocalDate.parse("2019-04-01")),
            InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER, BigDecimal.valueOf(1000)))
        .addBruddpunkter(new SivilstandPeriode(SIVILSTAND_REFERANSE_GIFT, new Periode(LocalDate.parse("2019-02-01"), LocalDate.parse("2019-04-01")),
            GIFT))
        .finnPerioder(LocalDate.parse("2000-01-01"), LocalDate.parse("2100-01-01"));

    assertAll(
        () -> assertThat(perioder).isNotNull(),
        () -> assertThat(perioder.size()).isEqualTo(2),

        () -> assertThat(perioder.get(0).getDatoFom()).isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(perioder.get(0).getDatoTil()).isEqualTo(LocalDate.parse("2019-02-01")),

        () -> assertThat(perioder.get(1).getDatoFom()).isEqualTo(LocalDate.parse("2019-02-01")),
        () -> assertThat(perioder.get(1).getDatoTil()).isEqualTo(LocalDate.parse("2019-04-01"))
    );
  }

  @Test
  void testPeriodiseringMedGrunnlagOgAapenSlutt() {
    List<Periode> perioder = new Periodiserer()
        .addBruddpunkter(new SivilstandPeriode(SIVILSTAND_REFERANSE_GIFT, new Periode(LocalDate.parse("2019-02-01"), null), GIFT))
        .finnPerioder(LocalDate.parse("2000-01-01"), LocalDate.parse("2100-01-01"));

    assertAll(
        () -> assertThat(perioder).isNotNull(),
        () -> assertThat(perioder.size()).isEqualTo(1),

        () -> assertThat(perioder.get(0).getDatoFom()).isEqualTo(LocalDate.parse("2019-02-01")),
        () -> assertThat(perioder.get(0).getDatoTil()).isNull()
    );
  }

  @Test
  void testPeriodiseringMedUtvidetGrunnlag() {
    List<Periode> perioder = new Periodiserer()
        .addBruddpunkter(new InntektPeriode(INNTEKT_REFERANSE_1, new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2018-01-01")),
            InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER, BigDecimal.valueOf(250000)))
        .addBruddpunkter(new InntektPeriode(INNTEKT_REFERANSE_2, new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2019-01-01")),
            InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER, BigDecimal.valueOf(400000)))
        .addBruddpunkter(new InntektPeriode(INNTEKT_REFERANSE_3, new Periode(LocalDate.parse("2019-01-01"), null),
            InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER, BigDecimal.valueOf(500000)))
        .addBruddpunkter(new SivilstandPeriode(SIVILSTAND_REFERANSE_GIFT, new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2018-04-17")),
            GIFT))
        .addBruddpunkter(new SivilstandPeriode(SIVILSTAND_REFERANSE_ENSLIG, new Periode(LocalDate.parse("2018-04-17"), null), ENSLIG))
        .addBruddpunkter(new BarnIHusstandenPeriode(BARN_REFERANSE_1, new Periode(LocalDate.parse("2017-01-01"), null), 1d))
        .addBruddpunkter(new BarnIHusstandenPeriode(BARN_REFERANSE_2, new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2018-06-16")), 1d))
        .addBruddpunkter(new BarnIHusstandenPeriode(BARN_REFERANSE_3, new Periode(LocalDate.parse("2019-03-31"), null), 1d))
        .addBruddpunkter(new BostatusPeriode(BOSTATUS_REFERANSE_MED_FORELDRE_1,
            new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2018-08-16")), MED_FORELDRE))
        .addBruddpunkter(new BostatusPeriode(BOSTATUS_REFERANSE_MED_ANDRE_ENN_FORELDRE,
            new Periode(LocalDate.parse("2018-08-16"), LocalDate.parse("2018-11-13")), MED_ANDRE_ENN_FORELDRE))
        .addBruddpunkter(new BostatusPeriode(BOSTATUS_REFERANSE_MED_FORELDRE_2,
            new Periode(LocalDate.parse("2018-11-13"), null), MED_FORELDRE))
        .finnPerioder(LocalDate.parse("2000-01-01"), LocalDate.parse("2100-01-01"));

    assertAll(
        () -> assertThat(perioder).isNotNull(),
        () -> assertThat(perioder.size()).isEqualTo(8),

        () -> assertThat(perioder.get(0).getDatoFom()).isEqualTo(LocalDate.parse("2017-01-01")),
        () -> assertThat(perioder.get(0).getDatoTil()).isEqualTo(LocalDate.parse("2018-01-01")),

        () -> assertThat(perioder.get(1).getDatoFom()).isEqualTo(LocalDate.parse("2018-01-01")),
        () -> assertThat(perioder.get(1).getDatoTil()).isEqualTo(LocalDate.parse("2018-04-17")),

        () -> assertThat(perioder.get(2).getDatoFom()).isEqualTo(LocalDate.parse("2018-04-17")),
        () -> assertThat(perioder.get(2).getDatoTil()).isEqualTo(LocalDate.parse("2018-06-16")),

        () -> assertThat(perioder.get(3).getDatoFom()).isEqualTo(LocalDate.parse("2018-06-16")),
        () -> assertThat(perioder.get(3).getDatoTil()).isEqualTo(LocalDate.parse("2018-08-16")),

        () -> assertThat(perioder.get(4).getDatoFom()).isEqualTo(LocalDate.parse("2018-08-16")),
        () -> assertThat(perioder.get(4).getDatoTil()).isEqualTo(LocalDate.parse("2018-11-13")),

        () -> assertThat(perioder.get(5).getDatoFom()).isEqualTo(LocalDate.parse("2018-11-13")),
        () -> assertThat(perioder.get(5).getDatoTil()).isEqualTo(LocalDate.parse("2019-01-01")),

        () -> assertThat(perioder.get(6).getDatoFom()).isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(perioder.get(6).getDatoTil()).isEqualTo(LocalDate.parse("2019-03-31")),

        () -> assertThat(perioder.get(7).getDatoFom()).isEqualTo(LocalDate.parse("2019-03-31")),
        () -> assertThat(perioder.get(7).getDatoTil()).isNull()
    );
  }
}