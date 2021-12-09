package com.example.playit

import android.database.Cursor
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playit.adapter.TrackAdapter
import com.example.playit.dataclass.TrackDetails
import kotlinx.android.synthetic.main.activity_album_list.*
import java.io.File
import java.util.ArrayList

class AlbumList : AppCompatActivity() {
    companion object{
        public lateinit var albumSongList : ArrayList<TrackDetails>
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_album_list)

        supportActionBar?.hide()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }

        val album = intent.getStringExtra("Album")!!
        val artist = intent.getStringExtra("Artist")
        val uri = intent.getStringExtra("image")
        val count = intent.getStringExtra("Count")

        albumSongList = getAlbumSongList(album)
        val adapter = TrackAdapter(albumSongList,"Album")
        rvAlbumTrack.layoutManager = LinearLayoutManager(this)
        rvAlbumTrack.adapter = adapter

        val img = Drawable.createFromPath(uri)
        sivAlbumListPoster.setImageDrawable(img);
        tvAlbumName.text = album
        tvAlbumArtistName.text = artist
        tvAlbumSongCount.text = "Song : "+ albumSongList.size
    }

    private fun getAlbumSongList(album:String):ArrayList<TrackDetails>{
        val temp = ArrayList<TrackDetails>()

        val selection = android.provider.MediaStore.Audio.Media.ALBUM + "=?"
        val projection = arrayOf(MediaStore.Audio.Media._ID,MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ALBUM,MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM_ID)

        val cursor: Cursor? = contentResolver?.
        query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,projection,selection,
            arrayOf(album),MediaStore.Audio.Media.DATE_ADDED + " DESC",
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