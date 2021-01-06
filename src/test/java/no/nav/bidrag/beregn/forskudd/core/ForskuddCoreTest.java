package no.nav.bidrag.beregn.forskudd.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import no.nav.bidrag.beregn.felles.bo.Avvik;
import no.nav.bidrag.beregn.felles.enums.AvvikType;
import no.nav.bidrag.beregn.felles.enums.BostatusKode;
import no.nav.bidrag.beregn.felles.enums.InntektType;
import no.nav.bidrag.beregn.felles.enums.SivilstandKode;
import no.nav.bidrag.beregn.forskudd.core.bo.BeregnForskuddResultat;
import no.nav.bidrag.beregn.forskudd.core.dto.BeregnForskuddGrunnlagCore;
import no.nav.bidrag.beregn.forskudd.core.periode.ForskuddPeriode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@DisplayName("ForskuddCore (dto test)")
public class ForskuddCoreTest {

  private ForskuddCore forskuddCore;

  @Mock
  private ForskuddPeriode forskuddPeriodeMock;

  private final BeregnForskuddGrunnlagCore beregnForskuddGrunnlagCore = TestUtil.byggForskuddGrunnlagCore();
  private final BeregnForskuddResultat beregnForskuddResultat = TestUtil.byggForskuddResultat();
  private final List<Avvik> avvikListe = TestUtil.byggAvvikListe();

  @BeforeEach
  void initMocksAndService() {
    MockitoAnnotations.initMocks(this);
    forskuddCore = new ForskuddCoreImpl(forskuddPeriodeMock);
  }

