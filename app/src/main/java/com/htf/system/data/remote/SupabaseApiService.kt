package com.htf.system.data.remote

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

// API Service para Supabase usando Retrofit
interface SupabaseApiService {
    
    // Consultar la vista directamente (ya tiene los JOINs)
    @GET("v_assignments_details")
    suspend fun getAssignmentsFromView(
        @Query("select") select: String = "*",
        @Query("id_miembro") idMiembro: String,
        @Query("limit") limit: Int = 100,
        @Query("order") order: String = "id_asignacion.desc"
    ): Response<List<AssignmentDetailsResponse>>
}

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
