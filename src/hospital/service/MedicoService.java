package hospital.service;

import hospital.model.Medico;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MedicoService {
    private static List<Medico> medicos = new ArrayList<>();

    public static void adicionarMedico(Medico medico) {
        // Validação de CRM duplicado
        if (buscarMedicoPorCrm(medico.crm()).isPresent()) {
            System.out.println("[ERRO] Já existe um médico cadastrado com este CRM: " + medico.crm());
            return;
        }

        medicos.add(medico);
        System.out.println("[SUCESSO] Médico(a) " + medico.nome() + " cadastrado!");
    }

    public static Optional<Medico> buscarMedicoPorCrm(String crm) {
        for (Medico m : medicos) {
            if (m.crm().equals(crm)) {
                return Optional.of(m);
            }
        }
        return Optional.empty();
    }

    public static void setAll(List<Medico> listaCarregada) {
        if (listaCarregada != null) {
            medicos = listaCarregada;
        }
    }

    public static List<Medico> listarTodos() {
        return new ArrayList<>(medicos); // Retorna cópia
    }
}
