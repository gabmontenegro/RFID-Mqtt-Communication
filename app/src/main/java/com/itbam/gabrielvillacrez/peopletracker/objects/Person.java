package com.itbam.gabrielvillacrez.peopletracker.objects;

public class Person
{
    private String nome;

    public String getDataPassagem() {
        return dataPassagem;
    }

    public void setDataPassagem(String dataPassagem) {
        this.dataPassagem = dataPassagem;
    }

    private String dataPassagem;


    public Person(String nome, String data)
    {
        this.nome = nome;
        this.dataPassagem = data;
   }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

}
