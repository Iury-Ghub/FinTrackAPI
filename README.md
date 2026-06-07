# FinTrack (atividadeCapacita)

Resumo
------
FinTrack é uma aplicação desktop Java (JavaFX) para controle simples de transações financeiras. Projeto em Java 21 usando JavaFX para interface e SQLite (fintrack.db) para persistência. O código principal está em src/main/java/org/example; a interface FXML em src/main/resources/org/example/fxml.

Índice
------
- Visão geral
- Tecnologias
- Estrutura do projeto
- Banco de dados
- Como compilar e executar
- Testes
- Ordenação de transações (do mais recente para o mais antigo)
- Observações e contribuições

Visão geral
----------
Aplicação GUI (desktop) que permite adicionar e remover transações. Cada transação tem: id, descrição, valor, tipo (RECEITA/DESPESA) e data. A aplicação cria a tabela se necessário e armazena dados em fintrack.db por padrão.

Tecnologias
-----------
- Java 21
- JavaFX 21 (FXML)
- Maven
- SQLite (org.xerial:sqlite-jdbc)
- JUnit 5 (testes)

Estrutura do projeto
--------------------
- src/main/java/org/example
  - Main.java — inicializa a UI e garante criação da tabela
  - Transacao.java — modelo de domínio
  - TransacaoDAO.java — acesso a dados (SQLite)
  - Conexao.java — entrega Connection JDBC (usa JDBC_URL system property ou env var)
- src/main/resources/org/example/fxml/main.fxml — layout JavaFX
- src/main/resources/org/example/css/style.css — estilos
- fintrack.db — arquivo SQLite (gerado/uso local)
- pom.xml — build

Banco de dados
--------------
O DAO cria a tabela com o SQL:

CREATE TABLE IF NOT EXISTS transacoes (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  descricao VARCHAR(100),
  valor DECIMAL(10,2),
  tipo VARCHAR(10),
  data DATE
);

Por padrão o arquivo usado é fintrack.db no diretório do projeto (Conexao.DEFAULT_URL = jdbc:sqlite:fintrack.db). Para apontar outro DB, exporte a variável de ambiente JDBC_URL ou defina a propriedade do sistema (-DJDBC_URL=jdbc:sqlite:/path/other.db).

Como compilar e executar
-----------------------
Requisitos: JDK 21 e Maven instalados.

1) Compilar:
   mvn clean package

2) Executar (modo desenvolvimento com plugin JavaFX):
   mvn javafx:run

3) Executável jar (se necessário):
   mvn package
   java -jar target/atividadeCapacita-1.0-SNAPSHOT.jar

(Nota: para rodar jar com JavaFX é comum usar o plugin ou empacotar dependências; o projeto já tem javafx-maven-plugin configurado no pom.xml.)

Testes
------
Executar suite de testes com:

mvn test

Ordenação de transações (do mais recente para o mais antigo)
-----------------------------------------------------------
Atualmente TransacaoDAO.listarTodos() usa:

String sql = "SELECT * FROM transacoes";

Para listar do mais recente para o menos recente, ajustar para ordenar pela coluna data em ordem decrescente:

String sql = "SELECT * FROM transacoes ORDER BY data DESC";

Exemplo (arquivo src/main/java/org/example/TransacaoDAO.java — método listarTodos):
- localizar a linha que define sql e substituir por: "SELECT * FROM transacoes ORDER BY data DESC";

Por que isso é necessário
- SQLite (tipo DATE ou TIMESTAMP) pode ser ordenado diretamente por coluna de data; aqui a coluna é DATE e armazena LocalDate. ORDER BY data DESC garantirá que a interface exiba transações do mais recente para o mais antigo.

Query SQL direta (para uso em DB browser):

SELECT * FROM transacoes ORDER BY data DESC;

Observações e contribuições
--------------------------
- Para alterar o comportamento de ordenação na UI, aplicar a mudança no DAO ou ordenar na camada de apresentação (MainController.refreshTable) antes de enviar para TableView.
- Se desejar que a data inclua hora, alterar o tipo para TIMESTAMP e adaptar Transacao/DAO para usar LocalDateTime.
- Pull requests: abrir issue descrevendo a alteração, criar branch com nome claro e submeter PR com testes quando aplicável.

Contato
-------
- Autor: (preencha seu nome)
- Projeto gerado: atividadeCapacita / FinTrack

Licença
-------
Escolha uma licença (ex: MIT) e adicione LICENSE.md

---

Posso aplicar a alteração em TransacaoDAO.listarTodos() para você (commit e testar). Deseja que eu faça isso agora?