package com.bancoeconomico.model;

public class ClientePF extends Cliente {

    private String cpf;

    public ClientePF(String nome, String cpf) {
        super(nome);
        this.tipo = 2;
        this.cpf = cpf;
    }

    public String getCpf() {
        return cpf;
    }

    @Override
    public String getId() {
        return getCpf();
    }
}
