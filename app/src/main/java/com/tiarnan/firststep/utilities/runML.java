package com.tiarnan.firststep.utilities;


import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.Browser;
import android.util.Log;
import android.webkit.WebIconDatabase;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class runML extends Worker {

    /*
     * To call  create intent = new Intent(context, runMLService.class)
     * intent.setAction(String aciton)
     * startService(intent)
     */
    private String ml_tag = "ML_WORKER_LOGGING";

    private String mSearches[];
    private static String CHROME_BOOKMARKS_URI =
            "content://com.android.chrome.browser/bookmarks";

    public runML(@NonNull Context context, @NonNull WorkerParameters params){
        super(context, params);
    }

    @Override
    public Result doWork() {
        //get social media data too
        ArrayList <String> searches = getSearchHistory();
        if (runTFModel(searches)){
            //TFModel flagged a search
            //Send message to this person
        };
        return Result.success();
    }

    private ArrayList<String> getSearchHistory() {
        ArrayList <String> searches = new ArrayList<String>();
        return searches;
      /*  ContentResolver cr =  getApplicationContext().getContentResolver();
        String[] proj = new String[] { Browser.BookmarkColumns.TITLE, Browser.BookmarkColumns.URL , Browser.BookmarkColumns.DATE};
        Uri uriCustom = Uri.parse("content://com.android.chrome.browser/bookmarks");
        String sel = Browser.BookmarkColumns.BOOKMARK + " = 0"; // 0 = history, 1 = bookmark
        Cursor mCur = cr.query(uriCustom, proj, sel, null, null);
        String title = "";
        String url = "";
        if (mCur.moveToFirst() && mCur.getCount() > 0) {
            while (mCur.isAfterLast() == false) {
                title = mCur.getString(mCur.getColumnIndex(Browser.BookmarkColumns.TITLE));
                url = mCur.getString(mCur.getColumnIndex(Browser.BookmarkColumns.URL));
                // Do something with title and url
                searches.add("Title="+title+"---"+url);
                mCur.moveToNext();
            }
        }
        return searches;*/
    };

    private boolean runTFModel(ArrayList<String> searches){

        return false;
    }

}
