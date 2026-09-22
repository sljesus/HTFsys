package com.htf.system

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.htf.system.data.repository.Assignment
import com.htf.system.data.repository.PendingPayment
import com.htf.system.ui.viewmodel.AssignmentViewModel
import java.util.*

/**
 * MainActivity implementa la Capa VIEW del patrón MVVM
 * Solo maneja UI, toda la lógica de negocio está en AssignmentViewModel
 */
class MainActivity : AppCompatActivity() {

    private lateinit var editTextMemberId: EditText
    private lateinit var editTextMemberName: EditText
    private lateinit var buttonSearch: Button
    private lateinit var buttonSearchByName: Button
    private lateinit var listViewAssignments: ListView
    private lateinit var textViewEmpty: TextView
    private lateinit var textViewMemberName: TextView
    private lateinit var layoutMemberActions: View
    private lateinit var buttonResetTodayAccess: Button
    private lateinit var buttonPendingPayments: Button
    private lateinit var buttonMemberSales: Button

    // ViewModel siguiendo patrón MVVM
    private val viewModel: AssignmentViewModel by viewModels()

    // ID del miembro actual
    private var currentMemberId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar vistas
        editTextMemberId = findViewById(R.id.editTextMemberId)
        editTextMemberName = findViewById(R.id.editTextMemberName)
        buttonSearch = findViewById(R.id.buttonSearch)
        buttonSearchByName = findViewById(R.id.buttonSearchByName)
        listViewAssignments = findViewById(R.id.listViewAssignments)
        textViewEmpty = findViewById(R.id.textViewEmpty)
        textViewMemberName = findViewById(R.id.textViewMemberName)
        layoutMemberActions = findViewById(R.id.layoutMemberActions)
        buttonResetTodayAccess = findViewById(R.id.buttonResetTodayAccess)
        buttonPendingPayments = findViewById(R.id.buttonPendingPayments)
        buttonMemberSales = findViewById(R.id.buttonMemberSales)

        // Estado inicial
        showEmptyState("Ingrese ID o nombre del miembro")

        // Configurar listeners de UI
        setupListeners()

