package hospital.service;

import hospital.model.Consulta;
import hospital.model.Internacao;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class InternacaoService {
    private static List<Internacao> internacoes = new ArrayList<>();

    /**
     * Verifica se um quarto está ocupado por uma internação ATIVA.
     */
    public static boolean quartoEstaOcupado(String numeroQuarto) {
        return internacoes.stream()
                .filter(i -> i.status().equals("ATIVA")) // Filtra apenas internações ativas
                .anyMatch(i -> i.numeroQuarto().equalsIgnoreCase(numeroQuarto)); // Verifica se o quarto coincide
    }

    public static Optional<Internacao> buscarInternacaoAtivaPorQuarto(String numeroQuarto) {
        return internacoes.stream()
                .filter(i -> i.status().equals("ATIVA") && i.numeroQuarto().equalsIgnoreCase(numeroQuarto))
                .findFirst();
    }


    public static void internarPaciente(Internacao novaInternacao) {
        if (quartoEstaOcupado(novaInternacao.numeroQuarto())) {
            System.out.println("[ERRO FATAL] O quarto " + novaInternacao.numeroQuarto() + " já está ocupado. Internação abortada.");
            return;
        }

        internacoes.add(novaInternacao);
        System.out.println("[SUCESSO] Internação iniciada para " + novaInternacao.paciente().nome() + " no Quarto " + novaInternacao.numeroQuarto() + ".");
    }

    public static List<Internacao> listarAtivas() {
        return internacoes.stream()
                .filter(i -> i.status().equals("ATIVA"))
                .collect(Collectors.toList());
    }

    public static void setAll(List<Internacao> listaCarregada) {
        if (listaCarregada != null) {
            internacoes = listaCarregada;
        }
    }

    public static List<Internacao> listarTodas() {
        return new ArrayList<>(internacoes);
    }
}
