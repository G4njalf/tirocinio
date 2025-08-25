package com.example.myapplication.ui.home

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.databinding.FragmentHomeBinding
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate

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

        val textView2: TextView = binding.textsepolia
        homeViewModel.text2.observe(viewLifecycleOwner) {
            textView2.text = it
        }

        homeViewModel.loadContracts()

        val pieChart: PieChart = binding.piechart
        pieChart.setNoDataText("Fetching data...")
        pieChart.setDrawEntryLabels(false)

        homeViewModel.contracts.observe(viewLifecycleOwner) { contratti ->
            val totalContracts = contratti.size
            val pendingContracts = contratti.count { !it.isFundend && !it.isAttivato && !it.isLiquidato }
            val fundedContracts = contratti.count { it.isFundend && !it.isLiquidato && !it.isAttivato }
            val activatedContracts = contratti.count { it.isAttivato && !it.isLiquidato }
            val liquidatedContracts = contratti.count { it.isLiquidato }


            val summaryText = "Total: $totalContracts\nActivated: $activatedContracts\nFunded: $fundedContracts\nLiquidated: $liquidatedContracts \nPending: $pendingContracts"
            Log.d("HomeFragment", "Contract summary: $summaryText")

            val categoryColors = mapOf(
                "Pending Contracts" to Color.parseColor("#FFA726"),    // arancione
                "Funded Contracts" to Color.parseColor("#66BB6A"),     // verde
                "Activated Contracts" to Color.parseColor("#29B6F6"),  // azzurro
                "Liquidated Contracts" to Color.parseColor("#EF5350")  // rosso
            )

            val entries = listOf(
                PieEntry(pendingContracts.toFloat(), "Pending Contracts"),
                PieEntry(fundedContracts.toFloat(), "Funded Contracts"),
                PieEntry(activatedContracts.toFloat(), "Activated Contracts"),
                PieEntry(liquidatedContracts.toFloat(), "Liquidated Contracts")
            ).filter { it.value > 0 }

            val dataSet = PieDataSet(entries, "Categorie")

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

            pieChart.setEntryLabelColor(Color.WHITE)
            pieChart.setEntryLabelTextSize(12f)

            pieChart.legend.isEnabled = true // se vuoi puoi metterlo a false
            pieChart.legend.textColor = Color.WHITE
            pieChart.legend.textSize = 14f
            pieChart.legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
            pieChart.legend.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
            pieChart.legend.orientation = Legend.LegendOrientation.VERTICAL
            //pieChart.legend.setDrawInside(false)
// 🔥 Animazione
            pieChart.animateY(1400, Easing.EaseInOutQuad)

// Ridisegna
            pieChart.invalidate()
        }

        binding.button1.setOnClickListener {
            homeViewModel.getDataFromSepolia()
        }

        /*binding.buttonMintTkn.setOnClickListener {
            homeViewModel.mintTokens()
        }*/

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}