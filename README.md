# Sentinel SDK

**Sentinel** é uma biblioteca Android para detecção de anomalias e possíveis fraudes em tempo real no ambiente de execução do seu aplicativo.

## Funcionalidades

O SDK monitora continuamente uma variedade de sinais, incluindo:

- **Status do Dispositivo**:
  - Depuração USB (Debugging)
  - Emuladores (Emulated)
  - Acesso Root (Jailbroken/Rooted)
  - Uso de Proxy
  - Mascaramento de Dispositivo (Device Masking)

- **Hooks (Hooks)**:
  - Frameworks de Hooking (ex: Xposed, Frida)

- **Apps e Comportamentos Suspeitos**:
  - Auto-Clickers
  - Clonagem de Apps (App Cloning)
  - GPS Spoofing
  - Compartilhamento de Tela (Screen Sharing)
  - Conexão VPN
  - Sistemas Operacionais Virtuais (Virtual OS)
  - Reset Suspeito (Suspicious Reset)

## Como Usar

### 1. Dependências

Adicione-o ao final da seção `repositories` no arquivo `settings.gradle` da raiz do seu projeto:

```kotlin
dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    mavenCentral()
    maven { url 'https://jitpack.io' }
  }
}
```

Adicione a dependência ao seu arquivo `build.gradle.kts` nivel modulo/app:

```kotlin
dependencies {
    implementation(project("com.github.evd-evanss:Sentinel:<last_version>"))
}
```

### 2. Inicializar o SDK

Na classe `Application` do seu app, chame o método `Sentinel.initialize()` dentro do `onCreate()`.

- **`context`**: O contexto da aplicação.
- **`environment`**: (Opcional) Use `Environment.STAGE` para ver logs detalhados no Logcat. O padrão é `Environment.PROD`.

**Exemplo (`YourApplication.kt`):**

```kotlin
import com.sugarspoon.sentinel.Sentinel
import com.sugarspoon.sentinel.Environment

class YourApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Inicializa o Sentinel
        Sentinel.initialize(this, Environment.STAGE)
    }
}
```

Não se esqueça de registrar a sua classe `Application` no `AndroidManifest.xml`:

```xml
<application
    android:name=".YourApplication"
    ...
>
</application>
```

### 3. Observar os Resultados em um Composable

Para monitorar os resultados em tempo real na sua UI com Jetpack Compose, colete o `StateFlow` `Sentinel.detectionResult`.

**Exemplo (`YourComposable.kt`):**

```kotlin
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.sugarspoon.sentinel.Sentinel

@Composable
fun YourScreen() {
    val detectionResult by Sentinel.detectionResult.collectAsState()

    // Agora você pode usar os resultados na sua UI
    if (detectionResult.isEmulated) {
        // Mostra um aviso
    }
}
```
