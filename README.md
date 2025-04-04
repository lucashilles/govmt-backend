# govmt-backend

<!-- TOC -->
* [govmt-backend](#govmt-backend)
  * [Dependências do projeto](#dependências-do-projeto)
  * [Executando a aplicação](#executando-a-aplicação)
    * [Ambiente de desenvolvimento](#ambiente-de-desenvolvimento)
  * [Utilizando a API](#utilizando-a-api)
    * [Banco de dados](#banco-de-dados)
    * [Minio](#minio)
  * [Requisitos](#requisitos)
    * [Requisitos Gerais](#requisitos-gerais)
    * [Requisitos Específicos](#requisitos-específicos)
<!-- TOC -->

## Dependências do projeto

Build e execução:
- Java JDK  21
- Gradle 8.12
- Quarkus 3.21

Containers:
- postgres:17
- quay.io/minio/minio
- minio/mc
- adminer

## Executando a aplicação

Os certificados não estão incluidos neste repositório sendo necessário gera-los antes de fazer o _build_ da image da aplicação. Ao executar o script `create-certs.sh` os certificados serão gerado na pasta correta da aplicação.

```shell
sh scripts/create-certs.sh
```

Após gerar os certificados execute o _build_ da imagem, você precisa ter JDK 21 para isto, e então utilize o docker compose para subir todos os containers necessários.

```shell
./gradlew imageBuild -Dquarkus.container-image.tag=latest

docker compose --profile prod up
```

A especificação da API estará disponível em:

http://localhost:8080/q/openapi/

> [!TIP]
> É possível verificar as definições e testar os endpoints utilizando o Swagger UI:
> 
> http://localhost:8080/q/swagger/

### Ambiente de desenvolvimento

Para executar como ambiente de desenvolvimento, execute o docker-compose sem especificar o _profile_ e então execute o Quarkus.

```shell
docker compose up

./gradlew quarkusDev
```

## Utilizando a API

Durante a inicialização um usuário com permissões 'Admin' será criado, este tipo de usuário permite acesso a todos endpoints disponíveis. O endpoint para registro só cria usuários com nível de acesso reduzido.

As credenciais padrão do usuário são podem ser alteradas nas variáveis de ambiente do serviço **_backend_** no docker-compose.yml

```yaml
      GOVMT_DEFAULT_USER: Admin
      GOVMT_DEFAULT_EMAIL: admin@email.com
      GOVMT_DEFAULT_PASSWORD: admin
```

Para conseguir utilizar os endpoints da aplicação é necessário utilizar o endpoint de login para obter o token Bearer que deve ser incluido nas requisões aos endpoints autenticados.

```shell
curl -X 'POST' 'http://localhost:8080/api/auth/login' \
  -H 'accept: */*' \
  -H 'Content-Type: multipart/form-data' \
  -F 'email=admin@email.com' \
  -F 'password=admin'

# Resposta
# {"token":"eyJ0e..."}

curl -H "Authorization: Bearer eyJ0e..." http://localhost:8080/api/auth/me

# Resposta
# {Usuario=admin@email.com, Papel=Admin}
```

O token JWT expirará 5min após ser gerado sendo possível fazer a renovação antes expiração para extender a duração.

```shell
curl -H "Authorization: Bearer eyJ0e..." http://localhost:8080/api/auth/refresh

# Resposta
# {"token":"eyJ0e..."}
```

### Banco de dados

Este projeto utiliza migrations para gerar a estrutura do banco de dados e inserir dados de exemplo.

O banco de dados pode ser inspecionado utilizando o Adminer incluso e com os dados de login da tabela.

http://localhost:7000/

| Field    | Value      |
|----------|------------|
| System   | PostgreSQL |
| Server   | db         |
| Username | root       |
| Password | root       |
| Database | govmtdb    |

### Minio

O console de gestão do Min.IO é disponibilizado na URL abaixo.

http://localhost:9001/

> **Usuário:** minio
> 
> **Senha:** minio123

## Requisitos
### Requisitos Gerais

- [X] Implementar mecanismo de autorização e autenticação, bem como não permitir acesso ao endpoint a partir de domínios diversos do qual estará hospedado o serviço; **Todos endpoint aceitam somente acesso autenticado e o CORS está ativado.**

- [X] A solução de autenticação deverá expirar a cada 5 minutos e oferecer a possibilidade de renovação do período; **Utilizado de JWT com expiração e endpoint para renovação, detalhes de implementação na classe JwtUtils.java**

- [X] Implementar pelo menos os verbos post, put, get; **CRUD para todas entidades**

- [X] Conter recursos de paginação em todas as consultas; **Toda requisição que pode resultar em mais de um resultado é paginada utilizando a class _PagedList.java_**

- [X] Os dados produzidos deverão ser armazenados no servidor de banco de dados previamente criado em container; **Utilizando container PostgreSQL de versão mais recente**

- [X] Orquestrar a solução final utilizando Docker Compose de modo que inclua todos os contêineres utilizados. **Docker compose para orquestra API, Banco de Dados, Object Storage e Front-end para DB**"

### Requisitos Específicos

Implementar uma API Rest para o diagrama de banco de dados acima tomando por base as seguintes orientações:
- [X] Criar um CRUD para Servidor Efetivo, Servidor Temporário, Unidade e Lotação. Deverá ser contemplado a inclusão e edição dos dados das tabelas relacionadas; **Todas entidade possuem CRUD e as tabelas relacionadas são geridas pelos mapeamentos JPA**

- [X] Criar um endpoint que permita consultar os servidores efetivos lotados em determinada unidade parametrizando a consulta pelo atributo unid_id; Retornar os seguintes campos: Nome, idade, unidade de lotação e fotografia; **Disponibilizado no endpoint** `GET /api/servidor-efetivo?unid_id={id}`

- [X] Criar um endpoint que permita consultar o endereço funcional (da unidade onde o servidor é lotado) a partir de uma parte do nome do servidor efetivo. **Disponibilizado no endpoint** `GET /api/endereco?nome_parcial={nome_parcial}`

- [X] Realizar o upload de uma ou mais fotografias enviando-as para o Min.IO; **Disponibilizado no endpoint** `POST /api/foto-pessoa/{pessoaId}`

- [X] A recuperação das imagens deverá ser através de links temporários gerados pela biblioteca do Min.IO com tempo de expiração de 5 minutos. **Todos campos relacionados a images retornam endereço temporário com expiração de 5 min**