package com.example.appcadastro

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appcadastro.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val feriados = mutableListOf<Feriado>()
    private val calendar = Calendar.getInstance()
    private var feriadoSelecionado: Feriado? = null // Para editar o feriado selecionado
    private lateinit var adapter: FeriadoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val tiposFeriado = arrayOf("Nacional", "Estadual", "Municipal")
        val adapterSpinner = ArrayAdapter(this, android.R.layout.simple_spinner_item, tiposFeriado)
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTipoFeriado.adapter = adapterSpinner

        binding.etDataFeriado.setOnClickListener {
            mostrarDatePickerDialog()
        }

        binding.btnCadastrar.setOnClickListener { cadastrarFeriado() }
        binding.btnVisualizar.setOnClickListener { visualizarFeriados() }

        // Configura o RecyclerView
        adapter = FeriadoAdapter(feriados, { editarFeriado(it) }, { excluirFeriado(it) })
        binding.recyclerViewFeriados.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewFeriados.adapter = adapter
    }

    private fun mostrarDatePickerDialog() {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                calendar.set(selectedYear, selectedMonth, selectedDay)
                val formato = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                binding.etDataFeriado.setText(formato.format(calendar.time))
            },
            year, month, day
        )

        datePickerDialog.datePicker.minDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    private fun cadastrarFeriado() {
        val nome = binding.etNomeFeriado.text.toString()
        val dataStr = binding.etDataFeriado.text.toString()
        val tipo = binding.spinnerTipoFeriado.selectedItem.toString()
        val estado = binding.etEstado.text.toString()
        val municipio = binding.etMunicipio.text.toString()

        if (validarFeriado(nome, dataStr, tipo)) {
            val data = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dataStr)
            val feriado = Feriado(nome, data, tipo, estado, municipio)

            // Se um feriado está sendo editado, atualiza ele
            if (feriadoSelecionado != null) {
                val index = feriados.indexOf(feriadoSelecionado)
                if (index != -1) {
                    feriados[index] = feriado
                    binding.tvResultado.text = "Feriado editado com sucesso!"
                }
            } else {
                // Se não há feriado selecionado, adiciona um novo
                feriados.add(feriado)
                binding.tvResultado.text = "Feriado cadastrado com sucesso!"
            }

            adapter.notifyDataSetChanged() // Notifica o adapter sobre mudanças
            limparCampos()
            feriadoSelecionado = null // Reseta a seleção após cadastro ou edição
        } else {
            binding.tvResultado.text = "Erro ao cadastrar o feriado!"
        }
    }

    private fun validarFeriado(nome: String, dataStr: String, tipo: String): Boolean {
        if (TextUtils.isEmpty(nome) || TextUtils.isEmpty(dataStr)) return false
        val data = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dataStr)
        return data != null && data >= Date()
    }

    private fun editarFeriado(feriado: Feriado) {
        feriadoSelecionado = feriado
        binding.etNomeFeriado.setText(feriado.nome)
        binding.etDataFeriado.setText(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(feriado.data!!))
        binding.spinnerTipoFeriado.setSelection((binding.spinnerTipoFeriado.adapter as ArrayAdapter<String>).getPosition(feriado.tipo))
        binding.etEstado.setText(feriado.estado)
        binding.etMunicipio.setText(feriado.municipio)
    }

    private fun excluirFeriado(feriado: Feriado) {
        feriados.remove(feriado)
        binding.tvResultado.text = "Feriado excluído com sucesso!"
        adapter.notifyDataSetChanged() // Atualiza a lista
    }

    private fun visualizarFeriados() {
        if (feriados.isEmpty()) {
            AlertDialog.Builder(this)
                .setTitle("Feriados Cadastrados")
                .setMessage("Nenhum feriado cadastrado.")
                .setPositiveButton("OK", null)
                .show()
            return
        }

        val builder = StringBuilder("Feriados Cadastrados:\n")
        for (feriado in feriados) {
            builder.append(feriado.toString()).append("\n")
        }

        AlertDialog.Builder(this)
            .setTitle("Feriados Cadastrados")
            .setMessage(builder.toString())
            .setPositiveButton("OK", null)
            .show()
    }

    private fun limparCampos() {
        binding.etNomeFeriado.text.clear()
        binding.etDataFeriado.text.clear()
        binding.etEstado.text.clear()
        binding.etMunicipio.text.clear()
    }

    data class Feriado(
        val nome: String,
        val data: Date?,
        val tipo: String,
        val estado: String,
        val municipio: String
    ) {
        override fun toString(): String {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            return "$nome - ${sdf.format(data)} - $tipo - $estado - $municipio"
        }
    }
}
