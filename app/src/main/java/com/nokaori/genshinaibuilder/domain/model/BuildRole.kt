package com.nokaori.genshinaibuilder.domain.model

enum class BuildRole {
    // --- УРОН ---
    ON_FIELD_DPS,   // Основной ДД, который проводит время на поле
    OFF_FIELD_DPS,  // Карманный ДД
    BURST_SUPPORT,  // "Нюкер", выходит только нажать ульту

    // --- ЗАЩИТА ---
    HEALER,         // Лечение
    SHIELDER,       // Щит
    TANK,           // Впитывание урона

    // --- УТИЛИТА ---
    BATTERY,        // Генерация энергии для команды
    BUFFER,         // Усиление статов союзников
    DEBUFFER,       // Ослабление врагов
    CROWD_CONTROL,  // Стяжка врагов
    
    // --- РЕАКЦИИ ---
    ELEMENTAL_APPLICATOR, // Накладывает статус для реакций
    DRIVER                // "Водитель" реакций, бьет рукой, чтобы работали ульты других
}