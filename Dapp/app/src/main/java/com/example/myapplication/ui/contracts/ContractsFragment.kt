package com.example.myapplication.ui.contracts

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.FragmentContractsBinding

class ContractsFragment : Fragment() {

    private var _binding: FragmentContractsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val contractViewModel =
            ViewModelProvider(this)[ContractsViewModel::class.java]

        _binding = FragmentContractsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.progressBarContracts.visibility = View.GONE

        contractViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBarContracts.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        val recyclerView = binding.recyclerView

        recyclerView.layoutManager = LinearLayoutManager(context)

        val emptyAdapter = ContractAdapter(emptyList())
        recyclerView.adapter = emptyAdapter

        contractViewModel.loadContracts()
        Log.d("ContractsFragment", "aaa")
        contractViewModel.contracts.observe(viewLifecycleOwner) { contratti ->
            Log.d("ContractsFragment", "ContractsFragment: Loaded contracts: $contratti")
            val adapter = ContractAdapter(contratti)
            recyclerView.adapter = adapter
        }



        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}