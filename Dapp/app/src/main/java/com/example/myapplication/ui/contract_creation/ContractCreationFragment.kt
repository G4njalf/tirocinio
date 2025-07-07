package com.example.myapplication.ui.contract_creation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.databinding.FragmentContractCreationBinding


class ContractCreationFragment : Fragment() {

    private var _binding: FragmentContractCreationBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val contractCreationViewModel =
            ViewModelProvider(this)[ContractCreationViewModel::class.java]

        _binding = FragmentContractCreationBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.progressBarCreateContract.visibility = View.GONE

        contractCreationViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBarCreateContract.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        binding.buttonCreateContract.setOnClickListener {
            val insuredAddress = binding.editTextEnsurer.text.toString()
            val premio = binding.editTextPremio.text.toString().toUIntOrNull() ?: 0u
            contractCreationViewModel.createContract(insuredAddress,premio)
        }



        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}