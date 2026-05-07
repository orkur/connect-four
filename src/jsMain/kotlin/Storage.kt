package org.example

import kotlinx.browser.localStorage
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private const val GameStorageKey = "connect_four"

private val json = Json {
    ignoreUnknownKeys = true
}

fun saveGameState(game: GameState) {
    localStorage.setItem(GameStorageKey, json.encodeToString(game))
}

fun loadGameState(): GameState? {
    return try {
        localStorage.getItem(GameStorageKey)?.let { saved -> json.decodeFromString<GameState>(saved) }
    } catch (e: Throwable) {
        null
    }
}

fun clearSavedGameState() {
    localStorage.removeItem(GameStorageKey)
}