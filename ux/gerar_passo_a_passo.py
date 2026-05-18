# -*- coding: utf-8 -*-
"""
Gera o PDF com o passo a passo detalhado das alterações realizadas
no documento UX do MercadoCerto.
Saída: MercadoCerto_PassoAPasso_Alteracoes.pdf
"""
import sys
import io
sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')

from reportlab.lib.pagesizes import A4
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.lib.units import cm
from reportlab.lib import colors
from reportlab.lib.enums import TA_JUSTIFY, TA_CENTER, TA_LEFT
from reportlab.platypus import (
    SimpleDocTemplate, Paragraph, Spacer, PageBreak, Table, TableStyle,
    KeepTogether
)
from reportlab.pdfbase import pdfmetrics
from reportlab.pdfbase.ttfonts import TTFont

PATH_OUT = r"C:\Users\david.6961\mercadocerto\MercadoCerto-main\startup\ux\MercadoCerto_PassoAPasso_Alteracoes.pdf"

# ---------- Estilos ----------
styles = getSampleStyleSheet()

st_titulo = ParagraphStyle(
    'TituloPrincipal', parent=styles['Title'],
    fontName='Helvetica-Bold', fontSize=18, leading=22,
    alignment=TA_CENTER, spaceAfter=18, textColor=colors.HexColor('#1A1A2E')
)
st_subtitulo = ParagraphStyle(
    'Subtitulo', parent=styles['Normal'],
    fontName='Helvetica', fontSize=11, leading=14,
    alignment=TA_CENTER, spaceAfter=24, textColor=colors.HexColor('#555555')
)
st_h1 = ParagraphStyle(
    'H1', parent=styles['Heading1'],
    fontName='Helvetica-Bold', fontSize=14, leading=18,
    spaceBefore=18, spaceAfter=10, textColor=colors.HexColor('#1A1A2E')
)
st_h2 = ParagraphStyle(
    'H2', parent=styles['Heading2'],
    fontName='Helvetica-Bold', fontSize=12, leading=16,
    spaceBefore=12, spaceAfter=6, textColor=colors.HexColor('#1A1A2E')
)
st_h3 = ParagraphStyle(
    'H3', parent=styles['Heading3'],
    fontName='Helvetica-Bold', fontSize=11, leading=14,
    spaceBefore=8, spaceAfter=4, textColor=colors.HexColor('#333333')
)
st_corpo = ParagraphStyle(
    'Corpo', parent=styles['Normal'],
    fontName='Helvetica', fontSize=10.5, leading=15,
    alignment=TA_JUSTIFY, spaceAfter=8, firstLineIndent=0
)
st_bullet = ParagraphStyle(
    'Bullet', parent=styles['Normal'],
    fontName='Helvetica', fontSize=10.5, leading=14,
    alignment=TA_JUSTIFY, leftIndent=14, bulletIndent=2,
    spaceAfter=4
)
st_destaque = ParagraphStyle(
    'Destaque', parent=styles['Normal'],
    fontName='Helvetica-Oblique', fontSize=10, leading=14,
    alignment=TA_LEFT, textColor=colors.HexColor('#7C3AED'),
    spaceAfter=4
)
st_pequeno = ParagraphStyle(
    'Pequeno', parent=styles['Normal'],
    fontName='Helvetica', fontSize=9, leading=12,
    alignment=TA_LEFT, textColor=colors.HexColor('#777777'),
    spaceAfter=2
)


def P(texto, style=st_corpo):
    return Paragraph(texto, style)


def bullet(texto):
    return Paragraph("• " + texto, st_bullet)


# ---------- Documento ----------
doc = SimpleDocTemplate(
    PATH_OUT, pagesize=A4,
    leftMargin=2.5 * cm, rightMargin=2.5 * cm,
    topMargin=2.5 * cm, bottomMargin=2.5 * cm,
    title="MercadoCerto - Passo a Passo das Alteracoes",
    author="Grupo MercadoCerto"
)

elementos = []

