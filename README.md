# Cache-Aside example
Simples projeto que implementa a estratégia de cache (Cache-Aside), *este repositório não contem as melhores
práticas de engenharia.*

# Pré Requisitos
- [X] Java 21 configurado 
- [X] Docker instalado na maquina

# Running
1 - Criar a instância do Redis via docker compose
```bash
sudo docker compose up -d
```
2 - Inicializar a aplicação via terminal após acessar a raíz do projeto
```bash
./gradlew bootRun
```

# Endpoints
Saving users

```curl
curl --request POST \
  --url http://localhost:8080/user/v1/users \
  --header 'Content-Type: application/json' \
  --data '{
	"name": "Aside",
	"age": 299,
	"email": "cache@sample.com"
}'
```

List All users

```curl
curl --request GET \
--url http://localhost:8080/user/v1/users 
```

Find user email

```curl
curl --request GET \
  --url http://localhost:8080/user/v1/users/{id}/email
```
