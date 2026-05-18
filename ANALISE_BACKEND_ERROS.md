# Analise de Erros - Backend MercadoCerto

**Projeto:** MercadoCerto - API Spring Boot
**Data:** 30/03/2026
**Stack:** Spring Boot 3.3.5 | Java 17 | MariaDB | Spring Security | JPA/Hibernate

---

## Resumo Executivo

Foram identificados **29 problemas** no backend, distribuidos por severidade:

| Severidade | Quantidade | Categorias |
|------------|------------|------------|
| CRITICO | 5 | Seguranca, Logica de negocio |
| ALTO | 7 | NullPointer, Perda de dados, Cast incorreto |
| MEDIO | 10 | Validacao, Design de API, Configuracao |
| BAIXO | 7 | Qualidade de codigo, Eficiencia |

---

## 1. VULNERABILIDADES DE SEGURANCA (CRITICO)

### 1.1 Seguranca completamente desabilitada

**Arquivo:** `config/SecurityConfig.java`

```java
// PROBLEMA: CSRF desabilitado + todos os endpoints publicos
csrf(csrf -> csrf.disable())
// ...
anyRequest().permitAll()
```

**Impacto:** Toda a API esta aberta ao publico sem nenhuma autenticacao. Qualquer pessoa pode acessar, criar, modificar e deletar dados.

**Correcao sugerida:**
```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable()) // OK para API REST com tokens
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/usuarios/register", "/api/usuarios/login").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/produtos/**", "/api/mercados/**").permitAll()
            .anyRequest().authenticated()
        )
        .sessionManagement(session ->
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
}
```

---

### 1.2 Login retorna entidade completa do usuario

**Arquivo:** `controller/UsuarioController.java`

```java
// PROBLEMA: Retorna objeto Usuario inteiro, incluindo senha hash
return ResponseEntity.ok(user.get());
```

**Impacto:** O hash da senha e outros dados sensiveis (CPF, CNPJ) sao enviados ao cliente.

**Correcao sugerida:**
```java
// Usar o LoginResponseDTO que ja existe no projeto
return ResponseEntity.ok(new LoginResponseDTO(
    user.get().getIdUsuario(),
    user.get().getNomeUsuario(),
    user.get().getEmail(),
    user.get().getLogin(),
    user.get().getTipoConta().name()
));
```

---

### 1.3 Endpoints publicos expoe dados de usuarios

**Arquivo:** `controller/UsuarioController.java`

```java
@GetMapping
public List<Usuario> listar() { ... }

@GetMapping("/login/{login}")
public Optional<Usuario> buscarPorLogin(@PathVariable String login) { ... }

@GetMapping("/email/{email}")
public Optional<Usuario> buscarPorEmail(@PathVariable String email) { ... }
```

**Impacto:** Qualquer pessoa pode enumerar todos os usuarios do sistema, descobrir emails e logins. Violacao de privacidade.

**Correcao sugerida:** Remover o endpoint `listarTodos` ou protege-lo com autenticacao de admin. Os endpoints de busca devem ser internos.

---

### 1.4 Credenciais do banco hardcoded

**Arquivo:** `application.properties`

```properties
spring.datasource.username=root
spring.datasource.password=root
```

**Impacto:** Se o repositorio for exposto, o banco de dados fica comprometido.

**Correcao sugerida:** Usar variaveis de ambiente:
```properties
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASS}
```

---

### 1.5 CORS permissivo demais

**Arquivo:** `config/WebConfig.java`

```java
.allowedMethods("*")
.allowedHeaders("*")
```

**Impacto:** Qualquer metodo HTTP e header sao aceitos de origens configuradas. Em producao, deve ser restrito.

---

## 2. BUGS DE LOGICA DE NEGOCIO (CRITICO/ALTO)

### 2.1 Otimizador de lista sempre mostra economia ZERO

**Arquivo:** `service/ListaComprasService.java`

```java
return new ResultadoOtimizadorDTO(
    idLista, lista.getNomeLista(), planos,
    totalOtimizado,
    totalOtimizado,    // BUG: deveria ser o total do mercado mais barato
    BigDecimal.ZERO    // BUG: economia sempre zero
);
```

**Impacto:** O recurso principal do app (otimizacao de compras) nunca mostra quanto o usuario economiza. Isso anula o proposito do produto.

**Correcao sugerida:**
```java
// Calcular o custo em cada mercado individualmente
BigDecimal totalMercadoMaisBarato = calcularMelhorMercadoUnico(lista);
BigDecimal economia = totalMercadoMaisBarato.subtract(totalOtimizado);

return new ResultadoOtimizadorDTO(
    idLista, lista.getNomeLista(), planos,
    totalOtimizado, totalMercadoMaisBarato, economia
);
```

---

