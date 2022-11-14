package com.sergeymikhovich.android.exchangeratestracker.ui.favorites

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
import com.sergeymikhovich.android.exchangeratestracker.data.database.FavoriteExchangeRateEntity
import com.sergeymikhovich.android.exchangeratestracker.databinding.FragmentFavoriteBinding
import com.sergeymikhovich.android.exchangeratestracker.ui.MainViewModel
import com.sergeymikhovich.android.exchangeratestracker.ui.adapters.FavoriteExchangeRatesAdapter
import com.sergeymikhovich.android.exchangeratestracker.ui.decorations.SpacingItemDecoration
import com.sergeymikhovich.android.exchangeratestracker.ui.dialogs.Sorting
import com.sergeymikhovich.android.exchangeratestracker.ui.dialogs.SortingDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FavoritesViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
    private val adapter by lazy { FavoriteExchangeRatesAdapter(viewModel::onFavoriteClick) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        observeFavoriteCachedExchangeRates()
    }

    private fun setupUI() = with(binding) {
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())

        val spacingBetweenItemsInPixels = resources.getDimensionPixelSize(R.dimen.margin_small)
        recyclerView.addItemDecoration(
            SpacingItemDecoration(1, spacingBetweenItemsInPixels, false)
        )

        sortingButton.setOnClickListener {
            SortingDialogFragment(mainViewModel.selectedFavoriteSorting) { sorting ->
                applySorting(sorting)
            }.show(childFragmentManager, null)
        }

        textRateName.setOnItemClickListener { adapterView, _, position, _ ->
            val selectedItem = adapterView.getItemAtPosition(position).toString()
            if (mainViewModel.selectedFavoriteBase != selectedItem) {
                mainViewModel.selectedFavoriteSorting = Sorting.NoSorting
                mainViewModel.selectedFavoriteBase = selectedItem
                applyFilter(selectedItem)
            }
        }
    }

    private fun initDropDownMenu(exchangeRates: List<FavoriteExchangeRateEntity>) {
        binding.textRateName.apply {
            val favoriteBases = exchangeRates.map { it.baseName }.distinct()

            if (favoriteBases.none { it == mainViewModel.selectedFavoriteBase }) {
                mainViewModel.selectedFavoriteBase = favoriteBases.minByOrNull { it } ?: ""
            }

            setText(mainViewModel.selectedFavoriteBase, false)
            setAdapter(
                ArrayAdapter(
                    requireContext(),
                    R.layout.dropdown_item,
                    favoriteBases.sortedBy { it }
                )
            )
        }
    }

    private fun applySorting(sorting: Sorting) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                val exchangeRates = viewModel.favoriteCachedExchangeRates.first()

                val filteredExchangeRates = exchangeRates
                    .filter { it.baseName == mainViewModel.selectedFavoriteBase }
                    .ifEmpty {
                        exchangeRates.filter { rate ->
                            rate.baseName == (exchangeRates.minByOrNull { it.baseName }?.baseName ?: "")
                        }
                    }

                val sortedExchangeRates = when (sorting) {
                    Sorting.ByAlphabeticAsc -> filteredExchangeRates.sortedBy { it.rateName }
                    Sorting.ByAlphabeticDesc -> filteredExchangeRates.sortedByDescending { it.rateName }
                    Sorting.ByValueAsc -> filteredExchangeRates.sortedBy { it.rateQuantity }
                    Sorting.ByValueDesc -> filteredExchangeRates.sortedByDescending { it.rateQuantity }
                    Sorting.NoSorting -> filteredExchangeRates
                }
                adapter.submitList(sortedExchangeRates) {
                    if (mainViewModel.selectedFavoriteSorting != sorting) {
                        binding.recyclerView.scrollToPosition(0)
                    }
                    mainViewModel.listStateFavoriteParcel?.let {
                        (binding.recyclerView.layoutManager as LinearLayoutManager).onRestoreInstanceState(it)
                        mainViewModel.listStateFavoriteParcel = null
                    }
                    mainViewModel.selectedFavoriteSorting = sorting
                }
            }
        }
    }

    private fun applyFilter(baseCurrency: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                val favoriteExchangeRates = viewModel.favoriteCachedExchangeRates.first()
                val filteredList = favoriteExchangeRates
                    .filter { it.baseName == baseCurrency }
                    .ifEmpty { favoriteExchangeRates }

                adapter.submitList(filteredList) {
                    binding.recyclerView.scrollToPosition(0)
                }
            }
        }
    }

    private fun observeFavoriteCachedExchangeRates() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.favoriteCachedExchangeRates.collect { exchangeRates ->
                    if (exchangeRates.isNotEmpty()) {
                        hideEmptyListMessage()
                        initDropDownMenu(exchangeRates)
                        applySorting(mainViewModel.selectedFavoriteSorting)
                    } else {
                        mainViewModel.selectedFavoriteBase = ""
                        mainViewModel.selectedFavoriteSorting = Sorting.NoSorting
                        showEmptyListMessage()
                    }
                }
            }
        }
    }

    private fun showEmptyListMessage() {
        binding.recyclerView.visibility = View.INVISIBLE
        binding.cardRateName.visibility = View.INVISIBLE
        binding.cardSorting.visibility = View.INVISIBLE
        binding.emptyListMessage.visibility = View.VISIBLE
    }

    private fun hideEmptyListMessage() {
        binding.emptyListMessage.visibility = View.INVISIBLE
        binding.recyclerView.visibility = View.VISIBLE
        binding.cardRateName.visibility = View.VISIBLE
        binding.cardSorting.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        mainViewModel.listStateFavoriteParcel =
            (binding.recyclerView.layoutManager as LinearLayoutManager).onSaveInstanceState()
        super.onDestroyView()
        _binding = null
    }
}