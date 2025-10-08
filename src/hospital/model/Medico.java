package hospital.model;

import java.util.ArrayList;
import java.util.List;

public class Medico extends Pessoa{

    private String crm;
    private String especialidade;
    private double custoConsulta;
    private final List<String> agendaHorarios;

    public Medico(String nome, String cpf, int idade, String crm, String especialidade, double custoConsulta) {
        super(nome, cpf, idade);
        this.crm = crm;
        this.especialidade = especialidade;
        this.custoConsulta = custoConsulta;
        this.agendaHorarios = new ArrayList<>();
    }

    public String crm() {
        return crm;
    }

    public String especialidade() {
        return especialidade;
    }

    public double custoConsulta() {
        return custoConsulta;
    }

    public void setCustoConsulta(double custoConsulta) {
        this.custoConsulta = custoConsulta;
    }

    public List<String> agendaHorarios() {
        return List.copyOf(agendaHorarios);
    }


    public boolean agendarHorario(String horario) {
        if (this.agendaHorarios.contains(horario)) {
            // O horário já está ocupado! (Regra de Negócio Encapsulada)
            return false;
        }
        this.agendaHorarios.add(horario);
        return true;
    }

    public void cancelarHorario(String horario) {
        this.agendaHorarios.remove(horario);
    }

    @Override
    public String exibirDetalhes() {
        return "Médico: " + nome() +
                " | CRM: " + crm +
                " | Especialidade: " + especialidade +
                " | Custo: R$" + String.format("%.2f", custoConsulta);
    }
}
