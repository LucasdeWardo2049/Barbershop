# Barbershop
 App para gerenciamento de barbearia usando jetpack compose + MVVM 
 # Barbershop App
​
 ![Static Badge](https://img.shields.io/badge/platform-android-green)
 ![Static Badge](https://img.shields.io/badge/language-kotlin-blue)
 ![Static Badge](https://img.shields.io/badge/UI-jetpack%20compose-blueviolet)
​
 Um aplicativo de barbearia moderno para Android, desenvolvido com as melhores práticas e bibliotecas do ecossistema Jetpack.
​
 ## 💈 Sobre o Projeto
​
 Este aplicativo simula a experiência de um app para uma barbearia, onde os usuários podem visualizar serviços e produtos. O projeto foi estruturado para ser escalável, testável e de fácil manutenção, seguindo uma arquitetura MVVM (Model-View-ViewModel).
​
 ## ✨ Features
​
 - **Catálogo Dinâmico**: Visualize a lista de serviços e produtos oferecidos pela barbearia.
 - **Perfil de Usuário**: Uma tela de perfil de usuário com imagem carregada dinamicamente.
 - **UI Moderna**: Interface de usuário totalmente construída com Jetpack Compose.
 - **Carregamento de Imagens**: Carregamento de imagens assíncrono e eficiente com a biblioteca Coil.
​
 ## 🛠️ Arquitetura e Tecnologias
​
 O projeto utiliza uma arquitetura limpa e moderna, com as seguintes tecnologias:
​
 - **Linguagem**: [Kotlin](https://kotlinlang.org/) (100%)
 - **UI**: [Jetpack Compose](https://developer.android.com/jetpack/compose) para uma UI declarativa e moderna.
 - **Injeção de Dependência**: [Hilt](https://developer.android.com/training/dependency-injection/hilt-android) para gerenciar dependências e facilitar a testabilidade.
 - **Processamento de Anotações**: [KSP (Kotlin Symbol Processing)](https://kotlinlang.org/docs/ksp-overview.html) para um processamento de anotações mais rápido com Hilt.
 - **Arquitetura**: MVVM (Model-View-ViewModel) com fluxos de dados unidirecionais.
 - **Gerenciamento de Estado**: `StateFlow` e `Sealed Classes` para um gerenciamento de estado robusto e previsível na camada de UI.
 - **Carregamento de Imagens**: [Coil](https://coil-kt.github.io/coil/) para carregar imagens da rede de forma eficiente.
 - **Navegação**: [Navigation Compose](https://developer.android.com/jetpack/compose/navigation) para navegação entre telas.
 - **Build System**: Gradle com Kotlin DSL e gerenciamento de dependências centralizado via `libs.versions.toml`.
​
 ### Estrutura do Projeto
​
 O código é organizado em três camadas principais:
​
 - `/data`: Camada de dados, responsável por fornecer os dados (repositórios).
 - `/domain`: Camada de domínio, contendo a lógica de negócios (modelos e casos de uso).
 - `/ui`: Camada de UI, contendo os Composables (telas e componentes) e ViewModels.
​
 ## 🚀 Como Executar
​
 1.  **Clone o repositório**:
     ```bash
     git clone <URL_DO_SEU_REPOSITORIO>
     ```
 2.  **Abra no Android Studio**:
     - Abra o Android Studio (versão Hedgehog ou mais recente).
     - Clique em `File > Open` e selecione a pasta do projeto clonado.
 3.  **Sincronize o Gradle**:
     - O Android Studio deve sincronizar o projeto automaticamente. Se não, clique no ícone de elefante do Gradle com uma seta para sincronizar.
 4.  **Execute o aplicativo**:
     - Selecione um emulador ou dispositivo físico e clique no botão de "Run".
​#Login
Login: cliente@cliente.com (perfil cliente)
Senha: cliente

Login: barbeiro@barbeiro.com (perfil barbeiro)
Senha: barbeiro
