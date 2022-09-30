package com.silverorange.videoplayer.ui

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Tracks
import com.google.android.exoplayer2.util.Util
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.silverorange.videoplayer.R
import com.silverorange.videoplayer.utils.LogUtil
import com.silverorange.videoplayer.utils.NetworkUtil
import com.silverorange.videoplayer.webservice.GeneralService
import com.silverorange.videoplayer.webservice.ServiceBuilder
import com.silverorange.videoplayer.webservice.response.Video
import io.noties.markwon.Markwon
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity(), Player.Listener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    public override fun onResume() {
        super.onResume()
        // check for internet connection
        if (NetworkUtil.isNetworkAvailable(this)) {
            //get video list by calling api using retrofit2
            getVideoListData()
        }
    }

    public override fun onPause() {
        super.onPause()
        //on API 23 or lower there might be chance not calling stop method
        // need to close player when existing activity
        if (Util.SDK_INT <= 23) {
            releasePlayer()
        }
    }

    public override fun onStop() {
        super.onStop()
        // need to close player when existing activity
        if (Util.SDK_INT > 23) {
            releasePlayer()
        }
    }

    lateinit var videoList: List<Video>
    private fun getVideoListData() {
        /* *
         * api call task done here using Retrofit
         * currently we used local server ip address as base url to fetch data on mobile (please check Constant file in utils directory)
         * on successfully getting response we sort video list in Descending to show latest data first
         * than we perform task to load data in ExoPlayer
         *  */
        val generalService = ServiceBuilder.buildStringService(GeneralService::class.java)
        val getVideosCall = generalService.getVideos()
        getVideosCall.enqueue(object : Callback<String> {
            override fun onResponse(
                call: Call<String>,
                response: Response<String>
            ) {
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        val dataValue = response.body().toString()
                        LogUtil.println("dataValue", dataValue)
                        try {
                            val typeToken = object : TypeToken<List<Video>>() {}.type
                            val videos = Gson().fromJson<List<Video>>(dataValue, typeToken)
                            videoList = videos.sortedByDescending { it.publishedAt }  // video list is shorted based on publishedAt value [latest date will show first]
                            preparePlayerAndShowDataView()
                        } catch (e: Exception) {
                            LogUtil.e("Exception=", "=" + e.message)
                        }
                    }
                }
            }
            override fun onFailure(call: Call<String>, t: Throwable) {
                LogUtil.println("onFailure status", t.message.toString())
            }
        })
    }

    private fun preparePlayerAndShowDataView() {
        /**
         * if data present in videoList than check for ExoPlayer instance is created or not by calling preparePlayer()
        *  add media item in player and also set listener so that we can update ui as track change
         * */
        if (this::videoList.isInitialized && videoList.isNotEmpty()) {
            preparePlayer()
            for (item in videoList) {
                val mediaItem: MediaItem = MediaItem.Builder().setUri(Uri.parse(item.fullURL))
                    .setMediaId(item.id.toString()).setTag(item).build()
                player!!.addMediaItem(mediaItem)
            }
            player!!.addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(@Player.State state: Int) {
                    //when player state change at that time need to update ui if needed here we mostly cross check as this task done in onTracksChanged Listener
                    if (state == ExoPlayer.STATE_READY) {
                        if (currentItem != player!!.currentMediaItemIndex) {
                            currentItem = player!!.currentMediaItemIndex
                            showDataInView(currentItem)
                        }
                    } else if (state == ExoPlayer.STATE_ENDED) {
                        if (currentItem != player!!.currentMediaItemIndex) {
                            currentItem = player!!.currentMediaItemIndex
                            showDataInView(currentItem)
                        }
                    }
                }
            })
            player!!.addListener(object : Player.Listener {
                override fun onTracksChanged(tracks: Tracks) {
                    // when track change at that time need to Update UI using current tracks.
                    LogUtil.e("onTracksChanged", "=> " + player!!.currentMediaItemIndex)
                    if (currentItem != player!!.currentMediaItemIndex) {
                        currentItem = player!!.currentMediaItemIndex
                        showDataInView(currentItem)
                    }
                }
            })
            player!!.playWhenReady = playWhenReady
            player!!.seekTo(currentItem, playbackPosition)
            player!!.prepare()
            showDataInView(currentItem)
        }
    }

    private fun showDataInView(itemPosition: Int) {
        /**
        * show data in view using markwon as data may be in MD type as per response
         * show data for Title, Author and Description in TextView
        * */
        //condition check if data is present than show
        if (this::videoList.isInitialized && videoList.isNotEmpty() && videoList.size > itemPosition) {
            val markwon = Markwon.builder(this).build()
            val item = videoList[itemPosition]
            markwon.setMarkdown(tvTitle, item.title.toString())
            if (item.author != null) {
                markwon.setMarkdown(tvAuthor, item.author!!.name.toString())
            } else {
                tvAuthor.text = ""
            }
            markwon.setMarkdown(tvDescription, item.description.toString())
        }
    }

    private var player: ExoPlayer? = null
    private fun preparePlayer() {
        //create instance for ExoPlayer and make sure it not created multiple time
        if (player == null) {
            player = ExoPlayer.Builder(this).build()
            pvExoplayer.player = player // attaching player to exoplayer view
        }
    }

    private var playWhenReady = true // used to play automatically 1st video in list
    private var currentItem = 0 // used to resume play and also used for showing current playing item detail in contain views
    private var playbackPosition = 0L //play video for where it reached

    private fun releasePlayer() {
        //when leaving screen at that time releasing exoplayer to avoid memory problem and also make sure its stop playing
        player.let { exoPlayer ->
            if (exoPlayer != null) {
                //before releasing need to save its position  and current playing item
                playbackPosition = exoPlayer.currentPosition
                currentItem = exoPlayer.currentMediaItemIndex
                exoPlayer.release()
            }
        }
        // after releasing ExoPlayer making variable player to null so that next time it create new instance
        player = null
    }

}


