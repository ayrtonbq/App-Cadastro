package com.example.appcadastro

import android.icu.text.SimpleDateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.Locale

class FeriadoAdapter(
    private val feriados: List<MainActivity.Feriado>,
    private val onEditClick: (MainActivity.Feriado) -> Unit,
    private val onDeleteClick: (MainActivity.Feriado) -> Unit
) : RecyclerView.Adapter<FeriadoAdapter.FeriadoViewHolder>() {

    inner class FeriadoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNome: TextView = itemView.findViewById(R.id.tv_nome)
        val tvData: TextView = itemView.findViewById(R.id.tv_data)
        val btnEdit: Button = itemView.findViewById(R.id.btn_edit)
        val btnDelete: Button = itemView.findViewById(R.id.btn_delete)

        fun bind(feriado: MainActivity.Feriado) {
            tvNome.text = feriado.nome
            tvData.text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(feriado.data!!)
            btnEdit.setOnClickListener { onEditClick(feriado) }
            btnDelete.setOnClickListener { onDeleteClick(feriado) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeriadoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_feriado, parent, false)
        return FeriadoViewHolder(view)
    }

    override fun onBindViewHolder(holder: FeriadoViewHolder, position: Int) {
        holder.bind(feriados[position])
    }

    override fun getItemCount(): Int {
        return feriados.size
    }
}
