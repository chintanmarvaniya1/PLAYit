package com.example.playit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_song_play.*
import kotlinx.android.synthetic.main.tab_layout.*
import com.example.playit.adapter.ViewPagerAdapter
import com.example.playit.databinding.ActivityHomeBinding
import com.example.playit.service.MusicService
import com.squareup.picasso.Picasso
import kotlin.system.exitProcess


class HomeActivity : AppCompatActivity() {
    companion object{
        lateinit var binding : ActivityHomeBinding
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.black))

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fvFragmentContainer.adapter = ViewPagerAdapter(this.supportFragmentManager, lifecycle)
        TabLayoutMediator(tlMyTabLayout, fvFragmentContainer) { tab, position ->
            when (position) {
                0 -> { tab.text = "Tracks" }
                1 -> { tab.text = "Albums" }
            }
        }.attach()

        llNowPlaying.setOnClickListener {
            val intent = Intent(this, SongPlay::class.java).apply {
                putExtra("index", SongPlay.index)
                putExtra("Class", "Home Activity")
            }
            startActivity(intent)
        }

        efbPlayPauseH.setOnClickListener {
            if (SongPlay.isplaying )
                pause()
            else
                play()
        }
    }
    override fun onResume() {
        super.onResume()
        if (SongPlay.musicService != null){
            if (SongPlay.songList[SongPlay.index].path != null) {
                Picasso.get().load(SongPlay.songList[SongPlay.index].artURI).into(sivPlayPoster)
            } else{
                Picasso.get().load(R.drawable.icon).into(sivPlayPoster)
            }
            binding.tvSongTitle.text = SongPlay.songList[SongPlay.index].title
            if (SongPlay.isplaying)
                binding.efbPlayPauseH.setIconResource(R.drawable.ic_baseline_pause)
            else
                binding.efbPlayPauseH.setIconResource(R.drawable.ic_baseline_play)
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        SongPlay.musicService!!.stopForeground(true)
        SongPlay.musicService!!.mediaPlayer!!.release()
        SongPlay.musicService = null
        exitProcess(1)
    }

    private fun play(){
        SongPlay.musicService!!.mediaPlayer!!.start()
        binding.efbPlayPauseH.setIconResource(R.drawable.ic_baseline_pause)
        SongPlay.musicService!!.showNotification(R.drawable.ic_baseline_pause)
        SongPlay.binding.efbPlayPause.setIconResource(R.drawable.ic_baseline_pause)
        SongPlay.isplaying = true
    }

    private fun pause(){
        SongPlay.musicService!!.mediaPlayer!!.pause()
        binding.efbPlayPauseH.setIconResource(R.drawable.ic_baseline_play)
        SongPlay.musicService!!.showNotification(R.drawable.ic_baseline_play)
        SongPlay.binding.efbPlayPause.setIconResource(R.drawable.ic_baseline_play)
        SongPlay.isplaying = false
    }
}