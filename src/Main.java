import hospital.model.*;
import hospital.model.records.DataModel;
import hospital.service.*;

private static final PriorityQueue<Triagem> filaTriagem = new PriorityQueue<>();
public static final Scanner scanner = new Scanner(System.in);
private static final DateTimeFormatter FORMATTER_INPUT =
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

private static void exibirOpcoesMenu() {
    System.out.println("\n--- MENU PRINCIPAL ---");
    System.out.println("1. Cadastro de Pacientes");
    System.out.println("2. Cadastro de Médicos");
    System.out.println("3. Agendamento de Consultas");
    System.out.println("4. Gerenciar Internações");
    System.out.println("5. Relatórios e Estatísticas");
    System.out.println("6. Triagem e Fila de Prioridade");
    System.out.println("7. Configurar Planos de Saúde");
    System.out.println("0. Sair");
    System.out.println("----------------------");
}

private static void cadastrarPaciente() {
    System.out.println("\n--- CADASTRO DE PACIENTE ---");

    System.out.print("Nome: ");
    String nome = scanner.nextLine();

    System.out.print("CPF: ");
    String cpf = scanner.nextLine();

    System.out.print("Idade: ");
    int idade;
    try {
        idade = Integer.parseInt(scanner.nextLine().trim());
    } catch (NumberFormatException e) {
        System.out.println("[ERRO] Idade inválida. Cadastro cancelado.");
        return;
    }

    System.out.print("É paciente especial/Plano de Saúde? (S/N): ");
    String tipo = scanner.nextLine().toUpperCase();

    Paciente novoPaciente;

    if (tipo.equals("S")) {
        System.out.print("Nome do Plano de Saúde: ");
        String nomePlano = scanner.nextLine();

        // Busca o objeto PlanoSaude
        PlanoSaude plano = PlanoSaudeService.buscarPlanoPorNome(nomePlano).orElse(null);

        if (plano == null) {
            System.out.println("[ALERTA] Plano de Saúde '" + nomePlano + "' não encontrado. Cadastrando como Paciente Comum.");
            novoPaciente = new Paciente(nome, cpf, idade);
        } else {
            // Instancia PacienteEspecial com o OBJETO PlanoSaude
            novoPaciente = new PacienteEspecial(nome, cpf, idade, plano);
        }

    } else {
        // Instancia a classe base (Paciente)
        novoPaciente = new Paciente(nome, cpf, idade);
    }

    // Delega a responsabilidade de adicionar ao serviço (Encapsulamento de dados)
    PacienteService.adicionarPaciente(novoPaciente);
}

private static void cadastrarMedico() {
    System.out.println("\n--- CADASTRO DE MÉDICO ---");

    System.out.print("Nome: ");
    String nome = scanner.nextLine();

    System.out.print("CPF (Opcional, use como identificador único se necessário): ");
    String cpf = scanner.nextLine();

    System.out.print("Idade: ");
    int idade;
    try {
        idade = Integer.parseInt(scanner.nextLine().trim());
    } catch (NumberFormatException e) {
        System.out.println("[ERRO] Idade inválida. Cadastro cancelado.");
        return;
    }

    System.out.print("CRM: ");
    String crm = scanner.nextLine();

    System.out.print("Especialidade (Ex: Cardiologia, Pediatria): ");
    String especialidade = scanner.nextLine();

    System.out.print("Custo da Consulta (R$): ");
    double custo;
    try {
        custo = Double.parseDouble(scanner.nextLine().replace(',', '.').trim());
    } catch (NumberFormatException e) {
        System.out.println("[ERRO] Custo inválido. Usando R$0.00.");
        custo = 0.0;
    }

    Medico novoMedico = new Medico(nome, cpf, idade, crm, especialidade, custo);

    MedicoService.adicionarMedico(novoMedico);
}