# ==========================================================
# CAPA / ABERTURA
# ==========================================================
elementos.append(Spacer(1, 1.5 * cm))
elementos.append(P("MERCADOCERTO", st_titulo))
elementos.append(P(
    "Passo a Passo das Alterações na Documentação UX (Trabalho 2)",
    st_subtitulo
))
elementos.append(Spacer(1, 0.6 * cm))
elementos.append(P(
    "Este documento descreve, ponto a ponto, as alterações realizadas na "
    "documentação UX do projeto MercadoCerto. Para cada modificação, são "
    "indicados: <b>o que foi alterado</b>, <b>o motivo da alteração</b>, "
    "<b>a melhoria obtida</b> e <b>como cada ajuste contribui</b> para o "
    "alinhamento do trabalho ao enunciado do Trabalho 2 e ao feedback "
    "recebido na avaliação anterior.",
    st_corpo
))
elementos.append(P(
    "O texto está organizado em três partes: (1) um quadro-resumo das "
    "alterações estruturais; (2) o detalhamento das alterações por seção; "
    "e (3) uma síntese das mudanças de tom e estilo aplicadas ao texto.",
    st_corpo
))
elementos.append(PageBreak())

# ==========================================================
# 1. QUADRO RESUMO
# ==========================================================
elementos.append(P("1. QUADRO RESUMO DAS ALTERAÇÕES ESTRUTURAIS", st_h1))
elementos.append(P(
    "A tabela a seguir lista, de forma sintética, as principais "
    "modificações estruturais aplicadas ao documento original. As alterações "
    "mais profundas estão detalhadas nas seções seguintes.",
    st_corpo
))

dados_tabela = [
    ["Antes (versão anterior)", "Depois (versão refinada)", "Motivo"],
    ["Capa simples, sem identificação institucional clara.",
     "Capa e folha de rosto formais (institucional + grupo + título + descrição), padrão ABNT.",
     "Conferir caráter acadêmico, próprio de um trabalho do 5º período."],
    ["Sumário com aviso “Atualize este campo no Word” (não preenchido).",
     "Sumário completo, com todas as seções e numeração de páginas estimada.",
     "Eliminar marca de documento não finalizado e facilitar a leitura."],
    ["Sem seção de fundamentação teórica.",
     "Nova Seção 2: Fundamentação Teórica (Interação, Qualidade de Uso, Interface, Affordance).",
     "Atender ao critério do enunciado de “análise crítica fundamentada nos princípios de IHC”."],
    ["Sem seção de metodologia.",
     "Nova Seção 3: Metodologia, descrevendo coleta de feedback, classificação, definição de ações e implementação.",
     "Demonstrar rigor metodológico e tornar o processo rastreável."],
    ["Parte 1 com 12 categorias bem descritas, mas com tom oscilante.",
     "Mesmas 12 categorias, revisadas para tom acadêmico-acessível e padronizadas em três blocos: problema, ações e efeito esperado.",
     "Manter o conteúdo original, ganhando coerência e fluidez textuais."],
    ["Parte 2 misturando descrição e fundamentação.",
     "Parte 2 reorganizada por conceito-chave, com subseções 5.1, 5.2, 5.3 e 5.4 alinhadas ao enunciado.",
     "Garantir cobertura explícita dos quatro conceitos exigidos."],
    ["Quadro Resumo presente apenas como tabela isolada (texto introdutório dizia “O quadro a seguir...” sem comentário).",
     "Quadro Resumo com texto introdutório, tabela completa e leitura analítica após a tabela.",
     "Cumprir requisito de documentação reflexiva."],
    ["Conclusão curta, com tom de fechamento simples.",
     "Seção 7: Análise Crítica e Considerações Finais, com balanço, escolhas mais relevantes, limitações e próximos passos.",
     "Atender o critério “documentação reflexiva e fundamentada” (10 pts)."],
    ["Referências com 6 itens.",
     "Referências ampliadas para 13 itens, incluindo Gibson (1979), Mayhew (1992), Nielsen (1993), ISO 9241-11 e materiais didáticos da disciplina.",
     "Reforçar embasamento teórico em autores citados nos slides."],
]

