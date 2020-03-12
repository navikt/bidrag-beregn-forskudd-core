package no.nav.bidrag.beregn.forskudd.core.beregning;

import static no.nav.bidrag.beregn.forskudd.core.beregning.grunnlag.BostedStatusKode.ENSLIG_ASYLANT;
import static no.nav.bidrag.beregn.forskudd.core.beregning.grunnlag.BostedStatusKode.MED_FORELDRE;
import static no.nav.bidrag.beregn.forskudd.core.beregning.grunnlag.SivilstandKode.ENSLIG;
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

    var maksInntektsgrense = grunnlag.getForskuddssats100Prosent() * grunnlag.getMultiplikatorMaksInntektsgrense();
    var inntektsIntervallTotal = (grunnlag.getAntallBarnIHusstand() - 1) * grunnlag.getInntektsintervallForskudd();
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
    } else if (erUnderInntektsGrense(grunnlag.getInntektsgrense100ProsentForskudd(), grunnlag.getBidragMottakerInntekt())) {
      resultatKode = (grunnlag.getSoknadBarnAlder().compareTo(11) >= 0) ? INNVILGET_125_PROSENT : INNVILGET_100_PROSENT;
      regel = (resultatKode.equals(INNVILGET_125_PROSENT) ? "REGEL 7" : "REGEL 8");

      // Resterende regler (gift/enslig) (REGEL 9/10/11/12/13/14/15/16)
    } else {
      resultatKode = (erUnderInntektsGrense(
          settInntektsgrense(grunnlag.getBidragMottakerSivilstandKode(), grunnlag.getInntektsgrenseEnslig75ProsentForskudd(),
              grunnlag.getInntektsgrenseGift75ProsentForskudd()) + inntektsIntervallTotal,
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

    return new ForskuddBeregningResultat(beregnForskudd(resultatKode, grunnlag.getForskuddssats100Prosent()), resultatKode, regel);
  }

  private static BigDecimal beregnForskudd(ResultatKode resultatKode, Integer forskuddssats100Prosent) {
    switch (resultatKode) {
      case INNVILGET_50_PROSENT:
        return BigDecimal.valueOf(forskuddssats100Prosent * 0.5);
      case INNVILGET_75_PROSENT:
        return BigDecimal.valueOf(forskuddssats100Prosent * 0.75);
      case INNVILGET_100_PROSENT:
        return BigDecimal.valueOf(forskuddssats100Prosent);
      case INNVILGET_125_PROSENT:
        return BigDecimal.valueOf(forskuddssats100Prosent * 1.25);
      case INNVILGET_200_PROSENT:
        return BigDecimal.valueOf(forskuddssats100Prosent * 2);
      case INNVILGET_250_PROSENT:
        return BigDecimal.valueOf(forskuddssats100Prosent * 2.5);
      default:
        return BigDecimal.ZERO;
    }
  }

  private static boolean erUnderInntektsGrense(Integer inntektsgrense, BigDecimal inntekt) {
    return inntekt.compareTo(BigDecimal.valueOf(inntektsgrense)) < 1;
  }

  private static Integer settInntektsgrense(SivilstandKode sivilstandKode, Integer inntektsgrenseEnslig75Prosent,
      Integer inntektsgrenseGift75Prosent) {
    return sivilstandKode.equals(ENSLIG) ? inntektsgrenseEnslig75Prosent : inntektsgrenseGift75Prosent;
  }
}
