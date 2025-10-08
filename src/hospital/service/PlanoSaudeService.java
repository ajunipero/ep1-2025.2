package hospital.service;

import hospital.model.Internacao;
import hospital.model.PlanoSaude;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PlanoSaudeService {
    private static List<PlanoSaude> planos = new ArrayList<>();

    public static void adicionarPlano(PlanoSaude plano) {
        planos.add(plano);
        System.out.println("[SUCESSO] Plano de Saúde '" + plano.nome() + "' cadastrado.");
    }

    public static Optional<PlanoSaude> buscarPlanoPorNome(String nome) {
        return planos.stream()
                .filter(p -> p.nome().equalsIgnoreCase(nome))
                .findFirst();
    }

    public static void setAll(List<PlanoSaude> listaCarregada) {
        if (listaCarregada != null) {
            planos = listaCarregada;
        }
    }

    public static List<PlanoSaude> listarTodos() {
        return new ArrayList<>(planos);
    }
}