tabela = Table(
    dados_tabela,
    colWidths=[5.0 * cm, 5.5 * cm, 5.0 * cm],
    repeatRows=1
)
tabela.setStyle(TableStyle([
    ('BACKGROUND', (0, 0), (-1, 0), colors.HexColor('#7C3AED')),
    ('TEXTCOLOR', (0, 0), (-1, 0), colors.white),
    ('FONTNAME', (0, 0), (-1, 0), 'Helvetica-Bold'),
    ('FONTSIZE', (0, 0), (-1, -1), 9),
    ('VALIGN', (0, 0), (-1, -1), 'TOP'),
    ('ALIGN', (0, 0), (-1, 0), 'CENTER'),
    ('GRID', (0, 0), (-1, -1), 0.4, colors.HexColor('#CCCCCC')),
    ('BACKGROUND', (0, 1), (-1, -1), colors.HexColor('#F5F3FF')),
    ('ROWBACKGROUNDS', (0, 1), (-1, -1),
     [colors.white, colors.HexColor('#F5F3FF')]),
    ('LEFTPADDING', (0, 0), (-1, -1), 6),
    ('RIGHTPADDING', (0, 0), (-1, -1), 6),
    ('TOPPADDING', (0, 0), (-1, -1), 6),
    ('BOTTOMPADDING', (0, 0), (-1, -1), 6),
]))
elementos.append(tabela)
elementos.append(PageBreak())

# ==========================================================
# 2. DETALHAMENTO POR SEÇÃO
# ==========================================================
elementos.append(P("2. DETALHAMENTO DAS ALTERAÇÕES POR SEÇÃO", st_h1))
elementos.append(P(
    "Nesta seção, cada alteração é detalhada com base no padrão "
    "<b>O que foi alterado / Por que / Melhoria obtida / Alinhamento "
    "com o enunciado e o feedback</b>.",
    st_corpo
))


def bloco_alteracao(titulo, alterado, porque, melhoria, alinhamento):
    grupo = [
        Paragraph(titulo, st_h2),
        Paragraph("<b>O que foi alterado:</b> " + alterado, st_corpo),
        Paragraph("<b>Por que a mudança foi feita:</b> " + porque, st_corpo),
        Paragraph("<b>Melhoria obtida:</b> " + melhoria, st_corpo),
        Paragraph("<b>Alinhamento com enunciado/feedback:</b> " + alinhamento, st_corpo),
        Spacer(1, 0.3 * cm),
    ]
    return KeepTogether(grupo)


# 2.1
elementos.append(bloco_alteracao(
    "2.1 Capa e folha de rosto",
    "Foi acrescentada uma capa institucional (com a identificação do Centro "
    "Universitário Senac, do curso e da disciplina) e uma folha de rosto "
    "específica, contendo a descrição acadêmica do trabalho e o nome do "
    "professor. A versão anterior trazia apenas uma capa simplificada com o "
    "título do projeto e os autores.",
    "O enunciado pede um trabalho acadêmico formal, com análise crítica "
    "fundamentada. A apresentação institucional reforça esse caráter desde "
    "o primeiro contato visual do avaliador com o documento.",
    "Aproxima o documento do padrão ABNT esperado para entregas do 5º "
    "período e reforça a identidade do grupo e da disciplina.",
    "Atende ao critério geral de qualidade da entrega e à percepção de "
    "“documentação reflexiva e fundamentada” cobrada na rubrica de avaliação."
))

# 2.2
elementos.append(bloco_alteracao(
    "2.2 Sumário",
    "O sumário, que antes continha apenas um aviso ao leitor (“Atualize "
    "este campo no Word: Ctrl+A e depois F9”), foi substituído por um índice "
    "manual completo, com a paginação estimada de cada seção. O documento "
    "agora apresenta 26 entradas no sumário, refletindo a nova estrutura.",
    "A presença de um aviso técnico de Word no documento final transmitia a "
    "sensação de entrega incompleta. Substituí-lo por um sumário pronto "
    "elimina esse ruído e melhora a leitura.",
    "O leitor passa a ter uma visão imediata da organização do trabalho, "
    "facilitando a localização das seções de maior interesse.",
    "Atende a um detalhe simples, mas relevante, do critério “documentação "
    "reflexiva e fundamentada”: a apresentação completa do material."
))