### 2.2 Preco do produto usa Double em vez de BigDecimal

**Arquivo:** `model/Produto.java`

```java
private Double preco; // ERRADO: erros de arredondamento com dinheiro
```

**Impacto:** Calculos monetarios com `Double` causam erros de precisao de ponto flutuante (ex: 0.1 + 0.2 = 0.30000000000000004).

**Correcao sugerida:**
```java
@Column(name = "preco", precision = 10, scale = 2)
private BigDecimal preco;
```

---

## 3. RISCOS DE NULLPOINTEREXCEPTION (ALTO)

### 3.1 Cast inseguro no PrecoController

**Arquivo:** `controller/PrecoController.java`

```java
Integer idProduto = (Integer) body.get("idProduto"); // ClassCastException se nao for Integer
Integer idMercado = (Integer) body.get("idMercado"); // ClassCastException se nao for Integer
Object valorObj = body.get("valor");

if (idProduto == null || idMercado == null || valorObj == null) {
    return ResponseEntity.badRequest().build(); // Validacao de null existe
}
```

**Nota:** O codigo ja valida campos nulos, mas o cast `(Integer)` ainda pode gerar `ClassCastException` se o JSON enviar um tipo diferente (ex: `"idProduto": "abc"`).

**Impacto:** Se o cliente enviar JSON com tipos incorretos, o servidor retorna 500 Internal Server Error antes de atingir a validacao de null.

**Correcao sugerida:** Criar um DTO tipado:
```java
public record PublicarPrecoDTO(
    @NotNull Integer idProduto,
    @NotNull Integer idMercado,
    @NotNull @Positive BigDecimal valor
) {}
```

---

### 3.2 Cast inseguro no ListaComprasController

**Arquivo:** `controller/ListaComprasController.java`

```java
Integer idUsuario = (Integer) body.get("idUsuario"); // ClassCastException
String nomeLista = (String) body.get("nomeLista");    // ClassCastException
```

**Impacto:** Mesmo problema - 500 error com JSON invalido.

**Correcao sugerida:** Substituir `Map<String, Object>` por DTOs tipados com `@Valid`.

---

### 3.3 Dashboard com notas nulas (CORRIGIDO - NAO E BUG)

**Arquivo:** `controller/DashboardController.java`

```java
double media = entry.getValue().stream()
    .filter(a -> a.getNota() != null)  // Filtra nulls ANTES do mapToInt
    .mapToInt(Avaliacao::getNota)
    .average().orElse(0.0);
```

**Nota:** O `.filter(a -> a.getNota() != null)` ja remove avaliacoes com nota nula antes do `mapToInt`, portanto nao ha risco de NPE por unboxing. Este trecho esta correto.

---

## 4. PROBLEMAS DE JPA/HIBERNATE (MEDIO)

### 4.1 Campo @Transient usado na logica de negocio

**Arquivo:** `model/Produto.java`

```java
@Transient
private Integer idMercado; // Nao e persistido, mas usado no service
```

**Impacto:** `idMercado` nao e salvo no banco e nao e validado. Se o mercado nao existir, o produto e salvo sem vinculo correto.

**Correcao sugerida:** Validar existencia do mercado no `ProdutoService.salvarProduto()`:
```java
Mercado mercado = mercadoRepo.findById(idMercado)
    .orElseThrow(() -> new IllegalArgumentException("Mercado nao encontrado"));
```

---

### 4.2 Avaliacao sem validacao de nota no JPA

**Arquivo:** `model/Avaliacao.java`

```java
private Integer nota; // Sem @Min(1) @Max(5)
```

**Impacto:** O JPA permite salvar notas fora do range 1-5. A validacao so acontece no banco via CHECK constraint, gerando uma exception pouco amigavel.

**Correcao sugerida:**
```java
@Min(1) @Max(5)
@Column(name = "nota")
private Integer nota;
```

---

### 4.3 Mercado/Produto sem validacao de coordenadas

**Arquivo:** `model/Mercado.java` e `model/Produto.java`

```java
private Double latitude;  // Sem @Min(-90) @Max(90)
private Double longitude; // Sem @Min(-180) @Max(180)
```

**Impacto:** Coordenadas invalidas corrompem a busca por proximidade (Haversine).

---

### 4.4 Foreign Keys sem ON DELETE CASCADE

**Arquivo:** `mercdadocertocreate.sql`

```sql
CONSTRAINT preco_ibfk_1 FOREIGN KEY (id_produto) REFERENCES produto (id_produto)
-- Sem ON DELETE CASCADE ou ON DELETE RESTRICT
```

**Impacto:** Deletar um produto/mercado falha silenciosamente ou gera erro de FK. O `MercadoService.remover()` faz `deleteById()` que vai falhar se existirem precos vinculados.

---

## 5. PROBLEMAS DE CONCORRENCIA (MEDIO)

