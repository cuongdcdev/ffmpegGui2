/*
* A project of CuongDCDev@gmail.com
 */
package model;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.RandomStringGenerator;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.ProcessResult;
import org.zeroturnaround.exec.stream.LogOutputStream;

/**
 *
 * @author Cường <duongcuong96 at gmail dot com>
 */
public class FfmpegApi {

    private static String FFMPEG_PATH = "/usr/bin/ffmpeg";
    private static String FFPROBE_PATH = "/usr/bin/ffprobe";
    private static String FFPLAY_PATH = "/usr/bin/ffplay";

    public static final String basePath = System.getProperty("user.dir");

    private String command = ""; //command to run 
    private String inputPath = "";
    private String outputPath = "";

    private String videoDuration = "00:00:00.00";
    private String currentVideoTime = "00:00:00.00";

    private boolean isConvertDone = false;

    private String log = "";
    private String videoProgressStat = "";

    private String params1 = " -hide_banner -y "; //tham so them vao 1 
//    private String params2 = " 1> " + basePath + File.separator +  "tmp" + File.separator + "convert_log.txt" + " 2>&1  "; // tham so them vao params 2
    private String params2 = "";

    public FfmpegApi(String commandType, String inputPath, String outputPath) {
        this.inputPath = inputPath;
        this.outputPath = outputPath;
        setCommand(commandType);
    }
    
    public FfmpegApi(String commandType, String inputPath) {
        this.inputPath = inputPath;
        setCommand(commandType);
    }

    public FfmpegApi() {

    }

    public void init(String commandType, String inputPath, String outputPath) {
        this.inputPath = inputPath;
        File f = new File(outputPath);
        if( !f.exists() )      
            this.outputPath = outputPath;
        else{
            RandomStringGenerator randomStringGen = new RandomStringGenerator.Builder().withinRange('a' , 'z').build();
            
            this.outputPath = f.getParent() + File.separator + (randomStringGen.generate(5) + "_" + f.getName());
            System.out.println("Output path : " + this.outputPath );
        }
        setCommand(commandType);
    }

    public static void main(String[] args) throws Exception {

        FfmpegApi ffmpeg = new FfmpegApi();
        ffmpeg.init("get_video_info", "/media/cuong/RELAX/MUSIC/Coldplay - Everglow.mp4", "/media/cuong/RELAX/MUSIC/Coldplay - Everglow_cutted.mp4");
        ffmpeg.cutVideo( "0:00:10", "-1" );
        
//        if (ffmpeg.startConvert()) {
//            System.out.println("\n success!!!");
//        } else {
//            System.out.println(" \n failed ! ");
//        }
//            System.out.println("video length : " + ffmpeg.getVideoLength() );

        // String x = "size=     552kB time=00:00:17.60 bitrate= 256.8kbits/s  ";
        //System.out.println("Chay thanh cong !, stt code :  " + pr.getOutput().getUTF8()  );
    }
/**
 * Trả về thời gian dã convert / tg tổng video 
 * @return 
 */
    public String getConvertStat() {
        if( currentVideoTime.trim().length() > 0 && videoDuration.trim().length() > 0  )
         return currentVideoTime + "/" + videoDuration;
        return "";
    }

