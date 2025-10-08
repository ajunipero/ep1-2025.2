package hospital.service;

import hospital.model.records.DataModel;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PersistenciaDeDadosService {
    private static final String PACIENTES_FILE = "data_pacientes.ser";
    private static final String MEDICOS_FILE = "data_medicos.ser";
    private static final String CONSULTAS_FILE = "data_consultas.ser";
    private static final String INTERNACOES_FILE = "data_internacoes.ser";
    private static final String PLANOS_FILE = "data_planos.ser";

    private static <T> void save(List<T> data, String fileName) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(data);
            System.out.println("[PERSISTÊNCIA] Dados salvos em: " + fileName);
        } catch (IOException e) {
            System.err.println("[ERRO DE SALVAMENTO] Não foi possível salvar em " + fileName + ": " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked") // Ignora o aviso de casting inseguro
    private static <T> List<T> load(String fileName) {
        File file = new File(fileName);
        if (!file.exists() || file.length() == 0) {
            // Se o arquivo não existe ou está vazio, retorna uma lista nova e vazia
            return new ArrayList<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            // O casting é necessário aqui, por isso usamos @SuppressWarnings
            return (List<T>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("[ERRO DE CARREGAMENTO] Arquivo " + fileName + " corrompido ou incompatível. Reiniciando dados. " + e.getMessage());
            return new ArrayList<>(); // Retorna vazio em caso de erro
        }
    }

    public static void saveAllData() {
        save(PacienteService.listarTodos(), PACIENTES_FILE);
        save(MedicoService.listarTodos(), MEDICOS_FILE);
        save(ConsultaService.listarTodas(), CONSULTAS_FILE);
        save(InternacaoService.listarTodas(), INTERNACOES_FILE);
        save(PlanoSaudeService.listarTodos(), PLANOS_FILE);
    }

    public static DataModel loadAllData() {
        return new DataModel(
                load(PACIENTES_FILE),
                load(MEDICOS_FILE),
                load(CONSULTAS_FILE),
                load(INTERNACOES_FILE),
                load(PLANOS_FILE)
        );
    }
}