# 2.3
elementos.append(bloco_alteracao(
    "2.3 Seção 2 — Fundamentação Teórica (NOVA)",
    "Foi acrescentada uma seção inteira de fundamentação teórica, com quatro "
    "subseções: 2.1 Interação Humano-Computador, 2.2 Qualidade de Uso, 2.3 "
    "Interface e 2.4 Affordance. Cada subseção apresenta os conceitos antes "
    "de aplicá-los, com base em Winograd (2003), Mayhew (1992), Norman "
    "(2013), Gibson (1979), Nielsen (1993, 1994) e a norma ISO 9241-11.",
    "A versão anterior aplicava os conceitos diretamente na Parte 2, sem "
    "apresentá-los previamente. Isso comprometia o critério de “análise "
    "crítica fundamentada nos princípios de IHC” pedido no enunciado, além "
    "de deixar o documento dependente de conhecimento prévio do leitor.",
    "O documento ganha uma base teórica explícita, em que cada conceito "
    "central é definido e referenciado antes de ser aplicado. Isso fortalece "
    "a coerência argumentativa e demonstra domínio do conteúdo.",
    "Cobre diretamente o requisito do enunciado de “análise crítica "
    "fundamentada nos princípios de IHC” (10 pts da rubrica). Também conecta "
    "o documento aos conceitos explicitados nos slides VIII e IX da disciplina."
))

# 2.4
elementos.append(bloco_alteracao(
    "2.4 Seção 3 — Metodologia (NOVA)",
    "Inclusão de uma seção descrevendo as quatro etapas do processo de "
    "refinamento: coleta de feedback, classificação por prioridade, "
    "definição de ações fundamentadas em IHC e implementação no código.",
    "A versão anterior mencionava o feedback de forma genérica (“o feedback "
    "veio de uma sessão de review com colegas e com o professor”). Não havia "
    "rigor metodológico explícito sobre como os apontamentos foram tratados.",
    "Torna o processo rastreável e demonstra que as decisões de design não "
    "foram aleatórias, mas resultado de um procedimento estruturado.",
    "Reforça o critério “documentação reflexiva e fundamentada”, mostrando "
    "como o grupo transformou crítica em ação concreta."
))

# 2.5
elementos.append(bloco_alteracao(
    "2.5 Parte 1 — Revisão do tom textual",
    "As 12 categorias de melhorias da Parte 1 foram preservadas em "
    "conteúdo, mas todos os parágrafos foram revisados para um tom "
    "acadêmico-acessível. Expressões coloquiais como “estilosa demais”, "
    "“no vácuo”, “tira a ordenação alfabética acidental” e “cara mais "
    "orientada e menos vazia” foram substituídas por formulações mais "
    "neutras, sem perder a clareza.",
    "O enunciado pede tom acadêmico, mas o material original oscilava entre "
    "trechos formais e expressões muito coloquiais. Esse contraste "
    "comprometia a uniformidade do texto e a percepção de profundidade.",
    "Texto mais fluido e profissional, mantendo a leitura acessível, mas "
    "sem o desnível entre seções formais e coloquiais.",
    "Atende à orientação explícita do usuário: “natural e humanizado; "
    "acadêmico, porém acessível; coerente com alunos do 5º período de ADS”."
))

# 2.6
elementos.append(bloco_alteracao(
    "2.6 Parte 1 — Padronização do bloco descritivo",
    "Cada subseção da Parte 1 foi padronizada em três blocos: (1) "
    "descrição do problema identificado; (2) lista de ações implementadas "
    "marcadas como tópicos; (3) efeito esperado no usuário. A versão "
    "anterior usava a mesma lógica, mas com hierarquia visual menos "
    "consistente entre seções.",
    "A padronização do formato facilita a comparação entre seções e a "
    "leitura do documento como referência para revisões futuras.",
    "Maior consistência editorial e tempo de leitura reduzido para "
    "avaliadores que queiram comparar pontos específicos.",
    "Cumpre o critério “documentação reflexiva e fundamentada” ao "
    "apresentar cada melhoria com a mesma estrutura analítica."
))