private static void agendarConsulta() {
    System.out.println("\n--- AGENDAMENTO DE CONSULTA ---");

    System.out.print("CPF do Paciente: ");
    String cpfPaciente = scanner.nextLine();
    Paciente paciente = PacienteService.buscarPacientePorCpf(cpfPaciente).orElse(null);

    if (paciente == null) {
        System.out.println("[ERRO] Paciente não encontrado.");
        return;
    }

    System.out.println("--- Paciente: " + paciente.nome() + " ---");

    System.out.print("CRM do Médico: ");
    String crmMedico = scanner.nextLine();
    Medico medico = MedicoService.buscarMedicoPorCrm(crmMedico).orElse(null);

    if (medico == null) {
        System.out.println("[ERRO] Médico não encontrado.");
        return;
    }

    System.out.println("--- Médico: " + medico.nome() + " (" + medico.especialidade() + ") ---");

    System.out.print("Data e Hora (dd/MM/yyyy HH:mm): ");
    String strDataHora = scanner.nextLine();

    LocalDateTime dataHora;
    try {
        dataHora = LocalDateTime.parse(strDataHora, FORMATTER_INPUT);
        // Regra simples: não agendar no passado
        if (dataHora.isBefore(LocalDateTime.now())) {
            System.out.println("[ERRO] Não é possível agendar consultas no passado.");
            return;
        }
    } catch (java.time.format.DateTimeParseException e) {
        System.out.println("[ERRO] Formato de data/hora inválido. Use dd/MM/yyyy HH:mm.");
        return;
    }

    System.out.print("Local da Consulta (Ex: Consultório 5): ");
    String local = scanner.nextLine();

    // 1. Verifica disponibilidade global (Médico e Local)
    if (ConsultaService.verificarDisponibilidade(medico, dataHora, local)) {

        // 2. Cria o objeto Consulta (o cálculo de valor com Polimorfismo ocorre aqui)
        Consulta novaConsulta = new Consulta(paciente, medico, dataHora, local);

        // 3. Finaliza o agendamento
        ConsultaService.agendarConsulta(novaConsulta);
    }
}

private static void gerenciarInternacoes() {
    System.out.println("\n--- GERENCIAR INTERNAÇÕES ---");
    System.out.println("1. Iniciar Nova Internação");
    System.out.println("2. Encerrar Internação Ativa");
    System.out.println("3. Cancelar Internação Ativa");
    System.out.println("0. Voltar");
    System.out.print("Escolha uma opção: ");

    String input = scanner.nextLine();
    int subOpcao;
    try {
        subOpcao = Integer.parseInt(input.trim());
    } catch (NumberFormatException e) {
        System.out.println("\n[ERRO] Opção inválida.\n");
        return;
    }

    switch (subOpcao) {
        case 1:
            iniciarInternacao();
            break;
        case 2:
            encerrarInternacao();
            break;
        case 3:
            cancelarInternacao();
            break;
        case 0:
            // Volta para o menu principal
            break;
        default:
            System.out.println("\n[ERRO] Opção inválida.\n");
    }
}

private static void iniciarInternacao() {
    System.out.println("\n--- INICIAR NOVA INTERNAÇÃO ---");

    // 1. Coleta Paciente
    System.out.print("CPF do Paciente: ");
    String cpfPaciente = scanner.nextLine();
    Paciente paciente = PacienteService.buscarPacientePorCpf(cpfPaciente).orElse(null);
    if (paciente == null) {
        System.out.println("[ERRO] Paciente não encontrado.");
        return;
    }

    // 2. Coleta Médico
    System.out.print("CRM do Médico Responsável: ");
    String crmMedico = scanner.nextLine();
    Medico medico = MedicoService.buscarMedicoPorCrm(crmMedico).orElse(null);
    if (medico == null) {
        System.out.println("[ERRO] Médico não encontrado.");
        return;
    }

    // 3. Coleta Quarto
    System.out.print("Número do Quarto Desejado: ");
    String quarto = scanner.nextLine();

    // 4. Checagem de Encapsulamento de Recurso (Quarto)
    if (InternacaoService.quartoEstaOcupado(quarto)) {
        System.out.println("[ERRO] O Quarto " + quarto + " está ATUALMENTE OCUPADO. Escolha outro.");
        return;
    }

    // 5. Coleta Custo Diário (Simplificação, na prática seria variável)
    System.out.print("Custo diário do Quarto (R$): ");
    double custoDiario;
    try {
        custoDiario = Double.parseDouble(scanner.nextLine().replace(',', '.').trim());
    } catch (NumberFormatException e) {
        System.out.println("[ERRO] Custo inválido. Usando R$100.00.");
        custoDiario = 100.00;
    }

    // Cria a Internação e a registra
    Internacao novaInternacao = new Internacao(paciente, medico, LocalDateTime.now(), quarto, custoDiario);
    InternacaoService.internarPaciente(novaInternacao);
}

