# Mudança na Lista de Compras — Vincular a Produtos Cadastrados

## Contexto e descoberta principal

Analisando o código atual, descobri um **descompasso arquitetural**:

### Backend — já pronto e estruturado
- `ListaComprasController` expõe `/api/lista/...` (CRUD completo)
- `ItemLista` referencia `idProduto` (relação real com tabela `produto`)
- `ListaComprasService.otimizar()` calcula o **melhor plano de compras
  distribuído por mercados** com base nos preços cadastrados
- Tudo persistido em MariaDB, com `dataCriacao` e `idUsuario`

### Frontend — não usa nada disso
- `lista-compra.html` salva tudo em `localStorage` como **texto livre**
  (`"Arroz:2\nFeijão:1"`)
- Nenhuma chamada às rotas `/api/lista/...`
- Não há vínculo com `idProduto` → o otimizador não pode rodar
- Lista vive só no navegador, não sincroniza entre dispositivos

**Conclusão**: o problema "aceitar somente produtos cadastrados" não é uma
restrição de validação a ser adicionada — é uma **reescrita do frontend
para usar o backend que já existe**. E isso desbloqueia o otimizador,
que é uma funcionalidade central do projeto hoje órfã.

## Opções analisadas

### Opção A — Reescrever frontend para usar o backend *(Recomendada)*

Substituir o textarea de texto livre por um **seletor de produto com
busca** (autocomplete). Cada item vira `{ idProduto, quantidade }`,
persistido via `POST /api/lista/{id}/item`. A lista inteira fica no
banco, vinculada ao usuário logado.

**Habilita**:
- Otimizador (`/api/lista/{id}/otimizar`) — já implementado, só
  esperando dados estruturados
- Sincronização entre dispositivos (a lista é do usuário, não do browser)
- Comparação de preços por item (já que cada item tem `idProduto`)
- Histórico (`dataCriacao` já existe)

**Custo**:
- Exige login para criar lista (já temos `requireLogin()`)
- Migração de listas antigas em localStorage: descartar (são texto
  livre, não dá pra mapear para produtos com segurança) — exibir aviso
  antes de apagar

### Opção B — Autocomplete só no frontend, mantendo localStorage

Substitui o textarea por input com sugestões da `/api/produtos`,
bloqueando texto livre. Salva `{idProduto, nome, qtd}` em localStorage.

**Prós**: mudança menor, não exige login.
**Contras**: backend continua não usado, otimizador continua morto,
lista não sincroniza, duplica modelo de dados (um no banco, outro no LS).

### Opção C — Híbrido (autocomplete + fallback de texto livre)

