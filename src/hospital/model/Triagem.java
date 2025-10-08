package hospital.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Triagem implements Comparable<Triagem>, Serializable {
    private static final long serialVersionUID = 1L;
    private final Paciente paciente;
    private final int nivelPrioridade; // 1 (Emergência) a 5 (Baixa)
    private final LocalDateTime horaChegada; // Para desempate

    public Triagem(Paciente paciente, int nivelPrioridade, LocalDateTime horaChegada) {
        this.paciente = paciente;
        this.nivelPrioridade = nivelPrioridade;
        this.horaChegada = horaChegada;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public int getNivelPrioridade() {
        return nivelPrioridade;
    }

    // Método que a PriorityQueue usará para ordenar
    @Override
    public int compareTo(Triagem outraTriagem) {
        // Prioridade principal: Menor número = maior prioridade (ex: 1 é mais prioritário que 5)
        int comparacaoPrioridade = Integer.compare(this.nivelPrioridade, outraTriagem.nivelPrioridade);

        if (comparacaoPrioridade != 0) {
            return comparacaoPrioridade;
        }

        // Critério de desempate: Quem chegou primeiro tem prioridade (menor hora é "menor")
        return this.horaChegada.compareTo(outraTriagem.horaChegada);
    }

    @Override
    public String toString() {
        return "Paciente: " + paciente.nome() +
                " | Prioridade: " + nivelPrioridade +
                " | Chegada: " + horaChegada.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }
}
