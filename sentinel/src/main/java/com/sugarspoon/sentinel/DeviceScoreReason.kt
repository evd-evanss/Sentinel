package com.sugarspoon.sentinel

enum class SentinelIndicator(val reasonLabel: String) {
    ROOT("Acesso root detectado"),
    HOOKING("Framework de hooking ativo"),
    DEVICE_MASKING("Mascaramento de dispositivo detectado"),
    EMULATOR("Ambiente emulado detectado"),
    VIRTUAL_OS("Sistema virtual detectado"),
    APP_CLONING("App rodando em ambiente clonado"),
    SUSPICIOUS_RESET("Reset suspeito detectado"),
    GPS_SPOOFING("GPS spoofing detectado"),
    AUTO_CLICKER("Auto-clicker ativo"),
    SCREEN_SHARING("Compartilhamento de tela detectado"),
    DEBUGGING("Debugging habilitado"),
    VPN("VPN ativa"),
    PROXY("Proxy ativo")
}

data class DeviceScoreReason(
    val indicator: SentinelIndicator,
    val penalty: Int,
)
