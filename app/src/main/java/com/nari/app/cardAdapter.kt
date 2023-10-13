package com.nari.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CardAdapter(private val data: List<CardData>) : RecyclerView.Adapter<CardAdapter.ViewHolder>() {

    // Listener to handle card clicks
    interface OnCardClickListener {
        fun onCardClick(cardId: Int)
    }

    var onCardClickListener: OnCardClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cardData = data[position]
        holder.bind(cardData)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Bind data to views in the card layout
        fun bind(cardData: CardData) {
            val titleTextView: TextView = itemView.findViewById(R.id.cardLayoutTitleText)
            val descriptionTextView: TextView = itemView.findViewById(R.id.cardLayoutDescriptionText)
            val imageView: ImageView = itemView.findViewById(R.id.cardLayoutImage)

            val img = R.drawable.placeholder
            titleTextView.text = cardData.title
            descriptionTextView.text = cardData.description
            imageView.setImageResource(img)

            // Set click listener for the card
            itemView.setOnClickListener {
                // Notify the listener that a card is clicked and pass the card ID
                onCardClickListener?.onCardClick(cardData.id)
            }
        }
    }
}
