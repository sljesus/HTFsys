package com.htf.system.data.repository

import android.util.Log
import com.htf.system.data.remote.AssignmentUpdateRequest
import com.htf.system.data.remote.SupabaseApiService
import com.htf.system.data.remote.SupabaseClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

// Clase de datos para pagos pendientes
data class PendingPayment(
    val idVentaDigital: Int,
    val idProductoDigital: Int,
    val monto: Double,
    val fechaCompra: String
)

// Clase de datos para una venta (cualquier estado)
data class Sale(
    val idVentaDigital: Int,
    val idProductoDigital: Int,
    val monto: Double,
    val estado: String,
    val metodoPago: String,
    val fechaCompra: String
)

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
     * Buscar asignaciones por nombre de miembro (búsqueda parcial, case-insensitive)
     */
    suspend fun getAssignmentsByMemberName(memberName: String): Result<List<Assignment>> = withContext(Dispatchers.IO) {
        try {
            Log.d("HTF_APP", "=== BUSCANDO SUPABASE POR NOMBRE: $memberName ===")

            // Usar ilike para búsqueda parcial case-insensitive (Supabase usa * como comodín)
            val response = apiService.getAssignmentsByMemberName(
                nombreCompleto = "ilike.*${memberName}*"
            )

            if (response.isSuccessful) {
                val supabaseAssignments = response.body() ?: emptyList()
                Log.d("HTF_APP", "✅ OBTENIDOS ${supabaseAssignments.size} REGISTROS PARA '$memberName'")

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

    /**
     * Borra los registros de entrada de HOY de un miembro, para que pueda volver a entrar
     * (corrige el caso de "días gratis"/errores de asignación que le bloquean el acceso hoy).
     */
    suspend fun resetTodayAccess(memberId: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val timeZone = TimeZone.getTimeZone("America/Mexico_City")
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply { this.timeZone = timeZone }
            val hoy = dateFormat.format(Date())
            val manana = dateFormat.format(Date(Date().time + 24 * 60 * 60 * 1000))

            Log.d("HTF_APP", "=== RESETEANDO ACCESO DE HOY PARA MIEMBRO $memberId ($hoy) ===")

            val response = apiService.deleteTodayEntradas(
                idMiembroFilter = "eq.$memberId",
                fechaDesdeFilter = "gte.${hoy}T00:00:00",
                fechaHastaFilter = "lt.${manana}T00:00:00"
            )

            if (response.isSuccessful) {
                Log.d("HTF_APP", "✅ ACCESO DE HOY RESETEADO PARA MIEMBRO $memberId")
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
     * Obtiene los pagos en efectivo pendientes de un miembro
     */
    suspend fun getPendingPayments(memberId: Int): Result<List<PendingPayment>> = withContext(Dispatchers.IO) {
        try {
            Log.d("HTF_APP", "=== CONSULTANDO PAGOS PENDIENTES PARA MIEMBRO $memberId ===")

            val response = apiService.getPendingPayments(idMiembro = "eq.$memberId")

            if (response.isSuccessful) {
                val pagos = (response.body() ?: emptyList()).map {
                    PendingPayment(
                        idVentaDigital = it.id_venta_digital,
                        idProductoDigital = it.id_producto_digital,
                        monto = it.monto,
                        fechaCompra = it.fecha_compra
                    )
                }
                Log.d("HTF_APP", "✅ ${pagos.size} PAGOS PENDIENTES ENCONTRADOS")
                Result.success(pagos)
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
     * Borra un pago pendiente: la venta digital Y su notificación POS asociada juntas.
     * Si solo se borrara la venta, el miembro quedaría con un código de barras viejo
     * atascado (el chequeo de duplicados de create-cash-payment-pending solo mira si existe
     * una notificación sin responder, no si la venta detrás sigue existiendo).
     */
    suspend fun deletePendingPayment(idVentaDigital: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            Log.d("HTF_APP", "=== BORRANDO PAGO PENDIENTE $idVentaDigital ===")

            val notifResponse = apiService.deleteNotificacionPorVenta(filter = "eq.$idVentaDigital")
            if (!notifResponse.isSuccessful) {
                Log.w("HTF_APP", "⚠️ No se pudo borrar la notificación asociada (continuando): ${notifResponse.code()}")
            }

            val ventaResponse = apiService.deletePendingPayment(filter = "eq.$idVentaDigital")

            if (ventaResponse.isSuccessful) {
                Log.d("HTF_APP", "✅ PAGO PENDIENTE $idVentaDigital BORRADO")
                Result.success(Unit)
            } else {
                Log.e("HTF_APP", "❌ ERROR HTTP: ${ventaResponse.code()} - ${ventaResponse.message()}")
                Result.failure(Exception("Error HTTP: ${ventaResponse.code()} - ${ventaResponse.message()}"))
            }
        } catch (e: Exception) {
            Log.e("HTF_APP", "❌ ERROR EN SUPABASE: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    /**
     * Obtiene las últimas ventas (cualquier estado) de un miembro, para localizar y corregir
     * cobros históricos incorrectos (ej. renovación mal cobrada a $600 en vez de $500).
     */
    suspend fun getMemberSales(memberId: Int): Result<List<Sale>> = withContext(Dispatchers.IO) {
        try {
            Log.d("HTF_APP", "=== CONSULTANDO VENTAS PARA MIEMBRO $memberId ===")

            val response = apiService.getMemberSales(idMiembro = "eq.$memberId")

            if (response.isSuccessful) {
                val ventas = (response.body() ?: emptyList()).map {
                    Sale(
                        idVentaDigital = it.id_venta_digital,
                        idProductoDigital = it.id_producto_digital,
                        monto = it.monto,
                        estado = it.estado,
                        metodoPago = it.metodo_pago,
                        fechaCompra = it.fecha_compra
                    )
                }
                Log.d("HTF_APP", "✅ ${ventas.size} VENTAS ENCONTRADAS")
                Result.success(ventas)
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
     * Corrige el monto de una venta ya existente (ej. $600 → $500 en renovaciones cobradas
     * de más por versiones viejas de la app — ver hallazgo #19 del registro de bugs).
     */
    suspend fun updateSaleAmount(idVentaDigital: Int, newAmount: Double): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            Log.d("HTF_APP", "=== CORRIGIENDO MONTO DE VENTA $idVentaDigital A \$$newAmount ===")

            val response = apiService.updateSaleAmount(
                filter = "eq.$idVentaDigital",
                updateData = com.htf.system.data.remote.SaleAmountUpdateRequest(monto = newAmount)
            )

            if (response.isSuccessful) {
                Log.d("HTF_APP", "✅ MONTO DE VENTA $idVentaDigital CORREGIDO")
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
