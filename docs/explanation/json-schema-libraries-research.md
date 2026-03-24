# Pesquisa: Bibliotecas de Validação JSON Schema

Este documento apresenta uma pesquisa abrangente sobre bibliotecas de validação JSON Schema disponíveis em diferentes linguagens de programação. O objetivo é fornecer informações técnicas detalhadas para auxiliar na seleção de ferramentas adequadas para projetos de validação de dados.

## Sumário

1. [Bibliotecas Java](#bibliotecas-java)
2. [Bibliotecas JavaScript/TypeScript](#bibliotecas-javascripttypescript)
3. [Bibliotecas Python](#bibliotecas-python)
4. [Bibliotecas Go](#bibliotecas-go)
5. [Bibliotecas C#/.NET](#bibliotecas-cnet)
6. [Comparação Técnica](#comparação-técnica)
7. [Casos de Uso Comuns](#casos-de-uso-comuns)

---

## Bibliotecas Java

### 1. everit-org/json-schema (org.everit.json.schema)

**Descrição:** Biblioteca madura e completa para validação JSON Schema em Java, utilizada em diversos projetos empresariais.

**Repositório:** https://github.com/everit-org/json-schema

**Especificações Suportadas:**
- Draft-07 ✓
- 2019-09 ✓
- 2020-12 ✓

**Validadores de Formato Suportados:**
- date-time, date, time, email, hostname, ipv4, ipv6, uri, uri-reference
- regex, uuid, duration, json-pointer, relative-json-pointer
- Formats customizáveis através de `FormatValidator`

**Licença:** Apache License 2.0

**Características de Desempenho:**
- Validação robusta com mensagens de erro detalhadas
- Suporte a `$ref` (referências externas)
- Tratamento de schemas complexos com validação lazy
- BOM (Byte Order Mark) opcional para entrada UTF-8

**Exemplo de Uso:**

```java
import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;
import java.io.InputStream;

InputStream schemaStream = getClass().getResourceAsStream("/schema.json");
Schema schema = SchemaLoader.load(new JSONObject(new JSONTokener(schemaStream)));

InputStream jsonStream = getClass().getResourceAsStream("/data.json");
JSONObject jsonData = new JSONObject(new JSONTokener(jsonStream));

try {
    schema.validate(jsonData);
    System.out.println("Validação passou com sucesso!");
} catch (ValidationException e) {
    System.out.println("Erros de validação: " + e.getMessage());
}
```

**Vantagens:**
- Suporte completo a todas as versões de especificação
- Excelente documentação e exemplos
- Mensagens de erro detalhadas e informativas
- Suporte a schemas Referenced ($ref)

**Desvantagens:**
- Tamanho maior da biblioteca comparada a alternativas
- Performance pode ser impacted em validações muito complexas

**Manutenção:** Ativa - releases regulares e contribuições da comunidade.

---

### 2. networknt/json-schema-validator

**Descrição:** Biblioteca leve e de alta performance, parte do ecossistema Light-4j, focada em velocidade e baixo consumo de memória.

**Repositório:** https://github.com/networknt/json-schema-validator

**Especificações Suportadas:**
- Draft-07 ✓
- 2019-09 ✓
- 2020-12 ✓

**Validadores de Formato Suportados:**
- date-time, date, time, email, hostname, ipv4, ipv6, uri
- uuid, regex, duration
- Formats customizáveis via `FormatFinder`

**Licença:** Apache License 2.0

**Características de Desempenho:**
- Designs para alta performance
- Suporte a cache de schemas compilados
- Validação incremental
- Baixo overhead de memória

**Exemplo de Uso:**

```java
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Set;

ObjectMapper mapper = new ObjectMapper();
JsonNode schemaNode = mapper.readTree(new File("schema.json"));
JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012);
JsonSchema schema = factory.getSchema(schemaNode);

JsonNode inputNode = mapper.readTree(new File("data.json"));
Set<ValidationMessage> errors = schema.validate(inputNode);

if (errors.isEmpty()) {
    System.out.println("Validação passou!");
} else {
    for (ValidationMessage error : errors) {
        System.out.println(error.getMessage());
    }
}
```

**Vantagens:**
- Excelente performance com schemas grandes
- Leve e com baixo consumo de memória
- Suporte a todas as versões draft
- Interface simples e direta

**Desvantagens:**
- Documentação menos abrangente que a everit
- Comunidade menor

**Manutenção:** Ativa - mantida pela equipe networknt com updates regulares.

---

### 3. jsonschema (Java)

**Descrição:** Fork do networknt com extensões adicionais e melhorias da comunidade.

**Repositório:** https://github.com/java-json-tools/json-schema-validator

**Especificações Suportadas:**
- Draft-04 ✓
- Draft-06 ✓
- Draft-07 ✓
- 2019-09 ✓

**Validadores de Formato Suportados:**
- date-time, date, time, email, hostname, ipv4, ipv6, uri
- uuid, regex, json-pointer, uri-template
- Formats estendidos para URI e Relative JSON Pointer

**Licença:** MIT License

**Características de Desempenho:**
- Performance competitiva
- Suporte a validação de múltiplos errors simultâneos
- Customização de format validators

**Exemplo de Uso:**

```java
import com.github.fge.jsonschema.core.load.SchemaLoader;
import com.github.fge.jsonschema.core.report.LogLevel;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

JsonNode schemaNode = mapper.readTree(new File("schema.json"));
JsonNode dataNode = mapper.readTree(new File("data.json"));

JsonSchema schema = SchemaLoader.load(schemaNode);
ProcessingReport report = schema.validate(dataNode);

for (ProcessingMessage message : report) {
    System.out.println(message.getMessage());
}
```

**Vantagens:**
- License MIT (mais permissiva que Apache)
- Suporte a Draft-04 e 06
- Múltiplos formatos adicionais

**Desvantagens:**
- Mantenedor único (vdeboer)
- Releases menos frequentes

**Manutenção:** Moderada - atualizações ocasionais, issues respondidas.

---

## Bibliotecas JavaScript/TypeScript

### 1. AJV (Another JSON Schema Validator)

**Descrição:** O validador JSON Schema mais populares do ecossistema JavaScript/TypeScript. Utiliza compilação de schemas para validação extremamente rápida.

**Repositório:** https://github.com/ajv-validator/ajv

**Especificações Suportadas:**
- Draft-07 ✓
- 2019-09 ✓
- 2020-12 ✓
- Draft-04, 06 (via opções)

**Validadores de Formato Suportados:**
- date-time, date, time, email, hostname, ipv4, ipv6, uri, uri-reference
- uuid, regex, duration, json-pointer, relative-json-pointer
- email, hostname, ipv4, ipv6, uri, uri-reference, regex, uuid

**Licença:** MIT License

**Características de Desempenho:**
- Validação extremamente rápida (compilação ahead-of-time)
- Suporte a plugins customizados
- Validação assíncrona para operações de I/O
- Suporte a refs circulares (com configuração)

**Exemplo de Uso:**

```javascript
const Ajv = require('ajv');
const ajv = new Ajv({ allErrors: true, verbose: true });

const schema = {
  type: 'object',
  properties: {
    user: { type: 'string', minLength: 3 },
    age: { type: 'integer', minimum: 0 },
    email: { type: 'string', format: 'email' }
  },
  required: ['user', 'email'],
  additionalProperties: false
};

const data = {
  user: 'Jo',
  age: -1,
  email: 'invalid-email'
};

const validate = ajv.compile(schema);
const valid = validate(data);

if (!valid) {
  console.log('Erros de validação:');
  validate.errors.forEach(err => {
    console.log(`- ${err.instancePath}: ${err.message}`);
  });
}
```

**Exemplo TypeScript:**

```typescript
import Ajv, { ErrorObject } from 'ajv';

interface UserData {
  name: string;
  email: string;
  age?: number;
}

const schema = {
  type: 'object',
  properties: {
    name: { type: 'string', minLength: 1 },
    email: { type: 'string', format: 'email' },
    age: { type: 'integer', minimum: 0 }
  },
  required: ['name', 'email'],
  additionalProperties: false
} as const;

const ajv = new Ajv();
const validate = ajv.compile<UserData>(schema);

const valid = validate({ name: 'João', email: 'joao@example.com' });
```

**Vantagens:**
- Excelente performance (uma das mais rápidas)
- Suporte a TypeScript com tipos
- Grande ecossistema de plugins
- Documentação excelente

**Desvantagens:**
- Configuração inicial pode ser complexa
- Formatos suportados dependem de plugins

**Manutenção:** Muito ativa - releases frequentes e grande comunidade.

---

### 2. jsonschema (JavaScript)

**Descrição:** Implementação pura em JavaScript do validador JSON Schema, sem dependências externas.

**Repositório:** https://github.com/tdegrunt/jsonschema

**Especificações Suportadas:**
- Draft-04 ✓
- Draft-06 ✓
- Draft-07 ✓

**Validadores de Formato Suportados:**
- date-time, date, time, email, hostname, ipv4, ipv6, uri
- regex, uuid

**Licença:** MIT License

**Características de Desempenho:**
- Implementação pura JS, sem dependências nativas
- Suporte a múltiplos formatos simultaneamente
- Interface simples

**Exemplo de Uso:**

```javascript
const validator = require('jsonschema').validator;
const v = new validator();

const schema = {
  type: 'object',
  properties: {
    name: { type: 'string' },
    age: { type: 'number', minimum: 0 }
  },
  required: ['name']
};

const instance = { name: 'João', age: 25 };

const result = v.validate(instance, schema);

if (result.valid) {
  console.log('Válido!');
} else {
  result.errors.forEach(err => {
    console.log(`${err.property}: ${err.message}`);
  });
}
```

**Vantagens:**
- Sem dependências
- Interface simples
- License permissiva

**Desvantagens:**
- Performance inferior ao AJV
- Manutenção menos ativa

**Manutenção:** Moderada - atualizações ocasionais.

---

## Bibliotecas Python

### 1. jsonschema (Python)

**Descrição:** Implementação referência do validador JSON Schema em Python, mantida pela comunidade JSON Schema.

**Repositório:** https://github.com/python-jsonschema/jsonschema

**Especificações Suportadas:**
- Draft-07 ✓
- 2019-09 ✓
- 2020-12 ✓
- Draft-04, 06 ✓

**Validadores de Formato Suportados:**
- date-time, date, time, email, hostname, ipv4, ipv6, uri
- uuid, regex, duration

**Licença:** MIT License

**Características de Desempenho:**
- API simples e intuitiva
- Suporte a validação de tipos Python
- Exceptions detalhadas

**Exemplo de Uso:**

```python
import jsonschema
from jsonschema import validate, ValidationError

schema = {
    "$schema": "https://json-schema.org/draft/2020-12/schema",
    "type": "object",
    "properties": {
        "name": {"type": "string", "minLength": 1},
        "email": {"type": "string", "format": "email"},
        "age": {"type": "integer", "minimum": 0}
    },
    "required": ["name", "email"]
}

data = {
    "name": "João",
    "email": "joao@example.com",
    "age": 25
}

try:
    validate(instance=data, schema=schema)
    print("Validação passou!")
except ValidationError as e:
    print(f"Erro: {e.message}")
    print(f"Caminho: {list(e.path)}")
```

**Vantagens:**
- Mantido pela equipe JSON Schema
- Suporte a todas as versões
- API pythonica e intuitiva
- Excelente documentação

**Desvantagens:**
- Não é a implementação mais rápida

**Manutenção:** Muito ativa.

---

### 2. Pydantic

**Descrição:** Biblioteca de validação de dados que usa anotações de tipo Python. Compatível com JSON Schema mas focado em Python nativo.

**Repositório:** https://github.com/pydantic/pydantic

**Especificações Suportadas:**
- Gera schemas JSON Schema
- Suporta validação baseada em tipos Python

**Validadores de Formato Suportados:**
- email, uuid, uri, hostname, ipv4, ipv6
- regex, color, date, datetime, time
- Tipos Pythoncustomizados

**Licença:** MIT License

**Características de Desempenho:**
- Validação rápida baseada em tipos
- Geração automática de schema JSON
- Type hints nativos do Python
- Serialização/deserialização integrada

**Exemplo de Uso:**

```python
from pydantic import BaseModel, EmailStr, Field, validator
from typing import Optional

class User(BaseModel):
    name: str = Field(..., min_length=1)
    email: EmailStr
    age: Optional[int] = Field(None, ge=0)
    
    @validator('name')
    def name_must_not_be_empty(cls, v):
        if not v.strip():
            raise ValueError('Nome não pode ser vazio')
        return v

# Validação automática
user = User(name="João", email="joao@example.com", age=25)
print(user.json())

# Geração de JSON Schema
print(User.schema_json(indent=2))
```

**Vantagens:**
- type hints nativos do Python
- Excelente performance
- Geração automática de JSON Schema
- Serializer/Deserializer integrado

**Desvantagens:**
- Não é um validador JSON Schema puro
- Algumas funcionalidades diferem do padrão

**Manutenção:** Muito ativa - uma das bibliotecas mais populares do ecossistema Python.

---

### 3. fastjsonschema

**Descrição:** Biblioteca que gera código de validação Python para performance máxima. Compila schemas em código Python otimizado.

**Repositório:** https://github.com/0xc/fastjsonschema

**Especificações Suportadas:**
- Draft-07 ✓
- Draft-04, 06 ✓

**Validadores de Formato Suportados:**
- date-time, date, time, email, hostname, ipv4, ipv6, uri, uri-reference
- uuid, regex, duration

**Licença:** MIT License

**Características de Desempenho:**
- Geração de código compilado
- Performance superior a jsonschema
- Suporte a cache de schemas compilados

**Exemplo de Uso:**

```python
import fastjsonschema

# Compila o schema uma vez
validate = fastjsonschema.compile({
    "type": "object",
    "properties": {
        "name": {"type": "string"},
        "age": {"type": "integer", "minimum": 0}
    },
    "required": ["name"]
})

# Validação rápida
try:
    validate({"name": "João", "age": 25})
except fastjsonschema.JsonSchemaValueException as e:
    print(f"Erro: {e.message}")
```

**Vantagens:**
- Excelente performance
- Geração de código
- Cache de schemas compilados

**Desvantagens:**
- Não suporta 2019-09 ou 2020-12
- Menos flexível que jsonschema

**Manutenção:** Ativa.

---

## Bibliotecas Go

### 1. gojsonschema

**Descrição:** Implementação completa de validador JSON Schema em Go, com suporte a todas as funcionalidades da especificação.

**Repositório:** https://github.com/xeipuuv/gojsonschema

**Especificações Suportadas:**
- Draft-04 ✓
- Draft-06 ✓
- Draft-07 ✓

**Validadores de Formato Suportados:**
- date-time, date, time, email, hostname, ipv4, ipv6, uri
- uuid, regex

**Licença:** Apache License 2.0

**Características de Desempenho:**
- API fluente e clara
- Suporte a referências ($ref)
- Validação detalhada com localização de erros

**Exemplo de Uso:**

```go
package main

import (
    "encoding/json"
    "fmt"
    "github.com/xeipuuv/gojsonschema"
)

func main() {
    schemaLoader := gojsonschema.NewReferenceLoader("file://schema.json")
    documentLoader := gojsonschema.NewStringLoader(`{"name": "João", "age": 25}`)

    schema, err := gojsonschema.NewSchema(schemaLoader)
    if err != nil {
        panic(err)
    }

    result, err := schema.Validate(documentLoader)
    if err != nil {
        panic(err)
    }

    if result.Valid() {
        fmt.Println("Documento é válido!")
    } else {
        fmt.Println("Erros de validação:")
        for _, err := range result.Errors() {
            fmt.Printf("- %s: %s\n", err.Field(), err.Description())
        }
    }
}
```

**Vantagens:**
- Suporte completo a Draft-07
- API clara e bem documentada
- Apache License 2.0

**Desvantagens:**
- Não suporta 2019-09 ou 2020-12
- Performance moderada

**Manutenção:** Moderada - updates ocasionais.

---

### 2. ajv (Go)

**Descrição:** Porta Go do AJV (Another JSON Schema Validator), oferecendo a mesma performance e funcionalidades da versão JavaScript.

**Repositório:** https://github.com/ajv-validator/ajv

**Especificações Suportadas:**
- Draft-07 ✓
- 2019-09 ✓
- 2020-12 ✓

**Validadores de Formato Suportados:**
- date-time, date, time, email, hostname, ipv4, ipv6, uri
- uuid, regex, duration

**Licença:** MIT License

**Características de Desempenho:**
- Excelente performance (compilação ahead-of-time)
- Suporte a plugins
- API compatível com AJV JS

**Exemplo de Uso:**

```go
package main

import (
    "encoding/json"
    "fmt"
    ajv "github.com/ajv-validator/ajv-go"
    "github.com/ajv-validator/ajv-go/pkg/jTD"
    "github.com/ajv-validator/ajv-go/pkg/json"
)

func main() {
    ajvInstance := ajv.New(ajv.AllErrors, ajv.Verbose)
    
    schema := json.MustLoadString(`{
        "type": "object",
        "properties": {
            "name": {"type": "string", "minLength": 1},
            "email": {"type": "string", "format": "email"},
            "age": {"type": "integer", "minimum": 0}
        },
        "required": ["name", "email"]
    }`)
    
    validate, err := ajvInstance.Compile(schema)
    if err != nil {
        panic(err)
    }
    
    data := json.MustLoadString(`{"name": "João", "email": "joao@example.com"}`)
    
    if validate(data) {
        fmt.Println("Válido!")
    } else {
        fmt.Println("Erros:", validate.Errors)
    }
}
```

**Vantagens:**
- Excelente performance
- Suporte a versões modernas do JSON Schema
- MIT License

**Desvantagens:**
- Projeto relativamente recente
- Documentação menos extensa

**Manutenção:** Ativa - desenvolvimento contínuo.

---

## Bibliotecas C#/.NET

### 1. Newtonsoft.Json.Schema

**Descrição:** Biblioteca comercial completa para validação JSON Schema no framework .NET, desenvolvida pela NewtonSoft (criadores do Newtonsoft.Json).

**Repositório:** https://www.newtonsoft.com/jsonschema

**Especificações Suportadas:**
- Draft-04 ✓
- Draft-06 ✓
- Draft-07 ✓
- 2019-09 ✓

**Validadores de Formato Suportados:**
- date-time, date, time, email, hostname, ipv4, ipv6, uri
- uuid, regex, json-pointer
- Todos os formatos drafts

**Licença:** Comercial (Paid)

**Características de Desempenho:**
- Alta performance
- Suporte a Linq to JSON
- Integração profunda com Newtonsoft.Json

**Exemplo de Uso:**

```csharp
using Newtonsoft.Json;
using Newtonsoft.Json.Schema;
using Newtonsoft.Json.Linq;
using System.Linq;

string schemaJson = @"{
    '$schema': 'https://json-schema.org/draft/07/schema',
    'type': 'object',
    'properties': {
        'name': { 'type': 'string', 'minLength': 1 },
        'email': { 'type': 'string', 'format': 'email' },
        'age': { 'type': 'integer', 'minimum': 0 }
    },
    'required': ['name', 'email']
}";

string dataJson = @"{
    'name': 'João',
    'email': 'joao@example.com',
    'age': 25
}";

JSchema schema = JSchema.Parse(schemaJson);
JObject data = JObject.Parse(dataJson);

IList<ValidationError> errors;
bool isValid = data.IsValid(schema, out errors);

if (isValid)
{
    Console.WriteLine("Válido!");
}
else
{
    Console.WriteLine("Erros de validação:");
    foreach (var error in errors)
    {
        Console.WriteLine($"- {error.Path}: {error.Message}");
    }
}
```

**Vantagens:**
- Suporte completo a todas as especificações
- Excelente documentação
- Suporte comercial
- Integração perfeita com Newtonsoft.Json

**Desvantagens:**
- Requer licença paga
- Custo pode ser proibitivo para projetos pequenos

**Manutenção:** Ativa - mantida por equipe profissional.

---

### 2. NJsonSchema

**Descrição:** Biblioteca open source para geração e validação de JSON Schema em .NET, amplamente utilizada em projetos open source.

**Repositório:** https://github.com/NJsonSchema/NJsonSchema

**Especificações Suportadas:**
- Draft-04 ✓
- Draft-06 ✓
- Draft-07 ✓
- 2019-09 ✓

**Validadores de Formato Suportados:**
- date-time, date, time, email, hostname, ipv4, ipv6, uri
- uuid, regex, json-pointer

**Licença:** MIT License

**Características de Desempenho:**
- Suporte a geração de schemas a partir de classes C#
- Validação de JSON e C# POCOs
- Geração de código TypeScript
- Suporte a ASP.NET Core

**Exemplo de Uso:**

```csharp
using NJsonSchema;

string schemaJson = @"{
    'type': 'object',
    'properties': {
        'name': { 'type': 'string' },
        'email': { 'type': 'string', 'format': 'email' }
    },
    'required': ['name', 'email']
}";

var schema = await JsonSchema.FromJsonAsync(schemaJson);

string dataJson = @"{
    'name': 'João',
    'email': 'joao@example.com'
}";

var errors = schema.Validate(dataJson);

if (!errors.Any())
{
    Console.WriteLine("Válido!");
}
else
{
    foreach (var error in errors)
    {
        Console.WriteLine($"- {error.Path}: {error.Kind}");
    }
}
```

**Exemplo com geração de classes:**

```csharp
// Gera schema a partir de uma classe C#
var schema = await JsonSchema.FromTypeAsync<Person>();

// Converte para JSON
string json = schema.ToJson();

// Gera código TypeScript
string typescript = schema.ToTypeScript(TypeScriptJsonSchemaGenerator.Settings);

// Gera código C#
string csharp = schema.ToCSharp().Class;
```

**Vantagens:**
- Open source com license MIT
- Geração de schemas a partir de classes C#
- Geração de código TypeScript
- Grande adoção em projetos .NET

**Desvantagens:**
- Não suporta 2020-12
- Documentação limitada

**Manutenção:** Ativa - usada em vários projetos populares (NSwag, Swashbuckle).

---

## Comparação Técnica

### Suporte a Versões de Especificação

| Biblioteca | Draft-04 | Draft-06 | Draft-07 | 2019-09 | 2020-12 |
|------------|:--------:|:--------:|:--------:|:-------:|:-------:|
| **Java** |
| everit-org | ✗ | ✓ | ✓ | ✓ | ✓ |
| networknt | ✗ | ✓ | ✓ | ✓ | ✓ |
| jsonschema (java-json-tools) | ✓ | ✓ | ✓ | ✓ | ✗ |
| **JavaScript/TypeScript** |
| AJV | ✓ | ✓ | ✓ | ✓ | ✓ |
| jsonschema (JS) | ✓ | ✓ | ✓ | ✗ | ✗ |
| **Python** |
| jsonschema | ✓ | ✓ | ✓ | ✓ | ✓ |
| pydantic | ✗ | ✗ | ✗ | ✗ | ✗* |
| fastjsonschema | ✓ | ✓ | ✓ | ✗ | ✗ |
| **Go** |
| gojsonschema | ✓ | ✓ | ✓ | ✗ | ✗ |
| ajv-go | ✗ | ✗ | ✓ | ✓ | ✓ |
| **C#** |
| Newtonsoft.Json.Schema | ✓ | ✓ | ✓ | ✓ | ✗ |
| NJsonSchema | ✓ | ✓ | ✓ | ✓ | ✗ |

*pydantic gera JSON Schema mas não valida contra especificações.

### Suporte a Format Validators

| Biblioteca | email | hostname | ipv4 | ipv6 | uri | uuid | regex | date-time | duration |
|------------|:-----:|:--------:|:----:|:----:|:---:|:----:|:-----:|:---------:|:--------:|
| **Java** |
| everit-org | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ |
| networknt | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ |
| jsonschema | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ |
| **JavaScript** |
| AJV | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ |
| jsonschema | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✗ |
| **Python** |
| jsonschema | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ |
| pydantic | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ |
| fastjsonschema | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ |
| **Go** |
| gojsonschema | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✗ |
| ajv-go | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ |
| **C#** |
| Newtonsoft | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ |
| NJsonSchema | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✗ |

### Performance

| Biblioteca | Performance | Notas |
|------------|-------------|-------|
| **Alta** |
| AJV (JS) | ★★★★★ | Compilação ahead-of-time |
| ajv-go (Go) | ★★★★★ | Mesmo princípio do AJV |
| fastjsonschema (Python) | ★★★★★ | Geração de código |
| pydantic (Python) | ★★★★★ | Type hints compilados |
| networknt (Java) | ★★★★☆ | Cache de schemas |
| **Média** |
| everit-org (Java) | ★★★★☆ | Validação robusta |
| jsonschema (Python) | ★★★☆☆ | Referência implementação |
| NJsonSchema (C#) | ★★★★☆ | Boa para uso geral |
| Newtonsoft.Json.Schema | ★★★★☆ | Otimizado |
| **Moderada** |
| jsonschema (JS) | ★★★☆☆ | Implementação pura |
| gojsonschema (Go) | ★★★☆☆ | Adequado para uso geral |

### Facilidade de Integração

| Biblioteca | Integração | Pontuação |
|------------|------------|-----------|
| AJV | npm/yarn, webpack-friendly | ★★★★★ |
| pydantic | pip, decorators | ★★★★★ |
| networknt | Maven/Gradle | ★★★★★ |
| everit-org | Maven/Gradle | ★★★★☆ |
| NJsonSchema | NuGet | ★★★★☆ |
| jsonschema (Python) | pip | ★★★★☆ |
| Newtonsoft.Json.Schema | NuGet | ★★★★☆ |
| gojsonschema | go mod | ★★★☆☆ |
| ajv-go | go mod | ★★★★☆ |

### Manutenção e Comunidade

| Biblioteca | Atividade | Tamanho Comunidade | Status |
|------------|-----------|-------------------|--------|
| AJV | ★★★★★ | ★★★★★ | Muito ativa |
| pydantic | ★★★★★ | ★★★★★ | Muito ativa |
| jsonschema (Python) | ★★★★★ | ★★★★☆ | Muito ativa |
| everit-org | ★★★★☆ | ★★★★☆ | Ativa |
| networknt | ★★★★☆ | ★★★☆☆ | Ativa |
| NJsonSchema | ★★★★☆ | ★★★★☆ | Ativa |
| Newtonsoft.Json.Schema | ★★★★★ | ★★★★☆ | Suporte comercial |
| jsonschema (JS) | ★★★☆☆ | ★★★☆☆ | Moderada |
| gojsonschema | ★★★☆☆ | ★★★☆☆ | Moderada |
| ajv-go | ★★★★☆ | ★★★☆☆ | Ativa |
| jsonschema (Java) | ★★★☆☆ | ★★★☆☆ | Moderada |

### Licenças

| Biblioteca | Licença | Tipo |
|------------|---------|------|
| **Permissivas (MIT)** |
| AJV | MIT | Open Source |
| pydantic | MIT | Open Source |
| jsonschema (Python) | MIT | Open Source |
| fastjsonschema | MIT | Open Source |
| jsonschema (JS) | MIT | Open Source |
| NJsonSchema | MIT | Open Source |
| ajv-go | MIT | Open Source |
| **Apache 2.0** |
| everit-org | Apache 2.0 | Open Source |
| networknt | Apache 2.0 | Open Source |
| gojsonschema | Apache 2.0 | Open Source |
| **Comercial** |
| Newtonsoft.Json.Schema | Commercial | Paid |

---

## Casos de Uso Comuns

### Java

- **everit-org/json-schema**: Projetos empresariais que necessitam de validação robusta com mensagens de erro detalhadas e suporte completo a referências.
- **networknt/json-schema-validator**: Microserviços que precisam de validação rápida e leve, especialmente no ecossistema Light-4j.
- **jsonschema (java-json-tools)**: Projetos que precisam de suporte a Draft-04/06 e preferem license MIT.

### JavaScript/TypeScript

- **AJV**: Aplicações web que necessitam de validação de alta performance, especialmente em APIs REST e validação no lado do cliente.
- **jsonschema (JS)**: Projetos simples onde performance não é crítica e simplicidade é preferida.

### Python

- **jsonschema**: Validação padrão de JSON Schema, especialmente para APIs e estruturas de dados.
- **Pydantic**: Aplicações Python modernas que usam type hints, validation de APIs (FastAPI, etc), e precisam de serialização.
- **fastjsonschema**: Validação em alta performance onde schemas são estáticos e conhecidos previamente.

### Go

- **ajv-go**: Projetos que necessitam de performance máxima e suporte a especificações modernas.
- **gojsonschema**: Projetos que necessitam de API clara e suporte a Draft-04/06/07.

### C#/.NET

- **Newtonsoft.Json.Schema**: Projetos comerciais que podem investir em licença e necessitam de suporte profissional.
- **NJsonSchema**: Projetos open source, geração de código TypeScript, integração com Swagger/OpenAPI.

---

## Recomendação para Schema-Validator

Para o projeto Schema-Validator (baseado em Java/Skript), as recomendações baseadas nesta pesquisa são:

1. **everit-org/json-schema**: Recomendado para validação robusta com mensagens de erro detalhadas. Adequado para sistemas de plugins onde os usuários precisam de feedback claro.

2. **networknt/json-schema-validator**: Recomendado para performance máxima, especialmente útil em aplicações com alto volume de validações.

A escolha entre as duas dependerá das prioridades do projeto: se a prioridade é detalhamento de erros e robustez, everit-org; se é performance e leveza, networknt.

---

*Documento atualizado em: 2026-03-24*