private static void encerrarInternacao() {
    System.out.println("\n--- ENCERRAR INTERNAÇÃO ATIVA ---");
    System.out.print("Número do Quarto a ser liberado: ");
    String quarto = scanner.nextLine();

    Optional<Internacao> internacaoOpt = InternacaoService.buscarInternacaoAtivaPorQuarto(quarto);

    if (internacaoOpt.isPresent()) {
        Internacao internacao = internacaoOpt.get();

        System.out.println("Encerrando internação do paciente " + internacao.paciente().nome() + ".");

        // Encerrar a internação (com Encapsulamento)
        internacao.encerrar(LocalDateTime.now());

        System.out.println("Custo total da internação: R$" + String.format("%.2f", internacao.calcularCustoTotal()));
    } else {
        System.out.println("[ERRO] Não há internação ATIVA no Quarto " + quarto + ".");
    }
}

private static void cancelarInternacao() {
    System.out.println("\n--- CANCELAR INTERNAÇÃO ATIVA ---");
    System.out.print("Número do Quarto para CANCELAMENTO: ");
    String quarto = scanner.nextLine();

    Optional<Internacao> internacaoOpt = InternacaoService.buscarInternacaoAtivaPorQuarto(quarto);

    if (internacaoOpt.isPresent()) {
        Internacao internacao = internacaoOpt.get();

        System.out.print("Tem certeza que deseja CANCELAR a internação do paciente " + internacao.paciente().nome() + "? (S/N): ");
        if (scanner.nextLine().equalsIgnoreCase("S")) {
            // Cancelar a internação (com Encapsulamento)
            internacao.cancelar();
            // A regra de ocupação do quarto é automaticamente atualizada, pois o status muda de "ATIVA" para "CANCELADA".
            System.out.println("[SUCESSO] Internação cancelada.");
        } else {
            System.out.println("Cancelamento abortado.");
        }
    } else {
        System.out.println("[ERRO] Não há internação ATIVA no Quarto " + quarto + ".");
    }
}

private static void configurarPlanosSaude() {
    System.out.println("\n--- CONFIGURAR PLANOS DE SAÚDE ---");
    System.out.println("1. Cadastrar Novo Plano");
    System.out.println("2. Adicionar Desconto por Especialidade");
    System.out.println("0. Voltar");
    System.out.print("Escolha uma opção: ");

    int subOpcao;
    try {
        subOpcao = Integer.parseInt(scanner.nextLine().trim());
    } catch (NumberFormatException e) {
        System.out.println("\n[ERRO] Opção inválida.\n");
        return;
    }

    switch (subOpcao) {
        case 1:
            cadastrarPlano();
            break;
        case 2:
            adicionarDescontoPlano();
            break;
    }
}

private static void cadastrarPlano() {
    System.out.print("Nome do Plano de Saúde: ");
    String nomePlano = scanner.nextLine();
    PlanoSaude novoPlano = new PlanoSaude(nomePlano);
    PlanoSaudeService.adicionarPlano(novoPlano);
}

private static void adicionarDescontoPlano() {
    System.out.print("Nome do Plano para configurar: ");
    String nomePlano = scanner.nextLine();

    PlanoSaude plano = PlanoSaudeService.buscarPlanoPorNome(nomePlano).orElse(null);
    if (plano == null) {
        System.out.println("[ERRO] Plano de Saúde não encontrado.");
        return;
    }

    System.out.print("Especialidade (Ex: CARDIOLOGIA): ");
    String especialidade = scanner.nextLine();

    System.out.print("Desconto (Ex: 0.30 para 30%): ");
    try {
        double desconto = Double.parseDouble(scanner.nextLine().replace(',', '.').trim());
        plano.adicionarDesconto(especialidade, desconto);
    } catch (NumberFormatException e) {
        System.out.println("[ERRO] Desconto inválido.");
    }
}

