package com.medictime.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.medictime.R
import com.medictime.entity.relation.UserMedicine
import java.time.format.DateTimeFormatter
import java.util.*

class MedicineAdapter(private val listMedicine: List<UserMedicine>) : RecyclerView.Adapter<MedicineAdapter.ListViewHolder>() {
    private lateinit var onCardClickCallback: OnCardClickCallback

    class ListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var medicineImage: ImageView = itemView.findViewById(R.id.image)
        var medicineName: TextView = itemView.findViewById(R.id.name)
        var medicineTime: TextView = itemView.findViewById(R.id.time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder = ListViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.card_medicine, parent, false))

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        Glide.with(holder.medicineImage.context)
            .load(when (listMedicine[position].detailMedicine.type) {
                "Tablet" -> R.drawable.ic_medicine_tablet
                "Syrup" -> R.drawable.ic_medicine_syrup
                "Capsule" -> R.drawable.ic_medicine_capsule
                else -> R.drawable.logo
            })
            .into(holder.medicineImage)
        holder.medicineName.text = String.format("%dx %s", listMedicine[position].detailMedicine.amount, listMedicine[position].detailMedicine.name)
        holder.medicineTime.text = listMedicine[position].detailMedicine.dateTime.format(DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault()))
        holder.itemView.setOnClickListener { onCardClickCallback.onCardClicked(listMedicine[holder.adapterPosition]) }
    }

    override fun getItemCount(): Int = listMedicine.size

    interface OnCardClickCallback {
        fun onCardClicked(data: UserMedicine)
    }

    fun setOnCardClickCallback(onCardClickCallback: OnCardClickCallback) {
        this.onCardClickCallback = onCardClickCallback
    }
}