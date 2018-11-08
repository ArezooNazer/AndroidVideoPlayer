# AndroidVideoPlayer
A video player based on Exoplayer 

customized playerView            |  quality options
:-------------------------:|:-------------------------:
![](https://github.com/ArezooNazer/AndroidVideoPlayer/blob/playerModule/demoImage/ExoplayerDemo.png)  |  ![](https://github.com/ArezooNazer/AndroidVideoPlayer/blob/playerModule/demoImage/ExoQuality.png)
# Features
 <ul>
  <li>
   Support different stream type including HLS, DASH, SmoothStreaming
  </li>
 <li>
   The ability to play single video or a list of videos
  </li>
  <li>
   cache video 
  </li>
  <li>
  support different video qualities
  </li>
 <li>
   Switch between different subtitles
  </li>
  <li>
   lock exoplayer screen
  </li>
  <li>
   forward and backward by double tap on screen
  </li>
  <li>
   mute mode
  </li>
  <li>
   loop toggle mode
  </li>
 </ul>
 
 # Get started
 Everything you need is in Player Module. just follow the instructions.
 
 ## 1. Add dependency
 In this project we use Exoplayer v.2.9.0 
 
```java
 //Exoplayer
 implementation 'com.google.android.exoplayer:exoplayer:2.9.0'
 
 //FFmpegMediaMetadataRetriever
 implementation 'com.github.wseemann:FFmpegMediaMetadataRetriever:1.0.14'
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

