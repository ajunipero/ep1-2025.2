package hospital.model;

import java.util.ArrayList;
import java.util.List;

public class Paciente extends Pessoa{

    private List<String> historicoConsultas;
    private List<String> historicoIternacoes;

    public Paciente(String nome, String cpf, int idade) {
        super(nome, cpf, idade);
        this.historicoConsultas = new ArrayList<>();
        this.historicoIternacoes = new ArrayList<>();
    }

    public List<String> historicoConsultas() {
        return historicoConsultas;
    }

    public void adicionarConsultaAoHistorico(String consulta) {
        this.historicoConsultas.add(consulta);
    }

    public List<String> historicoIternacoes() {
        return historicoIternacoes;
    }

    public void adicionarInternacaoAoHistorico(String internacao) {
        this.historicoIternacoes.add(internacao);
    }

    @Override
    public String exibirDetalhes() {
        return "Paciente Comum: " + nome() + "(CPF: "+cpf()+",Idade: "+idade()+")";
    }
    public double vantagemDescontoConsulta() {
        // Paciente comum não tem desconto (retorna 0)
        return 0.0;
    }

    public double vantagemDescontoConsulta(String especialidade) {
        // Paciente comum não tem desconto (retorna 0)
        return 0.0;
    }
}
