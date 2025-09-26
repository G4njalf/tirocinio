package com.example.myapplication.ui.chain_params

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ChainParamsViewModel : ViewModel() {

    private val _sliderValues = MutableLiveData(listOf(0, 0, 0, 0))
    val sliderValues: LiveData<List<Int>> = _sliderValues

    fun setInitialValues(values: List<Int>) {
        _sliderValues.value = values
    }

    fun updateSlider(index: Int, value: Int) {
        val current = _sliderValues.value ?: listOf(0, 0, 0, 0)
        val newValues = current.toMutableList()
        newValues[index] = value

        // Calcola somma e limita la somma a 100
        val total = newValues.sum()
        if (total > 100) {
            val overflow = total - 100
            // Riduce proporzionalmente gli altri slider (escludendo quello appena modificato)
            val others = newValues.mapIndexed { i, v -> i to v }.filter { it.first != index }
            var remainingOverflow = overflow
            val adjustedValues = newValues.toMutableList()
            for ((i, v) in others) {
                val reduce = minOf(v, remainingOverflow)
                adjustedValues[i] = v - reduce
                remainingOverflow -= reduce
                if (remainingOverflow <= 0) break
            }
            _sliderValues.value = adjustedValues
        } else {
            _sliderValues.value = newValues
        }
    }
}