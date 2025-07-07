package com.example.myapplication.ui.contracts

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.ContractDetailActivity
import com.example.myapplication.R

data class Contract(
    val address: String,
    val title: String)

class ContractAdapter(private val contractList: List<Contract>):RecyclerView.Adapter<ContractAdapter.ContractViewHolder>() {

    class ContractViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val addressText: TextView = itemView.findViewById(R.id.addressText)
        val titleText: TextView = itemView.findViewById(R.id.titleText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContractViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contract, parent, false)
        return ContractViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContractViewHolder, position: Int) {
        val contract = contractList[position]
        holder.addressText.text = contract.address
        holder.titleText.text = "${contract.title} !"

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context,ContractDetailActivity::class.java).apply {
                putExtra("contractAddress", contract.address)
                putExtra("contractTitle", contract.title)

            }
            context.startActivity(intent)
        }

    }

    override fun getItemCount() = contractList.size

}