        // Observar cambios del ViewModel (patrón Observer)
        observeViewModel()
    }

    private fun setupListeners() {
        // Botón de búsqueda por ID
        buttonSearch.setOnClickListener {
            searchMemberAssignments()
        }

        // Acción del teclado (botón búsqueda por ID)
        editTextMemberId.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchMemberAssignments()
                true
            } else {
                false
            }
        }

        // Botón de búsqueda por nombre
        buttonSearchByName.setOnClickListener {
            searchMemberByName()
        }

        // Acción del teclado (botón búsqueda por nombre)
        editTextMemberName.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchMemberByName()
                true
            } else {
                false
            }
        }

        // Botón de resetear acceso de hoy
        buttonResetTodayAccess.setOnClickListener {
            confirmResetTodayAccess()
        }

        // Botón de pagos pendientes
        buttonPendingPayments.setOnClickListener {
            viewModel.loadPendingPayments(currentMemberId)
        }

        // Botón de ver ventas (para corregir montos históricos, ej. 600 -> 500)
        buttonMemberSales.setOnClickListener {
            viewModel.loadMemberSales(currentMemberId)
        }
    }

    /**
     * Diálogo de confirmación para resetear el acceso de hoy
     */
    private fun confirmResetTodayAccess() {
        AlertDialog.Builder(this)
            .setTitle("Resetear acceso de hoy")
            .setMessage("¿Seguro que querés borrar los registros de entrada de HOY de este miembro, para que pueda volver a entrar?")
            .setPositiveButton("Resetear") { _, _ ->
                viewModel.resetTodayAccess(currentMemberId)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    /**
     * Muestra el diálogo con la lista de pagos pendientes del miembro actual
     */
    private fun showPendingPaymentsDialog(payments: List<PendingPayment>) {
        if (payments.isEmpty()) {
            Toast.makeText(this, "Sin pagos pendientes para este miembro", Toast.LENGTH_SHORT).show()
            return
        }

        val items = payments.map { p ->
            "Venta #${p.idVentaDigital} - Producto ${p.idProductoDigital} - \$${p.monto} - ${p.fechaCompra}"
        }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("Pagos pendientes (${payments.size})")
            .setItems(items) { _, position ->
                val pago = payments[position]
                AlertDialog.Builder(this)
                    .setTitle("Confirmar eliminación")
                    .setMessage("¿Borrar el pago pendiente #${pago.idVentaDigital} por \$${pago.monto}?\n\nEsto borra la venta Y su código de barras asociado.")
                    .setPositiveButton("Eliminar") { _, _ ->
                        viewModel.deletePendingPayment(pago.idVentaDigital, currentMemberId)
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
            .setNegativeButton("Cerrar", null)
            .show()
    }

    /**
     * Muestra el diálogo con las ventas del miembro actual (cualquier estado), con opción de
     * corregir el monto — pensado para el caso de renovaciones cobradas de más ($600 en vez
     * de $500 por inscripción renovada sin necesitarlo, versiones viejas de la app).
     */
    private fun showMemberSalesDialog(sales: List<com.htf.system.data.repository.Sale>) {
        if (sales.isEmpty()) {
            Toast.makeText(this, "Sin ventas para este miembro", Toast.LENGTH_SHORT).show()
            return
        }

        val items = sales.map { s ->
            "Venta #${s.idVentaDigital} - Producto ${s.idProductoDigital} - \$${s.monto} - ${s.estado} - ${s.metodoPago} - ${s.fechaCompra}"
        }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("Ventas (${sales.size})")
            .setItems(items) { _, position ->
                showEditSaleAmountDialog(sales[position])
            }
            .setNegativeButton("Cerrar", null)
            .show()
    }

    /**
     * Diálogo para corregir el monto de una venta específica
     */
    private fun showEditSaleAmountDialog(sale: com.htf.system.data.repository.Sale) {
        val input = EditText(this).apply {
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
            setText(sale.monto.toString())
        }

        AlertDialog.Builder(this)
            .setTitle("Corregir monto — Venta #${sale.idVentaDigital}")
            .setMessage("Producto ${sale.idProductoDigital} - ${sale.estado} - ${sale.fechaCompra}\n\nMonto actual: \$${sale.monto}")
            .setView(input)
            .setPositiveButton("Guardar") { _, _ ->
                val nuevoMonto = input.text.toString().toDoubleOrNull()
                if (nuevoMonto == null) {
                    Toast.makeText(this, "Monto inválido", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                AlertDialog.Builder(this)
                    .setTitle("Confirmar corrección")
                    .setMessage("¿Cambiar el monto de la venta #${sale.idVentaDigital} de \$${sale.monto} a \$$nuevoMonto?")
                    .setPositiveButton("Confirmar") { _, _ ->
                        viewModel.updateSaleAmount(sale.idVentaDigital, nuevoMonto, currentMemberId)
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun observeViewModel() {
        // Observar estado de carga
        viewModel.isLoading.observe(this) { isLoading ->
            buttonSearch.isEnabled = !isLoading
            buttonSearch.text = if (isLoading) "..." else "Buscar"
        }

        // Observar nombre del miembro
        viewModel.memberName.observe(this) { name ->
            if (name != null) {
                // Determinar plataforma
                val platform = viewModel.assignments.value?.firstOrNull()?.platform ?: "N/A"
                val platformEmoji = when (platform.uppercase()) {
                    "ANDROID" -> "📱 Android"
                    "IOS", "IPHONE" -> "📱 iOS"
                    else -> "📱 $platform"
                }
                textViewMemberName.text = "$platformEmoji - $name"
                textViewMemberName.visibility = View.VISIBLE
                layoutMemberActions.visibility = View.VISIBLE
            } else {
                textViewMemberName.visibility = View.GONE
                layoutMemberActions.visibility = View.GONE
            }
        }

        // Observar lista de asignaciones
        viewModel.assignments.observe(this) { assignments ->
            textViewEmpty.visibility = View.GONE
            listViewAssignments.visibility = View.VISIBLE

            if (assignments.isEmpty()) {
                showEmptyState("Sin resultados")
            } else {
                // Usar layout personalizado
                val adapter = AssignmentListAdapter(this, assignments)
                listViewAssignments.adapter = adapter

                // Click en asignación para editar
                listViewAssignments.setOnItemClickListener { _, _, position, _ ->
                    val assignment = assignments[position]
                    showEditDialog(assignment)
                }
            }
        }

        // Observar mensajes de error/estado
        viewModel.message.observe(this) { message ->
            if (message.isNotEmpty()) {
                showEmptyState(message)
            }
        }

        // Observar resultado de actualización
        viewModel.updateSuccess.observe(this) { success ->
            when (success) {
                true -> {
                    Toast.makeText(this, "Asignación actualizada", Toast.LENGTH_SHORT).show()
                    viewModel.clearUpdateStatus()
                }
                false -> {
                    Toast.makeText(this, viewModel.message.value ?: "Error al actualizar", Toast.LENGTH_LONG).show()
                    viewModel.clearUpdateStatus()
                }
                null -> { /* No hacer nada */ }
            }
        }

        // Observar resultado de eliminación
        viewModel.deleteSuccess.observe(this) { success ->
            when (success) {
                true -> {
                    Toast.makeText(this, "Asignación eliminada", Toast.LENGTH_SHORT).show()
                    viewModel.clearDeleteStatus()
                }
                false -> {
                    Toast.makeText(this, viewModel.message.value ?: "Error al eliminar", Toast.LENGTH_LONG).show()
                    viewModel.clearDeleteStatus()
                }
                null -> { /* No hacer nada */ }
            }
        }

        // Observar resultado de reseteo de acceso de hoy
        viewModel.resetAccessSuccess.observe(this) { success ->
            when (success) {
                true -> {
                    Toast.makeText(this, "Acceso de hoy reseteado", Toast.LENGTH_SHORT).show()
                    viewModel.clearResetAccessStatus()
                }
                false -> {
                    Toast.makeText(this, viewModel.message.value ?: "Error al resetear acceso", Toast.LENGTH_LONG).show()
                    viewModel.clearResetAccessStatus()
                }
                null -> { /* No hacer nada */ }
            }
        }

        // Observar pagos pendientes cargados (null = todavía no se consultaron, no mostrar nada)
        viewModel.pendingPayments.observe(this) { payments ->
            if (payments != null) {
                showPendingPaymentsDialog(payments)
            }
        }

        // Observar resultado de borrado de pago pendiente
        viewModel.deletePendingPaymentSuccess.observe(this) { success ->
            when (success) {
                true -> {
                    Toast.makeText(this, "Pago pendiente eliminado", Toast.LENGTH_SHORT).show()
                    viewModel.clearDeletePendingPaymentStatus()
                }
                false -> {
                    Toast.makeText(this, viewModel.message.value ?: "Error al eliminar pago", Toast.LENGTH_LONG).show()
                    viewModel.clearDeletePendingPaymentStatus()
                }
                null -> { /* No hacer nada */ }
            }
        }

        // Observar ventas del miembro cargadas (null = todavía no se consultaron)
        viewModel.memberSales.observe(this) { sales ->
            if (sales != null) {
                showMemberSalesDialog(sales)
            }
        }

        // Observar resultado de corrección de monto
        viewModel.updateSaleAmountSuccess.observe(this) { success ->
            when (success) {
                true -> {
                    Toast.makeText(this, "Monto corregido", Toast.LENGTH_SHORT).show()
                    viewModel.clearUpdateSaleAmountStatus()
                }
                false -> {
                    Toast.makeText(this, viewModel.message.value ?: "Error al corregir monto", Toast.LENGTH_LONG).show()
                    viewModel.clearUpdateSaleAmountStatus()
                }
                null -> { /* No hacer nada */ }
            }
        }
    }

    private fun searchMemberAssignments() {
        val memberIdText = editTextMemberId.text.toString().trim()

        // Validar que sea un número
        val memberId = memberIdText.toIntOrNull()
        if (memberId == null) {
            showEmptyState("ID inválido")
            return
        }

        // Ocultar teclado
        hideKeyboard()

        // Delegar al ViewModel
        viewModel.searchAssignments(memberId)
        currentMemberId = memberId
    }

    private fun searchMemberByName() {
        val memberName = editTextMemberName.text.toString().trim()

        // Validar que no esté vacío
        if (memberName.isEmpty()) {
            showEmptyState("Ingrese un nombre para buscar")
            return
        }

        if (memberName.length < 2) {
            showEmptyState("Ingrese al menos 2 caracteres")
            return
        }

        // Ocultar teclado
        hideKeyboard()

        // Delegar al ViewModel
        viewModel.searchAssignmentsByName(memberName)
    }

    private fun showEmptyState(message: String) {
        textViewEmpty.text = message
        textViewEmpty.visibility = View.VISIBLE
        listViewAssignments.visibility = View.GONE
        textViewMemberName.visibility = View.GONE
        layoutMemberActions.visibility = View.GONE
    }

    // Ocultar teclado virtual
    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        if (editTextMemberId.hasFocus()) {
            imm.hideSoftInputFromWindow(editTextMemberId.windowToken, 0)
        } else if (editTextMemberName.hasFocus()) {
            imm.hideSoftInputFromWindow(editTextMemberName.windowToken, 0)
        }
    }

    /**
     * Mostrar diálogo para editar asignación
     */
    private fun showEditDialog(assignment: Assignment) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_edit_assignment)
        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        // Referencias a los campos
        val textViewMiembro = dialog.findViewById<TextView>(R.id.textViewMiembroDialog)
        val textViewId = dialog.findViewById<TextView>(R.id.textViewIdAsignacion)
        val textViewProducto = dialog.findViewById<TextView>(R.id.textViewProducto)
        val editTextFechaInicio = dialog.findViewById<EditText>(R.id.editTextFechaInicio)
        val editTextFechaFin = dialog.findViewById<EditText>(R.id.editTextFechaFin)
        val checkBoxActiva = dialog.findViewById<CheckBox>(R.id.checkBoxActiva)
        val checkBoxCancelada = dialog.findViewById<CheckBox>(R.id.checkBoxCancelada)
        val buttonDelete = dialog.findViewById<Button>(R.id.buttonDelete)
        val buttonCancel = dialog.findViewById<Button>(R.id.buttonCancel)
        val buttonSave = dialog.findViewById<Button>(R.id.buttonSave)

        // Llenar datos actuales
        textViewMiembro.text = "ID ${assignment.idMiembro} — ${assignment.nombreCompleto}"
        textViewId.text = assignment.idAsignacion
        textViewProducto.text = assignment.nombreProducto
        editTextFechaInicio.setText(assignment.fechaInicio)
        editTextFechaFin.setText(assignment.fechaFin)
        checkBoxActiva.isChecked = assignment.activa
        checkBoxCancelada.isChecked = assignment.cancelada

        // DatePicker para fecha inicio
        editTextFechaInicio.setOnClickListener {
            showDatePicker { date ->
                editTextFechaInicio.setText(date)
            }
        }

        // DatePicker para fecha fin
        editTextFechaFin.setOnClickListener {
            showDatePicker { date ->
                editTextFechaFin.setText(date)
            }
        }

        // Botón cancelar
        buttonCancel.setOnClickListener {
            dialog.dismiss()
        }

        // Botón eliminar con confirmación
        buttonDelete.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Confirmar eliminación")
                .setMessage("¿Está seguro de eliminar esta asignación?\n\nID: ${assignment.idAsignacion}\nProducto: ${assignment.nombreProducto}")
                .setPositiveButton("Eliminar") { _, _ ->
                    viewModel.deleteAssignment(
                        assignmentId = assignment.idAsignacion.toInt(),
                        memberId = currentMemberId
                    )
                    dialog.dismiss()
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        // Botón guardar
        buttonSave.setOnClickListener {
            val fechaInicio = editTextFechaInicio.text.toString().trim()
            val fechaFin = editTextFechaFin.text.toString().trim()
            val activa = checkBoxActiva.isChecked
            val cancelada = checkBoxCancelada.isChecked

            // Validar fechas
            if (fechaInicio.isEmpty() || fechaFin.isEmpty()) {
                Toast.makeText(this, "Ingrese ambas fechas", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Actualizar usando ViewModel
            viewModel.updateAssignment(
                assignmentId = assignment.idAsignacion.toInt(),
                memberId = currentMemberId,
                fechaInicio = fechaInicio,
                fechaFin = fechaFin,
                activa = activa,
                cancelada = cancelada
            )

            dialog.dismiss()
        }

        dialog.show()
    }

    /**
     * Mostrar DatePicker y devolver fecha seleccionada
     */
    private fun showDatePicker(onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, year, month, day ->
                val date = String.format("%04d-%02d-%02d", year, month + 1, day)
                onDateSelected(date)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
}

/**
 * Adapter personalizado para la lista de asignaciones
 */
class AssignmentListAdapter(
    private val context: android.content.Context,
    private val assignments: List<Assignment>
) : BaseAdapter() {

    override fun getCount(): Int = assignments.size

    override fun getItem(position: Int): Any = assignments[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: android.view.ViewGroup?): View {
        val view = convertView ?: android.view.LayoutInflater.from(context)
            .inflate(R.layout.item_assignment, parent, false)

        val assignment = assignments[position]

        view.findViewById<TextView>(R.id.textViewMemberName).text = assignment.nombreCompleto
        view.findViewById<TextView>(R.id.textViewProducto).text = assignment.nombreProducto
        view.findViewById<TextView>(R.id.textViewFechaInicio).text = assignment.fechaInicio
        view.findViewById<TextView>(R.id.textViewFechaFin).text = assignment.fechaFin
        view.findViewById<TextView>(R.id.textViewActiva).text = if (assignment.activa) "Sí" else "No"
        view.findViewById<TextView>(R.id.textViewCancelada).text = if (assignment.cancelada) "Sí" else "No"

        // Colores según estado
        val colorActiva = if (assignment.activa) 
            android.graphics.Color.parseColor("#4CAF50") else android.graphics.Color.parseColor("#9E9E9E")
        view.findViewById<TextView>(R.id.textViewActiva).setTextColor(colorActiva)

        return view
    }
}
