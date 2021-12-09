package com.example.playit.fragment

import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playit.R
import com.example.playit.adapter.AlbumAdapter
import com.example.playit.adapter.TrackAdapter
import com.example.playit.dataclass.AlbumDetails
import kotlinx.android.synthetic.main.fragment_album.*
import kotlinx.android.synthetic.main.fragment_track.*
import java.util.ArrayList

class Album : Fragment() {

    lateinit var albums : ArrayList<AlbumDetails>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_album, container, false)

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        albums = getAlbumList()
        if (albums.size > 0) {
            val adapter = AlbumAdapter(albums)
            rvAlbum.layoutManager = GridLayoutManager(requireContext(), 2)
            rvAlbum.adapter = adapter
        }else{
            val adapter = TrackAdapter(Track.list,"Track")
            rvTrack.layoutManager = LinearLayoutManager(requireContext())
            rvTrack.adapter = adapter
        }
    }

    private fun getAlbumList():ArrayList<AlbumDetails>{
        val temp = ArrayList<AlbumDetails>()

        val projection = arrayOf(MediaStore.Audio.Albums._ID,MediaStore.Audio.Albums.ALBUM,
                                MediaStore.Audio.AlbumColumns.ARTIST,MediaStore.Audio.AlbumColumns.NUMBER_OF_SONGS,
                                MediaStore.Audio.AlbumColumns.ALBUM_ART)

        val cursor: Cursor? = context?.contentResolver?.query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,projection,null,null,null)

        if (cursor != null && cursor.count > 0){
            if (cursor.moveToFirst()){
                do {
                    val tempID = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums._ID))
                    val tempAlbumNAme = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM))
                    val tempArtist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AlbumColumns.ARTIST))
                    val tempCount = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AlbumColumns.NUMBER_OF_SONGS))
                    val artPath = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART))
                    val a = AlbumDetails(tempID,tempAlbumNAme,tempArtist,tempCount,artPath)
                    temp.add(a)
                }while (cursor.moveToNext())
                cursor.close()
            }
        }
        return temp
    }
}