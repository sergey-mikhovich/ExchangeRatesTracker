package com.sergeymikhovich.android.exchangeratestracker.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sergeymikhovich.android.exchangeratestracker.R
import com.sergeymikhovich.android.exchangeratestracker.data.database.ExchangeRateEntity
import com.sergeymikhovich.android.exchangeratestracker.databinding.ListItemExchangeRateBinding
import com.sergeymikhovich.android.exchangeratestracker.utils.Constants.Companion.DATE_TIME_FORMAT
import java.text.SimpleDateFormat
import java.util.*

class ExchangeRatesAdapter(
    private val onFavoriteClick: (ExchangeRateEntity) -> Unit
) : ListAdapter<ExchangeRateEntity, ExchangeRatesAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(
        private val binding: ListItemExchangeRateBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindTo(data: ExchangeRateEntity) = with(binding) {
            val favoriteImage = if (data.isFavorite) {
                R.drawable.ic_baseline_star_24
            } else {
                R.drawable.ic_baseline_star_border_24
            }

            val dateTime = SimpleDateFormat(DATE_TIME_FORMAT, Locale.US)
                .format(Date(data.timestamp.toLong() * 1000))

            currencyName.text = data.rateName
            currencyTimestamp.text = dateTime
            currencyQuantity.text = data.rateQuantity.toString()
            favoriteCurrencyButton.setImageResource(favoriteImage)

            favoriteCurrencyButton.setOnClickListener { onFavoriteClick(data) }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<ExchangeRateEntity>() {
        override fun areItemsTheSame(
            oldItem: ExchangeRateEntity,
            newItem: ExchangeRateEntity
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: ExchangeRateEntity,
            newItem: ExchangeRateEntity
        ): Boolean {
            return oldItem == newItem
        }

        override fun getChangePayload(
            oldItem: ExchangeRateEntity,
            newItem: ExchangeRateEntity
        ): Any? {
            return oldItem.isFavorite != newItem.isFavorite
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ListItemExchangeRateBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindTo(getItem(position))
    }
}