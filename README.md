# Barbershop App

![Static Badge](https://img.shields.io/badge/platform-android-green)
![Static Badge](https://img.shields.io/badge/language-kotlin-blue)
![Static Badge](https://img.shields.io/badge/UI-jetpack%20compose-blueviolet)

Um aplicativo de barbearia moderno para Android, desenvolvido com as melhores práticas e bibliotecas do ecossistema Jetpack.

## 💈 Sobre o Projeto

Este aplicativo simula a experiência de um app para uma barbearia, onde os usuários podem visualizar serviços e produtos. O projeto foi estruturado para ser escalável, testável e de fácil manutenção, seguindo uma arquitetura MVVM (Model-View-ViewModel).

## ✨ Features

- **Catálogo Dinâmico**: Visualize a lista de serviços e produtos oferecidos pela barbearia.
- **Perfil de Usuário**: Uma tela de perfil de usuário com imagem carregada dinamicamente.
- **Agendamentos**: Fluxo de agendamento integrado ao backend (serviços, barbeiros, disponibilidade e confirmação).
- **UI Moderna**: Interface de usuário totalmente construída com Jetpack Compose.
- **Carregamento de Imagens**: Carregamento de imagens assíncrono e eficiente com a biblioteca Coil.

## 🛠️ Arquitetura e Tecnologias

- **Linguagem**: Kotlin (100%)
- **UI**: Jetpack Compose
- **Injeção de Dependência**: Hilt
- **Processamento de Anotações**: KSP
- **Arquitetura**: MVVM + fluxo unidirecional
- **Gerenciamento de Estado**: StateFlow
- **Networking**: Retrofit + Gson (com adapter para OffsetDateTime)
- **Navegação**: Navigation Compose
- **Build System**: Gradle Kotlin DSL + libs.versions.toml

### Estrutura do Projeto

- `/data`: Camada de dados (serviços remotos, repositórios)
- `/domain`: Modelos e regras de negócio
- `/ui`: Telas, componentes e ViewModels

## 🔐 Login (Usuários de Demonstração)

```
Login: cliente@cliente.com  (perfil cliente)
Senha: cliente

Login: barbeiro@barbeiro.com (perfil barbeiro)
Senha: barbeiro
```

## 📅 Backend de Agendamentos

Base URL (emulador): `http://10.0.2.2:8080/api/v1/`

Endpoints principais:
- Listar serviços: `GET /services`
- Listar usuários (filtrar role BARBER no app): `GET /users`
- Disponibilidade: `GET /barbers/{barberId}/availability?serviceId=&date=YYYY-MM-DD`
- Agendar: `POST /appointments/book`
- Listar agendamentos do cliente: `GET /appointments/by_client?clientId=`

Regras de horário:
- `startTime` enviado em UTC: `yyyy-MM-dd'T'HH:mm:ss'Z'`
- Slots recebidos em OffsetDateTime são convertidos para fuso `America/Sao_Paulo` e mostrados em `HH:mm`.

## 🚀 Como Executar

1. Clone o repositório:
```bash
git clone <URL_DO_SEU_REPOSITORIO>
```
2. Abra no Android Studio (Hedgehog ou superior).
3. Sincronize o Gradle.
4. Execute em um emulador (garanta que o backend esteja rodando em `localhost:8080`).

## ✅ Fluxo de Agendamento

1. Escolher Serviço
2. Escolher Profissional
3. Escolher Data → carrega disponibilidade
4. Escolher Horário
5. Confirmar (envio UTC)

Mensagens de erro amigáveis são exibidas via estado (`errorMessage`).

## 📌 Próximos Passos
- Enriquecer nome de serviço e barbeiro nos cards de agendamento com cache local.
- Implementar cancelamento / reagendamento.
- Adicionar testes unitários para conversões de data/hora.

Obrigado por conferir o projeto!
