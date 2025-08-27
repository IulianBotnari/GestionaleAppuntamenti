package com.gestionale;

import java.time.LocalDate;

public class TabellaNero {

    private int id;
    private LocalDate data;
    private String nome;
    private String cognome;
    private int laminazione;
    private int unghie;
    private String durata;
    private double prezzo;
    private boolean completato;

    public TabellaNero(){

    }

    public TabellaNero(int id, LocalDate data, String nome, String cognome, int laminazione, int unghie, String durata, double prezzo, boolean completato){
        this.id = id;
        this.data = data;
        this.nome = nome;
        this.cognome = cognome;
        this.laminazione = laminazione;
        this.unghie = unghie;
        this.durata = durata;
        this.prezzo = prezzo;
        this.completato = completato;


    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public int getLaminazione() {
        return laminazione;
    }

    public void setLaminazione(int laminazione) {
        this.laminazione = laminazione;
    }

    public int getUnghie() {
        return unghie;
    }

    public void setUnghie(int unghie) {
        this.unghie = unghie;
    }

    public String getDurata() {
        return durata;
    }

    public void setDurata(String durata) {
        this.durata = durata;
    }

    public double getPrezzo() {
        return prezzo;
    }

    public void setPrezzo(double prezzo) {
        this.prezzo = prezzo;
    }

    public boolean isCompletato() {
        return completato;
    }

    public void setCompletato(boolean completato) {
        this.completato = completato;
    }

}