# 2.7
elementos.append(bloco_alteracao(
    "2.7 Parte 2 — Reorganização por conceito-chave",
    "A Parte 2 foi reorganizada em quatro subseções (5.1 Interação, "
    "5.2 Affordance, 5.3 Interface e 5.4 Qualidade de Uso), cada uma com "
    "subdivisões internas (por exemplo, 5.1.1 Definição do fluxo de tarefas, "
    "5.1.2 Ciclos de ação e feedback, 5.1.3 Respostas visíveis). Cada "
    "subdivisão referencia explicitamente a seção da Parte 1 em que a "
    "melhoria foi descrita.",
    "A estrutura anterior já era organizada por conceito, mas as "
    "referências cruzadas (“Seção X.Y”) eram esparsas e nem sempre "
    "explícitas. A nova organização torna o vínculo Parte 1 ↔ Parte 2 "
    "imediato.",
    "Leitura mais clara dos elos entre o problema observado, a ação "
    "tomada e o conceito de IHC que a fundamenta.",
    "Atende diretamente à exigência do enunciado: “considerando a parte um "
    "do projeto, aplique os conceitos-chave durante as melhorias "
    "propostas, justificando suas escolhas”."
))

# 2.8
elementos.append(bloco_alteracao(
    "2.8 Quadro Resumo — Comentário pós-tabela",
    "Foi mantida a tabela de correspondência entre melhorias e conceitos, "
    "mas com a adição de um parágrafo introdutório e de um parágrafo "
    "analítico após a tabela, identificando as melhorias de maior "
    "abrangência conceitual (4.3, 4.5 e 4.7) e as melhorias mais "
    "específicas (tipografia e responsividade).",
    "A tabela original aparecia praticamente sem contexto. A leitura "
    "ficava deixada inteiramente a cargo do avaliador, sem o esforço "
    "interpretativo do grupo.",
    "Demonstra capacidade analítica e fecha o ciclo entre dado e "
    "interpretação, que é justamente o que se espera em uma documentação "
    "reflexiva.",
    "Cumpre a expectativa de “análise crítica fundamentada” do enunciado, "
    "convertendo o quadro em insumo argumentativo."
))

# 2.9
elementos.append(bloco_alteracao(
    "2.9 Seção 7 — Análise Crítica e Considerações Finais",
    "A conclusão da versão anterior, com cerca de quatro parágrafos curtos, "
    "foi expandida em uma seção dedicada de análise crítica, com seis "
    "parágrafos. Inclui: (a) balanço do deslocamento de foco (do funcional "
    "para o centrado no usuário); (b) destaque das três escolhas mais "
    "relevantes; (c) reconhecimento das limitações (ausência de testes "
    "formais com usuários, validação por OCR ainda não implementada); "
    "(d) próximos passos sugeridos.",
    "A conclusão original cumpria o papel de fechamento, mas não atendia "
    "ao critério reflexivo do enunciado. Faltava admitir limitações e "
    "apontar evolução futura, traços típicos de um trabalho acadêmico "
    "sólido.",
    "Demonstra maturidade do grupo ao reconhecer pontos não resolvidos e "
    "evidencia capacidade de pensar o projeto além da entrega imediata.",
    "Atende diretamente ao critério “documentação reflexiva e fundamentada” "
    "(10 pts) da rubrica do enunciado."
))

# 2.10
elementos.append(bloco_alteracao(
    "2.10 Referências",
    "A lista de referências foi ampliada de 6 para 13 itens. Foram "
    "incluídos os autores citados nos slides da disciplina (Gibson 1979, "
    "Mayhew 1992, Winograd 2003, Nielsen 1993), a norma ISO 9241-11 e os "
    "materiais didáticos do Prof. Carlos Alberto Pedroso Junior (Aulas "
    "VIII e IX).",
    "A versão anterior referenciava apenas seis obras, deixando de fora "
    "autores centrais que aparecem explicitamente nos slides da disciplina. "
    "Isso enfraquecia a fundamentação e desconectava o trabalho do "
    "material visto em sala.",
    "Maior solidez teórica e ligação direta entre o trabalho e o conteúdo "
    "específico abordado nas aulas VIII e IX (slides anexos).",
    "Atende ao critério de fundamentação e demonstra leitura efetiva do "
    "material didático, conforme orientação do usuário."
))

