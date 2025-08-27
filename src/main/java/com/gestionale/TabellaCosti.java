package com.gestionale;

import java.time.LocalDate;

public class TabellaCosti {

    private int id;
    private String descrizione;
    private double importo;
    private LocalDate data;


    public TabellaCosti(int id, String descrizione, double importo, LocalDate data){
        this.id = id;
        this.descrizione = descrizione;
        this.importo = importo;
        this.data = data;
    }

    public TabellaCosti() {
        //TODO Auto-generated constructor stub
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public double getImporto() {
        return importo;
    }

    public void setImporto(double importo) {
        this.importo = importo;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }




    
}
