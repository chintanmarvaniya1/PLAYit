package com.example.playit

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.view.WindowManager
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.playit.databinding.ActivitySongPlayBinding
import com.example.playit.dataclass.TrackDetails
import com.example.playit.fragment.Track
import com.example.playit.service.MusicService
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_song_play.*


class SongPlay : AppCompatActivity(), ServiceConnection, MediaPlayer.OnCompletionListener {

    companion object {
        val songList: ArrayList<TrackDetails> = ArrayList()
        var index: Int = 0
        var shuffle = false
        var repeat = false
        var isplaying = true
        var currentID: String = ""
        var musicService: MusicService? = null
        lateinit var binding: ActivitySongPlayBinding
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySongPlayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        efbBackButton.setOnClickListener {
            finish()
        }

        initialize()
        efbPlayPause.setOnClickListener {
            if (isplaying) { pauseSong() }
            else { playSong() }
        }

        efbPrevious.setOnClickListener {
            previousSong()
        }

        efbNext.setOnClickListener {
            nextSong()
        }

        sbSongPlay.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                if (p2) {
                    musicService!!.mediaPlayer!!.seekTo(p1)
                }
            }
            override fun onStartTrackingTouch(p0: SeekBar?) = Unit
            override fun onStopTrackingTouch(p0: SeekBar?) = Unit
        })

        ibShuffle.setOnClickListener {
            shuffle = !shuffle
            if (shuffle) {
                repeat = false
                ibShuffle.setColorFilter(ContextCompat.getColor(this, R.color.orange))
                ibRepeat.setColorFilter(ContextCompat.getColor(this, R.color.gray1))
            } else {
                ibShuffle.setColorFilter(ContextCompat.getColor(this, R.color.gray1))
            }
        }

        ibRepeat.setOnClickListener {
            repeat = !repeat
            if (repeat) {
                shuffle = false
                ibRepeat.setColorFilter(ContextCompat.getColor(this, R.color.orange))
                ibShuffle.setColorFilter(ContextCompat.getColor(this, R.color.gray1))
            } else {
                ibRepeat.setColorFilter(ContextCompat.getColor(this, R.color.gray1))
            }
        }

        ibShare.setOnClickListener {
            val sharingIntent = Intent()
            sharingIntent.action = Intent.ACTION_SEND
            sharingIntent.type = "audio/*"
            sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(songList[index].path))
            startActivity(Intent.createChooser(sharingIntent, "Share Music"))
        }
    }

    private fun setLayout() {
        try {
            if (songList[index].path != null) {
                Picasso.get().load(songList[index].artURI).into(sivPlayPoster)
            } else{
                Picasso.get().load(R.drawable.icon).into(sivPlayPoster)
            }
            tvPlaySongName.text = songList[index].title
            tvPlayArtistName.text = songList[index].artists
            tvSongStartTime.text =
                functionality.setDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
            tvSongEndTime.text =
                functionality.setDuration(musicService!!.mediaPlayer!!.duration.toLong())
            sbSongPlay.progress = 0
            sbSongPlay.max = musicService!!.mediaPlayer!!.duration
        } catch (e: Exception) { return }
    }

    private fun createMediaPlayer() {
        musicService!!.createMediaPlayer()
        currentID = songList[index].id
        musicService!!.mediaPlayer!!.setOnCompletionListener(this)
    }

    private fun initialize() {
        index = intent.getIntExtra("index", 0)
        val className = intent.getStringExtra("Class")
        if (className == "Track") {
            val intent = Intent(this, MusicService::class.java)
            bindService(intent, this, BIND_AUTO_CREATE)
            startService(intent)
            songList.clear()
            songList.addAll(Track.list)
        } else if (className == "Home Activity") {
            binding.tvSongStartTime.text =
                functionality.setDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
            binding.tvSongEndTime.text =
                functionality.setDuration(musicService!!.mediaPlayer!!.duration.toLong())
            binding.sbSongPlay.progress = musicService!!.mediaPlayer!!.currentPosition
            binding.sbSongPlay.max = musicService!!.mediaPlayer!!.duration
            if (isplaying)
                binding.efbPlayPause.setIconResource(R.drawable.ic_baseline_pause)
            else
                binding.efbPlayPause.setIconResource(R.drawable.ic_baseline_play)
        } else {
            val intent = Intent(this, MusicService::class.java)
            bindService(intent, this, BIND_AUTO_CREATE)
            startService(intent)
            songList.clear()
            songList.addAll(AlbumList.albumSongList)
        }
        setLayout()
    }

    private fun playSong() {
        isplaying = true
        musicService!!.mediaPlayer!!.start()
        musicService!!.showNotification(R.drawable.ic_baseline_pause)
        binding.efbPlayPause.setIconResource(R.drawable.ic_baseline_pause)
        HomeActivity.binding.efbPlayPauseH.setIconResource(R.drawable.ic_baseline_pause)
    }

    private fun pauseSong() {
        isplaying = false
        musicService!!.mediaPlayer!!.pause()
        musicService!!.showNotification(R.drawable.ic_baseline_play)
        binding.efbPlayPause.setIconResource(R.drawable.ic_baseline_play)
        HomeActivity.binding.efbPlayPauseH.setIconResource(R.drawable.ic_baseline_play)
    }

    private fun next() {
        if (!repeat) {
            if (index == songList.size - 1)
                index = 0
            else
                index++
        }
        setLayout()
        createMediaPlayer()
    }

    private fun previous() {
        if (index == 0)
            index = songList.size - 1
        else
            index--
        setLayout()
        createMediaPlayer()
    }


    private fun nextSong() {
        if (shuffle) {
            index = (0..songList.size).random()
            next()
        } else { next() }
    }

    private fun previousSong() {
        if (shuffle) {
            index = (0..songList.size).random()
            previous()
        } else { previous() }
    }

    override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
        val binder = p1 as MusicService.MyBinder
        musicService = binder.currentService()
        createMediaPlayer()
        musicService!!.setSeekbar()
        musicService!!.audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        musicService!!.audioManager.requestAudioFocus(
            musicService,
            AudioManager.STREAM_MUSIC,
            AudioManager.AUDIOFOCUS_GAIN
        )
    }

    override fun onServiceDisconnected(p0: ComponentName?) {
        musicService = null
    }

    override fun onCompletion(p0: MediaPlayer?) {
        nextSong()
    }
}