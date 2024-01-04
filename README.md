# AndroidVideoPlayer
A video player based on [AndroidX Media](https://github.com/androidx/media)

Player view | Quality
:-------------------------:|:-------------------------:
<img src="https://github.com/ArezooNazer/AndroidVideoPlayer/blob/master/demo/Screenshot_20220328_194400.png" width=200/> | <img src="https://github.com/ArezooNazer/AndroidVideoPlayer/blob/master/demo/Screenshot_20220328_194324.png" width=200/>

## Features
 - Support different stream type including [Progressive, HLS, DASH, SmoothStreaming](https://exoplayer.dev/media-sources.html)
 - Support different video qualities
 - Capability of subtitle selection (VTT format)
 - Mute mode
 - Support portrait and landscape format

## Working on
- Remove deprecated and unstable Exoplayer APIs

## Version notes

#### V.3.0.0
- Migrate to AndroidX Media (previous version is available in [v.2.0.0](https://github.com/ArezooNazer/AndroidVideoPlayer/releases/tag/v.2.0.0))

#### V.2.0.0
- Refactor project and migrate to MVVM, Koltin, Coroutines and Hilt (previous versions are available in separate branches)
- Upgrade ExoPlayer to 2.17.1 
- Support portrait and landscape mode

#### V.1.1.0 (27.3.2020)
- Migrate to Androidx
- Upgrade Exoplayer to 2.11.3
- Customize next/previous buttons
- Fix bugs:
    - Resume video using last watched position
    - playing list of videos
    - Unlock player

#### V.1.0.0
- Upgrade Exoplayer to 2.9.2
