package no.nav.bidrag.beregn.forskudd.core.beregning;

import static no.nav.bidrag.beregn.forskudd.core.beregning.grunnlag.BostedStatusKode.ENSLIG_ASYLANT;
import static no.nav.bidrag.beregn.forskudd.core.beregning.grunnlag.BostedStatusKode.MED_FORELDRE;
import static no.nav.bidrag.beregn.forskudd.core.beregning.grunnlag.SivilstandKode.ENSLIG;
import static no.nav.bidrag.beregn.forskudd.core.beregning.grunnlag.Sjablonverdi.FORSKUDDSSATS_100_PROSENT;
import static no.nav.bidrag.beregn.forskudd.core.beregning.grunnlag.Sjablonverdi.FORSKUDDSSATS_125_PROSENT;
import static no.nav.bidrag.beregn.forskudd.core.beregning.grunnlag.Sjablonverdi.FORSKUDDSSATS_200_PROSENT;
import static no.nav.bidrag.beregn.forskudd.core.beregning.grunnlag.Sjablonverdi.FORSKUDDSSATS_250_PROSENT;
import static no.nav.bidrag.beregn.forskudd.core.beregning.grunnlag.Sjablonverdi.FORSKUDDSSATS_50_PROSENT;
import static no.nav.bidrag.beregn.forskudd.core.beregning.grunnlag.Sjablonverdi.FORSKUDDSSATS_75_PROSENT;
import static no.nav.bidrag.beregn.forskudd.core.beregning.grunnlag.Sjablonverdi.INNTEKTSGRENSE_100_PROSENT;
import static no.nav.bidrag.beregn.forskudd.core.beregning.grunnlag.Sjablonverdi.INNTEKTSGRENSE_75_PROSENT_ENSLIG;
import static no.nav.bidrag.beregn.forskudd.core.beregning.grunnlag.Sjablonverdi.INNTEKTSGRENSE_75_PROSENT_GIFT;
import static no.nav.bidrag.beregn.forskudd.core.beregning.grunnlag.Sjablonverdi.INNTEKTSINTERVALL_FORSKUDD;
import static no.nav.bidrag.beregn.forskudd.core.beregning.grunnlag.Sjablonverdi.MULTIPLIKATOR_MAKS_INNTEKTSGRENSE;
import static no.nav.bidrag.beregn.forskudd.core.beregning.resultat.ResultatKode.AVSLAG;
import static no.nav.bidrag.beregn.forskudd.core.beregning.resultat.ResultatKode.INNVILGET_100_PROSENT;
import static no.nav.bidrag.beregn.forskudd.core.beregning.resultat.ResultatKode.INNVILGET_125_PROSENT;
import static no.nav.bidrag.beregn.forskudd.core.beregning.resultat.ResultatKode.INNVILGET_200_PROSENT;
import static no.nav.bidrag.beregn.forskudd.core.beregning.resultat.ResultatKode.INNVILGET_250_PROSENT;
import static no.nav.bidrag.beregn.forskudd.core.beregning.resultat.ResultatKode.INNVILGET_50_PROSENT;
import static no.nav.bidrag.beregn.forskudd.core.beregning.resultat.ResultatKode.INNVILGET_75_PROSENT;

import com.google.common.base.Preconditions;
import java.math.BigDecimal;
import no.nav.bidrag.beregn.forskudd.core.beregning.grunnlag.ForskuddBeregningGrunnlag;
import no.nav.bidrag.beregn.forskudd.core.beregning.grunnlag.SivilstandKode;
import no.nav.bidrag.beregn.forskudd.core.beregning.resultat.ForskuddBeregningResultat;
import no.nav.bidrag.beregn.forskudd.core.beregning.resultat.ResultatKode;

public class ForskuddBeregningImpl implements ForskuddBeregning {

