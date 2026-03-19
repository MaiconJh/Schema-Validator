# Schema Validator

<div align="center">

![Java](https://img.shields.io/badge/Java-21-blue?style=for-the-badge&logo=java)
![Minecraft](https://img.shields.io/badge/Minecraft-1.21-green?style=for-the-badge&logo=minecraft)
![Skript](https://img.shields.io/badge/Skript-2.14-orange?style=for-the-badge)
![License](https://img.shields.io/badge/License-MIT-yellow?style=for-the-badge)

Um plugin Minecraft (Paper/Spigot) que permite validar dados YAML/JSON usando schemas, integrado com Skript.

</div>

---

## 📋 Descrição

O **Schema Validator** é um addon para Skript que fornece validação de dados poderosa usando schemas JSON Schema-like. Permite que você defina estruturas de dados complexas e valide arquivos de configuração YAML ou JSON contra elas.

### ✨ Características

- 📄 Suporte a **YAML** e **JSON**
- 🔍 Validação de esquemas complexos com **patternProperties**
- ⚡ Integração nativa com **Skript**
- 🎯 Validação de objetos, arrays, strings, números e booleanos
- 📊 Sistema de erros detalhado
- 🔄 Suporte a referências de schema (`$ref`)

---

## 🚀 Instalação

### Pré-requisitos

- [Paper](https://papermc.io/) ou [Spigot](https://www.spigotmc.org/) 1.21+
- [Skript](https://github.com/SkriptLang/Skript) 2.14+
- Java 21

### Passos de Instalação

1. **Baixe o plugin**
   - Compilou o projeto: `gradlew build`
   - Ou baixe a versão mais recente em [Releases](../../releases)

2. **Instale no servidor**
   ```
   /plugins/
   ├── Schema-Validator-0.1.0-SNAPSHOT.jar
   ├── Skript.jar
   └── [outros plugins]
   ```

3. **Configure**
   - Crie uma pasta `schemas/` em `plugins/Schema-Validator/`
   - Coloque seus arquivos de schema (.json/.yml) na pasta

4. **Reinicie o servidor**

---

## ⚙️ Configuração

### Estrutura de Pastas

```
plugins/
├── Schema-Validator/
│   ├── schemas/
│   │   ├── player-profile.schema.json
│   │   ├── custom-block.schema.json
│   │   └── ...
│   ├── examples/
│   │   └── ...
│   └── config.yml
└── [seus scripts Skript]
```

### config.yml

```yaml
# Configurações do Schema Validator
settings:
  # Cache de schemas carregados
  cache-enabled: true
  # Tempo de expiração do cache (em milissegundos)
  cache-expiry: 3600000
```

---

## 📖 Uso

### Sintaxe Skript

#### Validar YAML

```skript
validate yaml <caminho> using schema <caminho>
```

#### Validar JSON

```skript
validate json <caminho> using schema <caminho>
```

#### Obter Erros de Validação

```skript
set {_errors::*} to last schema validation errors
```

### Exemplos

#### Exemplo 1: Validação Básica

```skript
command /validar:
    trigger:
        validate yaml "examples/player.yml" using schema "schemas/player-profile.schema.json"
        
        set {_errors::*} to last schema validation errors
        
        if size of {_errors::*} is 0:
            broadcast "✓ Dados válidos!"
        else:
            broadcast "✗ Erros encontrados:"
            loop {_errors::*}:
                broadcast "- %loop-value%"
```

#### Exemplo 2: Sistema de Blocos Customizados

```skript
on script load:
    # Carregar schemas na inicialização
    validate yaml "schemas/custom-blocks.yml" using schema "schemas/custom-block.schema.json"

on player break diamond ore:
    # Validar configuração do bloco
    set {_block-id} to "diamond_ore_custom"
    validate yaml "blocks/%{_block-id}%.yml" using schema "schemas/custom-block.schema.json"
    
    if size of {validation::errors::*} is 0:
        # Prosseguir com lógica customizada
        broadcast "Bloco válido! Processando drops..."
    else:
        broadcast "Configuração inválida do bloco!"
```

#### Exemplo 3: Validação de Dados do Jogador

```skript
function validatePlayerData(player: player) :: boolean:
    set {_file} to "playerdata/%uuid of {_player}%.yml"
    validate yaml {_file} using schema "schemas/player-profile.schema.json"
    
    set {_errors::*} to last schema validation errors
    if size of {_errors::*} is 0:
        return true
    else:
        loop {_errors::*}:
            send "&cErro: %loop-value%" to {_player}
        return false
```

---

## 📝 Referência de Schema

### Tipos Suportados

| Tipo | Descrição | Exemplo |
|------|------------|---------|
| `string` | Texto | `"hello"` |
| `number` | Número | `42`, `3.14` |
| `integer` | Inteiro | `42` |
| `boolean` | Booleano | `true`, `false` |
| `object` | Objeto | `{ "key": "value" }` |
| `array` | Lista | `[1, 2, 3]` |
| `null` | Nulo | `null` |
| `any` | Qualquer tipo | qualquer valor |

### Propriedades de Schema

```json
{
  "type": "object",
  "properties": {
    "name": { "type": "string" },
    "age": { "type": "number", "minimum": 0 },
    "email": { "type": "string", "pattern": "^[^@]+@[^@]+$" }
  },
  "required": ["name", "email"],
  "additionalProperties": false
}
```

### Propriedades de Validação

| Propriedade | Descrição |
|-------------|------------|
| `type` | Tipo do dado |
| `properties` | Propriedades de um objeto |
| `patternProperties` | Propriedades com regex |
| `items` | Schema para itens de array |
| `required` | Campos obrigatórios |
| `minimum` / `maximum` | Limites numéricos |
| `minLength` / `maxLength` | Limites de string |
| `pattern` | Regex para string |
| `enum` | Valores permitidos |
| `additionalProperties` | Permitir propriedades extras |

### Exemplo de Schema Complexo

```json
{
  "type": "object",
  "patternProperties": {
    "^[a-zA-Z0-9_-]+$": {
      "type": "object",
      "properties": {
        "block-id": { "type": "string" },
        "info": {
          "type": "object",
          "properties": {
            "name": { "type": "string" },
            "category": {
              "type": "string",
              "enum": ["blocks", "ores", "metals", "crystals"]
            }
          },
          "required": ["name", "category"]
        },
        "hardness": {
          "type": "object",
          "properties": {
            "base": { "type": "number", "minimum": 0 }
          },
          "required": ["base"]
        }
      },
      "required": ["block-id", "info"]
    }
  }
}
```

---

## 📁 Estrutura do Projeto

```
Schema-Validator/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/maiconjh/schemacr/
│   │   │       ├── config/          # Configuração do plugin
│   │   │       ├── core/            # Lógica principal
│   │   │       ├── integration/    # Integração Skript
│   │   │       ├── schemes/        # Carregamento de schemas
│   │   │       └── validation/     # Validadores
│   │   └── resources/
│   │       ├── examples/            # Exemplos de uso
│   │       └── schemas/             # Schemas de exemplo
│   └── test/
├── docs/                            # Documentação
├── build.gradle
└── settings.gradle
```

---

## 🛠️ Compilação

### Compilar o Plugin

```bash
# Compilar com Gradle
gradlew build

# Limpar build anterior
gradlew clean build

# Gerar JAR com dependências
gradlew shadowJar
```

### Saída

O JAR compilado estará em:
```
build/libs/Schema-Validator-0.1.0-SNAPSHOT.jar
```

---

## 🤝 Contribution Guidelines

### Como Contribuir

1. **Fork** o repositório
2. Crie uma **branch** para sua feature (`git checkout -b feature/MinhaFeature`)
3. **Commit** suas mudanças (`git commit -m 'Adiciona nova feature'`)
4. **Push** para a branch (`git push origin feature/MinhaFeature`)
5. Abra um **Pull Request**

### Padrões de Código

- Use **Java 21**
- Sigua o estilo de código existente
- Adicione **JavaDoc** para novas classes/métodos
- Use **nomes descritivos** para variáveis e métodos

### Estrutura de Commits

```
feat:    Nova funcionalidade
fix:     Correção de bug
docs:    Documentação
refactor: Refatoração de código
test:    Adição de testes
chore:   Tarefas de manutenção
```

---

## 📄 Licença

Este projeto está licenciado sob a **MIT License** - veja o arquivo [LICENSE](LICENSE) para detalhes.

---

## 🙏 Agradecimentos

- [Skript](https://github.com/SkriptLang/Skript) - Por criar uma linguagem amazing
- [Paper](https://papermc.io/) - Pela API moderna
- [Jackson](https://github.com/FasterXML/jackson) - Pela biblioteca de parsing JSON/YAML

---

<div align="center">

Feito com ❤️ por [MaiconJH](https://github.com/MaiconJH)

</div>
