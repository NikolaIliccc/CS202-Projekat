package Entiteti;

import exceptions.AdresaException;
import exceptions.ImeException;
import exceptions.JMBGException;
import exceptions.PrezimeException;
import exceptions.TelefonException;

public class Student {

    private int id;
    private String ime;
    private String prezime;
    private String jmbg;
    private String adresa;
    private String telefon;

    public Student(String ime, String prezime, String jmbg, String adresa, String telefon) throws ImeException, PrezimeException, JMBGException, AdresaException, TelefonException {
        if (ime.matches(".*\\d.*") || ime.length() < 3) {
            throw new ImeException("Ime ne sme sadrzavati brojeve i ne sme biti krace od 3 karaktera");
        } else if (ime.length() > 15) {
            throw new ImeException("Ime ne sme biti duze od 15 karaktera.");
        }
        this.ime = ime;
        if (prezime.matches(".*\\d.*")|| prezime.length() < 3) {
            throw new PrezimeException("Prezime ne sme sadrzavati brojeve i ne sme biti krace od 3 karaktera");
        } else if (prezime.length() > 15) {
            throw new PrezimeException("Prezime ne sme biti duze od 15 karaktera.");
        }
        this.prezime = prezime;
        if (jmbg.length() > 13 || jmbg.length() < 13) {
            throw new JMBGException("JMBG mora sadrazati 13 karaktera.");
        }
        this.jmbg = jmbg;
        if (adresa.length() < 7 || !SadrziBrojeveiSlova(adresa) || adresa.length() > 20) {
            throw new AdresaException("Adresa mora sadržavati slova i brojeve, a dužina mora biti između 7 i 20 karaktera.");
        }
        this.adresa = adresa;
        if (!ispravanPozivni(telefon)) {
            throw new TelefonException("Broj telefona mora biti ispravan.");
        }
        this.telefon = telefon;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return ime;
    }

    public void setName(String ime) {
        this.ime = ime;
    }

    public String getSurname() {
        return prezime;
    }

    public void setSurname(String prezime) {
        this.prezime = prezime;
    }

    public String getJmbg() {
        return jmbg;
    }

    public void setJmbg(String jmbg) {
        this.jmbg = jmbg;
    }

    public String getAddress() {
        return adresa;
    }

    public void setAddress(String adresa) {
        this.adresa = adresa;
    }

    public String getMobile() {
        return telefon;
    }

    public void setMobile(String telefon) {
        this.telefon = telefon;
    }

    private static boolean SadrziBrojeveiSlova(String str) {
        return str.matches(".*[a-zA-Z].*") && str.matches(".*\\d.*");
    }

    private static boolean ispravanPozivni(String broj) {
        return broj.matches("(\\+381|0)(6\\d{8}|[1-9]\\d{8})");
    }

}
