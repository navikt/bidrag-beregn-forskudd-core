package no.nav.bidrag.beregn.forskudd.core;

import static no.nav.bidrag.beregn.forskudd.core.enums.ResultatKode.AVSLAG;
import static no.nav.bidrag.beregn.forskudd.core.enums.ResultatKode.FORHOYET_FORSKUDD_100_PROSENT;
import static no.nav.bidrag.beregn.forskudd.core.enums.ResultatKode.ORDINAERT_FORSKUDD_75_PROSENT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import no.nav.bidrag.beregn.felles.bo.Avvik;
import no.nav.bidrag.beregn.felles.enums.AvvikType;
import no.nav.bidrag.beregn.forskudd.core.bo.BeregnForskuddResultat;
import no.nav.bidrag.beregn.forskudd.core.dto.BeregnForskuddGrunnlagCore;
import no.nav.bidrag.beregn.forskudd.core.periode.ForskuddPeriode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("ForskuddCore (dto test)")
public class ForskuddCoreTest {

  private static final String INNTEKT_REFERANSE_1 = "INNTEKT_REFERANSE_1";
  private static final String SIVILSTAND_REFERANSE_ENSLIG = "SIVILSTAND_REFERANSE_ENSLIG";
  private static final String BARN_REFERANSE_1 = "BARN_REFERANSE_1";
  private static final String SOKNADBARN_REFERANSE = "SOKNADBARN_REFERANSE";
  private static final String BOSTATUS_REFERANSE_MED_FORELDRE_1 = "BOSTATUS_REFERANSE_MED_FORELDRE_1";

  private ForskuddCore forskuddCore;

  @Mock
  private ForskuddPeriode forskuddPeriodeMock;

  private final BeregnForskuddGrunnlagCore beregnForskuddGrunnlagCore = TestUtil.byggForskuddGrunnlagCore();
  private final BeregnForskuddResultat beregnForskuddResultat = TestUtil.byggForskuddResultat();
  private final List<Avvik> avvikListe = TestUtil.byggAvvikListe();

  @BeforeEach
  void initMocksAndService() {
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
        () -> assertThat(beregnForskuddResultatCore.getBeregnetForskuddPeriodeListe()).isNotEmpty(),
        () -> assertThat(beregnForskuddResultatCore.getBeregnetForskuddPeriodeListe().size()).isEqualTo(3),

        () -> assertThat(beregnForskuddResultatCore.getBeregnetForskuddPeriodeListe().get(0).getPeriode().getDatoFom())
            .isEqualTo(LocalDate.parse("2017-01-01")),
        () -> assertThat(beregnForskuddResultatCore.getBeregnetForskuddPeriodeListe().get(0).getPeriode().getDatoTil())
            .isEqualTo(LocalDate.parse("2018-01-01")),
        () -> assertThat(beregnForskuddResultatCore.getBeregnetForskuddPeriodeListe().get(0).getResultat().getBelop())
            .isEqualTo(BigDecimal.valueOf(1600)),
        () -> assertThat(beregnForskuddResultatCore.getBeregnetForskuddPeriodeListe().get(0).getResultat().getKode())
            .isEqualTo(FORHOYET_FORSKUDD_100_PROSENT.toString()),
        () -> assertThat(beregnForskuddResultatCore.getBeregnetForskuddPeriodeListe().get(0).getResultat().getRegel())
            .isEqualTo("REGEL 1"),

        () -> assertThat(beregnForskuddResultatCore.getBeregnetForskuddPeriodeListe().get(0).getGrunnlagReferanseListe().get(0))
            .isEqualTo(INNTEKT_REFERANSE_1),
        () -> assertThat(beregnForskuddResultatCore.getBeregnetForskuddPeriodeListe().get(0).getGrunnlagReferanseListe().get(1))
            .isEqualTo(SIVILSTAND_REFERANSE_ENSLIG),
        () -> assertThat(beregnForskuddResultatCore.getBeregnetForskuddPeriodeListe().get(0).getGrunnlagReferanseListe().get(2))
            .isEqualTo(BARN_REFERANSE_1),
        () -> assertThat(beregnForskuddResultatCore.getBeregnetForskuddPeriodeListe().get(0).getGrunnlagReferanseListe().get(3))
            .isEqualTo(SOKNADBARN_REFERANSE),
        () -> assertThat(beregnForskuddResultatCore.getBeregnetForskuddPeriodeListe().get(0).getGrunnlagReferanseListe().get(4))
            .isEqualTo(BOSTATUS_REFERANSE_MED_FORELDRE_1),
//        () -> assertThat(beregnForskuddResultatCore.getResultatPeriodeListe().get(0).getResultatGrunnlag().getSjablonListe())
//            .isEqualTo(TestUtil.byggSjablonCoreListeFraSjablonListe(TestUtil.byggSjablonNavnVerdiListe())),

        () -> assertThat(beregnForskuddResultatCore.getBeregnetForskuddPeriodeListe().get(1).getPeriode().getDatoFom())
            .isEqualTo(LocalDate.parse("2018-01-01")),
        () -> assertThat(beregnForskuddResultatCore.getBeregnetForskuddPeriodeListe().get(1).getPeriode().getDatoTil())
            .isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(beregnForskuddResultatCore.getBeregnetForskuddPeriodeListe().get(1).getResultat().getBelop())
            .isEqualTo(BigDecimal.valueOf(1200)),
        () -> assertThat(beregnForskuddResultatCore.getBeregnetForskuddPeriodeListe().get(1).getResultat().getKode())
            .isEqualTo(ORDINAERT_FORSKUDD_75_PROSENT.toString()),
        () -> assertThat(beregnForskuddResultatCore.getBeregnetForskuddPeriodeListe().get(1).getResultat().getRegel())
            .isEqualTo("REGEL 2"),

        () -> assertThat(beregnForskuddResultatCore.getBeregnetForskuddPeriodeListe().get(2).getPeriode().getDatoFom())
            .isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(beregnForskuddResultatCore.getBeregnetForskuddPeriodeListe().get(2).getPeriode().getDatoTil())
            .isEqualTo(LocalDate.parse("2020-01-01")),
        () -> assertThat(beregnForskuddResultatCore.getBeregnetForskuddPeriodeListe().get(2).getResultat().getBelop())
            .isEqualTo(BigDecimal.valueOf(0)),
        () -> assertThat(beregnForskuddResultatCore.getBeregnetForskuddPeriodeListe().get(2).getResultat().getKode())
            .isEqualTo(AVSLAG.toString()),
        () -> assertThat(beregnForskuddResultatCore.getBeregnetForskuddPeriodeListe().get(2).getResultat().getRegel())
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
        () -> assertThat(beregnForskuddResultatCore.getAvvikListe().get(0).getAvvikType()).isEqualTo(AvvikType.DATO_FOM_ETTER_DATO_TIL.toString()),
        () -> assertThat(beregnForskuddResultatCore.getBeregnetForskuddPeriodeListe()).isEmpty()
    );
  }
}
