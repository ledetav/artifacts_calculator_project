package com.nokaori.genshinaibuilder.presentation.viewmodel

sealed class EditorUiEvent {
    data object NavigateBack : EditorUiEvent()
    data object BatchCompleted : EditorUiEvent()
}
