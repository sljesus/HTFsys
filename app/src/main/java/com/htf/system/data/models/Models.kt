package com.htf.system.data.models

import kotlinx.serialization.Serializable

@Serializable
data class Assignment(
    val idAsignacion: String,
    val idMiembro: String,
    val platform: String?,
    val nombreCompleto: String,
    val idProducto: String,
    val nombreProducto: String,
    val fechaInicio: String,
    val fechaFin: String,
    val activa: Boolean,
    val cancelada: Boolean
)

data class Member(
    val id: Int,
    val nombre: String,
    val email: String,
    val telefono: String?,
    val fechaRegistro: String
)

data class Fine(
    val id: Int,
    val idMiembro: Int,
    val monto: Double,
    val pagada: Boolean,
    val fechaCreacion: String
)

data class DeviceToken(
    val id: Int,
    val idMiembro: Int,
    val token: String,
    val platform: String,
    val createdAt: String,
    val updatedAt: String
)
