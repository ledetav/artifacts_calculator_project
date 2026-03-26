package com.nokaori.genshinaibuilder.domain.util

fun main() {
    val parser = ArtifactOcrParser
    val fakePieces = listOf(
        com.nokaori.genshinaibuilder.domain.repository.PieceMatchInfo(
            setId = 1, setName = "Странствующий ансамбль", slot = com.nokaori.genshinaibuilder.domain.model.ArtifactSlot.PLUME_OF_DEATH, name = "Оперение стрелы барда"
        ),
        com.nokaori.genshinaibuilder.domain.repository.PieceMatchInfo(
            setId = 2, setName = "Рассветная песнь звезды и луны", slot = com.nokaori.genshinaibuilder.domain.model.ArtifactSlot.SANDS_OF_EON, name = "Последний час лунного подношения"
        ),
        com.nokaori.genshinaibuilder.domain.repository.PieceMatchInfo(
            setId = 3, setName = "День восходящих ветров", slot = com.nokaori.genshinaibuilder.domain.model.ArtifactSlot.GOBLET_OF_EONOTHEM, name = "Неизречённый эпос банкета"
        )
    )

    val image5 = """
        Оперение стрелы барда
        Перо смерти
        Сила атаки
        152
        +8
        • Шанс крит. попадания +3,5%
        • HP +209
        • Крит. урон +13,2%
        • Защита +39
        Странствующий ансамбль:
        2 предмет(а): Увеличивает
        Надето: Иллуги
    """.trimIndent()

    val res = parser.parse(image5, fakePieces)
    println(res)
}
