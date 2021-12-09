package com.example.playit

import java.util.concurrent.TimeUnit

class functionality {
    companion object{
        fun setDuration(time :Long):String{
            val min = TimeUnit.MINUTES.convert(time,TimeUnit.MILLISECONDS)
            val sec = (TimeUnit.SECONDS.convert(time,TimeUnit.MILLISECONDS) -
                    min*TimeUnit.SECONDS.convert(1,TimeUnit.MINUTES))

            return String.format("%02d:%02d",min,sec)
        }

    }

}
