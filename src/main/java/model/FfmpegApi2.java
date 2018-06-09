/*
* A project of CuongDCDev@gmail.com
 */
package model;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.ProcessResult;
import org.zeroturnaround.exec.stream.LogOutputStream;


/**
 *
 * @author Cường <duongcuong96 at gmail dot com>
 */
public class FfmpegApi2 {

    private static String FFMPEG_PATH = "/usr/bin/ffmpeg";
    private static String FFPROBE_PATH = "/usr/bin/ffprobe";

    private static final String basePath = System.getProperty("user.dir");

    private String command = ""; //command to run 
    private String inputPath = "";
    private String outputPath = "";

    private String videoDuration = "";
    private String currentVideoTime = "";
    
    private boolean isConvertDone = false;
    
    private String log = "";
    private String videoProgressStat = "";

    private String params1 = " -hide_banner -y "; //tham so them vao 1 
//    private String params2 = " 1> " + basePath + File.separator +  "tmp" + File.separator + "convert_log.txt" + " 2>&1  "; // tham so them vao params 2
    private String params2 = "";

    public FfmpegApi2(String commandType, String inputPath, String outputPath) {
        this.inputPath = inputPath;
        this.outputPath = outputPath;
        setCommand(commandType);

    }
    
    public FfmpegApi2(){
    
    }

    public void init(String commandType, String inputPath, String outputPath) {
        this.inputPath = inputPath;
        this.outputPath = outputPath;
        setCommand(commandType);
    }

    public static void main(String[] args) throws Exception {

        FfmpegApi2 ffmpeg = new FfmpegApi2();
        ffmpeg.init("mp3", "/home/cuong/Videos/koi koi.mp4", "/home/cuong/Videos/koi.mp3");
        System.out.println(" ffmpeg cmd : " + ffmpeg.command);

        if (ffmpeg.startConvert()) {
            System.out.println("\n success!!!");
        } else {
            System.out.println(" \n failed ! ");
        }

       // String x = "size=     552kB time=00:00:17.60 bitrate= 256.8kbits/s  ";

        //System.out.println("Chay thanh cong !, stt code :  " + pr.getOutput().getUTF8()  );
    }
    public String getConvertStat(){
        return currentVideoTime  + "/" + videoDuration;
    }
    public void parseLogStat(String logLine) {
        if (logLine.contains("size") && logLine.contains("time") && logLine.contains("bitrate")) {
            String[] lArray = logLine.split("=");
//            for( int i = 0 ; i < lArray.length ; i++ ){
//                System.out.println( "line: " + lArray[i] + " index : " + i ); 
//            }
            currentVideoTime = (lArray[2].replace("bitrate", "").trim()).trim();
            System.out.println("time: " + currentVideoTime);
        }

        if (logLine.contains("Duration") && logLine.contains("start") && logLine.contains("bitrate")) {
            String[] logArr = logLine.split(":");

//            for( int i = 0 ; i < logArr.length ; i++ ){
//                System.out.println("duration: " + logArr[i] + " | index: " + i  );
//            }
            videoDuration = (logArr[1] + ":" + logArr[2] + ":" + logArr[3].replace(", start", "").trim()).trim();

//            System.out.println("a: " + vidoeDuration );
        }
    }

    /**
     * Get noi dung file text by flie name
     *
     * @param fileName
     * @return
     */
    public String getStringFromCommandFile(String fileName) {

        try {
            String path = basePath + "/cmds/" + fileName;
            System.out.println("Path : " + path);
            File f = new File(path);
            String s = new String(Files.readAllBytes(f.toPath()));
            return s;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return "";
    }

    public String getCommand() {

        return command;
    }

    public void setCommand(String type) {
        switch (type) {
            case "mp3":
                command = getStringFromCommandFile("video_to_mp3");
                break;

            case "convert_video_fast":
                command = getStringFromCommandFile("convert_video_fast");
                break;

            case "x264_to_x265":
                command = getStringFromCommandFile("x264_to_x265");
                break;

            case "get_video_info":
                command = getStringFromCommandFile("get_video_info");
                break;

        }
        

        System.out.println("Input path:  " + inputPath );
//        
//        inputPath = inputPath.replace(" ", "\\");
//        outputPath = outputPath.replace(" ", "\\");
        
        command = " " + command.replace("{{i}}", "\'" + inputPath + "\'" ).replace("{{o}}","\'" + outputPath + "\'" );
        System.out.println("[command to run] " + FFMPEG_PATH + params1 + command + params2);
    }
    
    public String getVideoProgressStat(){
        return videoProgressStat;
    }
    
    public boolean startConvert() {
        try {
            String cmd2 = FFMPEG_PATH + params1 + command + params2;
            String filePath = basePath + File.separator  +"tmp" + File.separator + "tmpcmd";
            FileWriter cmdFile = new FileWriter( filePath );
            cmdFile.write( cmd2 );
            cmdFile.close();
            
            
            
            
//
//            ArrayList<String> cmd2List = new ArrayList();
//            cmd2List.add(0 , cmd2);
            System.out.println(" cmd2  " + cmd2  );
            
//            CommandLine cmdLine = CommandLine.parse( cmd2 );
            
            
            
            ProcessResult pr = new ProcessExecutor().commandSplit("bash " + filePath ).redirectOutput(new LogOutputStream() {
                @Override
                protected void processLine(String line) {
                    parseLogStat(line);
                    videoProgressStat = currentVideoTime + "/" + videoDuration;
//                    System.out.println("[LINE]" + line );
                    System.out.println( "Progress stat: " + getVideoProgressStat() );
                }
            }).readOutput(true).execute();

            int exitCode = pr.getExitValue();

            if (exitCode > 0) {
                return false;
            }
            isConvertDone = true;
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Có lỗi xảy ra : " + ex.getMessage() );
        }

        return false;
    }
    
    public boolean isVideoConvertDone(){
        return isConvertDone;
    }

}
