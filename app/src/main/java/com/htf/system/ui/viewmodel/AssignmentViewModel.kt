package com.htf.system.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.htf.system.data.repository.Assignment
import com.htf.system.data.repository.AssignmentRepository
import com.htf.system.data.repository.PendingPayment
import com.htf.system.data.repository.Sale
import kotlinx.coroutines.launch

/**
 * ViewModel para gestionar el estado de las asignaciones
 * Implementa el patrón Observer con LiveData
 */
class AssignmentViewModel : ViewModel() {

    private val repository = AssignmentRepository()

    // Estado de carga
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    // Lista de asignaciones
    private val _assignments = MutableLiveData<List<Assignment>>(emptyList())
    val assignments: LiveData<List<Assignment>> = _assignments

    // Mensaje de error o estado
    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    // Nombre del miembro para mostrar en UI
    private val _memberName = MutableLiveData<String?>()
    val memberName: LiveData<String?> = _memberName

    // Estado de actualización
    private val _updateSuccess = MutableLiveData<Boolean?>()
    val updateSuccess: LiveData<Boolean?> = _updateSuccess

    // Estado de eliminación
    private val _deleteSuccess = MutableLiveData<Boolean?>()
    val deleteSuccess: LiveData<Boolean?> = _deleteSuccess

    // Estado de reseteo de acceso de hoy
    private val _resetAccessSuccess = MutableLiveData<Boolean?>()
    val resetAccessSuccess: LiveData<Boolean?> = _resetAccessSuccess

    // Pagos pendientes del miembro actual (null = todavía no se consultaron)
    private val _pendingPayments = MutableLiveData<List<PendingPayment>?>(null)
    val pendingPayments: LiveData<List<PendingPayment>?> = _pendingPayments

    // Estado de borrado de pago pendiente
    private val _deletePendingPaymentSuccess = MutableLiveData<Boolean?>()
    val deletePendingPaymentSuccess: LiveData<Boolean?> = _deletePendingPaymentSuccess

    // Ventas del miembro actual (null = todavía no se consultaron)
    private val _memberSales = MutableLiveData<List<Sale>?>(null)
    val memberSales: LiveData<List<Sale>?> = _memberSales

    // Estado de corrección de monto
    private val _updateSaleAmountSuccess = MutableLiveData<Boolean?>()
    val updateSaleAmountSuccess: LiveData<Boolean?> = _updateSaleAmountSuccess

    /**
     * Buscar asignaciones por ID de miembro
     * Usa viewModelScope para mantener consistencia con MVVM
     */
    fun searchAssignments(memberId: Int) {
        _isLoading.value = true
        _message.value = ""

        viewModelScope.launch {
            repository.getAssignmentsByMemberId(memberId)
                .onSuccess { assignmentsList ->
                    _assignments.value = assignmentsList
                    _memberName.value = assignmentsList.firstOrNull()?.nombreCompleto
                    _message.value = if (assignmentsList.isEmpty()) {
                        "Sin resultados para ID: $memberId"
                    } else {
                        ""
                    }
                    _isLoading.value = false
                }
                .onFailure { exception ->
                    _assignments.value = emptyList()
                    _memberName.value = null
                    _message.value = "Error: ${exception.message}"
                    _isLoading.value = false
                }
        }
    }

    /**
     * Buscar asignaciones por nombre de miembro
     */
    fun searchAssignmentsByName(memberName: String) {
        _isLoading.value = true
        _message.value = ""

        viewModelScope.launch {
            repository.getAssignmentsByMemberName(memberName)
                .onSuccess { assignmentsList ->
                    _assignments.value = assignmentsList
                    _memberName.value = if (assignmentsList.isNotEmpty()) {
                        "${assignmentsList.size} resultado(s) para: $memberName"
                    } else {
                        null
                    }
                    _message.value = if (assignmentsList.isEmpty()) {
                        "Sin resultados para: $memberName"
                    } else {
                        ""
                    }
                    _isLoading.value = false
                }
                .onFailure { exception ->
                    _assignments.value = emptyList()
                    _memberName.value = null
                    _message.value = "Error: ${exception.message}"
                    _isLoading.value = false
                }
        }
    }

    /**
     * Limpiar el estado
     */
    fun clear() {
        _assignments.value = emptyList()
        _memberName.value = null
        _message.value = ""
        _isLoading.value = false
    }

