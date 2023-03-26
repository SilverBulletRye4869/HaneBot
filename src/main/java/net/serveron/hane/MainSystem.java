package net.serveron.hane;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.serveron.hane.ranking.Getter;
import net.serveron.hane.ranking.Sender;

import javax.security.auth.login.LoginException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class MainSystem {
    public static final String DOCUMENT_NAME = "users";  //データベース名
    public static final int RANKING_MAX_COUNT = 20;  //最大何人分のランキングを表示するか？

    private static JDA jda = null;
    private static TextChannel textChannel = null;
    private static Firestore db;
    private static String botToken = "";
    private static String rankingSendChannel = "";
    private static String firebaseJsonPath = "";


    public static void main(String[] args){
        if(!folderSetup())return;
        variableSetup();
        if(botToken.equals("") || rankingSendChannel.equals("") || firebaseJsonPath.equals("")){
            System.err.println("token又はランキングch、firebaseJsonパスの取得に失敗しました");
            return;
        }
        botSetup();firebaseSetup();


        new Getter().run();

    }

    private static boolean variableSetup(){
        try{
            Properties property = new Properties();
            property.load(new FileInputStream("hanebot/config.env"));
            botToken = property.getProperty("TOKEN");
            rankingSendChannel = property.getProperty("RANKING_CHANNEL");
            firebaseJsonPath = property.getProperty("FIREBASE_JSON_PATH");
            return true;
        }catch (IOException e){
            e.printStackTrace();
        }
        return false;
    }

    private static boolean folderSetup(){
        try{
            Files.createDirectories(Paths.get("hanebot"));
        }catch (IOException e){
            e.printStackTrace();
            return false;
        }
        File file = new File("hanebot/config.env");
        if(file.exists())return true;

        try {
            file.createNewFile();
        }catch (IOException e){
            System.err.println("config.envファイルの作成に失敗しました");
            e.printStackTrace();
            return false;
        }
        if(!file.isFile()){
            System.err.println("config.envはファイルではありません");
            return false;
        }
        if(!file.canWrite()){
            System.err.println("config.envは書き込み不可のファイルです");
            return false;
        }

        try {
            FileWriter fw = new FileWriter(file);
            fw.write(
                "TOKEN=\nRANKING_CHANNEL=\nFIREBASE_JSON_PATH="
            );
            fw.close();
        }catch (IOException e){
            System.err.println("README.txtファイルの書き込みに失敗しました");
            return false;
        }

        return true;
    }

    public static void botSetup(){
        try{
            jda = JDABuilder.createDefault(botToken, GatewayIntent.GUILD_MESSAGES)
                    .setRawEventsEnabled(true)
                    .setActivity(Activity.playing("TaichiServer"))
                    .build();
        }catch (LoginException e){
            e.printStackTrace();
            return;
        }
    }

    private static void firebaseSetup() {
        GoogleCredentials credentials;
        try {
            InputStream serviceAccount = new FileInputStream(firebaseJsonPath);
            credentials = GoogleCredentials.fromStream(serviceAccount);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(credentials)
                .build();
        FirebaseApp.initializeApp(options);

        db = FirestoreClient.getFirestore();
    }


    public static JDA getJDA(){return jda;}
    public static Firestore getDB(){return db;}

    public static TextChannel getTextChannel(){
        if(textChannel==null)textChannel = jda.getTextChannelById(rankingSendChannel);
        return textChannel;
    }


}
