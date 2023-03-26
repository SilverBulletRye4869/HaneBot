package net.serveron.hane.ranking;

import com.google.cloud.firestore.SetOptions;
import net.serveron.hane.MainSystem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class ResetPerMonth {
    private final Map<String,Integer> RESET_MAP = Map.of("blockBreakCount",0,"blockPlaceCount",0,"talkCount",0,"loginTime",0);
    private final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");
    private Timer timer = null;



    public void intervalRun(){
        /*
        this.timer = new Timer(false);
        long delay;
        try {
            Date resetDay = SDF.parse(LocalDate.now().plus(1, ChronoUnit.MONTHS).toString());
            resetDay.setDate(1);
            delay = resetDay.getTime() - System.currentTimeMillis();
        }catch (ParseException e){
            e.printStackTrace();
            return;
        }

        TimerTask task = new TimerTask(){
            @Override
            public void run() {
                MainSystem.getDB().collection(MainSystem.DOCUMENT_NAME).listDocuments().forEach(e-> e.set(RESET_MAP, SetOptions.merge()));
            }
        };

        timer.schedule(task,delay);*/
    }

    public boolean stop(){
        if(timer==null)return false;
        timer.cancel();
        return true;
    }
}
