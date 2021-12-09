package com.example.playit.adapter

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.playit.HomeActivity
import com.example.playit.R
import com.example.playit.SongPlay
import com.example.playit.dataclass.TrackDetails
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.song_snippet.view.*

class TrackAdapter(val trackList: ArrayList<TrackDetails>, val className : String) : RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val item =
            LayoutInflater.from(parent.context).inflate(R.layout.song_snippet, parent, false)
        return TrackViewHolder(item)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.song.text = trackList[position].title
        holder.artist.text = trackList[position].artists
        val image = trackList[position].artURI
        if (image != null)
            Picasso.get().load(image).into(holder.imageURI)
        else
            Picasso.get().load(R.drawable.icon).into(holder.imageURI)


        holder.item.setOnClickListener {
            if (trackList[position].id == SongPlay.currentID) {
                val intent = Intent(it.context, SongPlay::class.java).apply {
                    putExtra("index", SongPlay.index)
                    putExtra("Class", "Home Activity")
                }
                ContextCompat.startActivity(it.context, intent, null)
            } else {
                val intent = Intent(it.context, SongPlay::class.java).apply {
                    putExtra("index", position)
                    putExtra("Class", className)
                }
                ContextCompat.startActivity(it.context, intent, null)
            }
        }
    }

    override fun getItemCount(): Int {
        return trackList.size
    }

    inner class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val song = itemView.tvSongName
        val artist = itemView.tvArtistName
        val imageURI = itemView.sivSongPoster
        val item = itemView
    }
}