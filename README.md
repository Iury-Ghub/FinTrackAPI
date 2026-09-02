# FinTrack API

API RESTful para gerenciamento de finanças pessoais, desenvolvida como projeto do módulo avançado do curso **Capacita iRede**. A aplicação evoluiu de um protótipo desktop (JavaFX + JDBC) para um back-end multiusuário, stateless e seguro, construído com Spring Boot.

## Sobre o projeto

O FinTrack permite que cada usuário se cadastre, faça login e gerencie suas próprias transações financeiras (receitas e despesas) organizadas por categorias. A API garante, via autenticação JWT, que um usuário nunca tenha acesso aos dados de outro.

## Tecnologias utilizadas

- **Java 21**
- **Spring Boot 3.2.4**
  - Spring Web (API REST)
  - Spring Data JPA (Hibernate)
  - Spring Security + JWT (`jjwt`)
- **PostgreSQL** (banco de dados principal)
- **H2 Database** (banco em memória usado nos testes)
- **springdoc-openapi** (documentação Swagger/OpenAPI)
- **Lombok**
- **Maven**
- **Docker / Docker Compose**

## Funcionalidades

- Cadastro e login de usuários com senha criptografada (BCrypt) e emissão de token JWT.
- CRUD completo de **transações** (receitas e despesas) e **categorias**.
- Isolamento de dados por usuário: cada usuário só acessa suas próprias transações e categorias.
- Cálculo de saldo (receitas − despesas) via consulta agregada no banco.
- Tratamento centralizado de erros com respostas HTTP semânticas (`404`, `401`, etc.).
- Documentação interativa da API via Swagger UI.

## Configuração

A aplicação lê a configuração de conexão e segurança por variáveis de ambiente, com valores padrão para desenvolvimento local (ver `src/main/resources/application.properties`):

| Variável | Descrição | Padrão |
|---|---|---|
| `DB_URL` | URL JDBC do PostgreSQL | `jdbc:postgresql://localhost:5432/app_financeiro` |
| `DB_USERNAME` | Usuário do banco | `postgres` |
| `DB_PASSWORD` | Senha do banco | `123456` |
| `JWT_SECRET` | Chave usada para assinar os tokens JWT | chave de desenvolvimento embutida |

Para produção, defina essas variáveis com valores próprios em vez de usar os padrões.

## Como executar

### Opção A — Docker (recomendado)

Não exige Java, Maven nem PostgreSQL instalados na máquina — só o [Docker Desktop](https://www.docker.com/products/docker-desktop/).

1. Clone o repositório e entre na pasta do projeto.
2. Suba a aplicação e o banco de dados juntos:
   ```bash
   docker compose up --build
   ```
3. A API estará disponível em `http://localhost:8080`.

Isso sobe dois containers: `fintrack-postgres` (banco de dados, com os dados persistidos em um volume) e `fintrack-app` (a API). Para parar, `Ctrl+C` ou `docker compose down`.

### Opção B — Execução local

**Pré-requisitos:**
- [JDK 21](https://adoptium.net/)
- [Maven](https://maven.apache.org/) (ou usar o wrapper da IDE)
- [PostgreSQL](https://www.postgresql.org/) rodando localmente

**Passos:**

1. Crie o banco de dados no PostgreSQL:
   ```sql
   CREATE DATABASE app_financeiro;
   ```
2. Clone o repositório e entre na pasta do projeto.
3. Se o usuário/senha do seu PostgreSQL forem diferentes dos padrões (`postgres`/`123456`), defina as variáveis de ambiente `DB_USERNAME`/`DB_PASSWORD` antes de rodar.
4. Rode a aplicação:
   ```bash
   mvn spring-boot:run
   ```
5. A API estará disponível em `http://localhost:8080`.

## Documentação da API

Com a aplicação em execução, acesse:

```
http://localhost:8080/swagger-ui.html
```

## Autenticação

Todos os endpoints, exceto os de `/api/auth/**`, exigem um token JWT.

1. Registre-se ou faça login (`POST /api/auth/register` ou `POST /api/auth/login`) para obter um token.
2. Envie o token nas requisições seguintes no header:
   ```
   Authorization: Bearer <seu-token>
   ```

## Principais endpoints

| Método | Rota | Descrição | Autenticação |
|---|---|---|---|
| POST | `/api/auth/register` | Registra um novo usuário | Não |
| POST | `/api/auth/login` | Autentica e retorna um token | Não |
| GET | `/api/v1/transacoes` | Lista as transações do usuário logado | Sim |
| POST | `/api/v1/transacoes` | Cria uma nova transação | Sim |
| PUT | `/api/v1/transacoes/{id}` | Atualiza uma transação | Sim |
| DELETE | `/api/v1/transacoes/{id}` | Remove uma transação | Sim |
| GET | `/api/v1/transacoes/saldo` | Consulta o saldo do usuário logado | Sim |
| GET | `/api/v1/categorias` | Lista as categorias do usuário logado | Sim |
| POST | `/api/v1/categorias` | Cria uma nova categoria | Sim |
| PUT | `/api/v1/categorias/{id}` | Atualiza uma categoria | Sim |
| DELETE | `/api/v1/categorias/{id}` | Remove uma categoria | Sim |

## Executando os testes

```bash
mvn test
```

Os testes utilizam H2 em memória e não afetam o banco de desenvolvimento.

## Estrutura do projeto

```
.
├── Dockerfile             # Build multi-stage da aplicação (Maven -> JRE)
├── docker-compose.yml      # Orquestra a aplicação + PostgreSQL
└── src/main/java/org/example
    ├── config/              # Configuração de segurança (SecurityConfig, filtro JWT)
    ├── controller/          # Endpoints REST
    ├── dto/                 # Objetos de transferência de dados
    ├── exception/           # Exceções customizadas e handler global
    ├── model/               # Entidades JPA
    ├── repository/          # Interfaces Spring Data JPA
    └── service/             # Regras de negócio
```

## Autor

José Iury Vieira Costa
