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
import net.serveron.hane.ranking.ResetPerMonth;
import net.serveron.hane.ranking.Sender;

import javax.security.auth.login.LoginException;
import java.io.*;

public class MainSystem {
    public static final String DOCUMENT_NAME = "discordID";  //データベース名
    public static final int RANKING_MAX_COUNT = 20;  //最大何人分のランキングを表示するか？
    public static final String RANKING_SEND_CHANNEL = "";  //ランキングを送信するchのid

    private static final String TOKEN_FILE_PATH = "";
    private static final String FIREBASE_JSON_FILE_PATH = "";

    private static JDA jda = null;
    private static TextChannel textChannel = null;
    private static Firestore db;
    private static String botToken = null;


    public static void main(String[] args){
        botToken = getTokenFromFile();
        if(botToken==null){
            System.err.println("tokenの取得に失敗しました");
            return;
        }
        botSetup();firebaseSetup();


        new Getter();
        new ResetPerMonth();
        new Sender();


    }

    private static String getTokenFromFile(){
        try {
            File file = new File(TOKEN_FILE_PATH);
            if(!file.exists())file.createNewFile();
            if(!file.isFile() || !file.canRead())return null;
            BufferedReader br = new BufferedReader(new FileReader(file));
            return br.readLine();
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }

    public static void botSetup(){
        try{
            jda = JDABuilder.createDefault(botToken, GatewayIntent.GUILD_MESSAGES)
                    .setRawEventsEnabled(true)
                    .setActivity(Activity.playing("はね鯖"))
                    .build();
        }catch (LoginException e){
            e.printStackTrace();
            return;
        }
    }

    private static void firebaseSetup() {
        GoogleCredentials credentials;
        try {
            InputStream serviceAccount = new FileInputStream(FIREBASE_JSON_FILE_PATH);
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
        if(textChannel==null)textChannel = jda.getTextChannelById(RANKING_SEND_CHANNEL);
        return textChannel;
    }


}
