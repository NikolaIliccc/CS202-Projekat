package Entiteti;

import exceptions.OdgovorException;
import exceptions.OpcijaException;
import exceptions.PrazanException;

public class Pitanja {

    private int IDPitanja;
    private String tekstPitanja;
    private String opcija1;
    private String opcija2;
    private String opcija3;
    private String odgovor;

    public Pitanja() {
    }

    public Pitanja(String tekstPitanja, String opcija1, String opcija2, String opcija3, String odgovor) throws OpcijaException, OdgovorException, PrazanException {
        this.tekstPitanja = tekstPitanja;
        if (!jeTekstIliBroj(opcija1)) {
            throw new OpcijaException("Opcije moraju biti tekst ili broj.");
        } else if (opcija1.isEmpty()) {
            throw new PrazanException("Opcije ne smeju biti prazne.");
        }
        this.opcija1 = opcija1;
        if (!jeTekstIliBroj(opcija2)) {
            throw new OpcijaException("Opcije moraju biti tekst ili broj.");
        } else if (opcija2.isEmpty()) {
            throw new PrazanException("Opcije ne smeju biti prazne.");
        }
        this.opcija2 = opcija2;
        if (!jeTekstIliBroj(opcija3)) {
            throw new OpcijaException("Opcije moraju biti tekst ili broj.");
        } else if (opcija3.isEmpty()) {
            throw new PrazanException("Opcije ne smeju biti prazne.");
        }
        this.opcija3 = opcija3;
        if (!jeJednaOdOpcija(odgovor)) {
            throw new OdgovorException("Odgovor mora biti jedan od tri ponuđena.");
        }
        this.odgovor = odgovor;
    }

    public int getIDPitanja() {
        return IDPitanja;
    }

    public void setIDPitanja(int IDPitanja) {
        this.IDPitanja = IDPitanja;
    }

    public String getTekstPitanja() {
        return tekstPitanja;
    }

    public void setTekstPitanja(String tekstPitanja) {
        this.tekstPitanja = tekstPitanja;
    }

    public String getOpcija1() {
        return opcija1;
    }

    public void setOpcija1(String opcija1) {
        this.opcija1 = opcija1;
    }

    public String getOpcija2() {
        return opcija2;
    }

    public void setOpcija2(String opcija2) {
        this.opcija2 = opcija2;
    }

    public String getOpcija3() {
        return opcija3;
    }

    public void setOpcija3(String opcija3) {
        this.opcija3 = opcija3;
    }

    public String getOdgovor() {
        return odgovor;
    }

    public void setOdgovor(String odgovor) {
        this.odgovor = odgovor;
    }

    private boolean jeTekstIliBroj(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return true;
        }
    }

    public boolean jeJednaOdOpcija(String odgovor) {
        return odgovor.equals(opcija1) || odgovor.equals(opcija2) || odgovor.equals(opcija3);
    }
}
