package com.nokaori.genshinaibuilder.domain.model

enum class SynergyRating {
    PERFECT,      // Идеальная (Meta команды, National, Hyperbloom)
    GOOD,         // Хорошая (Рабочая команда, закрывает контент)
    AVERAGE,      // Средняя (Играть можно, но звезд с неба не хватает)
    BAD,          // Плохая (Нет реакций, персонажи не помогают друг другу)
    NEGATIVE      // Отрицательная (Персонажи мешают друг другу)
}