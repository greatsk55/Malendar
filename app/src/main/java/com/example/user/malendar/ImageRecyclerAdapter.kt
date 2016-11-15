package com.example.user.malendar;

import android.content.Context
import android.media.ExifInterface
import android.net.Uri
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import java.io.IOException
import java.util.*

/**
 * Created by user on 16. 2. 11.
 */

class ImageRecyclerAdapter(private val mContext: Context, var contactsList: ArrayList<ImageList>?,
                           private val linearLayoutManager: LinearLayoutManager)
    : RecyclerView.Adapter<ImageRecyclerAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(mContext)
                .inflate(R.layout.content_item, parent, false)

        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(mContext).load(Uri.fromFile(contactsList!![position].path)).thumbnail(0.1f).into(holder.album)

        try {
            val exifInterface = ExifInterface(contactsList!![position].path.absolutePath)
            val tmp = exifInterface.getAttribute(ExifInterface.TAG_DATETIME)
            holder.text.setText(tmp.substring(0, 4));
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    override fun getItemCount(): Int {
        if (contactsList == null) return 0
        return contactsList!!.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        public val album by lazy {
            itemView.findViewById(R.id.album_art1) as ImageView
        }
        public val text by lazy {
            itemView.findViewById(R.id.year) as TextView
        }

        init {

            itemView.isClickable = true
            itemView.setOnClickListener(this)

            itemView.setOnLongClickListener {
                // song is selected
                true
            }
        }

        override fun onClick(v: View) {
        }

    }
}