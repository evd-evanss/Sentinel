package com.sugarspoon.sentinel

/**
 * Define o ambiente de execução para controlar recursos como logs.
 */
enum class Environment {
    /** Ambiente de produção, com logs desativados. */
    PROD,

    /** Ambiente de desenvolvimento/stage, com logs de depuração ativados. */
    STAGE
}
