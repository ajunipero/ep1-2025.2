package hospital.model.records;

import hospital.model.*;

import java.util.List;

public record DataModel(List<Paciente> pacientes,
                        List<Medico> medicos,
                        List<Consulta> consultas,
                        List<Internacao> internacoes,
                        List<PlanoSaude> planos) {
}