private static void relatorioPacientesComHistorico() {
    System.out.println("\n==============================================");
    System.out.println("--- RELATÓRIO: PACIENTES CADASTRADOS (TOTAL: " + PacienteService.listarTodos().size() + ") ---");
    System.out.println("==============================================");

    if (PacienteService.listarTodos().isEmpty()) {
        System.out.println("Nenhum paciente cadastrado.");
        return;
    }

    for (Paciente p : PacienteService.listarTodos()) {

        System.out.println("\n" + p.exibirDetalhes());
        System.out.println("CPF: " + p.cpf() + " | Idade: " + p.idade());

        // Histórico de Consultas
        System.out.println("  -> Histórico de Consultas:");
        if (p.historicoConsultas().isEmpty()) {
            System.out.println("     (Nenhuma consulta registrada.)");
        } else {
            // Acessamos o histórico através do getter (Encapsulamento)
            p.historicoConsultas().forEach(h -> System.out.println("     - " + h));
        }

        // Histórico de Internações
        System.out.println("  -> Histórico de Internações:");
        if (p.historicoIternacoes().isEmpty()) {
            System.out.println("     (Nenhuma internação registrada.)");
        } else {
            p.historicoIternacoes().forEach(h -> System.out.println("     - " + h));
        }
    }
    System.out.println("\n==============================================");
}

private static void relatorioMedicosComAgenda() {
    System.out.println("\n==============================================");
    System.out.println("--- RELATÓRIO: MÉDICOS CADASTRADOS (TOTAL: " + MedicoService.listarTodos().size() + ") ---");
    System.out.println("==============================================");

    if (MedicoService.listarTodos().isEmpty()) {
        System.out.println("Nenhum médico cadastrado.");
        return;
    }

    for (Medico m : MedicoService.listarTodos()) {
        System.out.println("\n" + m.exibirDetalhes());
        System.out.println("  -> Agenda de Horários Ocupados:");

        if (m.agendaHorarios().isEmpty()) {
            System.out.println("     (Nenhum horário agendado.)");
        } else {
            m.agendaHorarios().forEach(h -> System.out.println("     - " + h));
        }
    }
    System.out.println("\n==============================================");
}

private static void relatorioPacientesInternados() {
    System.out.println("\n==============================================");
    System.out.println("--- RELATÓRIO: PACIENTES ATUALMENTE INTERNADOS ---");
    System.out.println("==============================================");

    // Usa o serviço para obter a lista filtrada (Encapsulamento de filtro)
    List<Internacao> ativas = InternacaoService.listarAtivas();

    if (ativas.isEmpty()) {
        System.out.println("Nenhum paciente internado no momento.");
        return;
    }

    for (Internacao i : ativas) {
        long dias = i.calcularTempoInternacaoDias(); // Lógica encapsulada na classe Internacao

        System.out.println("\n" + i.exibirDetalhes());
        System.out.println("  -> Paciente: " + i.paciente().nome());
        System.out.println("  -> Médico Responsável: " + i.medicoResponsavel().nome());
        System.out.println("  -> Tempo de Internação: " + dias + " dia(s).");
    }
    System.out.println("\n==============================================");
}

