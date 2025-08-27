package com.gestionale;

import java.time.LocalDate;

/**
 * Classe modello per rappresentare un appuntamento.
 * Contiene i dati relativi a un appuntamento.
 */
public class Appuntamento {
    private int id;
    private String nome;
    private String cognome;
    private String trattamento;
    private String oraInizio;
    private double durata;
    private LocalDate data;
    private double prezzo;
    private int fatturato;
    private int nero;
    private int completato;


    public Appuntamento(int id, String nome, String cognome, String trattamento, String oraInizio,
            double durata,
            LocalDate data, double prezzo, int fatturato, int nero, int completato) {
        this.id = id;
        this.nome = nome;
        this.cognome = cognome;
        this.trattamento = trattamento;
        this.oraInizio = oraInizio;
        this.durata = durata;
        this.data = data;
        this.prezzo = prezzo;
        this.fatturato = fatturato;
        this.nero = nero;
        this.completato  = completato;

    }

    public Appuntamento() {

    }
    public int getId(){
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getCognome() {
        return cognome;
    }

    public String getTrattamento() {
        return trattamento;
    }

    public double getDurata() {
        return durata;
    }

    public void setDurata(double durata) {
        this.durata = durata;
    }

    public String getOraInizio() {
        return oraInizio;
    }

    public LocalDate getData() {
        return data;
    }

    public int isCompletato() {
        return completato;
    }

    public void setId(int id){
        this.id = id;

    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public void setTrattamento(String trattamento) {
        this.trattamento = trattamento;
    }

    public void setOraInizio(String oraInizio) {
        this.oraInizio = oraInizio;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public void setCompletato(int completato) {
        this.completato = completato;
    }

    @Override
    public String toString() {
        return "Appuntamento [nome=" + nome + ", cognome=" + cognome + ", trattamento=" + trattamento + ", oraInizio="
                + oraInizio + ", minutoInizio=" + ", durata=" + durata + ", data=" + data
                + ", completato=" + completato + "]";
    }

    public double getPrezzo() {
        return prezzo;
    }

    public void setPrezzo(double prezzo) {
        this.prezzo = prezzo;
    }

    public int isFatturato() {
        return fatturato;
    }

    public void setFatturato(int fatturato) {
        this.fatturato = fatturato;
    }

    public int isNero() {
        return nero;
    }

    public void setNero(int nero) {
        this.nero = nero;
    }

}