### 5.1 Race condition ao adicionar item na lista

**Arquivo:** `service/ListaComprasService.java`

```java
// Check-then-act nao atomico
Optional<ItemLista> existente = lista.getItens().stream()
    .filter(i -> i.getIdProduto().equals(idProduto))
    .findFirst();

if (existente.isPresent()) {
    existente.get().setQuantidade(existente.get().getQuantidade() + quantidade);
} else {
    // Adiciona novo item
}
```

**Impacto:** Duas requisicoes simultaneas podem ambas encontrar o item como inexistente e criar duplicatas.

**Correcao sugerida:** Usar `@Transactional` com `SERIALIZABLE` isolation ou constraint UNIQUE no banco:
```sql
ALTER TABLE item_lista ADD UNIQUE KEY uk_lista_produto (id_lista, id_produto);
```

---

## 6. PROBLEMAS DE CONFIGURACAO (MEDIO)

### 6.1 DDL Auto em modo "update"

**Arquivo:** `application.properties`

```properties
spring.jpa.hibernate.ddl-auto=update
```

**Impacto:** Em producao, o Hibernate pode alterar o schema do banco automaticamente de forma imprevisivel. Pode corromper dados.

**Correcao sugerida:** Usar `validate` em producao e ferramentas de migracao (Flyway/Liquibase).

---

### 6.2 Show SQL ativado

**Arquivo:** `application.properties`

```properties
spring.jpa.show-sql=true
```

**Impacto:** Impacto de performance em producao e dados sensiveis nos logs.

---

### 6.3 Sem autenticacao JWT/Session

**Impacto:** O login valida credenciais mas nao retorna token. Nao ha como manter sessao autenticada. Cada requisicao subsequente nao sabe quem e o usuario.

**Correcao sugerida:** Implementar JWT:

1. Adicionar dependencia `jjwt` ao pom.xml
2. Criar `JwtService` para gerar/validar tokens
3. Criar `JwtAuthenticationFilter`
4. Retornar token no login
5. Validar token em cada requisicao protegida

---

## 7. VALIDACOES AUSENTES (MEDIO/BAIXO)

| Arquivo | Campo | Problema |
|---------|-------|----------|
| UsuarioService | email | Sem validacao de formato |
| ProdutoService | codigoBarras | Pode ser null/vazio |
| MercadoService | nomeMercado | Sem validacao de tamanho |
| PrecoController | valor | Pode ser negativo ou zero |
| AvaliacaoController | comentario | Sem limite de tamanho |

**Correcao geral:** Adicionar Bean Validation nos DTOs:
```java
public record CadastroProdutoDTO(
    @NotBlank String nomeProduto,
    @NotBlank String marca,
    @NotBlank String codigoBarras,
    @Positive BigDecimal preco
) {}
```

---

## 8. MELHORIAS DE QUALIDADE DE CODIGO (BAIXO)

### 8.1 Uso de Map generico nos controllers

O `PrecoController.publicarPreco` e o `ListaComprasController.criar` usam `Map<String, Object>` como parametro, o que elimina type safety e documentacao automatica da API. Nota: o endpoint `adicionarItem` do `ListaComprasController` ja utiliza o DTO tipado `ItemListaRequestDTO`.

### 8.2 Excecoes genericas no GlobalExceptionHandler

`RuntimeException` e capturada globalmente, o que pode mascarar bugs reais. Recomenda-se criar excecoes customizadas (ex: `RecursoNaoEncontradoException`, `ValidacaoException`).

### 8.3 Falta de paginacao

Os endpoints que listam todos os registros (`listarTodos`) nao tem paginacao. Com o crescimento dos dados, essas queries vao ficar cada vez mais lentas.

**Correcao sugerida:**
```java
@GetMapping
public Page<Produto> listarTodos(@RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "20") int size) {
    return repo.findAll(PageRequest.of(page, size));
}
```

---

## Prioridades de Correcao

**Fase 1 - Urgente (antes de ir para producao):**
1. Implementar autenticacao JWT
2. Proteger endpoints sensiveis
3. Corrigir calculo do otimizador (economia sempre zero)
4. Remover credenciais hardcoded
5. Substituir `Map<String, Object>` por DTOs tipados

**Fase 2 - Importante:**
6. Trocar `Double` por `BigDecimal` no Produto
7. Adicionar validacoes Bean Validation
8. Corrigir race condition na lista de compras
9. Mudar `ddl-auto` para `validate`
10. Adicionar ON DELETE CASCADE/RESTRICT nas FKs

**Fase 3 - Melhoria continua:**
11. Implementar paginacao
12. Criar excecoes customizadas
13. Adicionar testes unitarios e de integracao
14. Configurar profiles (dev/staging/prod)
15. Adicionar logging estruturado