private static void relatorioEstatisticasInternacao() {
    System.out.println("\n--- ESTATÍSTICAS AVANÇADAS DE INTERNAÇÃO ---");

    List<Internacao> todasInternacoes = InternacaoService.listarTodas();
    List<Internacao> ativas = InternacaoService.listarAtivas();

    if (todasInternacoes.isEmpty()) {
        System.out.println("[INFO] Nenhuma internação registrada.");
        return;
    }

    // 1. Tempo Médio de Internação (Apenas para internações encerradas)
    double tempoMedio = todasInternacoes.stream()
            .filter(i -> i.status().equals("ENCERRADA"))
            .mapToLong(Internacao::calcularTempoInternacaoDias)
            .average() // Calcula a média dos dias
            .orElse(0.0);

    System.out.println("-> Tempo Médio de Internação (Encerradas): " + String.format("%.1f", tempoMedio) + " dias");

    // 2. Taxa de Ocupação Atual (Considerando 20 quartos fixos para simplificação)
    final int TOTAL_QUARTOS = 20;
    double taxaOcupacao = (double) ativas.size() / TOTAL_QUARTOS;

    System.out.println("-> Taxa de Ocupação de Quartos: " + ativas.size() + "/" + TOTAL_QUARTOS +
            " (" + String.format("%.2f", taxaOcupacao * 100) + "%)");

    // 3. Taxa de Ocupação por Especialidade (Internações Ativas)
    System.out.println("\n-> Ocupação por Especialidade (Ativa):");
    Map<String, Long> ocupacaoPorEspecialidade = ativas.stream()
            // Agrupa pelo nome da especialidade do médico responsável
            .collect(Collectors.groupingBy(
                    i -> i.medicoResponsavel().especialidade().toUpperCase(),
                    Collectors.counting()
            ));

    ocupacaoPorEspecialidade.forEach((esp, count) -> {
        System.out.println("   - " + esp + ": " + count + " internação(ões).");
    });
}

private static void executarMenuRelatorios() {
    int opcao = -1;

    while (opcao != 0) {
        System.out.println("\n--- MENU RELATÓRIOS ---");
        System.out.println("1. Pacientes Cadastrados (com Histórico)");
        System.out.println("2. Médicos Cadastrados (com Agenda)");
        System.out.println("3. Consultas Futuras e Passadas (Filtros a implementar)");
        System.out.println("4. Pacientes Internados no Momento");
        System.out.println("5. Estatísticas Gerais");
        System.out.println("6. Estatísticas de Internação");
        System.out.println("0. Voltar ao Menu Principal");
        System.out.print("Escolha uma opção: ");

        try {
            String input = scanner.nextLine();
            opcao = Integer.parseInt(input.trim());
        } catch (NumberFormatException e) {
            System.out.println("\n[ERRO] Opção inválida! Digite um número.\n");
            continue;
        }

        switch (opcao) {
            case 1:
                relatorioPacientesComHistorico();
                break;
            case 2:
                relatorioMedicosComAgenda();
                break;
            case 3:
                relatorioConsultas();
                break;
            case 4:
                relatorioPacientesInternados();
                break;
            case 5:
                relatorioEstatisticas();
                break;
            case 6:
                relatorioEstatisticasInternacao();
                break;
            case 0:
                System.out.println("\nVoltando...");
                break;
            default:
                System.out.println("\n[ERRO] Opção inválida! Tente novamente.\n");
                break;
        }
    }
}

private static void filtrarConsultasPorEspecialidade(List<Consulta> todasConsultas) {
    System.out.print("Digite a Especialidade para filtrar: ");
    final String especialidadeFiltro = scanner.nextLine().toUpperCase();

    // Aplicação do Filtro com Streams (Encapsulamento de acesso)
    List<Consulta> filtradas = todasConsultas.stream()
            // Filtra pelo nome da especialidade do Médico, convertendo para maiúsculas
            .filter(c -> c.medico().especialidade().toUpperCase().contains(especialidadeFiltro))
            .collect(Collectors.toList());

    exibirConsultas(filtradas, "Consultas de " + especialidadeFiltro);
}

private static void filtrarConsultasPorMedico(List<Consulta> todasConsultas) {
    System.out.print("Digite o CRM do Médico: ");
    final String crmFiltro = scanner.nextLine();

    // Aplicação do Filtro com Streams
    List<Consulta> filtradas = todasConsultas.stream()
            // Filtra pelo CRM do Médico
            .filter(c -> c.medico().crm().equalsIgnoreCase(crmFiltro))
            .collect(Collectors.toList());

    // Busca o nome do médico para o título do relatório
    String nomeMedico = MedicoService.buscarMedicoPorCrm(crmFiltro)
            .map(Medico::nome)
            .orElse("CRM Desconhecido");

    exibirConsultas(filtradas, "Consultas com Dr(a). " + nomeMedico);
}

