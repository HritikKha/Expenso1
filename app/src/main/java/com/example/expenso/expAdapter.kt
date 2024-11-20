package com.example.expenso

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class expAdapter(private val expList: ArrayList<InsertionModel>) : RecyclerView.Adapter<expAdapter.ViewHolder>() {
    private lateinit var mLongClickListener: onItemLongClickListener
    interface onItemLongClickListener {
        fun onItemLongClick(position: Int)
    }
    fun setOnItemLongClickListener(longClickListener: onItemLongClickListener) {
        mLongClickListener = longClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): expAdapter.ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.exp_list_item,parent,false)
        return ViewHolder(itemView,mLongClickListener)
    }

    override fun onBindViewHolder(holder: expAdapter.ViewHolder, position: Int) {
        val curData=expList[position]
        holder.amount.text = curData.amount
        holder.title.text = curData.title
        holder.category.text = curData.category
        val location = "Lat: ${curData.latitude}, Lon: ${curData.longitude}"
        holder.location.text = location

    }

    override fun getItemCount(): Int {
        return expList.size
    }

    class ViewHolder(itemView: View,longClickListener: onItemLongClickListener):RecyclerView.ViewHolder(itemView){
        val amount : TextView =itemView.findViewById(R.id.tvAmount)
        val title : TextView =itemView.findViewById(R.id.tvTitle)
        val category : TextView =itemView.findViewById(R.id.tvCategory)
        val location: TextView = itemView.findViewById(R.id.tvLocation)

        init {
            itemView.setOnLongClickListener {
                longClickListener.onItemLongClick(adapterPosition)
                true
            }
        }
    }

}