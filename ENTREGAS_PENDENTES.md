# MercadoCerto — Entregas Pendentes e Plano de Execução

> Documento consolidado a partir da reunião de feedback com o professor.
> Base para organização das sprints até a banca final (22 de junho).

---

## 1. Contexto da Reunião

### 1.1. Feedback recebido sobre a apresentação
- Visualização/ponte ainda pequena — precisa ganhar destaque.
- **Modelo de dados (DER)**: retirar da apresentação final. A banca pode não ter conhecimento técnico.
- **Interface**: melhoria notável em relação à versão anterior, mas as imagens precisam de mais cuidado.
- **Funcionalidades**: ainda faltam ajustes — é preciso "correr atrás da funcionalidade" agora que a interface evoluiu.
- **Monetização**: não ficou claro como o projeto vai gerar receita. Precisa ser definido e documentado.
- **Papéis e responsabilidades**: falta clareza sobre o que cada membro fez/faz em cada sprint.
- **Documentação/especificação**: ainda não foi entregue — é um ponto crítico.
- **Evolução do projeto**: deve ficar visível ao longo das sprints (antes/depois).

### 1.2. Pontos técnicos levantados
- **Banco de dados centralizado** na máquina do David — precisa ser descentralizado.
  - Sugestão 1: subir o script/arquivo no GitHub para que a equipe continue desenvolvendo.
  - Sugestão 2: servidor único com banco hospedado (envolve custo).
- O código do backend e frontend já está em estado avançado (ver `DOCUMENTACAO.md`).

### 1.3. Calendário
| Data | Evento |
|---|---|
| Sprint 1–4 | Entregas incrementais (conteúdo liberado pelo professor sprint a sprint) |
| 08/06 | Apresentação para ajuste de slides |
| 15/06 | Novos ajustes |
| **22/06** | **Banca final — prova no formato "U Pit"** |

> Aviso do professor: o segundo mês passa voando. Abrir o Trello, revisar o backlog e distribuir tarefas **agora**.

---

## 2. Entregas Pendentes (visão geral)

| # | Entrega | Status | Responsável sugerido |
|---|---|---|---|
| 1 | Modelagem de processos de negócio (BPMN) | A fazer | — |
| 2 | Requisitos funcionais e não funcionais | A fazer | — |
| 3 | Documentação arquitetural do sistema | Parcial (existe `DOCUMENTACAO.md` técnica) | — |
| 4 | Documentação referente ao trabalho da equipe | A fazer | — |
| 5 | Desenvolvimento — refinamento das funcionalidades | Em andamento | — |
| 6 | Montagem da apresentação final | A fazer | — |
| 7 | Descentralização do banco de dados | A fazer | David |
| 8 | Definição de modelo de monetização | A fazer | — |

---

## 3. O que já pode ser adiantado (ações imediatas)

Estas ações **não dependem** de liberação do professor e podem ser iniciadas já, porque reutilizam informação que a equipe já possui no código e na documentação existente.

### 3.1. Descentralizar o banco de dados (prioridade máxima — desbloqueia equipe)
- Já existem os arquivos `mercdadocertocreate.sql` e `migrar_banco.sql` na pasta `startup/`.
- **Ação**: subir o projeto (incluindo os `.sql`) para o GitHub com um `README` de setup.
- **Passos**:
  1. Criar repositório privado/público da equipe no GitHub.
  2. Adicionar `.gitignore` (excluir `target/`, `uploads/`, credenciais).
  3. Documentar no `README` como rodar: instalar MariaDB local, executar os scripts, rodar `mvn spring-boot:run`.
  4. Cada membro clona e roda localmente. Banco deixa de ser SPOF.

### 3.2. Modelagem de processos de negócio (BPMN)
- **Não confundir com casos de uso**. Foco nos processos que o mercadoCerto automatiza/apoia.
- Processos prováveis a modelar:
  - **Comparar preços de um produto**: consumidor busca → sistema consulta mercados → retorna ranking → consumidor escolhe.
  - **Cadastrar/atualizar preço (comerciante)**: login → painel do comércio → cadastro/edição → publicação.
  - **Avaliar mercado**: consumidor visita mercado → abre ficha → envia avaliação → reputação é recalculada.
  - **Montar lista de compras otimizada**: consumidor adiciona produtos → sistema calcula mercado mais barato → exibe resultado.
