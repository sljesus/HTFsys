package com.htf.system

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import kotlinx.coroutines.*
import com.htf.system.data.repository.AssignmentRepository

class MainActivity : AppCompatActivity() {

    private lateinit var editTextMemberId: EditText
    private lateinit var buttonSearch: Button
    private lateinit var listViewAssignments: ListView
    private lateinit var textViewEmpty: TextView
    private lateinit var textViewMemberName: TextView

    private val repository = AssignmentRepository()

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

        // Configurar botón de búsqueda
        buttonSearch.setOnClickListener {
            searchMemberAssignments()
        }

        // Configurar acción del teclado (botón búsqueda)
        editTextMemberId.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchMemberAssignments()
                true
            } else {
                false
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

        // Mostrar progreso
        buttonSearch.isEnabled = false
        buttonSearch.text = "..."

        // Buscar en Supabase
        CoroutineScope(Dispatchers.IO).launch {
            repository.getAssignmentsByMemberId(memberId)
                .onSuccess { assignments ->
                    withContext(Dispatchers.Main) {
                        // Mostrar nombre del primer resultado (si existe)
                        if (assignments.isNotEmpty()) {
                            val first = assignments.first()
                            val platformEmoji = when (first.platform.uppercase()) {
                                "ANDROID" -> "📱 Android"
                                "IOS", "IPHONE" -> "📱 iOS"
                                else -> "📱 ${first.platform}"
                            }
                            textViewMemberName.text = "$platformEmoji - ${first.nombreCompleto}"
                            textViewMemberName.visibility = View.VISIBLE
                        } else {
                            textViewMemberName.visibility = View.GONE
                        }

                        textViewEmpty.visibility = View.GONE
                        listViewAssignments.visibility = View.VISIBLE

                        if (assignments.isEmpty()) {
                            showEmptyState("Sin resultados para ID: $memberId")
                            textViewMemberName.visibility = View.GONE
                        } else {
                            val displayList: List<String> = assignments.map { assignment ->
                                "ID: ${assignment.idAsignacion} | Prod: ${assignment.idProducto}\n" +
                                "${assignment.fechaInicio} → ${assignment.fechaFin}\n" +
                                if (assignment.activa) "ACTIVA" else "CANCELADA"
                            }
                            val adapter = ArrayAdapter(
                                this@MainActivity,
                                android.R.layout.simple_list_item_1,
                                displayList
                            )
                            listViewAssignments.adapter = adapter
                        }

                        buttonSearch.isEnabled = true
                        buttonSearch.text = "Buscar"
                    }
                }
                .onFailure { exception ->
                    withContext(Dispatchers.Main) {
                        showEmptyState("Error: ${exception.message}")
                        buttonSearch.isEnabled = true
                        buttonSearch.text = "Buscar"
                    }
                }
        }
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
