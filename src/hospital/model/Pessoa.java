package hospital.model;

import java.io.Serializable;

// Classe abstrata para ecampsular qualquer tipo de agente no sistema, como Méido e Paciente, que são pessoas
public abstract class Pessoa implements Serializable {
    private static final long serialVersionUID = 1L;
    private String nome;
    private final String cpf;
    private int idade;

    public Pessoa(String nome, String cpf, int idade) {
        this.nome = nome;
        this.cpf = cpf;
        this.idade = idade;
    }

    public String nome() {
        return nome;
    }

    public void setNome(String nome) {
        if (nome != null && nome.length() > 2) {
            this.nome = nome;
        } else {
            System.out.println("[ALERTA] Nome inválido fornecido.");
        }
    }

    public String cpf() {
        return cpf;
    }

    public int idade() {
        return idade;
    }

    public void setIdade(int idade) {
        this.idade = idade;
    }

    public abstract String exibirDetalhes();

}