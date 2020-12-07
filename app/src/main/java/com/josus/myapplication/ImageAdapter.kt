package com.josus.myapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso


class ImageAdapter(private  val mContext:Context,private val list:MutableList<String>):
    RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    override fun getItemCount(): Int {
        return list.size
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
     val view=LayoutInflater.from(parent.context).inflate(R.layout.grid_image,parent,false)
        return ImageViewHolder(view)

    }



    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val piclist= list[position]
        Picasso.with(mContext).load(list[position]).into(holder.image)

    }

    class ImageViewHolder(view: View):RecyclerView.ViewHolder(view){
        val image:ImageView=view.findViewById(R.id.img)

    }

}