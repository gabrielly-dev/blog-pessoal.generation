# Blog Pessoal

[![Java](https://img.shields.io/badge/Java-17-blue?logo=java&logoColor=white)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.4.3-green?logo=spring&logoColor=white)](https://spring.io/projects/spring-boot)
[![Maven](https://img.shields.io/badge/Maven-3.8.7-red?logo=apache-maven&logoColor=white)](https://maven.apache.org/)
[![Docker](https://img.shields.io/badge/Docker-Container-blue?logo=docker&logoColor=white)](https://www.docker.com/)
[![Swagger](https://img.shields.io/badge/Swagger-API_Docs-brightgreen?logo=swagger&logoColor=white)](https://swagger.io/)

---

## Sobre

Blog Pessoal é uma aplicação Spring Boot desenvolvida para servir como uma plataforma de blog pessoal. Utiliza Java 17 e Spring Boot 3.4.3, com APIs RESTful protegidas por Spring Security e JWT. O projeto suporta bancos de dados MySQL e PostgreSQL e inclui documentação da API via Swagger.

---

## Tecnologias Utilizadas

- Java 17
- Spring Boot 3.4.3
- Maven
- Docker
- Spring Security com JWT
- MySQL e PostgreSQL
- Swagger (springdoc-openapi)

---

## Como Executar

### Usando Maven

1. Clone o repositório:
   ```bash
   git clone https://github.com/gabrielly-dev/blog-pessoal.generation.git
   ```
2. Navegue até o diretório do projeto:
   ```bash
   cd blogpessoal
   ```
3. Execute a aplicação:
   ```bash
   ./mvnw spring-boot:run
   ```
   ou no Windows:
   ```bash
   mvnw.cmd spring-boot:run
   ```

### Usando Docker

1. Construa a imagem Docker:
   ```bash
   docker build -t blogpessoal .
   ```
2. Execute o container Docker:
   ```bash
   docker run -p 8080:8080 blogpessoal
   ```

---

## Documentação da API

A documentação da API está disponível via Swagger UI após a aplicação estar em execução:

```
http://localhost:8080/swagger-ui.html
```

---

## Licença

Este projeto está licenciado sob a Licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

---

## Contato

Conecte-se comigo nas redes sociais:

[![Instagram](https://img.shields.io/badge/Instagram-%23E4405F.svg?&style=for-the-badge&logo=instagram&logoColor=white)](https://instagram.com/gabrielly.dev)   [![LinkedIn](https://img.shields.io/badge/LinkedIn-%230077B5.svg?&style=for-the-badge&logo=linkedin&logoColor=white)](https://linkedin.com/in/gabrielly-dev)  [![GitHub](https://img.shields.io/badge/GitHub-%23121011.svg?&style=for-the-badge&logo=github&logoColor=white)](https://github.com/gabrielly-dev)
