package com.example.player.util;

public class Url {

    /***********************************************************
     Video urls
     ***********************************************************/

//     type 3
//    private String videoUri = "https://hw6.cdn.asset.aparat.com/aparat-video/22800e8c8e34bc7b232f1139e236e35c12202710-144p__53462.mp4";
//    private  String videoUri = "https://hw20.cdn.asset.aparat.com/aparat-video/b1a82edf9f71b969f8ddd0c0ce24dfd912382539-144p__28538.mp4";

//    hls stream type 2
//    private String videoUri = " http://qthttp.apple.com.edgesuite.net/1010qwoeiuryfg/sl.m3u8";

//    hls with 8 resolutions
//    private String videoUri = "https://bitdash-a.akamaihd.net/content/sintel/hls/playlist.m3u8";
//    private String videoUri = "http://trailers.divx.com/divx_prod/divx_plus_hd_showcase/Sintel_DivXPlus_6500kbps.mkv";

    private static String videoUri = "http://www.storiesinflight.com/js_videosub/jellies.mp4";

//     yaratube
//    private static String videoUri = "http://stream.vasapi.click:1935/vod/_definst_/smil:1509518630_4Y82wuRYhh.mp4.smil/playlist.m3u8?wprefixendtime=1541198749&wprefixstarttime=0&wprefixstore=16&wprefixuserid=0&wprefixhash=ZbYEBYO0wx6LWHC6dchldsupd9cS-zDX52Gop3CTdA0%3D";

    /***********************************************************
     subtitle urls
     ***********************************************************/

    //    private String subtitleUri = "http://www.storiesinflight.com/js_videosub/jellies.srt";

    private static String subtitleUri = "";

    public static String getVideoUri() {
        return videoUri;
    }

    public static String getSubtitleUri() {
        return subtitleUri;
    }
}