elementos.append(PageBreak())

# ==========================================================
# 3. AJUSTES DE TOM E ESTILO
# ==========================================================
elementos.append(P("3. AJUSTES DE TOM E ESTILO TEXTUAL", st_h1))
elementos.append(P(
    "Esta seção sintetiza os ajustes que afetam não o conteúdo, mas a "
    "<b>forma</b> como o documento se comunica com o leitor. Foram "
    "aplicados de forma transversal ao texto.",
    st_corpo
))

elementos.append(P("3.1 Substituição de expressões coloquiais", st_h2))
elementos.append(P(
    "Expressões com sabor de conversa informal foram reescritas em "
    "registro acadêmico-acessível, sem perder naturalidade:",
    st_corpo
))

dados_estilo = [
    ["Antes", "Depois"],
    ['"estilosa demais" / "atrapalhava a leitura"',
     '"caráter excessivamente decorativo, o que dificultava a leitura"'],
    ['"foi o ponto mais criticado no review"',
     '"foi um dos pontos mais recorrentes nos comentários da avaliação"'],
    ['"que tira a ordenação alfabética acidental"',
     '"em substituição à ordenação alfabética que ocorria por padrão"'],
    ['"o usuário não fica no vácuo"',
     '"garantem que o sistema nunca permaneça em silêncio durante uma interação"'],
    ['"resolve a frustração de leitura apontada no review"',
     '"resolve a queixa relacionada à leitura"'],
    ['"cara mais orientada e menos vazia"',
     '"proposta visual orientada e hierarquizada"'],
    ['"Para o futuro, está prevista a leitura automática..."',
     '"Previsão, em uma próxima iteração, de leitura automática..."'],
]

tab_estilo = Table(dados_estilo, colWidths=[7.5 * cm, 8.0 * cm], repeatRows=1)
tab_estilo.setStyle(TableStyle([
    ('BACKGROUND', (0, 0), (-1, 0), colors.HexColor('#7C3AED')),
    ('TEXTCOLOR', (0, 0), (-1, 0), colors.white),
    ('FONTNAME', (0, 0), (-1, 0), 'Helvetica-Bold'),
    ('FONTSIZE', (0, 0), (-1, -1), 9),
    ('VALIGN', (0, 0), (-1, -1), 'TOP'),
    ('ALIGN', (0, 0), (-1, 0), 'CENTER'),
    ('GRID', (0, 0), (-1, -1), 0.4, colors.HexColor('#CCCCCC')),
    ('ROWBACKGROUNDS', (0, 1), (-1, -1),
     [colors.white, colors.HexColor('#F5F3FF')]),
    ('LEFTPADDING', (0, 0), (-1, -1), 6),
    ('RIGHTPADDING', (0, 0), (-1, -1), 6),
    ('TOPPADDING', (0, 0), (-1, -1), 5),
    ('BOTTOMPADDING', (0, 0), (-1, -1), 5),
]))
elementos.append(tab_estilo)
elementos.append(Spacer(1, 0.6 * cm))

elementos.append(P("3.2 Padronização da norma culta", st_h2))
elementos.append(P(
    "Ao longo do documento foram aplicadas correções e padronizações "
    "discretas mas relevantes:",
    st_corpo
))
elementos.append(bullet("Uso consistente de “seção” (e não “sessão”) ao se referir às partes do trabalho;"))
elementos.append(bullet("Padronização do uso do travessão (—) em vez de hífen (-) para apostos e incisos;"))
elementos.append(bullet("Padronização de aspas tipográficas (curvas) em vez de aspas retas;"))
elementos.append(bullet("Verificação de regência (por exemplo, “atender a” em lugar de “atender à”/”atender de” quando inadequado);"))
elementos.append(bullet("Substituição de marcadores “–” por “•” nas listas de ações implementadas, em favor da convenção visual mais comum em documentos acadêmicos."))

