// app/src/main/java/com/nokaori/genshinaibuilder/domain/util/FuzzySearchUtils.kt
package com.nokaori.genshinaibuilder.domain.util

import kotlin.math.min

object FuzzySearchUtils {
    fun normalizeText(text: String): String {
        return text.lowercase().replace(Regex("[^\\p{L}\\p{N}]+"), "")
    }

    fun levenshteinDistance(s1: String, s2: String): Int {
        val a = normalizeText(s1)
        val b = normalizeText(s2)

        if (a.isEmpty()) return b.length
        if (b.isEmpty()) return a.length

        val dp = Array(a.length + 1) { IntArray(b.length + 1) }

        for (i in 0..a.length) dp[i][0] = i
        for (j in 0..b.length) dp[0][j] = j

        for (i in 1..a.length) {
            for (j in 1..b.length) {
                val cost = if (a[i - 1] == b[j - 1]) 0 else 1
                dp[i][j] = min(
                    dp[i - 1][j - 1] + cost, // Замена
                    min(
                        dp[i - 1][j] + 1,
                        dp[i][j - 1] + 1
                    )
                )
            }
        }
        return dp[a.length][b.length]
    }

    inline fun <T> findBestMatch(
        query: String,
        candidates: List<T>,
        textSelector: (T) -> String,
        maxAllowedDistance: Int = 3
    ): Pair<T, Int>? {
        val normalizedQuery = normalizeText(query)
        if (normalizedQuery.isEmpty()) return null

        var bestMatch: T? = null
        var minDistance = Int.MAX_VALUE

        for (candidate in candidates) {
            val candidateText = textSelector(candidate)
            val distance = levenshteinDistance(normalizedQuery, candidateText)

            if (distance == 0) return Pair(candidate, 0)

            if (distance < minDistance) {
                minDistance = distance
                bestMatch = candidate
            }
        }

        return if (bestMatch != null && minDistance <= maxAllowedDistance) {
            Pair(bestMatch, minDistance)
        } else null
    }
}