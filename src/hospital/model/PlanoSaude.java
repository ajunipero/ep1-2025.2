package hospital.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class PlanoSaude implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String nome;
    private final Map<String, Double> descontosPorEspecialidade;

    public PlanoSaude(String nome) {
        this.nome = nome;
        this.descontosPorEspecialidade = new HashMap<>();
    }

    public String nome() {
        return nome;
    }

    /**
     * Tenta obter o desconto para uma dada especialidade.
     * @return O valor do desconto (0.0 a 1.0) ou 0.0 se não houver desconto específico.
     */
    public double getDescontoParaEspecialidade(String especialidade) {
        // Usa getOrDefault para retornar 0.0 se a especialidade não estiver mapeada
        return descontosPorEspecialidade.getOrDefault(especialidade.toUpperCase(), 0.0);
    }

    /**
     * Adiciona ou atualiza um desconto para uma especialidade.
     */
    public void adicionarDesconto(String especialidade, double desconto) {
        if (desconto >= 0.0 && desconto <= 1.0) {
            this.descontosPorEspecialidade.put(especialidade.toUpperCase(), desconto);
            System.out.println("[INFO] Desconto de " + (desconto * 100) + "% adicionado para " + especialidade);
        } else {
            System.out.println("[ERRO] Desconto deve ser entre 0.0 e 1.0.");
        }
    }
}
