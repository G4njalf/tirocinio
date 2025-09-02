package com.example.myapplication.ui.contracts

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.ui.contracts.ContractsFragmentDirections

data class Contract(
    val address: String,
    val premio: UInt,
    val isLiquidato: Boolean,
    val isAttivato: Boolean,
    val isFundend: Boolean,
    val addressAssicurato: String,
    val addressAssicuratore: String,
    val requestId: String
)

class ContractAdapter(private val contractList: List<Contract>,private val navController: NavController):RecyclerView.Adapter<ContractAdapter.ContractViewHolder>() {

    class ContractViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val premioText: TextView = itemView.findViewById(R.id.premioText)
        val addressText: TextView = itemView.findViewById(R.id.addressText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContractViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contract, parent, false)
        return ContractViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContractViewHolder, position: Int) {
        val contract = contractList[position]
        holder.addressText.text = "contract : ${contract.address}"
        holder.premioText.text = "value : ${contract.premio}"

        holder.itemView.setOnClickListener {
            val contract = contractList[position]
            val action = ContractsFragmentDirections.actionContractsFragmentToContractDetailFragment(
                contract.address,
                contract.premio.toString(),
                contract.isLiquidato,
                contract.isAttivato,
                contract.isFundend,
                contract.addressAssicurato,
                contract.addressAssicuratore,
                contract.requestId
            )
            navController.navigate(action)
        }

    }

    override fun getItemCount() = contractList.size

}