  @Test
  @DisplayName("Skal beregne forskudd")
  void skalBeregneForskudd() {

    when(forskuddPeriodeMock.beregnPerioder(any())).thenReturn(beregnForskuddResultat);
    var beregnForskuddResultatCore = forskuddCore.beregnForskudd(beregnForskuddGrunnlagCore);

    assertAll(
        () -> assertThat(beregnForskuddResultatCore).isNotNull(),
        () -> assertThat(beregnForskuddResultatCore.getAvvikListe()).isEmpty(),
        () -> assertThat(beregnForskuddResultatCore.getResultatPeriodeListe()).isNotEmpty(),
        () -> assertThat(beregnForskuddResultatCore.getResultatPeriodeListe().size()).isEqualTo(3),

        () -> assertThat(beregnForskuddResultatCore.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getPeriodeDatoFra())
            .isEqualTo(LocalDate.parse("2017-01-01")),
        () -> assertThat(beregnForskuddResultatCore.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getPeriodeDatoTil())
            .isEqualTo(LocalDate.parse("2018-01-01")),
        () -> assertThat(beregnForskuddResultatCore.getResultatPeriodeListe().get(0).getResultatBeregning().getResultatBelop())
            .isEqualTo(BigDecimal.valueOf(1600)),
        () -> assertThat(beregnForskuddResultatCore.getResultatPeriodeListe().get(0).getResultatBeregning().getResultatKode())
            .isEqualTo("INNVILGET_100_PROSENT"),
        () -> assertThat(beregnForskuddResultatCore.getResultatPeriodeListe().get(0).getResultatBeregning().getResultatBeskrivelse())
            .isEqualTo("REGEL 1"),

        () -> assertThat(beregnForskuddResultatCore.getResultatPeriodeListe().get(0).getResultatGrunnlag().getBidragMottakerInntektListe().size())
            .isEqualTo(1),
        () -> assertThat(
            beregnForskuddResultatCore.getResultatPeriodeListe().get(0).getResultatGrunnlag().getBidragMottakerInntektListe().get(0).getInntektType())
            .isEqualTo(InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER.toString()),
        () -> assertThat(beregnForskuddResultatCore.getResultatPeriodeListe().get(0).getResultatGrunnlag().getBidragMottakerInntektListe().get(0)
            .getInntektBelop()).isEqualTo(BigDecimal.valueOf(500000)),
        () -> assertThat(beregnForskuddResultatCore.getResultatPeriodeListe().get(0).getResultatGrunnlag().getBidragMottakerSivilstandKode())
            .isEqualTo(SivilstandKode.ENSLIG.toString()),
        () -> assertThat(beregnForskuddResultatCore.getResultatPeriodeListe().get(0).getResultatGrunnlag().getAntallBarnIHusstand() == 2),
        () -> assertThat(beregnForskuddResultatCore.getResultatPeriodeListe().get(0).getResultatGrunnlag().getSoknadBarnAlder() == 10),
        () -> assertThat(beregnForskuddResultatCore.getResultatPeriodeListe().get(0).getResultatGrunnlag().getSoknadBarnBostatusKode())
            .isEqualTo(BostatusKode.MED_FORELDRE.toString()),
        () -> assertThat(beregnForskuddResultatCore.getResultatPeriodeListe().get(0).getResultatGrunnlag().getSjablonListe())
            .isEqualTo(TestUtil.byggSjablonCoreListeFraSjablonListe(TestUtil.byggSjablonNavnVerdiListe())),

        () -> assertThat(beregnForskuddResultatCore.getResultatPeriodeListe().get(1).getResultatDatoFraTil().getPeriodeDatoFra())
            .isEqualTo(LocalDate.parse("2018-01-01")),
        () -> assertThat(beregnForskuddResultatCore.getResultatPeriodeListe().get(1).getResultatDatoFraTil().getPeriodeDatoTil())
            .isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(beregnForskuddResultatCore.getResultatPeriodeListe().get(1).getResultatBeregning().getResultatBelop())
            .isEqualTo(BigDecimal.valueOf(1200)),
        () -> assertThat(beregnForskuddResultatCore.getResultatPeriodeListe().get(1).getResultatBeregning().getResultatKode())
            .isEqualTo("INNVILGET_75_PROSENT"),
        () -> assertThat(beregnForskuddResultatCore.getResultatPeriodeListe().get(1).getResultatBeregning().getResultatBeskrivelse())
            .isEqualTo("REGEL 2"),

        () -> assertThat(beregnForskuddResultatCore.getResultatPeriodeListe().get(2).getResultatDatoFraTil().getPeriodeDatoFra())
            .isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(beregnForskuddResultatCore.getResultatPeriodeListe().get(2).getResultatDatoFraTil().getPeriodeDatoTil())
            .isEqualTo(LocalDate.parse("2020-01-01")),
        () -> assertThat(beregnForskuddResultatCore.getResultatPeriodeListe().get(2).getResultatBeregning().getResultatBelop())
            .isEqualTo(BigDecimal.valueOf(0)),
        () -> assertThat(beregnForskuddResultatCore.getResultatPeriodeListe().get(2).getResultatBeregning().getResultatKode())
            .isEqualTo("AVSLAG"),
        () -> assertThat(beregnForskuddResultatCore.getResultatPeriodeListe().get(2).getResultatBeregning().getResultatBeskrivelse())
            .isEqualTo("REGEL 11")
    );
  }

  @Test
  @DisplayName("Skal ikke beregne forskudd ved avvik")
  void skalIkkeBeregneForskuddVedAvvik() {

    when(forskuddPeriodeMock.validerInput(any())).thenReturn(avvikListe);
    var beregnForskuddResultatCore = forskuddCore.beregnForskudd(beregnForskuddGrunnlagCore);

    assertAll(
        () -> assertThat(beregnForskuddResultatCore).isNotNull(),
        () -> assertThat(beregnForskuddResultatCore.getAvvikListe()).isNotEmpty(),
        () -> assertThat(beregnForskuddResultatCore.getAvvikListe()).hasSize(1),
        () -> assertThat(beregnForskuddResultatCore.getAvvikListe().get(0).getAvvikTekst()).isEqualTo("beregnDatoTil må være etter beregnDatoFra"),
        () -> assertThat(beregnForskuddResultatCore.getAvvikListe().get(0).getAvvikType()).isEqualTo(AvvikType.DATO_FRA_ETTER_DATO_TIL.toString()),
        () -> assertThat(beregnForskuddResultatCore.getResultatPeriodeListe()).isEmpty()
    );
  }
}
