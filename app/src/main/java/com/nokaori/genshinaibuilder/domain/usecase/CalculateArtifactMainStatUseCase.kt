package com.nokaori.genshinaibuilder.domain.usecase

import com.nokaori.genshinaibuilder.domain.model.StatCurve
import javax.inject.Inject

class CalculateArtifactMainStatUseCase @Inject constructor() {
    operator fun invoke(level: Int, curve: StatCurve?): Float {
        return curve?.points?.get(level) ?: 0f
    }
}