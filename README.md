# Cache Implementations

Projeto multimodule que demonstra diferentes estratégias de cache com Spring Boot e Redis. *Este repositório não contém as melhores práticas de engenharia.*

## Pré Requisitos
- Java 21 configurado
- Docker instalado na máquina

## Estrutura

| Módulo  | Estratégia    | Status      |
|---------|---------------|-------------|
| `aside` | Cache-Aside   | ✅ Disponível |

## Executando

1. Subir a instância do Redis via Docker Compose (necessário para todos os módulos):
```bash
sudo docker compose up -d
```

2. Inicializar o módulo desejado a partir da raíz do projeto:
```bash
./gradlew :<módulo>:bootRun
```

Exemplo:
```bash
./gradlew :aside:bootRun
```

---

## Módulos

### Cache-Aside (`aside`)

O cliente é responsável por gerenciar o cache: consulta o cache primeiro, vai ao banco em caso de miss e escreve o resultado no cache. Na atualização, persiste no banco e invalida a chave no cache (estratégia write + delete).

**Base URL:** `http://localhost:8080/user`

#### Endpoints

Criar usuário:
```bash
curl --request POST \
  --url http://localhost:8080/user/v1/users \
  --header 'Content-Type: application/json' \
  --data '{
    "name": "Aside",
    "age": 299,
    "email": "cache@sample.com"
}'
```

Listar todos os usuários:
```bash
curl --request GET \
  --url http://localhost:8080/user/v1/users
```

Buscar e-mail de um usuário (resposta inclui header `X-Cache: hit|miss`):
```bash
curl --request GET \
  --url http://localhost:8080/user/v1/users/{id}/email
```

Atualizar e-mail de um usuário:
```bash
curl --request PATCH \
  --url http://localhost:8080/user/v1/users/{id}/email \
  --header 'Content-Type: application/json' \
  --data '{"email": "novo@email.com"}'
```
