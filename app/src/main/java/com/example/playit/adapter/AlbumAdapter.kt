package com.example.playit.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.album_snippet.view.*
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import com.example.playit.dataclass.AlbumDetails
import com.example.playit.AlbumList
import com.example.playit.R
import com.squareup.picasso.Picasso


class AlbumAdapter(val albumList: ArrayList<AlbumDetails>) : RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup,viewType: Int): AlbumViewHolder {
        val item =
            LayoutInflater.from(parent.context).inflate(R.layout.album_snippet, parent, false)
        return AlbumViewHolder(item)
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        holder.albumName.text = albumList[position].title
        holder.albumArtist.text = albumList[position].artist
        val image = Drawable.createFromPath(albumList[position].artURI)
        if (image!= null)
            holder.albumPoster.setImageDrawable(image)
        else
            Picasso.get().load(R.drawable.icon).into(holder.albumPoster)

        holder.item.setOnClickListener {
            val intent = Intent(it.context, AlbumList::class.java).apply {
                putExtra("Album",albumList[position].title)
                putExtra("id",albumList[position].id)
                putExtra("Artist",albumList[position].artist)
                putExtra("image",albumList[position].artURI)
                putExtra("Count",albumList[position].count)
            }
            startActivity(it.context,intent,null)
        }
    }

    override fun getItemCount(): Int {
        return albumList.size
    }

    inner class AlbumViewHolder (itemView: View): RecyclerView.ViewHolder(itemView) {
        val albumName = itemView.tvAlbumName
        val albumArtist = itemView.tvAlbumArtistName
        val albumPoster = itemView.sivAlbumPoster
        val item = itemView
    }
}