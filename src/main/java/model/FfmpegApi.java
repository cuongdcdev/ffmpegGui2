/*
 * A project of CuongDCDev@gmail.com
 */
package model;

import net.bramp.ffmpeg.*;

/**
 *
 * @author Cường <duongcuong96 at gmail dot com>
 */
public class FfmpegApi {

    private static String FFMPEG_PATH = "/usr/bin/ffmpeg";
    private static String FFPROBE_PATH = "/usr/bin/ffprobe";
    private static FfmpegApi instance = null;
    private static FFmpeg ffmpeg = null;
    private static FFprobe ffprobe = null;

    private FfmpegApi() {
        try {
            ffmpeg = new FFmpeg(FFMPEG_PATH);
            ffprobe = new FFprobe(FFPROBE_PATH);
        } catch (Exception ex) {
            System.out.println("Có vẻ như ko tìm thấy path! ");
            ex.printStackTrace();
        }

    }

    public static FfmpegApi getInstance() {
        if (instance == null) {
            instance = new FfmpegApi();
        }
        return instance;
    }

    public static FFmpeg getFfmpeg() {
        return instance.ffmpeg;
    }

    public static FFprobe getFfprobe() {
        return instance.ffprobe;
    }

//    public static void main(String[] args) {
//        try {
//            FfmpegApi api = FfmpegApi.getInstance();
////       FFmpegBuilder builder = new FFmpegBuilder().setInput("koi.mp4").addOutput("ok.clgv")
////                            .setAudioChannels(89).setAudioCodec("aac")
////                            .setVideoCodec("libx264").setStrict(FFmpegBuilder.Strict.EXPERIMENTAL)
////                            .done();
////       FFmpegExecutor executor = new FFmpegExecutor( api.getFfmpeg() , api.getFfprobe() );
////       executor.createJob(builder).run(); //one pass encode
//
////show process while encoding 
//            FFmpegExecutor executor = new FFmpegExecutor(api.getFfmpeg(), api.getFfprobe());
//            FFmpegProbeResult in = api.getFfprobe().probe("koi.mp4");
//            FFmpegBuilder builder = new FFmpegBuilder().setInput(in).addOutput("ok.avi").done();
//            FFmpegJob job = executor.createJob(builder, new ProgressListener() {
//                // Using the FFmpegProbeResult determine the duration of the input
//                final double duration_ns = in.getFormat().duration * TimeUnit.SECONDS.toNanos(1);
//
//                @Override
//                public void progress(Progress progress) {
//                    double percent = progress.out_time_ns / duration_ns;
//                    String str = Double.toString(percent);
//                    //out put percent
//                   
//                    System.out.println("progress: " + str.substring(0,4)  + "/ 1");
//                }
//            });
//            job.run();
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//
//    }
}
