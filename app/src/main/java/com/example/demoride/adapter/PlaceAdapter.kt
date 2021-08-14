package com.example.demoride.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.demoride.databinding.SingleListBinding
import com.example.demoride.model.Prediction

class PlaceAdapter(private val listener:OnPlaceClickListener):RecyclerView.Adapter<PlaceAdapter.MyViewHolder>() {

    private var placeList= mutableListOf<Prediction>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        Log.d("mymsg", "onCreateViewHolder")
        val view= SingleListBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Log.d("mymsg", "onBindViewHolder")

        val place= placeList[position]
        holder.apply {

            val description= place.description
            Log.d("mymsg", "onBindViewHolder: $description")
            val structuredFormatting= place.structuredFormatting
            val secondaryText = structuredFormatting?.secondaryText

            firstName.text= description
            secondName.text= secondaryText
            layout.setOnClickListener {
                listener.onPlaceClick(place.placeId,description)
            }
        }

    }

    fun clear(notify:Boolean){
        Log.d("mymsg", "onClear: $notify")
        placeList.clear()
        if(notify) notifyDataSetChanged()
    }

    fun addAll(elements:MutableList<Prediction>,notify: Boolean){
        Log.d("mymsg", "addAll: ${elements[0].description}")
        placeList.addAll(elements)
        if(notify) notifyDataSetChanged()
    }

    override fun getItemCount()= placeList.size

    inner class MyViewHolder(binding: SingleListBinding):RecyclerView.ViewHolder(binding.root){
        val firstName: TextView= binding.txtFirst
        val secondName:TextView= binding.txtSecond
        val layout:ConstraintLayout= binding.singleLayout

    }

    interface OnPlaceClickListener {
        fun onPlaceClick(placeID:String?,placeName:String?)
    }
}