package hospital.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Consulta implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final Paciente paciente; // Relacionamento com Paciente
    private final Medico medico;     // Relacionamento com Médico
    private final LocalDateTime dataHora;
    private final String local;
    private String status; // Agendada, Concluída, Cancelada
    private double valorFinal;

    public Consulta(Paciente paciente, Medico medico, LocalDateTime dataHora, String local) {
        this.paciente = paciente;
        this.medico = medico;
        this.dataHora = dataHora;
        this.local = local;
        this.status = "AGENDADA";
        // O valorFinal é calculado no construtor
        this.valorFinal = calcularValorFinal();
    }

    // --- Getters ---

    public Paciente paciente() {
        return paciente;
    }

    public Medico medico() {
        return medico;
    }

    public LocalDateTime dataHora() {
        return dataHora;
    }

    public String dataHoraFormatada() {
        return dataHora.format(FORMATTER);
    }

    public String local() {
        return local;
    }

    public String status() {
        return status;
    }

    public double valorFinal() {
        return valorFinal;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private double calcularValorFinal() {
        double custoBase = medico.custoConsulta();
        double descontoTotal = paciente.vantagemDescontoConsulta(medico.especialidade());
        double valorDescontado = custoBase * descontoTotal;

        return Math.max(0, custoBase - valorDescontado);
    }

    public String exibirDetalhes() {
        return "Data: " + dataHoraFormatada() +
                " | Médico: " + medico.nome() +
                " | Especialidade: " + medico.especialidade() +
                " | Paciente: " + paciente.nome() +
                " | Local: " + local +
                " | Status: " + status +
                " | Valor Final: R$" + String.format("%.2f", valorFinal);
    }
}