private static void filtrarConsultasPorPaciente(List<Consulta> todasConsultas) {
    System.out.print("Digite o CPF do Paciente: ");
    final String cpfFiltro = scanner.nextLine();

    // Aplicação do Filtro com Streams
    List<Consulta> filtradas = todasConsultas.stream()
            // Filtra pelo CPF do Paciente
            .filter(c -> c.paciente().cpf().equalsIgnoreCase(cpfFiltro))
            .collect(Collectors.toList());

    // Busca o nome do paciente para o título do relatório
    String nomePaciente = PacienteService.buscarPacientePorCpf(cpfFiltro)
            .map(Paciente::nome)
            .orElse("CPF Desconhecido");

    exibirConsultas(filtradas, "Consultas do Paciente " + nomePaciente);
}

private static void relatorioConsultas() {
    System.out.println("\n--- RELATÓRIO DE CONSULTAS ---");
    System.out.println("1. Listar Todas (Futuras e Passadas)");
    System.out.println("2. Filtrar por Especialidade");
    System.out.println("3. Filtrar por Médico");
    System.out.println("4. Filtrar por Paciente (CPF)");
    System.out.println("0. Voltar");
    System.out.print("Escolha uma opção: ");

    int subOpcao;
    try {
        subOpcao = Integer.parseInt(scanner.nextLine().trim());
    } catch (NumberFormatException e) {
        System.out.println("\n[ERRO] Opção inválida.\n");
        return;
    }

    // Pega todas as consultas do serviço (Encapsulamento de dados)
    List<Consulta> todasConsultas = ConsultaService.listarTodas();

    switch (subOpcao) {
        case 1:
            exibirConsultas(todasConsultas, "Todas as Consultas");
            break;
        case 2:
            filtrarConsultasPorEspecialidade(todasConsultas);
            break;
        case 3:
            filtrarConsultasPorMedico(todasConsultas);
            break;
        case 4:
            filtrarConsultasPorPaciente(todasConsultas);
            break;
        case 0:
            break;
        default:
            System.out.println("\n[ERRO] Opção inválida.\n");
    }
}

/**
 * Método utilitário para exibir a lista de consultas.
 */
private static void exibirConsultas(List<Consulta> lista, String titulo) {
    System.out.println("\n==============================================");
    System.out.println("--- RELATÓRIO: " + titulo + " (TOTAL: " + lista.size() + ") ---");
    System.out.println("==============================================");

    if (lista.isEmpty()) {
        System.out.println("Nenhuma consulta encontrada com os filtros selecionados.");
        return;
    }

    // Exibe os detalhes de cada consulta
    lista.stream()
            .sorted((c1, c2) -> c1.dataHora().compareTo(c2.dataHora())) // Ordena por data/hora
            .forEach(c -> System.out.println("-> " + c.exibirDetalhes()));

    System.out.println("\n==============================================");
}
private static void medicoQueMaisAtendeu(List<Consulta> consultas) {
    if (consultas.isEmpty()) {
        System.out.println("[INFO] Não há consultas registradas para calcular o médico mais atendido.");
        return;
    }

    // Agrupa as consultas pelo objeto Medico e conta a ocorrência
    Map<Medico, Long> contagemPorMedico = consultas.stream()
            .collect(Collectors.groupingBy(Consulta::medico, Collectors.counting()));

    // Encontra o médico com a maior contagem (usando Optional para segurança)
    Optional<Map.Entry<Medico, Long>> topMedico = contagemPorMedico.entrySet().stream()
            .max(Map.Entry.comparingByValue());

    topMedico.ifPresent(entry -> {
        System.out.println("\n[ESTATÍSTICA] Médico(a) que mais atendeu:");
        System.out.println("-> " + entry.getKey().nome() +
                " (" + entry.getKey().especialidade() + "): " +
                entry.getValue() + " consultas.");
    });
}

