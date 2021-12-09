package com.example.playit.service

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import com.example.playit.HomeActivity
import com.example.playit.R
import com.example.playit.SongPlay
import com.example.playit.SongPlay.Companion.binding
import com.example.playit.functionality
import kotlinx.android.synthetic.main.activity_song_play.*

class MusicService:Service() ,AudioManager.OnAudioFocusChangeListener{

    private var myBinder = MyBinder()
    var mediaPlayer : MediaPlayer? = null
    private lateinit var mediaSession : MediaSessionCompat
    private lateinit var runnable: Runnable
    lateinit var audioManager: AudioManager

    override fun onBind(p0: Intent?): IBinder {
        mediaSession = MediaSessionCompat(baseContext,"PLAYit")
        return myBinder
    }

    inner class MyBinder:Binder(){
        fun currentService():MusicService{
            return this@MusicService
        }
    }

    fun showNotification(playPauseBtn : Int){
        val intent =Intent(baseContext,SongPlay::class.java)
        intent.putExtra("index",SongPlay.index)
        intent.putExtra("Class","Home Activity")
        val CIntent = PendingIntent.getActivity(this,0,intent,0)

        val prevIntent = Intent(this,NotificationReceiver::class.java).setAction(ApplicationClass.PREV)
        val prevPendingIntent = PendingIntent.getBroadcast(this,0,prevIntent,PendingIntent.FLAG_UPDATE_CURRENT)

        val playIntent = Intent(this,NotificationReceiver::class.java).setAction(ApplicationClass.PLAY)
        val playPendingIntent = PendingIntent.getBroadcast(this,0,playIntent,PendingIntent.FLAG_UPDATE_CURRENT)

        val nextIntent = Intent(this,NotificationReceiver::class.java).setAction(ApplicationClass.NEXT)
        val nextPendingIntent = PendingIntent.getBroadcast(this,0,nextIntent,PendingIntent.FLAG_UPDATE_CURRENT)

        val exitIntent = Intent(this,NotificationReceiver::class.java).setAction(ApplicationClass.EXIT)
        val exitPendingIntent = PendingIntent.getBroadcast(this,0,exitIntent,PendingIntent.FLAG_UPDATE_CURRENT)

        val imgArt = setImage(SongPlay.songList[SongPlay.index].path)
        val image = if (imgArt != null){
            BitmapFactory.decodeByteArray(imgArt,0,imgArt.size)
        }else{
            BitmapFactory.decodeResource(resources,R.drawable.icon)
        }
        val notification = NotificationCompat.Builder(baseContext,ApplicationClass.CHANNEL_ID)
            .setContentIntent(CIntent)
            .setContentTitle(SongPlay.songList[SongPlay.index].title)
            .setContentText(SongPlay.songList[SongPlay.index].artists)
            .setSmallIcon(R.drawable.icon)
            .setLargeIcon(image)
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(mediaSession.sessionToken))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .addAction(R.drawable.ic_baseline_previous,"Previous",prevPendingIntent)
            .addAction(playPauseBtn,"Play",playPendingIntent)
            .addAction(R.drawable.ic_baseline_next,"Next",nextPendingIntent)
            .addAction(R.drawable.ic_baseline_exit,"Exit",exitPendingIntent)
            .build()
        startForeground(11,notification)
    }

     fun createMediaPlayer(){
         if (mediaPlayer == null)
             mediaPlayer = MediaPlayer()
         if (mediaPlayer!!.isPlaying) {
             mediaPlayer!!.stop()
         }
         mediaPlayer!!.reset()
         mediaPlayer!!.setDataSource(SongPlay.songList[SongPlay.index].path)
         mediaPlayer!!.prepare()
         mediaPlayer!!.start()
         SongPlay.isplaying = true

         binding.tvSongStartTime.text = functionality.setDuration(mediaPlayer!!.currentPosition.toLong())
         binding.tvSongEndTime.text = functionality.setDuration(mediaPlayer!!.duration.toLong())
         binding.sbSongPlay.progress = 0
         binding.sbSongPlay.max =mediaPlayer!!.duration

         if (SongPlay.isplaying)
            binding.efbPlayPause.setIconResource(R.drawable.ic_baseline_pause)
         else
             binding.efbPlayPause.setIconResource(R.drawable.ic_baseline_play)

         SongPlay.currentID = SongPlay.songList[SongPlay.index].id
         SongPlay.musicService!!.showNotification(R.drawable.ic_baseline_pause)
    }

    private fun setImage(path : String):ByteArray?{
        val retrieve = MediaMetadataRetriever()
        retrieve.setDataSource(path)
        return retrieve.embeddedPicture
    }

    fun setSeekbar(){
        runnable = Runnable {
            binding.tvSongStartTime.text = functionality.setDuration(mediaPlayer!!.currentPosition.toLong())
            binding.sbSongPlay.progress = mediaPlayer!!.currentPosition
            Handler(Looper.getMainLooper()).postDelayed(runnable,500)

        }
        Handler(Looper.getMainLooper()).postDelayed(runnable,0)

    }

    override fun onAudioFocusChange(p0: Int) {
        if (p0 <=0){
            SongPlay.isplaying = false
            mediaPlayer!!.pause()
            showNotification(R.drawable.ic_baseline_play)
            binding.efbPlayPause.setIconResource(R.drawable.ic_baseline_play)
            HomeActivity.binding.efbPlayPauseH.setIconResource(R.drawable.ic_baseline_play)
        }else{
            SongPlay.isplaying = true
            mediaPlayer!!.start()
            SongPlay.musicService!!.showNotification(R.drawable.ic_baseline_pause)
            binding.efbPlayPause.setIconResource(R.drawable.ic_baseline_pause)
            HomeActivity.binding.efbPlayPauseH.setIconResource(R.drawable.ic_baseline_pause)

        }
    }

}