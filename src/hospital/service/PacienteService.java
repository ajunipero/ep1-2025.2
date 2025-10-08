package hospital.service;

import hospital.model.Paciente;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PacienteService {
    private static List<Paciente> pacientes = new ArrayList<>();

    public static void adicionarPaciente(Paciente paciente) {
        // Validação básica para evitar CPFs duplicados
        if (buscarPacientePorCpf(paciente.cpf()).isPresent()) {
            System.out.println("[ERRO] Já existe um paciente cadastrado com este CPF: " + paciente.cpf());
            return;
        }

        // Adiciona o paciente
        pacientes.add(paciente);
        System.out.println("[SUCESSO] Paciente " + paciente.nome() + " cadastrado!");
    }

    public static Optional<Paciente> buscarPacientePorCpf(String cpf) {
        for (Paciente p : pacientes) {
            if (p.cpf().equals(cpf)) {
                return Optional.of(p);
            }
        }
        // Retorna um Optional vazio se não encontrar
        return Optional.empty();
    }

    public static void setAll(List<Paciente> listaCarregada) {
        if (listaCarregada != null) {
            pacientes = listaCarregada;
        }
    }

    public static List<Paciente> listarTodos() {
        // Retorna uma CÓPIA da lista para proteger o encapsulamento interno
        return new ArrayList<>(pacientes);
    }
}