elementos.append(P("3.3 Tom alinhado ao perfil do autor", st_h2))
elementos.append(P(
    "Considerando o perfil descrito pelo usuário — alunos do 5º período "
    "de Análise e Desenvolvimento de Sistemas —, a escrita preserva "
    "conhecimento técnico intermediário, sem soar artificial nem demasiado "
    "avançado. Termos como “Bayesiana”, “QuaggaJS”, “Bean Validation”, "
    "“WCAG 2.1”, “ARIA” e “Geolocation API” aparecem naturalmente, mas "
    "sempre acompanhados de explicação breve, para não pressupor "
    "familiaridade total.",
    st_corpo
))
elementos.append(P(
    "O resultado é um documento que demonstra competência técnica sem "
    "afetação, com fluência argumentativa equilibrada ao longo de todo o texto.",
    st_corpo
))

elementos.append(PageBreak())

# ==========================================================
# 4. ALINHAMENTO COM O ENUNCIADO
# ==========================================================
elementos.append(P("4. COMO AS ALTERAÇÕES ATENDEM AO ENUNCIADO", st_h1))
elementos.append(P(
    "A tabela abaixo correlaciona os critérios da rubrica do Trabalho 2 com "
    "as alterações que mais contribuem para cada ponto avaliado.",
    st_corpo
))

dados_criterios = [
    ["Critério (pontuação)", "Alterações que atendem"],
    ["Clareza e eficácia da interação (20 pts)",
     "Reorganização da Parte 2, com subseção 5.1 dedicada à interação, "
     "explicitando fluxos do consumidor e do comerciante, ciclos de "
     "ação-feedback e respostas visíveis (Norman 2013, Mayhew 1992)."],
    ["Aplicação explícita do conceito de affordance (15 pts)",
     "Subseção 5.2 reorganizada por tipos de affordance, com exemplos "
     "concretos do projeto (botões, ícones, cores como signifiers) e "
     "discussão das falsas affordances eliminadas."],
    ["Qualidade da interface (visual, organização) (15 pts)",
     "Subseção 5.3 sobre Interface, abordando consistência visual, "
     "hierarquia em três níveis e adoção de padrões de design "
     "consagrados (Heurísticas de Nielsen, Material Design, Gestalt)."],
    ["Usabilidade e experiência do usuário (20 pts)",
     "Subseção 5.4 estruturada nas quatro dimensões da qualidade de uso "
     "(eficiência, eficácia, satisfação e acessibilidade), com exemplos "
     "quantificados de redução de cliques e melhorias percebidas."],
    ["Acessibilidade e inclusão (10 pts)",
     "Subseção 5.4.4 detalha conformidade com WCAG 2.1 níveis AA/AAA: "
     "contraste 7:1, navegação por teclado, ARIA, textos alternativos."],
    ["Criatividade, originalidade e adequação do tema (10 pts)",
     "Separação de perfis Consumidor/Comerciante, validação anti-fraude "
     "com leitor de código de barras e validação por faixa de preço — "
     "soluções originais para o contexto de comparação de preços."],
    ["Documentação reflexiva e fundamentada (10 pts)",
     "Nova Seção 2 (Fundamentação Teórica), Seção 3 (Metodologia), Seção 7 "
     "(Análise Crítica e Considerações Finais) e ampliação das "
     "referências para 13 itens. A reflexão sobre limitações e próximos "
     "passos atende diretamente este critério."],
]

tab_crit = Table(dados_criterios, colWidths=[5.5 * cm, 10.0 * cm], repeatRows=1)
tab_crit.setStyle(TableStyle([
    ('BACKGROUND', (0, 0), (-1, 0), colors.HexColor('#7C3AED')),
    ('TEXTCOLOR', (0, 0), (-1, 0), colors.white),
    ('FONTNAME', (0, 0), (-1, 0), 'Helvetica-Bold'),
    ('FONTSIZE', (0, 0), (-1, -1), 9),
    ('VALIGN', (0, 0), (-1, -1), 'TOP'),
    ('ALIGN', (0, 0), (-1, 0), 'CENTER'),
    ('GRID', (0, 0), (-1, -1), 0.4, colors.HexColor('#CCCCCC')),
    ('ROWBACKGROUNDS', (0, 1), (-1, -1),
     [colors.white, colors.HexColor('#F5F3FF')]),
    ('LEFTPADDING', (0, 0), (-1, -1), 6),
    ('RIGHTPADDING', (0, 0), (-1, -1), 6),
    ('TOPPADDING', (0, 0), (-1, -1), 6),
    ('BOTTOMPADDING', (0, 0), (-1, -1), 6),
]))
elementos.append(tab_crit)

