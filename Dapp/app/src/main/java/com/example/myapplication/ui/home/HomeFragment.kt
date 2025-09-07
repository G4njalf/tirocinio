package com.example.myapplication.ui.home

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.databinding.FragmentHomeBinding
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        homeViewModel.getDataFromSepolia()
        val textView2: TextView = binding.tokenBalanceTextView
        homeViewModel.text2.observe(viewLifecycleOwner) {
            textView2.text = it
        }

        binding.numberNotPendingContractTextView.text = ""

        homeViewModel.loadContracts()
        val totalLiquidatedTextView: TextView = binding.totalMoneyLiquidatedTextView
        homeViewModel.totalLiquidated.observe(viewLifecycleOwner){
            if(homeViewModel.userRoleText.value == "cliente"){
                totalLiquidatedTextView.text = "You have received $it MTK from liquidated contracts"
            }
            else{
                totalLiquidatedTextView.text = "You have liquidated contracts worth $it MTK"
            }

        }

        val pieChart: PieChart = binding.piechart
        pieChart.setNoDataText("Fetching data...")
        pieChart.setDrawEntryLabels(false)

        homeViewModel.contracts.observe(viewLifecycleOwner) { contratti ->
            val totalContracts = contratti.size
            val pendingContracts =
                contratti.count { !it.isFundend && !it.isAttivato && !it.isLiquidato }
            val fundedContracts =
                contratti.count { it.isFundend && !it.isLiquidato && !it.isAttivato }
            val activatedContracts = contratti.count { it.isAttivato && !it.isLiquidato }
            val liquidatedContracts = contratti.count { it.isLiquidato }

            val notPendingContracts = totalContracts - pendingContracts

            binding.numberNotPendingContractTextView.text =
                "You have $notPendingContracts active contracts"

            val summaryText =
                "Total: $totalContracts\nActivated: $activatedContracts\nFunded: $fundedContracts\nLiquidated: $liquidatedContracts \nPending: $pendingContracts"
            Log.d("HomeFragment", "Contract summary: $summaryText")

            val categoryColors = mapOf(
                "Pending" to Color.parseColor("#FFA726"),    // arancione
                "Funded" to Color.parseColor("#66BB6A"),     // verde
                "Activated" to Color.parseColor("#29B6F6"),  // azzurro
                "Liquidated" to Color.parseColor("#EF5350")  // rosso
            )

            val entries = listOf(
                PieEntry(pendingContracts.toFloat(), "Pending"),
                PieEntry(fundedContracts.toFloat(), "Funded"),
                PieEntry(activatedContracts.toFloat(), "Activated"),
                PieEntry(liquidatedContracts.toFloat(), "Liquidated")
            ).filter { it.value > 0 }

            val dataSet = PieDataSet(entries, "")

            dataSet.colors = entries.map { categoryColors[it.label] ?: Color.GRAY }


            dataSet.valueTextSize = 14f
            dataSet.valueTextColor = Color.WHITE

            val data = PieData(dataSet)
            data.setValueFormatter(PercentFormatter(pieChart)) // Mostra percentuali

            pieChart.data = data

            pieChart.setUsePercentValues(true)
            pieChart.description.isEnabled = false
            pieChart.isDrawHoleEnabled = true
            pieChart.setHoleColor(Color.TRANSPARENT)
            pieChart.setTransparentCircleAlpha(0)
            pieChart.setDrawEntryLabels(true)
            pieChart.setEntryLabelColor(Color.WHITE)
            pieChart.setEntryLabelTextSize(12f)

            pieChart.setEntryLabelColor(Color.WHITE)
            pieChart.setEntryLabelTextSize(12f)

            pieChart.legend.isEnabled = false

            pieChart.animateY(1400, Easing.EaseInOutQuad)

            pieChart.invalidate()
        }



        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}