package com.gestionale;



/**
 * classe trattamento, specifica i tipi di trattamento che una persona puo prenotare
 * 
 */
public class Trattamento {

    private String nome;
    private double durata;
    private double prezzo;

    public Trattamento(String nome, double durata, double prezzo) {
        this.nome = nome;
        this.durata = durata;
        this.prezzo = prezzo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public double getDurata() {
        return durata;
    }

    public void setDurata(double durata) {
        this.durata = durata;
    }

    public double getPrezzo() {
        return prezzo;
    }

    public void setPrezzo(double prezzo) {
        this.prezzo = prezzo;
    }

}
