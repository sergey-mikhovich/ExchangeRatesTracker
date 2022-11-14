/*
 * Copyright (c) 2020 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 *
 * This project and source code may use libraries or frameworks that are
 * released under various Open-Source licenses. Use of those libraries and
 * frameworks are governed by their own individual licenses.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.sergeymikhovich.android.exchangeratestracker.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.sergeymikhovich.android.exchangeratestracker.R
import com.sergeymikhovich.android.exchangeratestracker.databinding.DialogRatesSortingBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SortingDialogFragment(
    private val selectedSorting: Sorting,
    private val onSortingSelected: (Sorting) -> Unit
) : DialogFragment() {

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

        with(binding.filterOptions) {
            when (selectedSorting) {
                Sorting.ByAlphabeticAsc -> check(R.id.by_alphabetic_asc)
                Sorting.ByAlphabeticDesc -> check(R.id.by_alphabetic_desc)
                Sorting.ByValueAsc -> check(R.id.by_value_asc)
                Sorting.ByValueDesc -> check(R.id.by_value_desc)
                Sorting.NoSorting -> clearCheck()
            }
        }
    }

    private fun sortingExchangeRates() {
        val sorting = when (binding.filterOptions.checkedRadioButtonId) {
            R.id.by_alphabetic_asc -> Sorting.ByAlphabeticAsc
            R.id.by_alphabetic_desc -> Sorting.ByAlphabeticDesc
            R.id.by_value_asc -> Sorting.ByValueAsc
            R.id.by_value_desc -> Sorting.ByValueDesc
            else -> Sorting.NoSorting
        }

        onSortingSelected(sorting)
        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}