# AndroidVideoPlayer
A video player based on Exoplayer 

customized playerView            |  quality options
:-------------------------:|:-------------------------:
![](https://github.com/ArezooNazer/AndroidVideoPlayer/blob/master/demo/Screenshot_2020-03-26-19-40-37.png)  |  ![](https://github.com/ArezooNazer/AndroidVideoPlayer/blob/master/demo/Screenshot_2020-03-26-16-32-28.png)
# Features
 <ul>
  <li>
   Support different stream type including HLS, DASH, SmoothStreaming
  </li>
 <li>
   The ability to play single video or a list of videos
  </li>
  <li>
   Cache video
  </li>
  <li>
  Support different video qualities
  </li>
 <li>
   Switch between different subtitles
  </li>
  <li>
   Lock player screen
  </li>
  <li>
   Forward and backward by double tap on screen
  </li>
  <li>
   Mute mode
  </li>
  <li>
   Loop toggle mode
  </li>
   <li>
     AndroidX, ExoPlayer version is 2.11.3
   </li>
 </ul>
 
 # Get started

 ## 1. Dependencies
 In this project we used Exoplayer v.2.11.3

```java
 //Exoplayer
 implementation 'com.google.android.exoplayer:exoplayer:2.11.3'

 /** Room
 *  to save each video subtitles & video last watched length to resume player on next play
 */
 implementation 'androidx.room:room-runtime:2.2.5'

 //stetho Optional
 debugImplementation 'com.facebook.stetho:stetho:1.5.1'

 //leak canary Optional
 debugImplementation 'com.squareup.leakcanary:leakcanary-android:1.6.3'
```

## 2. Create an instance of VideoPlayer in your activity

```java
    //videoSource is a list of SingleVideo which contains list of videos,
    //their subtitle list & their last watched length (used to resume the video)
    player = new VideoPlayer(playerView, getApplicationContext(), videoSource, this);

    //used to pause/resume player on incoming calls
    mAudioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);

    //used to hide/show player controller views
    playerView.setControllerVisibilityListener(visibility ->
      {
         if (player.isLock())
             playerView.hideController();
             back.setVisibility(visibility == View.VISIBLE && !player.isLock() ? View.VISIBLE : View.GONE);
      });

    //optional setting
    playerView.getSubtitleView().setVisibility(View.GONE);
    player.seekToOnDoubleTap();
```
    
## 3. Initialize ExoPlayer
Initialize your ExoPlayer in VideoPlayer as follow :
 
 ```java
        cacheDataSourceFactory = new CacheDataSourceFactory(
          context,
          100 * 1024 * 1024,
          5 * 1024 * 1024);

        trackSelector = new DefaultTrackSelector(context);
        trackSelector.setParameters(trackSelector
                        .buildUponParameters()
                        .setMaxVideoSizeSd());

        exoPlayer = new SimpleExoPlayer.Builder(context)
                        .setTrackSelector(trackSelector)
                        .build();

        playerView.setPlayer(exoPlayer);
        playerView.setKeepScreenOn(true);
        exoPlayer.setPlayWhenReady(true);
        exoPlayer.addListener(componentListener);
        //build mediaSource depend on video type (Regular, HLS, DASH, etc)
        mediaSource = buildMediaSource(videoSource.getVideos().get(index), cacheDataSourceFactory);
        exoPlayer.prepare(mediaSource);
        //resume video
        seekToSelectedPosition(videoSource.getVideos().get(index).getWatchedLength(), false);
```
## 4. Add listener
Add listener implementations for player control buttons in your activity.

## Version notes

#### V.1.1.0 (27.3.2020)
 <ul>
   <li>
      Migrate to Androidx
   </li>
   <li>
      Exoplayer v.2.11.3
   </li>
   <li>
      Resume player using watched length bug fixed
   </li>
   <li>
      Unlock player bug fixed
   </li>
 </ul>

#### V.1.0.0
 <ul>
   <li>
      Exoplayer v.2.9.2
   </li>
 </ul>