    /**
     * Actualizar una asignación
     */
    fun updateAssignment(
        assignmentId: Int,
        memberId: Int,
        fechaInicio: String? = null,
        fechaFin: String? = null,
        activa: Boolean? = null,
        cancelada: Boolean? = null
    ) {
        _isLoading.value = true

        viewModelScope.launch {
            repository.updateAssignment(
                assignmentId = assignmentId,
                fechaInicio = fechaInicio,
                fechaFin = fechaFin,
                activa = activa,
                cancelada = cancelada
            )
                .onSuccess {
                    _updateSuccess.value = true
                    _message.value = "Asignación actualizada exitosamente"
                    _isLoading.value = false
                    // Recargar las asignaciones
                    searchAssignments(memberId)
                }
                .onFailure { exception ->
                    _updateSuccess.value = false
                    _message.value = "Error: ${exception.message}"
                    _isLoading.value = false
                }
        }
    }

    /**
     * Limpiar estado de actualización
     */
    fun clearUpdateStatus() {
        _updateSuccess.value = null
    }

    /**
     * Eliminar una asignación
     */
    fun deleteAssignment(assignmentId: Int, memberId: Int) {
        _isLoading.value = true

        viewModelScope.launch {
            repository.deleteAssignment(assignmentId)
                .onSuccess {
                    _deleteSuccess.value = true
                    _message.value = "Asignación eliminada exitosamente"
                    _isLoading.value = false
                    // Recargar las asignaciones
                    searchAssignments(memberId)
                }
                .onFailure { exception ->
                    _deleteSuccess.value = false
                    _message.value = "Error: ${exception.message}"
                    _isLoading.value = false
                }
        }
    }

    /**
     * Limpiar estado de eliminación
     */
    fun clearDeleteStatus() {
        _deleteSuccess.value = null
    }

    /**
     * Resetear el acceso de hoy de un miembro (borra sus registros de entrada de hoy)
     */
    fun resetTodayAccess(memberId: Int) {
        _isLoading.value = true

        viewModelScope.launch {
            repository.resetTodayAccess(memberId)
                .onSuccess {
                    _resetAccessSuccess.value = true
                    _message.value = "Acceso de hoy reseteado"
                    _isLoading.value = false
                }
                .onFailure { exception ->
                    _resetAccessSuccess.value = false
                    _message.value = "Error: ${exception.message}"
                    _isLoading.value = false
                }
        }
    }

    fun clearResetAccessStatus() {
        _resetAccessSuccess.value = null
    }

    /**
     * Cargar los pagos pendientes de un miembro
     */
    fun loadPendingPayments(memberId: Int) {
        _isLoading.value = true

        viewModelScope.launch {
            repository.getPendingPayments(memberId)
                .onSuccess { payments ->
                    _pendingPayments.value = payments
                    _isLoading.value = false
                }
                .onFailure { exception ->
                    _pendingPayments.value = emptyList()
                    _message.value = "Error: ${exception.message}"
                    _isLoading.value = false
                }
        }
    }

    /**
     * Borrar un pago pendiente específico (venta + notificación asociada)
     */
    fun deletePendingPayment(idVentaDigital: Int, memberId: Int) {
        _isLoading.value = true

        viewModelScope.launch {
            repository.deletePendingPayment(idVentaDigital)
                .onSuccess {
                    _deletePendingPaymentSuccess.value = true
                    _message.value = "Pago pendiente eliminado"
                    _isLoading.value = false
                    // Recargar la lista de pagos pendientes
                    loadPendingPayments(memberId)
                }
                .onFailure { exception ->
                    _deletePendingPaymentSuccess.value = false
                    _message.value = "Error: ${exception.message}"
                    _isLoading.value = false
                }
        }
    }

    fun clearDeletePendingPaymentStatus() {
        _deletePendingPaymentSuccess.value = null
    }

    /**
     * Cargar las ventas (cualquier estado) de un miembro
     */
    fun loadMemberSales(memberId: Int) {
        _isLoading.value = true

        viewModelScope.launch {
            repository.getMemberSales(memberId)
                .onSuccess { sales ->
                    _memberSales.value = sales
                    _isLoading.value = false
                }
                .onFailure { exception ->
                    _memberSales.value = emptyList()
                    _message.value = "Error: ${exception.message}"
                    _isLoading.value = false
                }
        }
    }

    /**
     * Corregir el monto de una venta (ej. 600 -> 500)
     */
    fun updateSaleAmount(idVentaDigital: Int, newAmount: Double, memberId: Int) {
        _isLoading.value = true

        viewModelScope.launch {
            repository.updateSaleAmount(idVentaDigital, newAmount)
                .onSuccess {
                    _updateSaleAmountSuccess.value = true
                    _message.value = "Monto corregido"
                    _isLoading.value = false
                    // Recargar la lista de ventas
                    loadMemberSales(memberId)
                }
                .onFailure { exception ->
                    _updateSaleAmountSuccess.value = false
                    _message.value = "Error: ${exception.message}"
                    _isLoading.value = false
                }
        }
    }

    fun clearUpdateSaleAmountStatus() {
        _updateSaleAmountSuccess.value = null
    }
}
