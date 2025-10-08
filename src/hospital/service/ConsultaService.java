package hospital.service;

import hospital.model.Consulta;
import hospital.model.Medico;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ConsultaService {
    private static List<Consulta> consultas = new ArrayList<>();

    public static boolean verificarDisponibilidade(Medico medico, LocalDateTime dataHora, String local) {
        String horarioFormatado = dataHora.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

        for (Consulta c : consultas) {
            if (c.medico().equals(medico) && c.dataHora().equals(dataHora)) {
                System.out.println("[ERRO] O médico " + medico.nome() + " já tem uma consulta agendada nesta data/hora.");
                return false;
            }

            if (c.local().equalsIgnoreCase(local) && c.dataHora().equals(dataHora)) {
                System.out.println("[ERRO] O local '" + local + "' já está ocupado nesta data/hora.");
                return false;
            }
        }

        if (!medico.agendarHorario(horarioFormatado)) {
            System.out.println("[ERRO INTERNO] Agenda do médico já está bloqueada.");
            return false;
        }

        return true;
    }

    public static void agendarConsulta(Consulta consulta) {
        consultas.add(consulta);

        // Adiciona a consulta ao histórico do paciente
        String detalhes = consulta.dataHoraFormatada() + " com Dr(a). " + consulta.medico().nome()
                + " (Valor: R$" + String.format("%.2f", consulta.valorFinal()) + ")";
        consulta.paciente().adicionarConsultaAoHistorico(detalhes);

        System.out.println("\n[SUCESSO] Consulta agendada com sucesso!");
        System.out.println(consulta.exibirDetalhes());
    }

    public static void setAll(List<Consulta> listaCarregada) {
        if (listaCarregada != null) {
            consultas = listaCarregada;
        }
    }

    public static List<Consulta> listarTodas() {
        return new ArrayList<>(consultas);
    }
}
