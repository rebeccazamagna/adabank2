package com.bancoeconomico;

import com.bancoeconomico.model.*;
import com.bancoeconomico.service.factory.OperacoesBancariasFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) throws IOException {
        processarClientes();
    }

    static Consumer<Cliente> deposito(BigDecimal valor) {
        return cliente -> {
            Conta conta = cliente.getContas().get(0);
            OperacoesBancariasFactory.getInstance().get(cliente)
                    .depositar(cliente, conta.getNumero(), valor);
            print("deposito: " + valor + " saldo " + conta.getSaldo());
        };
    }

    static void print(Object o) {
        System.out.println(o);
    }

    static void processarClientes() throws IOException {
        try (Stream<String> lines = Files.lines(Paths.get("C:\\arquivo\\pessoas.csv"));
             PrintWriter writer = new PrintWriter(Files.newBufferedWriter(Paths.get("C:\\arquivo\\clientes_importados.csv")))) {

            writer.println("nome;documento;PF/PJ;número da conta;saldo em conta");

            lines.skip(1)
                    .map(line -> line.split(","))
                    .filter(values -> values.length == 4)
                    .map(Main::createAccount)
                    .filter(Objects::nonNull)
                    .forEach(data -> {
                        Cliente cliente = (Cliente) data[0];
                        Conta conta = (Conta) data[1];

                        deposito(BigDecimal.valueOf(50)).accept(cliente);

                        writer.printf("%s;%s;%s;%s;%s%n", cliente.getNome(), cliente.getDocumento(), cliente.getTipo() == 1 ? "PJ" : "PF",
                                conta.getNumero(), conta.getSaldo());
                    });

        }
    }

    private static Conta criarContaCorrente() {
        Conta conta = new Conta();
        conta.setSaldo(BigDecimal.valueOf(50));
        conta.setNumero(Math.abs(new Random().nextInt()));
        return conta;
    }

    private static Object[] createAccount(String[] values) {
        String nome = values[0];
        LocalDate nascimentoCriacao = LocalDate.parse(values[1], DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String documento = values[2];
        int tipo = Integer.parseInt(values[3]);

        Cliente cliente = criarCliente(nome, documento, tipo, nascimentoCriacao);
        if (cliente == null) {
            return null;
        }

        Conta conta = criarContaCorrente();
        cliente.addConta(conta);

        return new Object[]{cliente, conta};
    }

    private static Cliente criarCliente(String nome, String documento, int tipo, LocalDate nascimentoCriacao) {
        // Verificar se é pessoa jurídica ou física
        if (tipo == 1) {
            // Pessoa Jurídica
            return new ClientePJ(nome, documento);
        } else if (tipo == 2) {
            // Pessoa Física, verificar se é maior de 18 anos
            LocalDate today = LocalDate.now();
            int age = Period.between(nascimentoCriacao, today).getYears();
            if (age >= 18) {
                return new ClientePF(nome, documento);
            } else {
                return null;
            }
        } else {
            // Tipo não reconhecido
            return null;
        }
    }
}









