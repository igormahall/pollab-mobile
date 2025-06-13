# 📱 App de Enquetes — Android Nativo

Cliente mobile **nativo** para o Sistema de Enquetes, desenvolvido em **Kotlin** com as tecnologias mais modernas do ecossistema Android.  
O aplicativo consome a **API RESTful** construída em Django para **listar**, **criar** e **votar** em enquetes.

---

## 🛠️ Arquitetura & Tecnologias

### Stack utilizada

| Camada | Tecnologia |
|--------|------------|
| **IDE** | Android Studio (recomendado) ou IntelliJ IDEA Ultimate |
| **UI** | Jetpack Compose |
| **Arquitetura** | MVVM (Model-View-ViewModel) |
| **Networking** | Retrofit |
| **Concorrência** | Kotlin Coroutines |
| **Gerenciamento de estado** | StateFlow, SharedFlow |
| **Navegação** | Navigation Compose |

### Justificativa

- **Jetpack Compose**: Kit de ferramentas **moderno e declarativo** para UIs nativas, substituindo XML.
- **MVVM**: Padrão recomendado pelo Google para separar UI e lógica de negócios.
- **Retrofit**: Cliente HTTP robusto e seguro.
- **Coroutines**: Simplificam código assíncrono, sem travar a UI.

---

## ⚙️ Configuração & Execução

### 1️⃣ Clonar o Repositório

```bash
git clone https://github.com/seu-usuario/seu-repositorio-android.git
cd seu-repositorio-android
```

### 2️⃣ Abrir no Android Studio

- Abra o Android Studio (ou IntelliJ IDEA).
- Selecione **Open** → escolha a pasta do projeto clonado.
- Aguarde o **Gradle** sincronizar todas as dependências.

### 3️⃣ Executar o Backend Django

- A API deve estar rodando localmente para o app funcionar.
- No projeto do backend Django:

```bash
python manage.py runserver
```

### 4️⃣ Executar o App

- Inicie um emululador Android **ou** conecte um dispositivo físico.
- No Android Studio, clique em **Run 'app'** (▶️).

---

## 🏗️ Etapas de Implementação

### 1️⃣ Camada de Rede & Dados

- **Modelos de Dados**  
  Data classes em Kotlin (`Enquete`, `Opcao`, `Payloads`) espelham o JSON da API.

- **API Service (Retrofit)**  
  Interface `ApiService` com anotações Retrofit (`@GET`, `@POST`, `@Path`, `@Body`).

- **Conexão com localhost**  
  `baseUrl`: `http://10.0.2.2:8000/`  
  (IP especial para o emulador acessar o localhost da máquina hospedeira.)

- **Permissões**  
  Adicionadas no `AndroidManifest.xml`:
  - `INTERNET`
  - `usesCleartextTraffic` (para acesso não HTTPS em dev).

---

### 2️⃣ Camada de Lógica (ViewModel & Repository)

- **Repository Pattern**  
  `PollRepository` abstrai a fonte de dados, isolando lógica de rede.

- **ViewModels**  
  Um ViewModel por tela:
  - `PollListViewModel`
  - `PollDetailViewModel`
  - `PollFormViewModel`

- **Gerenciamento de Estado**
  - `StateFlow` → estado reativo da UI (`Loading`, `Success`, `Error`).
  - `SharedFlow` → eventos únicos (toasts, snackbars).

- **Segurança de Thread**  
  Chamadas de rede em thread de fundo:  
  `withContext(Dispatchers.IO)` dentro de `viewModelScope`.

---

### 3️⃣ Camada de Apresentação (UI com Jetpack Compose)

- **Navegação**  
  `Navigation Compose` com gráfico de navegação (`AppNavigation.kt`):
  - Rota dinâmica: `/enquetes/{pollId}`.

- **Componentização**  
  Telas como funções `@Composable`:
  - `PollListScreen`
  - `PollDetailScreen`
  - `PollFormScreen`

- **UI Reativa**  
  - `LazyColumn` → listas performáticas.
  - `Card` → itens de enquete.
  - `OutlinedTextField` → formulários.
  - `remember { mutableStateOf(...) }` → gerenciamento local de estado.

---

### 4️⃣ Melhorias de Usabilidade (UX)

- **Feedback Visual**
  - **Loading indicators**
  - **Mensagens de erro / sucesso** via Snackbar.
  - **Botões desabilitados** durante operações de rede.

- **Destaque da Opção Vencedora**
  - Opção com mais votos recebe destaque visual (ex: borda colorida).

- **Simulação de Múltiplos Usuários**
  - Campo de texto para o "Nome do Participante", testando a regra **1 voto por usuário**.

---

## 🚀 Próximos Passos (Melhorias Futuras)

- Persistência offline com Room.
- Testes unitários de ViewModels.
- Testes instrumentados com Compose UI Test.
- Tela de histórico de enquetes (abertas e fechadas).
- Autenticação real (JWT).

---

## 🤝 Contribuindo

1. Fork → branch (`git checkout -b feature/xyz`)  
2. Commit claro e descritivo  
3. Abra um Pull Request explicando a motivação

---

## 📜 Licença

Distribuído sob a licença **MIT**.

---
