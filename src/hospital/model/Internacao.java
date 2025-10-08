package hospital.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

public class Internacao implements Serializable {
    private static final long serialVersionUID = 1L;
    private final Paciente paciente;
    private final Medico medicoResponsavel;
    private final LocalDateTime dataEntrada;
    private Optional<LocalDateTime> dataSaida; // Optional para indicar que a internação pode estar ativa
    private final String numeroQuarto;
    private final double custoDiario;
    private String status; // Ativa, Encerrada, Cancelada

    public Internacao(Paciente paciente, Medico medicoResponsavel, LocalDateTime dataEntrada, String numeroQuarto, double custoDiario) {
        this.paciente = paciente;
        this.medicoResponsavel = medicoResponsavel;
        this.dataEntrada = dataEntrada;
        this.numeroQuarto = numeroQuarto;
        this.custoDiario = custoDiario;
        this.dataSaida = Optional.empty(); // Começa sem data de saída (ativa)
        this.status = "ATIVA";
    }

    public Medico medicoResponsavel() {
        return medicoResponsavel;
    }

    public Paciente paciente() {
        return paciente;
    }

    public String numeroQuarto() {
        return numeroQuarto;
    }

    public LocalDateTime dataEntrada() {
        return dataEntrada;
    }

    public String status() {
        return status;
    }

    /**
     * Calcula o custo total da internação com base na duração.
     */
    public double calcularCustoTotal() {
        if (status.equals("CANCELADA")) {
            return 0.0; // Internação cancelada não gera custo (ou geraria uma taxa administrativa, simplificando para 0)
        }

        LocalDateTime fim = dataSaida.orElse(LocalDateTime.now());
        long dias = ChronoUnit.DAYS.between(dataEntrada.toLocalDate(), fim.toLocalDate());
        long diasEfetivos = Math.max(1, dias);
        double custoBase = diasEfetivos * custoDiario;

        // Regra de Paciente Especial: internação de menos de uma semana (7 dias) gratuita
        if (paciente instanceof PacienteEspecial && diasEfetivos < 7) {
            System.out.println("[VANTAGEM] Internação inferior a 7 dias gratuita para Plano Especial.");
            return 0.0;
        }

        return custoBase;
    }

    /**
     * Encerra a internação, registrando a data de saída.
     */
    public void encerrar(LocalDateTime dataSaida) {
        if (status.equals("ATIVA")) {
            this.dataSaida = Optional.of(dataSaida);
            this.status = "ENCERRADA";
            System.out.println("[ALERTA] Internação do paciente " + paciente.nome() + " encerrada em " + dataSaida.toString());

            // Adiciona ao histórico do paciente
            String detalhes = "Internação no Quarto " + numeroQuarto + " de " + dataEntrada.toString() + " até " + dataSaida.toString() + ". Custo Total: R$" + String.format("%.2f", calcularCustoTotal());
            paciente.adicionarInternacaoAoHistorico(detalhes);
        }
    }

    /**
     * Cancela a internação.
     */
    public void cancelar() {
        if (status.equals("ATIVA")) {
            this.status = "CANCELADA";
            this.dataSaida = Optional.of(LocalDateTime.now()); // Registro de cancelamento
            System.out.println("[ALERTA] Internação do paciente " + paciente.nome() + " no quarto " + numeroQuarto + " CANCELADA.");
        }
    }

    public long calcularTempoInternacaoDias() {
        LocalDateTime fim = dataSaida.orElse(LocalDateTime.now());
        return ChronoUnit.DAYS.between(dataEntrada.toLocalDate(), fim.toLocalDate());
    }

    public String exibirDetalhes() {
        String saida = dataSaida.map(d -> " até " + d.toString()).orElse(" (Ativa)");
        return "Quarto: " + numeroQuarto +
                " | Paciente: " + paciente.nome() +
                " | Médico: " + medicoResponsavel.nome() +
                " | Entrada: " + dataEntrada.toString() +
                saida +
                " | Status: " + status;
    }
}
