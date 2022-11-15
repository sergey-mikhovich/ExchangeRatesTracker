package com.sergeymikhovich.android.exchangeratestracker.ui.popular

import android.os.Build
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
import com.sergeymikhovich.android.exchangeratestracker.ui.dialogs.SortingDialogFragment.Companion.REQUEST_SELECTED_SORTING_KEY
import com.sergeymikhovich.android.exchangeratestracker.ui.dialogs.SortingDialogFragment.Companion.SELECTED_SORTING_KEY
import com.sergeymikhovich.android.exchangeratestracker.utils.showSnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PopularFragment : Fragment() {

    private var _binding: FragmentPopularBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PopularViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
    private val adapter by lazy { ExchangeRatesAdapter(viewModel::onFavoriteClick) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        childFragmentManager
            .setFragmentResultListener(REQUEST_SELECTED_SORTING_KEY, this) { _, bundle ->
                val selectedSorting = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    bundle.getParcelable(SELECTED_SORTING_KEY, Sorting::class.java)
                } else {
                    bundle.getParcelable(SELECTED_SORTING_KEY)
                } ?: Sorting.NoSorting
                applySorting(selectedSorting)
        }
    }

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
            viewModel.getRemoteExchangeRates(mainViewModel.selectedBase)
        }

        swipeRefresh.setOnRefreshListener {
            viewModel.getRemoteExchangeRates(mainViewModel.selectedBase)
        }

        sortingButton.setOnClickListener {
            SortingDialogFragment
                .newInstance(mainViewModel.selectedSorting)
                .show(childFragmentManager, null)
        }

        textRateName.setOnItemClickListener { adapterView, _, position, _ ->
            val selectedBase = adapterView.getItemAtPosition(position).toString()
            if (mainViewModel.selectedBase != selectedBase) {
                swipeRefresh.isRefreshing = true
                mainViewModel.selectedBase = selectedBase
                viewModel.getRemoteExchangeRates(mainViewModel.selectedBase)
            }
        }
    }

    private fun initDropDownMenu(exchangeRates: List<ExchangeRateEntity>) {
        binding.textRateName.apply {
            mainViewModel.selectedBase = exchangeRates.firstOrNull()?.baseName ?: ""
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
                val exchangeRates = viewModel.cachedExchangeRates.firstOrNull() ?: emptyList()
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
                    mainViewModel.recyclerViewStateParcel?.let {
                        (binding.recyclerView.layoutManager as LinearLayoutManager).onRestoreInstanceState(it)
                        mainViewModel.recyclerViewStateParcel = null
                    }
                    mainViewModel.selectedSorting = sorting
                }
            }
        }
    }

    private fun loadData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                if (viewModel.cachedExchangeRates.firstOrNull()?.isEmpty() == true) {
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
                        is NetworkResult.Success -> {
                            val currentBase =
                                viewModel.cachedExchangeRates.firstOrNull()?.firstOrNull()?.baseName
                            if (mainViewModel.selectedBase != currentBase) {
                                mainViewModel.selectedSorting = Sorting.NoSorting
                            }
                        }
                        is NetworkResult.Error -> {
                            binding.swipeRefresh.isRefreshing = false
                            if (viewModel.cachedExchangeRates.firstOrNull()?.isEmpty() == true) {
                                showErrorMessage()
                            } else {
                                requireView().showSnackBar(
                                    R.string.fail_message,
                                    requireActivity().findViewById(R.id.bottomNavigation))
                            }
                        }
                        is NetworkResult.Loading -> {
                            viewModel.cachedExchangeRates.firstOrNull()?.ifEmpty { showProgressBar() }
                        }
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
                        showMainViews()
                        initDropDownMenu(exchangeRates)
                        applySorting(mainViewModel.selectedSorting)
                    }
                }
            }
        }
    }

    private fun showProgressBar() = with(binding) {
        errorLayout.visibility = View.INVISIBLE
        progressBar.visibility = View.VISIBLE
    }

    private fun showMainViews() = with(binding) {
        swipeRefresh.isRefreshing = false
        progressBar.visibility = View.INVISIBLE
        errorLayout.visibility = View.INVISIBLE
        swipeRefresh.visibility = View.VISIBLE
        cardRateName.visibility = View.VISIBLE
        cardSorting.visibility = View.VISIBLE
    }

    private fun showErrorMessage() = with(binding) {
        progressBar.visibility = View.INVISIBLE
        errorLayout.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        mainViewModel.recyclerViewStateParcel =
            (binding.recyclerView.layoutManager as LinearLayoutManager).onSaveInstanceState()
        super.onDestroyView()
        _binding = null
    }
}