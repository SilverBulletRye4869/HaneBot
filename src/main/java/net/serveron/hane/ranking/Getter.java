package net.serveron.hane.ranking;


import com.google.cloud.firestore.Query;
import net.serveron.hane.MainSystem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

public class Getter {
    private final Set<String> GET_TYPE_SET = Set.of("blockBreakCount","blockPlaceCount","talkCount","loginTime");
    //private final Set<String> GET_TYPE_SET = Set.of("blockBreakCount");
    private final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");
    private Timer timer;

    public Getter(){
        intervalRun();
    }

    public void intervalRun(){
        this.timer = new Timer(false);
        long delay;
        try {
            delay = SDF.parse(LocalDate.now().plus(1, ChronoUnit.DAYS).toString()).getTime() - System.currentTimeMillis();
        }catch (ParseException e){
            e.printStackTrace();
            return;
        }
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                GET_TYPE_SET.forEach(type->{
                    Query que = MainSystem.getDB().collection(MainSystem.DOCUMENT_NAME).orderBy(type, Query.Direction.DESCENDING).limit(MainSystem.RANKING_MAX_COUNT);
                    LinkedHashMap<String,Long> data = new LinkedHashMap<>();
                    try {
                        que.get().get().getDocuments().forEach(e->data.put(e.getString("discordId"),e.getLong(type)));
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                    Sender.send(type,data);
                });
            }
        };

        timer.schedule(task,delay,86400000);
    }

    public boolean stop(){

        if(timer==null)return false;
        timer.cancel();
        return true;
    }
}
