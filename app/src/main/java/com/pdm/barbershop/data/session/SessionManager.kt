package com.pdm.barbershop.data.session

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor() {
    private val _sessionExpired = MutableSharedFlow<Unit>(replay = 0, extraBufferCapacity = 1)
    val sessionExpired: SharedFlow<Unit> = _sessionExpired

    suspend fun notifyExpired() { _sessionExpired.emit(Unit) }
}

