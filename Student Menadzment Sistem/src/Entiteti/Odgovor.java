package Entiteti;


public class Odgovor {

    public int odgovorID;
    private String ime;
    private String prezime;
    private String pitanje;
    private String odgovor;

    public Odgovor() {
    }

    public Odgovor(String ime, String prezime, String pitanje, String odgovor) {
        this.ime = ime;
        this.prezime = prezime;
        this.pitanje = pitanje;
        this.odgovor = odgovor;
    }

    public int getOdgovorID() {
        return odgovorID;
    }

    public void setOdgovorID(int odgovorID) {
        this.odgovorID = odgovorID;
    }

    public String getIme() {
        return ime;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }

    public String getPrezime() {
        return prezime;
    }

    public void setPrezime(String prezime) {
        this.prezime = prezime;
    }

    public String getPitanje() {
        return pitanje;
    }

    public void setPitanje(String pitanje) {
        this.pitanje = pitanje;
    }

    public String getOdgovor() {
        return odgovor;
    }

    public void setOdgovor(String odgovor) {
        this.odgovor = odgovor;
    }
}
