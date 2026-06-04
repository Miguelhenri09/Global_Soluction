# InovaGAB — Plataforma de Inovação Corporativa
**FIAP × Grupo Águia Branca | Sprint 1**

---

## 📱 Stack Tecnológica

| Camada | Tecnologia |
|--------|-----------|
| Linguagem | Kotlin |
| UI | Jetpack Compose + Material3 |
| Arquitetura | Clean Architecture + MVVM |
| DI | Hilt |
| Autenticação | Firebase Auth (Email/Senha) |
| Banco de Dados | Firebase Firestore (real-time) |
| Navegação | Navigation Compose |
| Estado | StateFlow + collectAsState |

---

## 🚀 Como rodar o projeto

### 1. Pré-requisitos
- Android Studio Hedgehog ou superior
- JDK 17
- Conta Google para Firebase

### 2. Configurar Firebase

1. Acesse [console.firebase.google.com](https://console.firebase.google.com)
2. Crie um projeto chamado **InovaGAB**
3. Adicione app Android: `br.com.inovagab`
4. Baixe `google-services.json` e substitua o arquivo em `app/google-services.json`
5. No console Firebase:
   - **Authentication** → Ativar provedor **E-mail/Senha**
   - **Firestore Database** → Criar banco em modo de teste

### 3. Regras do Firestore (cole no console)

```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{userId} {
      allow read: if request.auth != null;
      allow write: if request.auth.uid == userId;
    }
    match /ideas/{ideaId} {
      allow read: if request.auth != null;
      allow create: if request.auth != null;
      allow update: if request.auth != null;
    }
    match /projects/{projectId} {
      allow read: if request.auth != null;
      allow write: if request.auth != null;
    }
    match /guidelines/{guidelineId} {
      allow read: if request.auth != null;
      allow write: if request.auth != null;
    }
  }
}
```

### 4. Criar usuários de teste

No Firebase Authentication, crie manualmente:
- `operador@gab.com` / `123456`
- `gestor@gab.com` / `123456`
- `lider@gab.com` / `123456`

No Firestore, crie a coleção `users` com documentos usando os UIDs gerados:

```json
// Operador
{
  "name": "João Operador",
  "email": "operador@gab.com",
  "role": "OPERATOR",
  "department": "Logística"
}

// Gestor
{
  "name": "Maria Gestora",
  "email": "gestor@gab.com",
  "role": "MANAGER",
  "department": "Inovação"
}

// Líder
{
  "name": "Carlos Líder",
  "email": "lider@gab.com",
  "role": "LEADER",
  "department": "Diretoria"
}
```

### 5. Rodar

Abra o projeto no Android Studio e clique em **Run**.

---

## 🗂️ Estrutura do Projeto

```
app/src/main/java/br/com/inovagab/
├── InovaGABApp.kt              # Application class (Hilt)
├── MainActivity.kt             # Entry point
├── di/
│   └── AppModule.kt            # Hilt providers (Firebase)
├── domain/model/
│   └── Models.kt               # User, Idea, Project, Guideline
├── data/repository/
│   └── InovaGABRepository.kt   # Firestore + Auth operations
└── ui/
    ├── theme/Theme.kt           # Material3 color scheme
    ├── navigation/
    │   ├── Screen.kt            # Sealed routes
    │   └── AppNavGraph.kt       # NavHost wiring
    ├── shared/
    │   └── SharedComponents.kt  # Reusable composables
    ├── auth/
    │   ├── AuthViewModel.kt
    │   └── LoginScreen.kt
    ├── operator/
    │   ├── OperatorViewModel.kt
    │   └── OperatorScreens.kt   # Home, Ideas, NewIdea, Guidelines
    ├── manager/
    │   ├── ManagerViewModel.kt
    │   └── ManagerScreens.kt    # Home, Ideas, Projects, Form
    └── leader/
        ├── LeaderViewModel.kt
        └── LeaderScreens.kt     # Home, Projects, Dashboard, Guidelines
```

---

## 👥 Perfis de Usuário

| Perfil | Funcionalidades |
|--------|----------------|
| **Operador** | Ver diretrizes, cadastrar ideias, acompanhar status das suas ideias |
| **Gestor** | Curadoria de ideias (avaliar/priorizar/aprovar), criar e atualizar projetos |
| **Líder** | Dashboard executivo (ROI, investimento), acompanhar projetos, CRUD de diretrizes |

---

## 🔥 Coleções Firestore

| Coleção | Descrição |
|---------|-----------|
| `users` | Cadastro e perfil dos usuários |
| `ideas` | Ideias/problemas cadastrados pelos operadores |
| `projects` | Projetos criados pelos gestores |
| `guidelines` | Diretrizes estratégicas geridas pela liderança |