    /**
     * Tra về tỉ lệ % của job
     *
     * @return
     */
    public long getProgressPercent() {

        String[] arrStr = videoProgressStat.split("/");

        try {
            if (arrStr.length == 2) {
                SimpleDateFormat sf1 = new SimpleDateFormat("HH:mm:ss.SS");

                Date d1 = sf1.parse(currentVideoTime);
                Date d2 = sf1.parse(videoDuration);
                System.out.println("current video time : " + d1.getTime() );
                System.out.println("video end : " + d2.getTime() );
                
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return 0;
    }
    
    public String getOutputFilePath(){
        return  outputPath;
    }
    public void parseLogStat(String logLine) {
        if (logLine.contains("size") && logLine.contains("time") && logLine.contains("bitrate")) {
//            System.out.println("Log line : " + logLine );
            currentVideoTime = ( StringUtils.substringAfter(logLine , "time=").substring(0, 11) );
        }

        if (logLine.contains("Duration") && logLine.contains("start") && logLine.contains("bitrate")) {
            String[] logArr = logLine.split(":");

//            for( int i = 0 ; i < logArr.length ; i++ ){
//                System.out.println("duration: " + logArr[i] + " | index: " + i  );
//            }
//            videoDuration = (logArr[1] + ":" + logArr[2] + ":" + logArr[3].replace(", start", "").trim()).trim();
                videoDuration = StringUtils.substringAfter(logLine, "Duration:").substring(0,11).trim();
            
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
    
    /**
     * Chay command va tra ve true or false
     * @param command
     * @return 
     */
    public boolean runCommand( String command ){
        String cmd2 = command.trim();
        try {
            String cmdFilePath = basePath + File.separator + "tmp" + File.separator + "tmpcmd2";
            FileWriter cmdFile = new FileWriter(cmdFilePath);
            cmdFile.write(cmd2);
            cmdFile.close();

            System.out.println(" [RAW COMMAND TO RUN ]:  " + cmd2);
            ProcessResult pr = new ProcessExecutor().commandSplit("bash " + cmdFilePath).redirectOutput(new LogOutputStream() {
                @Override
                protected void processLine(String line) {
                    System.out.println("[RAW COMMAND:]" + line );
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
            System.out.println("Có lỗi xảy ra : " + ex.getMessage());
        }
        return false;
    }

    public String getCommand() {

        return command;
    }
    
    /**
     * Su dung command de play video
     * @param filePath
     * @return 
     */
    public boolean playVideo( String filePath ){
        File f = new File(filePath);
        if( !f.exists() ) return false;
        return  runCommand( FFPLAY_PATH + " " + wrapString(filePath) );
    }
    
    /**
     * Wrap String inside comma
     * @param name
     * @return 
     */
    public String wrapString( String name ){
        return "'" + name.trim() + "'";
    }
    
    public void setCommand(String type) {
        switch (type) {
            case "mp3":
                command = getStringFromCommandFile("video_to_mp3");
                break;

            case "convert_video_fast":
                command = getStringFromCommandFile("convert_video_fast");
                break;

            case "h265":
                command = getStringFromCommandFile("to_h265");
                break;

            case "wmv":
                command = getStringFromCommandFile("wmv");
                break;
                
            case "vp9" : 
                command = getStringFromCommandFile("vp9");
                break;
                
            case "mkv" : 
                command = getStringFromCommandFile("mkv");
                break;
                
            case "avi" : 
                command = getStringFromCommandFile("avi");
                break;
                
            case "mp4" : 
                command = getStringFromCommandFile("to_h264mp4");
                break;
                
            case "join_video":
                command = getStringFromCommandFile( "join_video" );
                break;
                
                
                
            case "scale_video_720":
                command = getStringFromCommandFile( "scale_down_720" );
                break;
                
            case "scale_video_480":
                command = getStringFromCommandFile( "scale_down_480" );
                break;
                
            case "scale_video_320":
                command = getStringFromCommandFile( "scale_down_320" );
                break;
                
            case "get_video_info" : 
                command = getStringFromCommandFile( "get_video_info" );
                break;

        }

        System.out.println("Input path:  " + inputPath);

        command = " " + command.replace("{{i}}",  "'" + inputPath + "'").replace("{{o}}", "'" + outputPath + "'");
        System.out.println("[command to run] " + FFMPEG_PATH + params1 + command + params2);
    }

    public String getVideoProgressStat() {
        return videoProgressStat;
    }
    
    public String getVideoInfo(){
        try{
            String cmd2 = FFMPEG_PATH + params1 + command + params2;
            String cmdFilePath = basePath + File.separator + "tmp" + File.separator + "tmpcmd";
            FileWriter cmdFile = new FileWriter(cmdFilePath);
            cmdFile.write(cmd2);
            cmdFile.close();

            System.out.println(" command to run:  " + cmd2);
            //            ProcessResult pr = new ProcessExecutor().command( cmd2List ).redirectOutput(new LogOutputStream() {
            ProcessResult pr = new ProcessExecutor().commandSplit("bash " + cmdFilePath).readOutput(true).execute();
            int exitCode = pr.getExitValue();
//            if( exitCode > 0  ) return "hmm, Có vẻ như có lỗi xảy ra, bạn vui lòng check lại định dạng file nha :) ";
           return pr.outputUTF8();
        }catch( Exception ex ){
            ex.printStackTrace();
        }
        return "";
    }
    /**
     * return video length, in seconds
     * @return 
     */
    public double getVideoLength(){
        double secs = 0 ; 
        try{
            String videoInfo = getVideoInfo();
            String videoLength =  StringUtils.substringAfter(videoInfo, "Duration: ").substring(0,11).trim(); 
            System.out.println("Video length : " + videoLength);
            String[] time = videoLength.split(":");
            secs = Integer.parseInt(time[0] )*60*60 +  Integer.parseInt( time[1] )*60 +   Double.parseDouble(time[2] ); 
            return secs;
        }catch( Exception ex ){
            ex.printStackTrace();
        }
        return secs ;
    };
    /**
     * Get Video length in string
     * example: 00:12:34,00
     * @return 
     */
    public String getVideoLengthInString(){
            String videoInfo = getVideoInfo();
            String videoLength =  StringUtils.substringAfter(videoInfo, "Duration: ").substring(0,11).trim(); 
            System.out.println("Video length : " + videoLength);
            return videoLength;
    };
    
    public boolean cutVideo( String from, String to ){
//        params2 = 
        String command = "-i " + "'" + inputPath + "'" + " -ss " + from + "  -to " + to + " -c copy " + "'" + outputPath + "'";
        
        try {
            String cmd2 = FFMPEG_PATH + params1 + command ;
            String cmdFilePath = basePath + File.separator + "tmp" + File.separator + "tmpcmd";
            FileWriter cmdFile = new FileWriter(cmdFilePath);
            cmdFile.write(cmd2);
            cmdFile.close();
            System.out.println(" Cut command to run:  " + cmd2);
//            ProcessResult pr = new ProcessExecutor().command( cmd2List ).redirectOutput(new LogOutputStream() {
            ProcessResult pr = new ProcessExecutor().commandSplit("bash " + cmdFilePath).redirectOutput(new LogOutputStream() {
                @Override
                protected void processLine(String line) {
                    parseLogStat(line);
                    videoProgressStat = currentVideoTime + "/" + videoDuration;
                  //  System.out.println("[LINE]" + videoProgressStat);
                    System.out.println("progress percent : " + getConvertStat());
                }
            }).readOutput(true).execute();

            int exitCode = pr.getExitValue();

            if (exitCode > 0) {
                return false;
            }
            isConvertDone = true;
        }catch( Exception ex ){
            ex.printStackTrace();
        }
        return false;
    }
    
    
    public boolean startConvert() {
        try {
            String cmd2 = FFMPEG_PATH + params1 + command + params2;
            String cmdFilePath = basePath + File.separator + "tmp" + File.separator + "tmpcmd";
            FileWriter cmdFile = new FileWriter(cmdFilePath);
            cmdFile.write(cmd2);
            cmdFile.close();

            System.out.println(" command to run:  " + cmd2);
//            ProcessResult pr = new ProcessExecutor().command( cmd2List ).redirectOutput(new LogOutputStream() {
            ProcessResult pr = new ProcessExecutor().commandSplit("bash " + cmdFilePath).redirectOutput(new LogOutputStream() {
                @Override
                protected void processLine(String line) {
                    parseLogStat(line);
                    videoProgressStat = currentVideoTime + "/" + videoDuration;
                  //  System.out.println("[LINE]" + videoProgressStat);
                    System.out.println("progress percent : " + getConvertStat());
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
            System.out.println("Có lỗi xảy ra : " + ex.getMessage());
        }

        return false;
    }

    public boolean isVideoConvertDone() {
        return isConvertDone;
    }

}
