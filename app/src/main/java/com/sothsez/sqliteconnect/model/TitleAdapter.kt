package com.sothsez.sqliteconnect.model

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sothsez.sqliteconnect.R
import kotlinx.android.synthetic.main.item_namedata.view.*

class TitleAdapter(private val items: ArrayList<TitleData>, private val listener: TitleInterface) : RecyclerView.Adapter<TitleAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_namedata, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
        holder.itemView.btn_select.setOnClickListener {
            listener.onSelectTitleItem(items[position].title, items[position].subtitle, items[position].id)
        }
    }

    class ViewHolder(itemsView: View): RecyclerView.ViewHolder(itemsView) {
        fun bind(item: TitleData){
            itemView.apply{
                tv_name.text = item.title
                tv_nickname.text = item.subtitle
            }
        }
    }

    interface TitleInterface {
        fun onSelectTitleItem(title: String, subtitle: String, id: Long)
    }
}