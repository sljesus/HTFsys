package com.htf.system.data.remote

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

// API Service para Supabase usando Retrofit
interface SupabaseApiService {
    
    @GET("asignaciones_activas")
    suspend fun getAllAssignments(
        @Query("select") select: String = "*",
        @Query("limit") limit: Int = 1000,
        @Query("order") order: String = "id_asignacion.desc"
    ): Response<List<AssignmentResponse>>
    
    @GET("asignaciones_activas")
    suspend fun getAssignmentsByMember(
        @Query("select") select: String = "*",
        @Query("id_miembro") idMiembro: Int,
        @Query("limit") limit: Int = 100,
        @Query("order") order: String = "id_asignacion.desc"
    ): Response<List<AssignmentResponse>>
    
    // Consultar la vista directamente (ya tiene los JOINs)
    @GET("v_assignments_details")
    suspend fun getAssignmentsFromView(
        @Query("select") select: String = "*",
        @Query("id_miembro") idMiembro: String,
        @Query("limit") limit: Int = 100,
        @Query("order") order: String = "id_asignacion.desc"
    ): Response<List<AssignmentDetailsResponse>>
}

// Response data classes
data class AssignmentResponse(
    @SerializedName("id_asignacion") val id_asignacion: Int,
    @SerializedName("id_miembro") val id_miembro: Int,
    @SerializedName("id_producto_digital") val id_producto_digital: Int,
    @SerializedName("id_venta") val id_venta: Any? = null,
    @SerializedName("id_locker") val id_locker: Any? = null,
    @SerializedName("fecha_inicio") val fecha_inicio: String,
    @SerializedName("fecha_fin") val fecha_fin: String,
    @SerializedName("activa") val activa: Boolean,
    @SerializedName("cancelada") val cancelada: Boolean,
    @SerializedName("fecha_cancelacion") val fecha_cancelacion: Any? = null,
    @SerializedName("fecha_registro") val fecha_registro: String,
    @SerializedName("usos_disponibles") val usos_disponibles: Int,
    @SerializedName("usos_total") val usos_total: Int,
    @SerializedName("id_venta_digital") val id_venta_digital: Int? = null,
    // Datos relacionados (JOIN)
    @SerializedName("miembros") val miembros: MiembroData? = null,
    @SerializedName("ca_productos_digitales") val ca_productos_digitales: ProductoData? = null
)

// Datos del miembro para JOIN
data class MiembroData(
    @SerializedName("nombres") val nombres: String? = null,
    @SerializedName("apellido_paterno") val apellido_paterno: String? = null,
    @SerializedName("apellido_materno") val apellido_materno: String? = null
)

// Datos del producto para JOIN
data class ProductoData(
    @SerializedName("nombre") val nombre: String? = null
)

// Datos del device token para JOIN
data class DeviceTokenData(
    @SerializedName("platform") val platform: String? = null
)

// Response para la vista con detalles (incluye nombre completo)
data class AssignmentDetailsResponse(
    @SerializedName("id_asignacion") val id_asignacion: Int,
    @SerializedName("id_miembro") val id_miembro: Int,
    @SerializedName("platform") val platform: String?,
    @SerializedName("nombre_completo") val nombre_completo: String?,
    @SerializedName("id_producto_digital") val id_producto_digital: Int,
    @SerializedName("nombre_producto") val nombre_producto: String?,
    @SerializedName("fecha_inicio") val fecha_inicio: String,
    @SerializedName("fecha_fin") val fecha_fin: String,
    @SerializedName("activa") val activa: Boolean,
    @SerializedName("cancelada") val cancelada: Boolean,
    @SerializedName("usos_disponibles") val usos_disponibles: Int,
    @SerializedName("usos_total") val usos_total: Int
)
