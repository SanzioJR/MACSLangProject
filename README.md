# Compilador MACSLang

Este projeto implementa um compilador para a linguagem de programação MACSLang, desenvolvido como trabalho para a disciplina de Compiladores.

## Descrição da Linguagem MACSLang

MACSLang é uma linguagem de programação imperativa e estruturada com as seguintes características:

### Características Principais
- Tipagem estática
- Sintaxe simplificada
- Controle de fluxo com condicionais e laços
- Operações básicas de entrada e saída
- Suporte a funções

### Tipos de Dados
- `int`: Números inteiros
- `float`: Números de ponto flutuante
- `char`: Caracteres
- `bool`: Valores booleanos (true ou false)
- `string`: Cadeias de caracteres

### Sintaxe Básica

#### Declaração de Variáveis
````
var : = ;
````

#### Entrada e Saída
````
print(<expressão>);
// Exibe no console input(<variável>);
// Lê valor do usuário
````

#### Estruturas Condicionais
````
if (<condição>) {
// bloco de código 
} else {
// bloco alternativo 
}
````

#### Laços de Repetição
````
while (<condição>) {
// bloco de código 
}
````
````
for (<inicialização>; <condição>; ){
// bloco de código
}
````

#### Funções
````
func (): <tipo_retorno> {
// bloco de código return;
}
````

### Operadores
- Aritméticos: `+`, `-`, `*`, `/`, `%`
- Relacionais: `==`, `!=`, `<`, `<=`, `>`, `>=`
- Lógicos: `&&` (and), `||` (or), `!` (not)
- Atribuição: `=`
  
## Arquitetura do Compilador
O compilador MACSLang é dividido em quatro fases principais:

### 1. Analisador Léxico
Implementado na classe Lexer, converte o código-fonte em tokens. 
Esta fase é responsável por:

- Identificar tokens como palavras-chave, identificadores, literais e operadores
- Ignorar espaços em branco e comentários
- Reportar erros léxicos (caracteres inválidos)

### 2. Analisador Sintático
Implementado na classe Parser, constrói a árvore sintática abstrata (AST) a partir dos tokens. 
Esta fase:

- Verifica se a sequência de tokens forma estruturas válidas da linguagem
- Constrói uma representação hierárquica do programa (AST)
- Reporta erros sintáticos (estruturas inválidas)
### 3. Analisador Semântico
Implementado na classe SemanticAnalyzer, verifica a coerência semântica do programa.
Esta fase:

- Verifica tipos e compatibilidade de operações
- Gerencia escopos e tabela de símbolos
- Verifica se variáveis são declaradas antes de serem usadas
- Verifica se funções retornam valores apropriados
- Reporta erros semânticos (uso incorreto da linguagem)
### 4. Gerador de Código
Implementado na classe CodeGenerator, traduz a AST para código assembly x86. 
Esta fase:

- Gera instruções assembly para cada nó da AST
- Gerencia alocação de memória e registradores
- Implementa chamadas de função e passagem de parâmetros
- Produz um arquivo assembly executável

