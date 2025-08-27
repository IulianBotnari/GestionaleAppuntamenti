package com.gestionale;





/**
 * Classe che descrive una giornata lavorativa, composta da un 
 * {@code id}, id numerico che identifica ogni blocco/istanza
 * {@code ora} che si riferisce a un blocco della durata di 5 minuti, 
 * {@code occupato} descrive lo stato del, blocco 1 se occupato, 0 se libero
 */
public class Disponibilita {


    private int id;
    private String data;
    private int idBloccoOra;
    private String ora;
    private int occupato;

    public Disponibilita(){

    }


    public Disponibilita(int id, String data, int idBloccoOra, String ora, int occupato){
        this.id = id;
        this.data = data;
        this.idBloccoOra = idBloccoOra;
        this.ora = ora;
        this.occupato = occupato;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOra() {
        return ora;
    }

    public void setOra(String ora) {
        this.ora = ora;
    }

    public int getOccupato() {
        return occupato;
    }

    public void setOccupato(int occupato) {
        if (occupato == 1 || occupato == 0){
            this.occupato = occupato;
        } else{
            System.err.println("Il metodo accetta in entrata un valore di 1 oopure 0");
        }

        
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getIdBloccoOra() {
        return idBloccoOra;
    }

    public void setIdBloccoOra(int idBloccoOra) {
        this.idBloccoOra = idBloccoOra;
    }



    
}
