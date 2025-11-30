package com.pdm.barbershop.util

import java.time.*
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

object DateTimeUtils {

    private val DATE_FMT: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    private val TIME_FMT: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    // Define Manaus como padrão para todo o app
    val DEFAULT_ZONE_ID: ZoneId = ZoneId.of("America/Manaus")

    // Converte data + hora local para Instant UTC
    fun toStartInstant(dateIso: String, timeHHmm: String, zoneId: ZoneId = DEFAULT_ZONE_ID): Instant {
        val date = LocalDate.parse(dateIso, DATE_FMT)
        val time = LocalTime.parse(timeHHmm, TIME_FMT)
        return date.atTime(time).atZone(zoneId).toInstant()
    }

    // Converte data + hora local para string UTC ISO-8601 (termina com Z)
    fun toStartTimeUtc(dateIso: String, timeHHmm: String, zoneId: ZoneId = DEFAULT_ZONE_ID): String {
        return toStartInstant(dateIso, timeHHmm, zoneId).truncatedTo(ChronoUnit.SECONDS).let { DateTimeFormatter.ISO_INSTANT.format(it) }
    }

    // Verifica se um instant está no passado
    fun isPast(instant: Instant, toleranceSeconds: Long = 0): Boolean {
        val now = Instant.now().plusSeconds(toleranceSeconds)
        return instant.isBefore(now)
    }

    // Extrai label HH:mm de um ISO completo para exibir na UI
    fun labelFromIso(iso: String, zoneId: ZoneId = DEFAULT_ZONE_ID): String {
        val odt = OffsetDateTime.parse(iso)
        val local = odt.atZoneSameInstant(zoneId).toLocalTime()
        return "%02d:%02d".format(local.hour, local.minute)
    }
    
    // Extrai label DD/MM de um ISO completo
    fun dateLabelFromIso(iso: String, zoneId: ZoneId = DEFAULT_ZONE_ID): String {
        val odt = OffsetDateTime.parse(iso)
        val local = odt.atZoneSameInstant(zoneId).toLocalDate()
        return "%02d/%02d".format(local.dayOfMonth, local.monthValue)
    }

    // Filtra horários futuros (útil para hoje)
    fun filterFutureTimes(dateIso: String, times: List<String>, zoneId: ZoneId = DEFAULT_ZONE_ID): List<String> {
        val today = LocalDate.now(zoneId).format(DATE_FMT)
        if (dateIso != today) return times
        return times.filter {
            val inst = toStartInstant(dateIso, it, zoneId)
            !isPast(inst, toleranceSeconds = 60) // 1min de tolerância
        }
    }

    // Verifica se um ISO (com offset) está no passado
    fun isIsoPast(iso: String): Boolean {
        val start = OffsetDateTime.parse(iso).toInstant()
        return start.isBefore(Instant.now())
    }

    // Converte um ISO com offset (ex.: 2025-11-13T13:50:00-03:00) para UTC sem milissegundos e com 'Z'
    fun toUtcZ(iso: String): String {
        val instant = OffsetDateTime.parse(iso).toInstant().truncatedTo(ChronoUnit.SECONDS)
        return DateTimeFormatter.ISO_INSTANT.format(instant) // ex.: 2025-11-13T16:50:00Z
    }
}