- **Ferramenta sugerida**: [bpmn.io](https://bpmn.io) (gratuito, exporta PNG/SVG).

### 3.3. Requisitos funcionais e não funcionais
Boa parte já está implícita no código. Basta extrair e formalizar.

**Funcionais (exemplos a partir do que já existe)**:
- RF01: O sistema deve permitir cadastro e login de consumidores e comerciantes.
- RF02: O sistema deve permitir que comerciantes cadastrem mercados e produtos.
- RF03: O sistema deve permitir comparação de preços de um produto entre diferentes mercados.
- RF04: O sistema deve permitir que consumidores avaliem mercados.
- RF05: O sistema deve exibir mercados em um mapa interativo.
- RF06: O sistema deve permitir criação de listas de compras otimizadas.
- RF07: O sistema deve exibir histórico de preços em gráficos.

**Não funcionais (exemplos)**:
- RNF01: Autenticação com BCrypt (já implementado via Spring Security).
- RNF02: Interface responsiva (CSS Grid/Flexbox — já implementado).
- RNF03: Tempo de resposta da API ≤ 2s em consultas típicas.
- RNF04: Persistência em MariaDB 10.x+.
- RNF05: Compatibilidade com navegadores modernos (Chrome, Firefox, Edge).

### 3.4. Documentação arquitetural (aproveitar o que já existe)
- O arquivo `DOCUMENTACAO.md` já tem uma parte técnica forte (seções 3, 7, 8).
- **Ação**: derivar a partir dele:
  - Diagrama de componentes (Frontend ↔ Controllers ↔ Services ↔ JPA ↔ MariaDB).
  - Diagrama de implantação (onde roda cada peça).
  - Decisões arquiteturais (por que Spring Boot, por que HTML estático, etc.).

### 3.5. Definição do modelo de monetização
Ponto explicitamente cobrado pela banca. Brainstorm sugerido:
- **Plano premium para comerciantes**: destaque em buscas, analytics avançados.
- **Comissão por conversão**: taxa por lista de compras fechada que direciona consumidor a mercado parceiro.
- **Publicidade segmentada**: banners de marcas/mercados.
- **Assinatura do consumidor**: alertas de queda de preço, histórico estendido.
- Escolher 1–2 modelos principais e justificar no pitch.

### 3.6. Papéis e responsabilidades por sprint
- Abrir o Trello (conforme orientação do professor) e registrar, por sprint e por membro:
  - Cards concluídos.
  - Cards em andamento.
  - Próximos cards.
- Essa tabela vai para a documentação final e para o slide da apresentação.

### 3.7. Refinamento de funcionalidades (desenvolvimento)
- Revisar visualmente cada tela com foco em **imagens** (feedback direto da banca).
- Validar fluxos ponta a ponta: cadastro → login → comparar → avaliar → lista.
- Ajustar o componente de "visualização/ponte" mencionado pelo professor.

### 3.8. Apresentação final
- Retirar slide do DER.
- Incluir slide de **monetização**.
- Incluir slide com **evolução sprint a sprint** (antes/depois da interface).
- Incluir slide de **papéis e responsabilidades**.
- Foco em linguagem não-técnica para a banca.

---

## 4. Sugestão de divisão por sprint

| Sprint | Foco principal | Entregas |
|---|---|---|
| 1 (atual) | Desbloquear equipe + fundação documental | GitHub com banco descentralizado, requisitos funcionais/não funcionais, esqueleto de BPMN |
| 2 | Arquitetura + monetização | Doc arquitetural consolidada, modelo de monetização definido, refinamento de telas |
| 3 | Fechamento de funcionalidades | Todas as funcionalidades testadas ponta a ponta, documentação de trabalho da equipe |
| 4 | Apresentação | Slides finalizados (8/06 ajuste → 15/06 novo ajuste → 22/06 banca) |

---

## 5. Checklist rápido (o que começar hoje)

- [ ] Criar repositório no GitHub e subir o projeto com os `.sql`.
- [ ] Abrir o Trello, revisar backlog e atribuir responsáveis.
- [ ] Escrever primeira versão dos requisitos funcionais/não funcionais (extrair do código).
- [ ] Rascunhar os 3–4 processos de negócio principais no [bpmn.io](https://bpmn.io).
- [ ] Definir (em grupo) o modelo de monetização principal.
- [ ] Listar papéis/responsabilidades de cada membro nas sprints já realizadas.