Tenta autocomplete; se o usuário insistir em texto livre, aceita com
aviso. **Descartada** — não resolve o problema declarado ("somente
produtos cadastrados") e cria duas formas paralelas de lista.

### Opção D — Bloquear só no backend

Validar em `ListaComprasService.adicionarItem()` que `idProduto` existe.
**Descartada** — o frontend hoje nem chama o backend, então a validação
ficaria inerte.

## Recomendação: Opção A

É a única que:
1. Resolve o problema declarado (lista só aceita produtos cadastrados,
   por construção — não há campo de texto livre)
2. Desbloqueia o otimizador, que é diferencial do MercadoCerto
3. Elimina o descompasso entre o backend bem feito e o frontend
   desconectado dele
4. Permite extensões futuras (compartilhar lista, recorrência, alertas
   de preço) sem retrabalho

## Arquivos a alterar (Opção A)

### Frontend
- **`src/main/resources/static/lista-compra.html`** — reescrever:
  - Substituir textarea por input de busca + dropdown de sugestões
    (datalist ou render custom de produtos da `/api/produtos`)
  - Cada item adicionado vira card com nome, marca, embalagem e qtd
  - Botão "Otimizar" que chama `/api/lista/{id}/otimizar` e mostra o
    plano de compras (mercado → itens → subtotal, economia total)
  - `requireLogin()` no início da página
- **`src/main/resources/static/js/main.js`** — reaproveitar
  `formatarEmbalagem()` na exibição dos itens

### Backend
- **`ListaComprasService.adicionarItem()`** — adicionar guarda
  `produtoRepo.existsById(idProduto)` → retorna 404 se produto não
  existir (defesa em profundidade, mesmo com o frontend só mostrando
  produtos válidos)
- **`ListaComprasController.adicionarItem()`** — diferenciar 404 de
  "lista não encontrada" vs "produto não encontrado" via mensagem

### Banco
- Sem alteração de schema. `id_produto` em `item_lista` já é NOT NULL
  e referencia produtos existentes.

### Migração de listas antigas
- Listas em `localStorage` (chave `mc_listas`) **são descartadas**.
  Aviso na primeira carga: "Suas listas antigas estavam em modo legado
  e foram limpas. Recrie usando os produtos cadastrados."
- Para usuários que quiserem migrar manualmente, mantemos um botão
  "Ver listas antigas" que mostra read-only e permite copiar texto.

## Casos de uso suportados depois da mudança

1. **Criar lista** — usuário logado dá nome, busca produtos, adiciona
   com quantidade.
2. **Marcar item comprado** — checkbox local (UI), opcionalmente
   persistido depois.
3. **Otimizar** — sistema mostra "compre X no mercado A, Y no B; total
   R$ Z, economiza R$ W comparado ao melhor mercado único".
4. **Comparar item** — link direto pro `comparar.html?produto={id}`.
5. **Remover item / excluir lista** — já implementados no backend.

## Fora de escopo desta mudança

- Marcação "comprado" persistente (hoje seria local na UI; persistir
  exige nova coluna `comprado boolean` em `item_lista`).
- Compartilhar lista entre usuários.
- Sugestão automática de produtos por histórico.
- Listas para visitantes (não logados) — exigiria modelo paralelo.

---

# Implementação realizada

A Opção A foi implementada nos arquivos abaixo. Nenhuma alteração de
schema foi necessária — o backend já estava modelado para esse fluxo.

## Backend

### `ListaComprasService.adicionarItem()`
- Adicionada guarda `produtoRepo.existsById(idProduto)` antes de criar
  ou incrementar o `ItemLista`.
- Lança `IllegalArgumentException` se o produto não existir
  (mensagem `"Produto não cadastrado: {id}"`).

### `ListaComprasController.adicionarItem()`
- Tipo de retorno relaxado para `ResponseEntity<?>` para permitir
  body de erro.
- `IllegalArgumentException` → `400 Bad Request` com a mensagem.
- `RuntimeException` (lista não encontrada) → `404 Not Found`.

Defesa em profundidade: mesmo com o frontend só mostrando produtos
válidos no autocomplete, uma requisição direta (ex: Postman) com
`idProduto` inexistente é rejeitada.

## Frontend

### `lista-compra.html` (reescrito por completo)

Substituiu o `localStorage` por integração total com a API:

**Layout em duas colunas:**
- Coluna esquerda: criar lista nova + lista de "Minhas listas" do
  usuário logado.
- Coluna direita: painel da lista ativa com busca, itens e otimizador.

**Funcionalidades:**

| Ação | Rota usada | Detalhe |
|---|---|---|
| Listar minhas listas | `GET /api/lista/usuario/{id}` | Carrega no início |
| Abrir lista | `GET /api/lista/{id}` | Carrega itens com `produtoMap` para nome/marca/embalagem |
| Criar lista | `POST /api/lista` | Body `{idUsuario, nomeLista}` |
| Excluir lista | `DELETE /api/lista/{id}` | Confirma antes |
| Buscar produto | `GET /api/produtos` (in-memory) | Filtro local por nome/marca/código de barras, máx. 8 resultados |
| Adicionar item | `POST /api/lista/{id}/item` | Sempre `quantidade: 1` (incremento server-side) |
| Remover item | `DELETE /api/lista/{id}/item/{idProduto}` | Botão em cada linha |
| Otimizar | `GET /api/lista/{id}/otimizar` | Botão "✨ Otimizar compras" |

**Autocomplete (`#busca-produto`):**
- Renderiza dropdown custom com nome, marca, embalagem (via
  `formatarEmbalagem`) e preço.
- Filtra produtos já adicionados à lista (evita duplicar).
- Navegação por teclado: `↓`, `↑`, `Enter`, `Esc`.
- Mensagem amigável quando nada bate, com link para `cadastro-produto.html`.
- **Não há campo de texto livre** — adicionar item só pelo dropdown,
  portanto o vínculo com `idProduto` é por construção.

**Painel do otimizador:**
- Box verde no topo: total otimizado + economia versus o melhor
  mercado único.
- Um card por mercado, com itens (nome × quantidade) e subtotal.
- Tratamento de "lista sem preços cadastrados" com aviso amarelo.
- Tratamento de "comprar tudo num único mercado custaria o mesmo".

**Migração de listas antigas:**
- Na primeira carga, se `localStorage.mc_listas` existir e não estiver
  vazio, mostra toast de aviso e grava `mc_listas_avisado=1` para
  não repetir.
- Não tenta importar — os dados antigos eram texto livre, sem como
  mapear para `idProduto` com segurança.

**Login obrigatório:**
- `requireLogin()` no início do script. Sem usuário logado, redireciona
  para `login.html`.

## Arquivos modificados

```
src/main/java/com/mercadocerto/service/ListaComprasService.java
src/main/java/com/mercadocerto/controller/ListaComprasController.java
src/main/resources/static/lista-compra.html  (reescrita)
```

## Não alterado (intencionalmente)

- `ItemLista`, `ListaCompras`, `ListaComprasRepository` — já estavam
  no formato necessário.
- `migrar_banco.sql` — nenhuma coluna nova.
- Tabela `item_lista` no banco — `id_produto NOT NULL` já existia.

## Como validar manualmente

1. Subir o backend (`mvnw spring-boot:run`).
2. Logar como `USUARIO`.
3. Abrir `lista-compra.html`.
4. Criar uma lista nova ("Teste").
5. Buscar um produto cadastrado — deve aparecer no dropdown com
   nome/marca/embalagem.
6. Adicionar 2-3 produtos. Cada um deve aparecer como linha no painel.
7. Tentar uma busca por algo inexistente — deve mostrar mensagem
   amigável + link para cadastrar.
8. Clicar em "✨ Otimizar compras" — deve mostrar o plano por mercado
   se houver preços, ou aviso se não houver.
9. Excluir um item; depois excluir a lista inteira.

## Próximos passos sugeridos (fora desta entrega)

- Persistir o estado "comprado" de cada item.
- Permitir editar a quantidade de um item já adicionado (hoje o input
  está disabled — incremento se faz adicionando o mesmo produto de
  novo, que soma server-side).
- Botão "Comparar este item" em cada linha → link para `comparar.html`.
- Exportar plano otimizado como PDF/imagem para levar ao mercado.
