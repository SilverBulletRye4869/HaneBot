package net.serveron.hane.ranking;


import com.google.cloud.firestore.Query;
import net.serveron.hane.MainSystem;

import java.util.*;
import java.util.concurrent.ExecutionException;

public class Getter {
    private final Map<String,String> TYPE_UNIT_MAP = Map.of("blockBreakCount","個","blockPlaceCount","個","talkCount","回","loginTime","時間");
    //private final Set<String> GET_TYPE_SET = Set.of("blockBreakCount");


    public void run(){
                TYPE_UNIT_MAP.keySet().forEach(type->{
                Query que = MainSystem.getDB().collection(MainSystem.DOCUMENT_NAME).orderBy(type, Query.Direction.DESCENDING).limit(MainSystem.RANKING_MAX_COUNT);
                LinkedHashMap<String,Long> data = new LinkedHashMap<>();
                try {
                    que.get().get().getDocuments().forEach(e->data.put(e.getString("discordId"),e.getLong(type)));
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                Sender.send(type,data,TYPE_UNIT_MAP.get(type));
            });

    }
}