## Estrutura do Projeto
```
MACSLangCompiler/
├── src/
│   ├── com/
│   │   ├── macslang/
│   │   │   ├── Main.java                     # Ponto de entrada do compilador
│   │   │   ├── lexer/
│   │   │   │   ├── Lexer.java                # Analisador léxico
│   │   │   │   ├── Token.java                # Representa tokens
│   │   │   │   ├── TokenType.java            # Tipos de tokens
│   │   │   │   ├── LexerException.java       # Exceções do analisador léxico
│   │   │   ├── parser/
│   │   │   │   ├── Parser.java               # Analisador sintático
│   │   │   │   ├── ParserException.java      # Exceções do analisador sintático
│   │   │   │   ├── ast/                      # Árvore Sintática Abstrata (AST)
│   │   │   │   │   ├── Node.java             # Interface base para nós da AST
│   │   │   │   │   ├── NodeVisitor.java      # Padrão visitor para AST
│   │   │   │   │   ├── Program.java          # Nó raiz do programa
│   │   │   │   │   ├── expression/           # Expressões
│   │   │   │   │   ├── statement/            # Declarações
│   │   │   ├── semantic/
│   │   │   │   ├── SemanticAnalyzer.java     # Analisador semântico
│   │   │   │   ├── SymbolTable.java          # Tabela de símbolos
│   │   │   │   ├── Symbol.java               # Representa símbolos
│   │   │   │   ├── Type.java                 # Tipos da linguagem
│   │   │   │   ├── SemanticException.java    # Exceções semânticas
│   │   │   ├── codegen/
│   │   │   │   ├── CodeGenerator.java        # Gerador de código
│   │   │   │   ├── X86Instruction.java       # Representa instruções x86
│   │   │   │   ├── AssemblyWriter.java       # Escritor de código assembly
│   │   │   ├── util/
│   │   │   │   ├── FileHandler.java          # Manipulação de arquivos
│   │   │   │   ├── ErrorReporter.java        # Relatório de erros
├── examples/                                 # Exemplos de programas MACSLang
````

### Como Compilar e Executar

#### Pré-requisitos
- Java Development Kit (JDK) 21 ou superior
- Eclipse IDE (recomendado)

#### Compilação
Clone este repositório
Abra o projeto no Eclipse ou compile usando javac:
javac -d bin src/com/macslang/*.java src/com/macslang/*/*.java src/com/macslang/*/*/*.java

### Execução
#### Para compilar um programa MACSLang:

java -cp bin com.macslang.Main <arquivo.macs>
#### Exemplo:

java -cp bin com.macslang.Main examples/factorial.macs
Isso gerará um arquivo assembly (.asm) que pode ser montado e executado em um sistema x86.

### Exemplos
#### Fatorial
````
// factorial.macs
func fatorial(n: int): int {
 var resultado: int = 1;
 for (var i: int = 1; i <= n; i = i + 1) {
     resultado = resultado * i;
 }
 return resultado;
}

print("Digite um número para calcular o fatorial:");
var numero: int;
input(numero);

var fat: int = fatorial(numero);
print("O fatorial de " + numero + " é " + fat);
````
#### Verificador de Paridade
````
// paridade.macs
func ehPar(n: int): bool {
 return (n % 2) == 0;
}

print("Digite um número:");
var num: int;
input(num);

if (ehPar(num)) {
 print(num + " é par");
} else {
 print(num + " é ímpar");
}
````
#### Verificador de Números Primos
````
// primo.macs
func ehPrimo(n: int): bool {
 if (n <= 1) {
     return false;
 }
 
 var i: int = 2;
 while (i * i <= n) {
     if (n % i == 0) {
         return false;
     }
     i = i + 1;
 }
 
 return true;
}

print("Digite um número:");
var num: int;
input(num);

if (ehPrimo(num)) {
 print(num + " é primo");
} else {
 print(num + " não é primo");
}
````
## Implementação Detalhada
### Analisador Léxico
O analisador léxico utiliza um autômato finito para reconhecer tokens. Ele lê o código-fonte caractere por caractere e os agrupa em tokens significativos. Cada token contém informações sobre seu tipo, valor literal, e posição no código (linha e coluna).

### Analisador Sintático
O analisador sintático implementa um parser descendente recursivo que segue a gramática da linguagem MACSLang. Ele constrói a AST de cima para baixo, começando com as estruturas de mais alto nível (como declarações de funções e variáveis) e descendo para expressões e termos.

### Analisador Semântico
O analisador semântico utiliza o padrão visitor para percorrer a AST e verificar a coerência semântica do programa. Ele mantém uma tabela de símbolos para rastrear variáveis e funções declaradas, seus tipos e escopos.

### Gerador de Código
O gerador de código também utiliza o padrão visitor para percorrer a AST e gerar código assembly x86 correspondente. Ele implementa estratégias para:

- Alocação de variáveis na pilha
- Avaliação de expressões
- Implementação de estruturas de controle (if, while, for)
- Chamadas de função e retorno de valores

### Extensões Implementadas
- Além dos requisitos básicos, este compilador implementa as seguintes extensões:

- Operador de módulo (%): Adicionamos suporte ao operador de módulo, que é útil para muitas operações matemáticas como verificação de paridade e cálculos de resto.

- Mensagens de erro detalhadas: O compilador fornece mensagens de erro detalhadas com informações sobre a linha e coluna onde o erro ocorreu, facilitando a depuração de programas MACSLang.

Autor:
  Sânzio de Jesus Ribeiro
