# AndroidVideoPlayer
A video player based on Exoplayer 

customized playerView            |  quality options
:-------------------------:|:-------------------------:
![](https://github.com/ArezooNazer/AndroidVideoPlayer/demo/Screenshot_2020-03-26-19-40-37.png)  |  ![](https://github.com/ArezooNazer/AndroidVideoPlayer/demo/Screenshot_2020-03-26-16-32-28.png)
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

## 2. Create a VideoPlayer instance
You can use PlayerActivity.java class and extend the features you want but if you want to use this player in another activity, create an instance of VideoPlayer class in your activity.
```java
    VideoPlayer player;
    
    //as you can see in activity_player.xml you have to use PlayerView for exoplayer content to be played
    PlayerView playerView;
    
    //you can customize exoplayer ui and adding your desire ImageButtons by overriding exo_playback_control_view
    ImageButton mute, unMute, subtitle, setting, lock, unLock;
```
    
## 3. Initialize player
 Initialize your player as follow :
 
 ```java
        if (singleORMultipleVideo == 1) {
            //if you have single video and a list of subtitles
            setVideoSubtitleList();
            player = new VideoPlayer(playerView, getApplicationContext(), videoUri, this);
        } else {
            //if you have a list of videos and their subtitles
            initializeDb();
            player = new VideoPlayer(playerView, getApplicationContext(), urlDatabase.urlDao().getAllUrls(), this);
        }
        //optional setting
        playerView.getSubtitleView().setVisibility(View.GONE);
        player.seekToOnDoubleTap();

        //optional setting : start video from selected time
        player.seekToSelectedPosition(0, 0, 10);  
```
## 4. Add listener
Add listener implementations for ImageButtons in your activity.

