package com.example.myapplication.ui.chain_params

import android.content.Context
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentChainParamsBinding
import com.example.myapplication.databinding.FragmentContractCreationBinding
import com.example.myapplication.ui.contract_creation.ContractCreationViewModel

class ChainParamsFragment : Fragment() {

    private var _binding: FragmentChainParamsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var seekBars: List<SeekBar>
    private lateinit var valueTexts: List<TextView>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val chainParamsViewModel =
            ViewModelProvider(this)[ChainParamsViewModel::class.java]

        _binding = FragmentChainParamsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        seekBars = listOf(
            binding.seekBarchp1,
            binding.seekBarchp2,
            binding.seekBarchp3,
            binding.seekBarchp4
        )

        val prefs = requireContext().getSharedPreferences("chain_params_prefs", MODE_PRIVATE)
        val initialValues = listOf(
            prefs.getInt("chp1", 0),
            prefs.getInt("chp2", 0),
            prefs.getInt("chp3", 0),
            prefs.getInt("chp4", 0)
        )

        chainParamsViewModel.setInitialValues(initialValues)


        valueTexts = listOf(
            binding.textViewchp1,
            binding.textViewchp2,
            binding.textViewchp3,
            binding.textViewchp4
        )

        chainParamsViewModel.sliderValues.observe(viewLifecycleOwner) { values ->
            values.forEachIndexed { index, value ->
                if (seekBars[index].progress != value) {
                    seekBars[index].progress = value
                }
                val progress = value.toString()
                valueTexts[index].text = "Chain Param ${index + 1} - Value: $progress"
            }
        }

        seekBars.forEachIndexed { index, seekBar ->
            seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (fromUser) {
                        chainParamsViewModel.updateSlider(index, progress)
                    }
                }
                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    Log.d("ChainParamsFragment", "saving prefs")
                    val prefs = requireContext().getSharedPreferences("chain_params_prefs", Context.MODE_PRIVATE)
                    prefs.edit()
                        .putInt("chp1", seekBars[0].progress)
                        .putInt("chp2", seekBars[1].progress)
                        .putInt("chp3", seekBars[2].progress)
                        .putInt("chp4", seekBars[3].progress)
                        .apply()
                }
            })
        }


        return root
    }
}