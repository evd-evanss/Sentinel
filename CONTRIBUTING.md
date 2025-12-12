# Padrão de Commits

Para manter o histórico do projeto limpo e permitir a automação de tarefas como a geração de changelogs, este projeto utiliza o padrão **Conventional Commits**.

## Formato

Cada mensagem de commit deve seguir o formato:

```
<tipo>[escopo opcional]: <descrição>
```

## Tipos de Commit

A seguir estão os tipos mais importantes para garantir que a automação de changelog funcione corretamente:

- **`feat`**: Para uma nova funcionalidade.
  - *Exemplo*: `feat: Add screen sharing detection`

- **`fix`**: Para uma correção de bug.
  - *Exemplo*: `fix: Emulator detection now works on API 34`

- **`build`**: Mudanças que afetam o sistema de build ou dependências externas.
  - *Exemplo*: `build: Update Kotlin version`

- **`refactor`**: Uma mudança de código que não corrige um bug nem adiciona uma nova funcionalidade.
  - *Exemplo*: `refactor: Simplify singleton initialization`

- **`docs`**: Mudanças apenas na documentação.
  - *Exemplo*: `docs: Explain conventional commit standard`

Usar este padrão é crucial para que a action do GitHub consiga gerar o `CHANGELOG.md` de forma correta e automática.
