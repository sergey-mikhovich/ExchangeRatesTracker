package com.sergeymikhovich.android.exchangeratestracker.ui.dialogs

import android.os.Build
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.sergeymikhovich.android.exchangeratestracker.R
import com.sergeymikhovich.android.exchangeratestracker.databinding.DialogRatesSortingBinding

class SortingDialogFragment: DialogFragment() {

    private var _binding: DialogRatesSortingBinding? = null
    private val binding get() = _binding!!

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DialogRatesSortingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.confirm.setOnClickListener { sortingExchangeRates() }
        binding.cancel.setOnClickListener { dismiss() }

        val savedSorting: Sorting = if (Build.VERSION.SDK_INT >= VERSION_CODES.TIRAMISU) {
            requireArguments().getParcelable(SAVED_SORTING_KEY, Sorting::class.java)
        } else {
            requireArguments().getParcelable(SAVED_SORTING_KEY)
        } ?: Sorting.NoSorting


        with(binding.filterOptions) {
            when (savedSorting) {
                Sorting.ByAlphabeticAsc -> check(R.id.by_alphabetic_asc)
                Sorting.ByAlphabeticDesc -> check(R.id.by_alphabetic_desc)
                Sorting.ByValueAsc -> check(R.id.by_value_asc)
                Sorting.ByValueDesc -> check(R.id.by_value_desc)
                Sorting.NoSorting -> clearCheck()
            }
        }
    }

    private fun sortingExchangeRates() {
        val selectedSorting = when (binding.filterOptions.checkedRadioButtonId) {
            R.id.by_alphabetic_asc -> Sorting.ByAlphabeticAsc
            R.id.by_alphabetic_desc -> Sorting.ByAlphabeticDesc
            R.id.by_value_asc -> Sorting.ByValueAsc
            R.id.by_value_desc -> Sorting.ByValueDesc
            else -> Sorting.NoSorting
        }

        setFragmentResult(
            REQUEST_SELECTED_SORTING_KEY,
            bundleOf(SELECTED_SORTING_KEY to selectedSorting)
        )
        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    companion object {
        private const val SAVED_SORTING_KEY = "saved_sorting_key"
        const val REQUEST_SELECTED_SORTING_KEY = "request_selected_sorting_key"
        const val SELECTED_SORTING_KEY = "selected_sorting_key"

        fun newInstance(savedSorting: Sorting): SortingDialogFragment {
            return SortingDialogFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(SAVED_SORTING_KEY, savedSorting)
                }
            }
        }
    }
}