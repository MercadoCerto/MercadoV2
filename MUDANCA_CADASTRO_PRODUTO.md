# Mudança no Cadastro de Produto — Tipo de Medida

## Contexto

O formulário atual de cadastro de produto (`cadastro-produto.html`) apresenta dois
campos paralelos — **Quantidade na embalagem** e **Peso/Volume + Unidade** — sem
regra clara de qual preencher. Isso gera:

- Ambiguidade (uma "cartela de 12 ovos" pode ser `quantidade=12` ou `peso=12+un=un`).
- Impossibilidade de representar multipacks (ex.: 6 garrafas de 500 ml).
- Cálculo de preço por unidade inconsistente (não suporta `un` nem pack).

## Decisão

Adotar a **Alternativa 1 + 3** discutida: introduzir um campo discriminador
`tipoMedida` que determina quais campos numéricos são relevantes e como o preço
por unidade é calculado.

### Tipos de medida

| Tipo      | Quando usar                          | Campos preenchidos                          | Exemplo                  |
|-----------|--------------------------------------|---------------------------------------------|--------------------------|
| `PESO`    | Vendido por massa                    | `peso` + `unidade ∈ {kg, g}`                | `5 kg` de arroz          |
| `VOLUME`  | Líquidos                             | `peso` (= volume) + `unidade ∈ {L, ml}`     | `1.5 L` de óleo          |
| `UNIDADE` | Itens contáveis simples              | `quantidade` (Integer)                      | `12 un` (ovos)           |
| `PACK`    | Multipack: N itens × conteúdo fixo   | `quantidade` + `peso` + `unidade`           | `6 × 500 ml`             |

A coluna `peso` é reaproveitada para **conteúdo unitário** (massa ou volume),
evitando criar uma coluna nova. O significado da unidade depende do `tipoMedida`.

### Cálculo de preço normalizado

| Tipo      | Fórmula                                            | Apresentação |
|-----------|----------------------------------------------------|--------------|
| `PESO`    | `preco / (peso em kg)`                             | `R$/kg`      |
| `VOLUME`  | `preco / (volume em L)`                            | `R$/L`       |
| `UNIDADE` | `preco / quantidade`                               | `R$/un`      |
| `PACK`    | `preco / (quantidade × conteúdo em kg ou L)`       | `R$/kg` ou `R$/L` |

A normalização (g→kg, ml→L) é feita em uma função única em `js/main.js` para
evitar divergência entre lista, comparar e painel.

## Arquivos alterados

### Backend (Java)
- **`src/main/java/com/mercadocerto/model/Produto.java`** — novo enum
  `TipoMedida` e coluna `tipo_medida`.

### Banco
- **`migrar_banco.sql`** — `ALTER TABLE produto ADD COLUMN tipo_medida` + `UPDATE`
  para inferir o tipo dos produtos existentes a partir da unidade atual.

### Frontend
- **`src/main/resources/static/cadastro-produto.html`** — seletor de tipo de
  medida no topo + campos dinâmicos que aparecem/escondem conforme o tipo.
- **`src/main/resources/static/js/main.js`** — novas funções utilitárias:
  - `precoUnitBase(produto)` — retorna `{ valor, unidadeBase }` ou `null`.
  - `formatarEmbalagem(produto)` — string amigável (ex.: `"6 × 500ml"`).
- **`src/main/resources/static/lista-produtos.html`** — passa a usar as funções
  centralizadas; passa a suportar `UNIDADE` e `PACK` no cálculo.
- **`src/main/resources/static/comparar.html`** — idem.
- **`src/main/resources/static/painel-comercio.html`** — exibe a embalagem
  formatada via função centralizada.

## Migração de dados existentes

O `UPDATE` no `migrar_banco.sql` infere o tipo a partir da unidade já cadastrada:

- `unidade IN ('kg','g')` → `PESO`
- `unidade IN ('L','ml')` → `VOLUME`
- `unidade = 'un'`        → `UNIDADE`
- demais (NULL etc.)      → `UNIDADE` por default

`PACK` não é inferido automaticamente — produtos multipack precisam ser editados
manualmente após a migração (o comerciante reabre o cadastro e marca como Pack).

## Compatibilidade

- `ddl-auto=update` cria a coluna `tipo_medida` automaticamente em bancos novos.
- Para bancos existentes, rodar `migrar_banco.sql`.
- Produtos sem `tipoMedida` definido (legado) caem no fluxo `UNIDADE` por default
  no frontend, mantendo retrocompatibilidade visual.

## Fora de escopo

- Padronização de categorias (ainda texto livre).
- Sugestão automática de tipo a partir da categoria (Alternativa 4 discutida).
- Validação de coerência (ex.: rejeitar `peso > 1000 kg`).
