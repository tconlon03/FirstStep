package services;

import android.app.IntentService;
import android.content.Intent;

public class runMLService extends IntentService {

    /*
     * To call  create intent = new Intent(context, runMLService.class)
     * intent.setAction(String aciton)
     * startService(intent)
     */
    public runMLService(){
        super("runMLService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();
        //do whatever needs done with action

    }
}