elementos.append(Spacer(1, 0.6 * cm))

elementos.append(P("4.1 Aproveitamento do feedback recebido", st_h2))
elementos.append(P(
    "Embora o feedback original tenha sido tratado conceitualmente como "
    "“colega + professor”, todas as melhorias documentadas na Parte 1 "
    "originam-se de apontamentos efetivos. As principais conexões são:",
    st_corpo
))
elementos.append(bullet("Feedback sobre tipografia → Seção 4.1 (Tipografia e legibilidade);"))
elementos.append(bullet("Feedback sobre contraste e cores → Seção 4.2;"))
elementos.append(bullet("Feedback sobre navegação e botões de voltar → Seção 4.3 e 4.12;"))
elementos.append(bullet("Feedback sobre mapa inicial pouco útil → Seção 4.4;"))
elementos.append(bullet("Feedback sobre falta de separação consumidor/comerciante → Seção 4.5;"))
elementos.append(bullet("Feedback sobre validação de preços e fraude → Seção 4.6;"))
elementos.append(bullet("Feedback sobre formulário de preço quebrando → Seção 4.7;"))
elementos.append(bullet("Feedback sobre lista de compras limitada → Seção 4.8;"))
elementos.append(bullet("Feedback sobre home vazia → Seção 4.9;"))
elementos.append(bullet("Feedback sobre layout mobile quebrando → Seção 4.10;"))
elementos.append(bullet("Feedback sobre ranking com fórmula ingênua → Seção 4.11."))

elementos.append(Spacer(1, 0.6 * cm))
elementos.append(P(
    "Essa rastreabilidade — fruto da metodologia descrita na Seção 3 — é "
    "um dos principais atrativos da nova versão: o leitor consegue "
    "identificar, para cada melhoria implementada, qual era o problema "
    "original e qual conceito de IHC fundamenta a solução adotada.",
    st_corpo
))

elementos.append(PageBreak())

# ==========================================================
# 5. ARQUIVOS GERADOS
# ==========================================================
elementos.append(P("5. ARQUIVOS GERADOS NESTA ENTREGA", st_h1))
elementos.append(P(
    "Os dois arquivos abaixo compõem o pacote final entregue ao usuário:",
    st_corpo
))
elementos.append(bullet(
    "<b>MercadoCerto_Trabalho2_UX_Final.docx</b> — documento principal, "
    "com a versão refinada da Parte 1, da Parte 2, do Quadro Resumo e da "
    "Análise Crítica, além das novas seções de Fundamentação Teórica e "
    "Metodologia."
))
elementos.append(bullet(
    "<b>MercadoCerto_PassoAPasso_Alteracoes.pdf</b> — este documento, "
    "que registra o que foi alterado, por que, qual a melhoria obtida e "
    "como cada ajuste atende ao enunciado e ao feedback."
))

elementos.append(Spacer(1, 0.6 * cm))
elementos.append(P(
    "Recomenda-se que o documento principal seja revisado pelo grupo no "
    "Word antes da entrega, em especial: (a) a numeração de páginas do "
    "sumário, que foi estimada e pode variar conforme as configurações "
    "locais; (b) a inserção dos prints da interface refinada nas seções "
    "correspondentes da Parte 1, caso o grupo deseje ilustrar as "
    "melhorias; (c) eventuais ajustes finais de formatação ABNT exigidos "
    "pela instituição.",
    st_corpo
))

elementos.append(Spacer(1, 1.0 * cm))
elementos.append(P(
    "<i>Curitiba, 2026.</i>", st_destaque
))

# ==========================================================
# Geração
# ==========================================================
doc.build(elementos)
print("PDF gerado com sucesso em:", PATH_OUT)