  @Override
  public ForskuddBeregningResultat beregn(ForskuddBeregningGrunnlag grunnlag) {

    Preconditions.checkNotNull(grunnlag, "Grunnlag kan ikke være null");

    var maksInntektsgrense = FORSKUDDSSATS_100_PROSENT * MULTIPLIKATOR_MAKS_INNTEKTSGRENSE;
    var inntektsIntervallTotal = (grunnlag.getAntallBarnIHusstand() - 1) * INNTEKTSINTERVALL_FORSKUDD;
    if (inntektsIntervallTotal < 0) {
      inntektsIntervallTotal = 0;
    }
    ResultatKode resultatKode;
    String regel;

    // Søknadsbarn er over 18 år (REGEL 1)
    if (grunnlag.getSoknadBarnAlder().compareTo(18) >= 0) {
      resultatKode = AVSLAG;
      regel = "REGEL 1";

      // Søknadsbarn er enslig asylant (REGEL 2/3)
    } else if (grunnlag.getSoknadBarnBostedStatusKode().equals(ENSLIG_ASYLANT)) {
      resultatKode = (grunnlag.getSoknadBarnAlder().compareTo(11) >= 0) ? INNVILGET_250_PROSENT : INNVILGET_200_PROSENT;
      regel = (resultatKode.equals(INNVILGET_250_PROSENT) ? "REGEL 2" : "REGEL 3");

      // Søknadsbarn bor alene eller ikke med foreldre (REGEL 4/5)
    } else if (!(grunnlag.getSoknadBarnBostedStatusKode().equals(MED_FORELDRE))) {
      resultatKode = (grunnlag.getSoknadBarnAlder().compareTo(11) >= 0) ? INNVILGET_125_PROSENT : INNVILGET_100_PROSENT;
      regel = (resultatKode.equals(INNVILGET_125_PROSENT) ? "REGEL 4" : "REGEL 5");

      // Over maks inntektsgrense for forskudd (REGEL 6)
    } else if (!(erUnderInntektsGrense(maksInntektsgrense, grunnlag.getBidragMottakerInntekt()))) {
      resultatKode = AVSLAG;
      regel = "REGEL 6";

      // Under maks inntektsgrense for fullt forskudd (REGEL 7/8)
    } else if (erUnderInntektsGrense(INNTEKTSGRENSE_100_PROSENT, grunnlag.getBidragMottakerInntekt())) {
      resultatKode = (grunnlag.getSoknadBarnAlder().compareTo(11) >= 0) ? INNVILGET_125_PROSENT : INNVILGET_100_PROSENT;
      regel = (resultatKode.equals(INNVILGET_125_PROSENT) ? "REGEL 7" : "REGEL 8");

      // Resterende regler (gift/enslig) (REGEL 9/10/11/12/13/14/15/16)
    } else {
      resultatKode = (erUnderInntektsGrense(settInntektsgrense(grunnlag.getBidragMottakerSivilstandKode()) + inntektsIntervallTotal,
          grunnlag.getBidragMottakerInntekt())) ? INNVILGET_75_PROSENT : INNVILGET_50_PROSENT;
      if (grunnlag.getBidragMottakerSivilstandKode().equals(ENSLIG)) {
        if (grunnlag.getAntallBarnIHusstand().equals(1)) {
          regel = (resultatKode.equals(INNVILGET_75_PROSENT) ? "REGEL 9" : "REGEL 10");
        } else {
          regel = (resultatKode.equals(INNVILGET_75_PROSENT) ? "REGEL 11" : "REGEL 12");
        }
      } else {
        if (grunnlag.getAntallBarnIHusstand().equals(1)) {
          regel = (resultatKode.equals(INNVILGET_75_PROSENT) ? "REGEL 13" : "REGEL 14");
        } else {
          regel = (resultatKode.equals(INNVILGET_75_PROSENT) ? "REGEL 15" : "REGEL 16");
        }
      }
    }

    return new ForskuddBeregningResultat(beregnForskudd(resultatKode), resultatKode, regel);
  }

  private static BigDecimal beregnForskudd(ResultatKode resultatKode) {
    switch (resultatKode) {
      case INNVILGET_50_PROSENT:
        return BigDecimal.valueOf(FORSKUDDSSATS_50_PROSENT);
      case INNVILGET_75_PROSENT:
        return BigDecimal.valueOf(FORSKUDDSSATS_75_PROSENT);
      case INNVILGET_100_PROSENT:
        return BigDecimal.valueOf(FORSKUDDSSATS_100_PROSENT);
      case INNVILGET_125_PROSENT:
        return BigDecimal.valueOf(FORSKUDDSSATS_125_PROSENT);
      case INNVILGET_200_PROSENT:
        return BigDecimal.valueOf(FORSKUDDSSATS_200_PROSENT);
      case INNVILGET_250_PROSENT:
        return BigDecimal.valueOf(FORSKUDDSSATS_250_PROSENT);
      default:
        return BigDecimal.ZERO;
    }
  }

  private static boolean erUnderInntektsGrense(Integer inntektsgrense, BigDecimal inntekt) {
    return inntekt.compareTo(BigDecimal.valueOf(inntektsgrense)) < 1;
  }

  private static Integer settInntektsgrense(SivilstandKode sivilstandKode) {
    return sivilstandKode.equals(ENSLIG) ? INNTEKTSGRENSE_75_PROSENT_ENSLIG : INNTEKTSGRENSE_75_PROSENT_GIFT;
  }
}
