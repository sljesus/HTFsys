package com.htf.system

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.htf.system.ui.viewmodel.AssignmentViewModel

/**
 * MainActivity implementa la Capa VIEW del patrón MVVM
 * Solo maneja UI, toda la lógica de negocio está en AssignmentViewModel
 */
class MainActivity : AppCompatActivity() {

    private lateinit var editTextMemberId: EditText
    private lateinit var buttonSearch: Button
    private lateinit var listViewAssignments: ListView
    private lateinit var textViewEmpty: TextView
    private lateinit var textViewMemberName: TextView

    // ViewModel siguiendo patrón MVVM
    private val viewModel: AssignmentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar vistas
        editTextMemberId = findViewById(R.id.editTextMemberId)
        buttonSearch = findViewById(R.id.buttonSearch)
        listViewAssignments = findViewById(R.id.listViewAssignments)
        textViewEmpty = findViewById(R.id.textViewEmpty)
        textViewMemberName = findViewById(R.id.textViewMemberName)

        // Estado inicial
        showEmptyState("Ingrese ID de miembro")

        // Configurar listeners de UI
        setupListeners()

        // Observar cambios del ViewModel (patrón Observer)
        observeViewModel()
    }

    private fun setupListeners() {
        // Botón de búsqueda
        buttonSearch.setOnClickListener {
            searchMemberAssignments()
        }

        // Acción del teclado (botón búsqueda)
        editTextMemberId.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchMemberAssignments()
                true
            } else {
                false
            }
        }
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
            } else {
                textViewMemberName.visibility = View.GONE
            }
        }

        // Observar lista de asignaciones
        viewModel.assignments.observe(this) { assignments ->
            textViewEmpty.visibility = View.GONE
            listViewAssignments.visibility = View.VISIBLE

            if (assignments.isEmpty()) {
                showEmptyState("Sin resultados")
            } else {
                val displayList: List<String> = assignments.map { assignment ->
                    "ID: ${assignment.idAsignacion} | Prod: ${assignment.idProducto}\n" +
                    "${assignment.fechaInicio} → ${assignment.fechaFin}\n" +
                    if (assignment.activa) "ACTIVA" else "CANCELADA"
                }
                val adapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_list_item_1,
                    displayList
                )
                listViewAssignments.adapter = adapter
            }
        }

        // Observar mensajes de error/estado
        viewModel.message.observe(this) { message ->
            if (message.isNotEmpty()) {
                showEmptyState(message)
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
    }

    private fun showEmptyState(message: String) {
        textViewEmpty.text = message
        textViewEmpty.visibility = View.VISIBLE
        listViewAssignments.visibility = View.GONE
        textViewMemberName.visibility = View.GONE
    }

    // Ocultar teclado virtual
    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editTextMemberId.windowToken, 0)
    }
}