private static void especialidadeMaisProcurada(List<Consulta> consultas) {
    if (consultas.isEmpty()) {
        System.out.println("[INFO] Não há consultas registradas para calcular a especialidade mais procurada.");
        return;
    }

    // Agrupa pela Especialidade do Médico e conta
    Map<String, Long> contagemPorEspecialidade = consultas.stream()
            // Mapeia cada consulta para o nome da especialidade em maiúsculas
            .collect(Collectors.groupingBy(
                    c -> c.medico().especialidade().toUpperCase(),
                    Collectors.counting()
            ));

    // Encontra a especialidade com a maior contagem
    Optional<Map.Entry<String, Long>> topEspecialidade = contagemPorEspecialidade.entrySet().stream()
            .max(Map.Entry.comparingByValue());

    topEspecialidade.ifPresent(entry -> {
        System.out.println("\n[ESTATÍSTICA] Especialidade mais procurada:");
        System.out.println("-> " + entry.getKey() + ": " + entry.getValue() + " consultas.");
    });
}

private static void estatisticasPlanoSaude() {
    System.out.println("\n[ESTATÍSTICA] Economia por Planos de Saúde:");

    // Agrupa todos os pacientes por Plano de Saúde (apenas Pacientes Especiais)
    Map<String, List<PacienteEspecial>> pacientesPorPlano = PacienteService.listarTodos().stream()
            .filter(p -> p instanceof PacienteEspecial)
            .map(p -> (PacienteEspecial) p) // Converte para PacienteEspecial
            .collect(Collectors.groupingBy(p -> p.planoDeSaude().nome()));

    if (pacientesPorPlano.isEmpty()) {
        System.out.println("-> Nenhum paciente especial cadastrado para calcular a economia.");
        return;
    }

    List<Consulta> todasConsultas = ConsultaService.listarTodas();

    // Itera sobre todos os planos que têm pacientes cadastrados
    pacientesPorPlano.forEach((nomePlano, pacientesDoPlano) -> {

        // 1. Quantidade de pessoas no plano
        long totalPessoas = pacientesDoPlano.size();

        // 2. Cálculo da economia total do plano
        final Set<String> cpfsDoPlano = pacientesDoPlano.stream()
                .map(Paciente::cpf)
                .collect(Collectors.toSet());

        double economiaTotal = todasConsultas.stream()
                // Filtra consultas que envolvam pacientes deste plano
                .filter(c -> cpfsDoPlano.contains(c.paciente().cpf()))
                // Calcula a economia de cada consulta
                .mapToDouble(c -> {
                    double custoBase = c.medico().custoConsulta();
                    double valorPago = c.valorFinal();
                    return custoBase - valorPago; // Economia é o desconto
                })
                .sum(); // Soma todas as economias

        System.out.println("\n-> Plano: " + nomePlano);
        System.out.println("   - Total de Clientes: " + totalPessoas + " pessoas");
        System.out.println("   - Economia Total para Clientes: R$" + String.format("%.2f", economiaTotal));
    });
}

private static void relatorioEstatisticas() {
    System.out.println("\n==============================================");
    System.out.println("--- RELATÓRIO: ESTATÍSTICAS GERAIS ---");
    System.out.println("==============================================");

    List<Consulta> todasConsultas = ConsultaService.listarTodas();

    // 1. Médico que mais atendeu
    medicoQueMaisAtendeu(todasConsultas);

    // 2. Especialidade mais procurada
    especialidadeMaisProcurada(todasConsultas);

    // 3. Estatísticas de Plano de Saúde (Economia)
    estatisticasPlanoSaude();

    System.out.println("==============================================\n");
}

private static void entrarNaFilaTriagem() {
    System.out.print("CPF do Paciente para triagem: ");
    String cpfPaciente = scanner.nextLine();
    Paciente paciente = PacienteService.buscarPacientePorCpf(cpfPaciente).orElse(null);

    if (paciente == null) {
        System.out.println("[ERRO] Paciente não encontrado.");
        return;
    }

    System.out.print("Nível de Prioridade (1=Mais Alto / 5=Mais Baixo): ");
    int nivel;
    try {
        nivel = Integer.parseInt(scanner.nextLine().trim());
        if (nivel < 1 || nivel > 5) throw new Exception();
    } catch (Exception e) {
        System.out.println("[ERRO] Nível deve ser entre 1 e 5. Usando 5 (Baixo).");
        nivel = 5;
    }

    Triagem novaTriagem = new Triagem(paciente, nivel, LocalDateTime.now());
    filaTriagem.offer(novaTriagem); // Adiciona na fila e reordena

    System.out.println("[SUCESSO] Paciente " + paciente.nome() + " adicionado à fila com Prioridade " + nivel + ".");
}

