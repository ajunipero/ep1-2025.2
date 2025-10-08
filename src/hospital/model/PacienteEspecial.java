package hospital.model;

public class PacienteEspecial extends Paciente{

    private PlanoSaude planoSaude;

    public PacienteEspecial(String nome, String cpf, int idade, PlanoSaude planoDeSaude) {
        super(nome, cpf, idade);
        this.planoSaude = planoDeSaude;
    }

    public PlanoSaude planoDeSaude() {
        return planoSaude;

    }

    @Override
    public double vantagemDescontoConsulta(String especialidade) {
        double descontoPlano = planoSaude.getDescontoParaEspecialidade(especialidade);

        // 2. Desconto extra por idade (60+ anos)
        double descontoIdoso = (idade() >= 60) ? 0.15 : 0.0; // Novo valor de 15% para idosos

        // Retorna a soma dos descontos (Não vamos somar o idoso se o plano já der 100%)
        return descontoPlano + descontoIdoso;
    }

    @Override
    public double vantagemDescontoConsulta() {
        return 0.20;
    }

    @Override
    public String exibirDetalhes() {
        return "Paciente Especial: " + nome() + " (Plano: " + planoSaude.nome() + ", Idade: " + idade() + ")";

    }

    public double descontoExtraPorIdade() {
        if (idade() >= 60) {
            return 0.10;
        }
        return 0.0;
    }
}
