# Sentinel SDK

**Sentinel** é uma biblioteca Android para detecção de anomalias e possíveis fraudes em tempo real no ambiente de execução do seu aplicativo.

A ideia do SDK é funcionar como um guardião silencioso: ele observa sinais do dispositivo, calcula um **Device Score** e permite que o app tome decisões proporcionais ao risco encontrado.

Leia também o artigo: [Sentinel: o guardião silencioso que protege seu app Android contra fraudes](https://medium.com/@sayhitoiot/sentinel-o-guardi%C3%A3o-silencioso-que-protege-seu-app-android-contra-fraudes-7215b185c2a6).

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

## Device Score

O **Device Score** é uma pontuação de confiança do dispositivo calculada a cada ciclo de monitoramento. O valor começa em `100` e cada sinal de risco detectado reduz a pontuação conforme sua severidade. O resultado final nunca fica abaixo de `0`.

Use o score como uma leitura consolidada de risco:

| Score | Interpretação sugerida |
| --- | --- |
| `76 - 100` | Dispositivo com baixo risco aparente |
| `41 - 75` | Dispositivo com sinais de atenção |
| `0 - 40` | Dispositivo com alto risco |

### Pesos atuais

| Indicador | Penalidade |
| --- | ---: |
| Root detectado | `-20` |
| Hooking detectado, como Xposed ou Frida | `-20` |
| Mascaramento de dispositivo | `-15` |
| Emulador | `-15` |
| Sistema operacional virtual | `-15` |
| App clonado | `-10` |
| Reset suspeito | `-10` |
| GPS spoofing | `-10` |
| Auto-clicker | `-10` |
| Compartilhamento de tela | `-5` |
| Debugging habilitado | `-5` |
| VPN ativa | `-5` |
| Proxy configurado | `-5` |

Exemplo: um dispositivo com root (`-20`), VPN ativa (`-5`) e debugging habilitado (`-5`) terá `deviceScore = 70`.

> O Device Score é uma heurística de risco. Evite bloquear usuários apenas por um único sinal leve; prefira combinar o score com contexto de negócio, autenticação, histórico do usuário e observabilidade.

## Como Usar

### 1. Dependências

Adicione-o ao final da seção `repositories` no arquivo `settings.gradle` da raiz do seu projeto:

```kotlin
dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
  }
}
```

Adicione a dependência ao seu arquivo `build.gradle.kts` no nível módulo/app:

```kotlin
dependencies {
    implementation("com.github.evd-evanss:Sentinel:<last_version>")
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

### 4. Usar o Device Score no App

O `deviceScore` faz parte de `DetectionResult` e pode ser usado para ajustar a experiência do usuário, exigir uma etapa extra de validação ou enviar métricas para sua stack de observabilidade.

```kotlin
val result by Sentinel.detectionResult.collectAsState()

when {
    result.deviceScore <= 40 -> {
        // Alto risco: reforce autenticação, limite operações sensíveis
        // ou envie o evento para análise.
    }
    result.deviceScore <= 75 -> {
        // Atenção: monitore e aplique validações adicionais se necessário.
    }
    else -> {
        // Baixo risco aparente.
    }
}
```

### 5. Receber Métricas em um Listener

Se você quiser enviar o resultado para ferramentas como Sentry, Firebase, Datadog ou um backend próprio, implemente `FraudMetricListener`:

```kotlin
import android.app.Application
import com.sugarspoon.sentinel.DetectionResult
import com.sugarspoon.sentinel.Environment
import com.sugarspoon.sentinel.FraudMetricListener
import com.sugarspoon.sentinel.Sentinel

class YourApplication : Application(), FraudMetricListener {
    override fun onCreate() {
        super.onCreate()

        Sentinel.initialize(this, Environment.STAGE, enableLogs = true)
        Sentinel.setListener(this)
    }

    override fun onMetricsGenerated(result: DetectionResult, deviceId: String?) {
        val score = result.deviceScore

        // Envie score, deviceId e indicadores detectados para sua observabilidade.
    }
}
```

## Permissões Recomendadas

Alguns checks dependem de permissões do Android. O SDK continua funcionando sem elas, mas os sinais relacionados podem ficar incompletos.

```xml
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```

As permissões de localização ajudam nos sinais de GPS spoofing e enriquecem o resultado com latitude/longitude quando disponível.

## Sugestões de Evolução

- Expor uma configuração de pesos para que cada app ajuste o Device Score ao seu próprio modelo de risco.
- Incluir uma lista de motivos do score, por exemplo `scoreReasons`, para explicar quais indicadores reduziram a pontuação.
- Separar severidade técnica de ação de negócio: o SDK calcula risco, e o app decide se bloqueia, alerta ou apenas monitora.
- Adicionar testes unitários para validar combinações de indicadores e impedir regressões nos pesos do Device Score.
- Enviar apenas dados necessários para observabilidade, evitando expor localização ou identificadores quando não forem essenciais.