private static void chamarProximoPaciente() {
    if (filaTriagem.isEmpty()) {
        System.out.println("[INFO] A fila de triagem está vazia.");
        return;
    }

    Triagem proximo = filaTriagem.poll(); // Remove e retorna o item de maior prioridade

    System.out.println("\n--- PRÓXIMO PACIENTE ---");
    System.out.println("O paciente mais prioritário é: " + proximo.getPaciente().nome());
    System.out.println("Prioridade: " + proximo.getNivelPrioridade());
    System.out.println("Iniciando atendimento...");
}

private static void exibirFilaTriagem() {
    System.out.println("\n--- FILA DE ESPERA (ORDEM DE PRIORIDADE) ---");
    if (filaTriagem.isEmpty()) {
        System.out.println("[INFO] Fila vazia.");
        return;
    }

    // Cria uma cópia temporária para iterar sem remover da fila original
    PriorityQueue<Triagem> copia = new PriorityQueue<>(filaTriagem);
    int contador = 1;
    while (!copia.isEmpty()) {
        System.out.println(contador++ + ". " + copia.poll());
    }
}

private static void executarMenuTriagem() {
    System.out.println("\n--- SISTEMA DE TRIAGEM E FILA ---");
    System.out.println("1. Entrar na Fila de Triagem");
    System.out.println("2. Chamar Próximo Paciente (Atender)");
    System.out.println("3. Exibir Fila de Espera");
    System.out.println("0. Voltar");
    System.out.print("Escolha uma opção: ");

    int subOpcao;
    try {
        subOpcao = Integer.parseInt(scanner.nextLine().trim());
    } catch (NumberFormatException e) {
        System.out.println("\n[ERRO] Opção inválida.\n");
        return;
    }

    switch (subOpcao) {
        case 1:
            entrarNaFilaTriagem();
            break;
        case 2:
            chamarProximoPaciente();
            break;
        case 3:
            exibirFilaTriagem();
            break;
        case 0:
            break;
        default:
            System.out.println("\n[ERRO] Opção inválida.\n");
    }
}

private static void executarMenuPrincipal() {
    int opcao = -1;
    while (opcao != 0) {
        exibirOpcoesMenu();
        System.out.print("Escolha uma opção: ");

        try {
            String input = scanner.nextLine();
            opcao = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("\n [ERRO] Opção inválida! Digite um número de 0 a 6. \n");
        }

        //Menu principal
        switch (opcao) {
            case 1:
                cadastrarPaciente();
                break;
            case 2:
                cadastrarMedico();
                break;
            case 3:
                agendarConsulta();
                break;
            case 4:
                gerenciarInternacoes();
                break;
            case 5:
                executarMenuRelatorios();
                break;
            case 6:
                executarMenuTriagem();
                break;
            case 7:
                configurarPlanosSaude();
                break;
            case 0:
                System.out.println("\nSalvando dados antes de encerrar...");
                PersistenciaDeDadosService.saveAllData();
                System.out.println("Dados persistidos.");
                System.out.println("Encerrando o Sistema Hospitalar. Até logo!");
                break;
            default:
                System.out.println("\n[ERRO] Opção inválida! Tente novamente.\n");
                break;
        }
    }
    scanner.close();
}
private static void CarregaDados() {
    DataModel data = PersistenciaDeDadosService.loadAllData();

    // Injeta as listas carregadas nos Services
    PacienteService.setAll(data.pacientes());
    MedicoService.setAll(data.medicos());
    ConsultaService.setAll(data.consultas());
    InternacaoService.setAll(data.internacoes());
    PlanoSaudeService.setAll(data.planos());

    System.out.println("[INFO] Dados carregados com sucesso.");
}


public static void main() {
    System.out.print("--- Sistema Hospital Core ---");
    CarregaDados();

    executarMenuPrincipal();
}



