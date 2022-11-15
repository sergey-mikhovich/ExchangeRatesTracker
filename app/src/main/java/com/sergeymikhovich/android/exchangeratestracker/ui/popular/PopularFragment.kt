package com.sergeymikhovich.android.exchangeratestracker.ui.popular

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.sergeymikhovich.android.exchangeratestracker.R
import com.sergeymikhovich.android.exchangeratestracker.data.database.ExchangeRateEntity
import com.sergeymikhovich.android.exchangeratestracker.data.network.NetworkResult
import com.sergeymikhovich.android.exchangeratestracker.databinding.FragmentPopularBinding
import com.sergeymikhovich.android.exchangeratestracker.ui.MainViewModel
import com.sergeymikhovich.android.exchangeratestracker.ui.adapters.ExchangeRatesAdapter
import com.sergeymikhovich.android.exchangeratestracker.ui.decorations.SpacingItemDecoration
import com.sergeymikhovich.android.exchangeratestracker.ui.dialogs.Sorting
import com.sergeymikhovich.android.exchangeratestracker.ui.dialogs.SortingDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PopularFragment : Fragment() {

    private var _binding: FragmentPopularBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PopularViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
    private val adapter by lazy { ExchangeRatesAdapter(viewModel::onFavoriteClick) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPopularBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        loadData()
    }

    private fun setupUI() = with(binding) {
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())

        val spacingBetweenItemsInPixels = resources.getDimensionPixelSize(R.dimen.margin_small)
        recyclerView.addItemDecoration(
            SpacingItemDecoration(1, spacingBetweenItemsInPixels, false)
        )

        retryButton.setOnClickListener {
            mainViewModel.selectedSorting = Sorting.NoSorting
            viewModel.getRemoteExchangeRates(mainViewModel.selectedBase)
        }

        swipeRefresh.setOnRefreshListener {
            swipeRefresh.isRefreshing = false
            showProgressBar()
            viewModel.getRemoteExchangeRates(mainViewModel.selectedBase)
        }

        sortingButton.setOnClickListener {
            SortingDialogFragment(mainViewModel.selectedSorting) { sorting ->
                applySorting(sorting)
            }.show(childFragmentManager, null)
        }

        textRateName.setOnItemClickListener { adapterView, _, position, _ ->
            val selectedItem = adapterView.getItemAtPosition(position).toString()
            mainViewModel.selectedBase = selectedItem
            mainViewModel.selectedSorting = Sorting.NoSorting
            viewModel.getRemoteExchangeRates(mainViewModel.selectedBase)
        }
    }

    private fun initDropDownMenu(exchangeRates: List<ExchangeRateEntity>) {
        binding.textRateName.apply {
            mainViewModel.selectedBase = exchangeRates.first().baseName
            setText(mainViewModel.selectedBase, false)
            setAdapter(
                ArrayAdapter(
                    requireContext(),
                    R.layout.dropdown_item,
                    exchangeRates.map { it.rateName }.sortedBy { it }
                )
            )
        }
    }

    private fun applySorting(sorting: Sorting) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                val exchangeRates = viewModel.cachedExchangeRates.first()
                val sortedExchangeRates = when (sorting) {
                    Sorting.ByAlphabeticAsc -> exchangeRates.sortedBy { it.rateName }
                    Sorting.ByAlphabeticDesc -> exchangeRates.sortedByDescending { it.rateName }
                    Sorting.ByValueAsc -> exchangeRates.sortedBy { it.rateQuantity }
                    Sorting.ByValueDesc -> exchangeRates.sortedByDescending { it.rateQuantity }
                    Sorting.NoSorting -> exchangeRates
                }
                adapter.submitList(sortedExchangeRates) {
                    if (mainViewModel.selectedSorting != sorting) {
                        binding.recyclerView.scrollToPosition(0)
                    }
                    mainViewModel.listStateParcel?.let {
                        (binding.recyclerView.layoutManager as LinearLayoutManager).onRestoreInstanceState(it)
                        mainViewModel.listStateParcel = null
                    }
                    mainViewModel.selectedSorting = sorting
                }
            }
        }
    }

    private fun loadData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                if (viewModel.cachedExchangeRates.first().isEmpty()) {
                    viewModel.getRemoteExchangeRates(mainViewModel.selectedBase)
                }
                observeRemoteExchangeRates()
                observeCachedExchangeRates()
            }
        }
    }

    private fun observeRemoteExchangeRates() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.responseState.collect { networkResponse ->
                    when (networkResponse) {
                        is NetworkResult.Success -> hideProgressBar()
                        is NetworkResult.Error -> showErrorMessage()
                        is NetworkResult.Loading -> showProgressBar()
                    }
                }
            }
        }
    }

    private fun observeCachedExchangeRates() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.cachedExchangeRates.collect { exchangeRates ->
                    if (exchangeRates.isNotEmpty()) {
                        hideProgressBar()
                        initDropDownMenu(exchangeRates)
                        applySorting(mainViewModel.selectedSorting)
                    }
                }
            }
        }
    }

    private fun showProgressBar() = with(binding) {
        swipeRefresh.visibility = View.INVISIBLE
        errorLayout.visibility = View.INVISIBLE
        cardRateName.visibility = View.INVISIBLE
        cardSorting.visibility = View.INVISIBLE
        progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() = with(binding) {
        progressBar.visibility = View.INVISIBLE
        errorLayout.visibility = View.INVISIBLE
        swipeRefresh.visibility = View.VISIBLE
        cardRateName.visibility = View.VISIBLE
        cardSorting.visibility = View.VISIBLE
    }

    private fun showErrorMessage() = with(binding) {
        progressBar.visibility = View.INVISIBLE
        swipeRefresh.visibility = View.INVISIBLE
        cardRateName.visibility = View.INVISIBLE
        cardSorting.visibility = View.INVISIBLE
        errorLayout.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        mainViewModel.listStateParcel =
            (binding.recyclerView.layoutManager as LinearLayoutManager).onSaveInstanceState()
        super.onDestroyView()
        _binding = null
    }
}