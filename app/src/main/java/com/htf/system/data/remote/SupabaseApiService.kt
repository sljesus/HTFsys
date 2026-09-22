package com.htf.system.data.remote

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.*

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

    // Buscar por nombre de miembro (búsqueda parcial, case-insensitive)
    @GET("v_assignments_details")
    suspend fun getAssignmentsByMemberName(
        @Query("select") select: String = "*",
        @Query("nombre_completo") nombreCompleto: String,
        @Query("limit") limit: Int = 100,
        @Query("order") order: String = "nombre_completo.asc,id_asignacion.desc"
    ): Response<List<AssignmentDetailsResponse>>

    // Actualizar una asignación (fecha_inicio, fecha_fin, activa, cancelada)
    @PATCH("asignaciones_activas")
    suspend fun updateAssignment(
        @Query("id_asignacion") filter: String,
        @Body updateData: AssignmentUpdateRequest
    ): Response<Unit>

    // Eliminar una asignación
    @DELETE("asignaciones_activas")
    suspend fun deleteAssignment(
        @Query("id_asignacion") filter: String
    ): Response<Unit>

    // Borrar los registros de entrada de HOY de un miembro (para que pueda volver a entrar)
    @DELETE("registro_entradas")
    suspend fun deleteTodayEntradas(
        @Query("id_miembro") idMiembroFilter: String,
        @Query("fecha_entrada") fechaDesdeFilter: String,
        @Query("fecha_entrada") fechaHastaFilter: String
    ): Response<Unit>

    // Listar pagos en efectivo pendientes de un miembro
    @GET("ventas_digitales")
    suspend fun getPendingPayments(
        @Query("id_miembro") idMiembro: String,
        @Query("estado") estado: String = "eq.pendiente_pago",
        @Query("select") select: String = "id_venta_digital,id_miembro,id_producto_digital,monto,fecha_compra",
        @Query("order") order: String = "fecha_compra.desc"
    ): Response<List<PendingPaymentResponse>>

    // Borrar una venta digital pendiente
    @DELETE("ventas_digitales")
    suspend fun deletePendingPayment(
        @Query("id_venta_digital") filter: String
    ): Response<Unit>

    // Borrar la notificación POS asociada (para que el miembro no quede con un código viejo
    // atascado — ver hallazgo #20 del registro de bugs)
    @DELETE("notificaciones_pos")
    suspend fun deleteNotificacionPorVenta(
        @Query("id_venta_digital") filter: String
    ): Response<Unit>

    // Listar ventas (cualquier estado) de un miembro, más recientes primero — para localizar
    // y corregir cobros históricos incorrectos (ej. $600 en vez de $500)
    @GET("ventas_digitales")
    suspend fun getMemberSales(
        @Query("id_miembro") idMiembro: String,
        @Query("select") select: String = "id_venta_digital,id_miembro,id_producto_digital,monto,estado,metodo_pago,fecha_compra",
        @Query("order") order: String = "fecha_compra.desc",
        @Query("limit") limit: Int = 30
    ): Response<List<SaleResponse>>

    // Corregir el monto de una venta ya existente
    @PATCH("ventas_digitales")
    suspend fun updateSaleAmount(
        @Query("id_venta_digital") filter: String,
        @Body updateData: SaleAmountUpdateRequest
    ): Response<Unit>
}

// Response para una venta (cualquier estado)
data class SaleResponse(
    @SerializedName("id_venta_digital") val id_venta_digital: Int,
    @SerializedName("id_miembro") val id_miembro: Int,
    @SerializedName("id_producto_digital") val id_producto_digital: Int,
    @SerializedName("monto") val monto: Double,
    @SerializedName("estado") val estado: String,
    @SerializedName("metodo_pago") val metodo_pago: String,
    @SerializedName("fecha_compra") val fecha_compra: String
)

data class SaleAmountUpdateRequest(
    @SerializedName("monto") val monto: Double
)

// Response para un pago pendiente
data class PendingPaymentResponse(
    @SerializedName("id_venta_digital") val id_venta_digital: Int,
    @SerializedName("id_miembro") val id_miembro: Int,
    @SerializedName("id_producto_digital") val id_producto_digital: Int,
    @SerializedName("monto") val monto: Double,
    @SerializedName("fecha_compra") val fecha_compra: String
)

// Request para actualizar asignación
data class AssignmentUpdateRequest(
    @SerializedName("fecha_inicio") val fechaInicio: String?,
    @SerializedName("fecha_fin") val fechaFin: String?,
    @SerializedName("activa") val activa: Boolean?,
    @SerializedName("cancelada") val cancelada: Boolean?
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
