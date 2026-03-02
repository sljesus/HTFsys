package com.htf.system.data.repository

import android.util.Log
import com.htf.system.data.remote.AssignmentUpdateRequest
import com.htf.system.data.remote.SupabaseApiService
import com.htf.system.data.remote.SupabaseClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Clase de datos para asignaciones
data class Assignment(
    val idAsignacion: String,
    val idMiembro: String,
    val platform: String,
    val nombreCompleto: String,
    val idProducto: String,
    val nombreProducto: String,
    val fechaInicio: String,
    val fechaFin: String,
    val activa: Boolean,
    val cancelada: Boolean
)

class AssignmentRepository {
    private val apiService = SupabaseClient.apiService

    suspend fun getAssignmentsByMemberId(memberId: Int): Result<List<Assignment>> = withContext(Dispatchers.IO) {
        try {
            Log.d("HTF_APP", "=== CONSULTANDO SUPABASE PARA MIEMBRO $memberId ===")

            // Usar la vista directamente (ya tiene los JOINs: miembros, productos, device_tokens)
            val response = apiService.getAssignmentsFromView(
                idMiembro = "eq.$memberId"
            )

            if (response.isSuccessful) {
                val supabaseAssignments = response.body() ?: emptyList()
                Log.d("HTF_APP", "✅ OBTENIDOS ${supabaseAssignments.size} REGISTROS PARA MIEMBRO $memberId")

                // Convertir a formato de app
                val result = supabaseAssignments.map { supabaseItem ->
                    Assignment(
                        idAsignacion = supabaseItem.id_asignacion.toString(),
                        idMiembro = supabaseItem.id_miembro.toString(),
                        platform = supabaseItem.platform ?: "N/A",
                        nombreCompleto = supabaseItem.nombre_completo ?: "Miembro ${supabaseItem.id_miembro}",
                        idProducto = supabaseItem.id_producto_digital.toString(),
                        nombreProducto = supabaseItem.nombre_producto ?: "Producto ${supabaseItem.id_producto_digital}",
                        fechaInicio = supabaseItem.fecha_inicio.toString(),
                        fechaFin = supabaseItem.fecha_fin.toString(),
                        activa = supabaseItem.activa,
                        cancelada = supabaseItem.cancelada
                    )
                }

                Log.d("HTF_APP", "✅ PROCESADOS ${result.size} ASIGNACIONES")
                Result.success(result)
            } else {
                Log.e("HTF_APP", "❌ ERROR HTTP: ${response.code()} - ${response.message()}")
                Log.e("HTF_APP", "❌ Response body: ${response.errorBody()?.string()}")
                Result.failure(Exception("Error HTTP: ${response.code()} - ${response.message()}"))
            }

        } catch (e: Exception) {
            Log.e("HTF_APP", "❌ ERROR EN SUPABASE: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    /**
     * Actualizar una asignación (fecha_inicio, fecha_fin, activa, cancelada)
     */
    suspend fun updateAssignment(
        assignmentId: Int,
        fechaInicio: String? = null,
        fechaFin: String? = null,
        activa: Boolean? = null,
        cancelada: Boolean? = null
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            Log.d("HTF_APP", "=== ACTUALIZANDO ASIGNACIÓN $assignmentId ===")

            val updateData = AssignmentUpdateRequest(
                fechaInicio = fechaInicio,
                fechaFin = fechaFin,
                activa = activa,
                cancelada = cancelada
            )

            val response = apiService.updateAssignment(
                filter = "eq.$assignmentId",
                updateData = updateData
            )

            if (response.isSuccessful) {
                Log.d("HTF_APP", "✅ ASIGNACIÓN $assignmentId ACTUALIZADA")
                Result.success(Unit)
            } else {
                Log.e("HTF_APP", "❌ ERROR HTTP: ${response.code()} - ${response.message()}")
                Result.failure(Exception("Error HTTP: ${response.code()} - ${response.message()}"))
            }

        } catch (e: Exception) {
            Log.e("HTF_APP", "❌ ERROR EN SUPABASE: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    /**
     * Eliminar una asignación
     */
    suspend fun deleteAssignment(assignmentId: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            Log.d("HTF_APP", "=== ELIMINANDO ASIGNACIÓN $assignmentId ===")

            val response = apiService.deleteAssignment(
                filter = "eq.$assignmentId"
            )

            if (response.isSuccessful) {
                Log.d("HTF_APP", "✅ ASIGNACIÓN $assignmentId ELIMINADA")
                Result.success(Unit)
            } else {
                Log.e("HTF_APP", "❌ ERROR HTTP: ${response.code()} - ${response.message()}")
                Result.failure(Exception("Error HTTP: ${response.code()} - ${response.message()}"))
            }

        } catch (e: Exception) {
            Log.e("HTF_APP", "❌ ERROR EN SUPABASE: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
