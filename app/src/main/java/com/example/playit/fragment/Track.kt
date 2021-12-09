package com.example.playit.fragment

import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playit.R
import com.example.playit.adapter.TrackAdapter
import com.example.playit.dataclass.TrackDetails
import kotlinx.android.synthetic.main.fragment_track.*
import java.io.File
import java.util.ArrayList

class Track : Fragment() {

    companion object{
        lateinit var list : ArrayList<TrackDetails>
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_track, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        list = getTrackList()
        val adapter = TrackAdapter(list,"Track")
        rvTrack.layoutManager = LinearLayoutManager(requireContext())
        rvTrack.adapter = adapter
    }

    private fun getTrackList():ArrayList<TrackDetails>{
        val temp = ArrayList<TrackDetails>()

        val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"
        val projection = arrayOf(MediaStore.Audio.Media._ID,MediaStore.Audio.Media.TITLE,
                                MediaStore.Audio.Media.ALBUM,MediaStore.Audio.Media.ARTIST,
                                MediaStore.Audio.Media.DURATION,MediaStore.Audio.Media.DATA,
                                MediaStore.Audio.Media.ALBUM_ID)

        val cursor: Cursor? = context?.contentResolver?.
                                query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,projection,selection,
                                    null,MediaStore.Audio.Media.TITLE,
                                    null)

        if (cursor != null){
            if (cursor.moveToFirst()){
                do {
                    val tempTitle = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                    val tempArtist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                    val tempID = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID))
                    val tempAlbum = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                    val tempDuration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                    val tempPath = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                    val tempAlbumID = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)).toString()
                    val uri = Uri.parse("content://media/external/audio/albumart")
                    val tempArtUri =Uri.withAppendedPath(uri,tempAlbumID).toString()
                    val t = TrackDetails(tempID,tempTitle,tempAlbum,tempArtist,tempDuration,tempPath,tempArtUri)
                    val file = File(t.path)
                    if (file.exists()){
                        temp.add(t)
                    }
                }while (cursor.moveToNext())
                cursor.close()
            }
        }
        return temp;
    }
}