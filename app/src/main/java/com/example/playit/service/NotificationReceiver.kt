package com.example.playit.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.playit.HomeActivity
import com.example.playit.R
import com.example.playit.SongPlay
import com.example.playit.SongPlay.Companion.binding
import com.example.playit.functionality
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_song_play.*
import kotlin.system.exitProcess

class NotificationReceiver:BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        when(p1?.action){
            ApplicationClass.PREV -> { previousSong() }
            ApplicationClass.PLAY -> {
                if (SongPlay.isplaying) { pauseSong() }
                else { playSong() }
            }
            ApplicationClass.NEXT -> { nextSong() }
            ApplicationClass.EXIT -> {
                SongPlay.musicService!!.stopForeground(true)
                SongPlay.musicService!!.mediaPlayer!!.release()
                SongPlay.musicService = null
                exitProcess(1)
            }
        }
    }

    private fun playSong(){
        SongPlay.isplaying = true
        SongPlay.musicService!!.mediaPlayer!!.start()
        SongPlay.musicService!!.showNotification(R.drawable.ic_baseline_pause)
        SongPlay.binding.efbPlayPause.setIconResource(R.drawable.ic_baseline_pause)
        HomeActivity.binding.efbPlayPauseH.setIconResource(R.drawable.ic_baseline_pause)

    }

    private fun pauseSong(){
        SongPlay.isplaying = false
        SongPlay.musicService!!.mediaPlayer!!.pause()
        SongPlay.musicService!!.showNotification(R.drawable.ic_baseline_play)
        SongPlay.binding.efbPlayPause.setIconResource(R.drawable.ic_baseline_play)
        HomeActivity.binding.efbPlayPauseH.setIconResource(R.drawable.ic_baseline_play)
    }

    private fun nextSong(){
        if (SongPlay.shuffle){
            SongPlay.index = (0..SongPlay.songList.size).random()
            next()
        }else{ next() }
    }

    private fun previousSong(){
        if (SongPlay.shuffle){
            SongPlay.index = (0..SongPlay.songList.size).random()
            previous()
        }else{ previous() }
    }

    private fun next(){
        if (SongPlay.index == SongPlay.songList.size -1)
            SongPlay.index = 0
        else
            SongPlay.index++
        setSong()
    }

    private fun previous(){
        if (SongPlay.index == 0)
            SongPlay.index = SongPlay.songList.size -1
        else
            SongPlay.index--
        setSong()
    }

    private fun setSong(){
        Picasso.get().load(SongPlay.songList[SongPlay.index].artURI).into(binding.sivPlayPoster)
        binding.tvPlaySongName.text = SongPlay.songList[SongPlay.index].title
        binding.tvPlayArtistName.text = SongPlay.songList[SongPlay.index].artists
        binding.tvSongEndTime.text = functionality.setDuration(SongPlay.songList[SongPlay.index].duration)

        Picasso.get().load(SongPlay.songList[SongPlay.index].artURI).into(HomeActivity.binding.sivSongPoster)
        HomeActivity.binding.tvSongTitle.text = SongPlay.songList[SongPlay.index].title

        SongPlay.musicService!!.createMediaPlayer()
    }
}