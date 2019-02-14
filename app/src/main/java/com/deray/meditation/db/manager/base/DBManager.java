package com.deray.meditation.db.manager.base;

import android.content.Context;

import com.deray.meditation.db.dao.DaoMaster;
import com.deray.meditation.db.dao.DaoSession;
import com.deray.meditation.db.dao.MusicDao;
import com.deray.meditation.db.dao.PlayListDao;
import com.deray.meditation.db.dao.SearchDao;

import org.greenrobot.greendao.database.Database;

public class DBManager {

    private static final String DB_NAME = "MEDITATION";
    private MusicDao musicDao;
    private SearchDao searchDao;
    private PlayListDao playListDao;

    private DBManager(){

    }

    private static class GetInstance{
        private static DBManager manager = new DBManager();
    }

    public static DBManager get(){
        return GetInstance.manager;
    }

    public void init(Context context){
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, DB_NAME);
        Database db = helper.getWritableDb();
        DaoSession session = new DaoMaster(db).newSession();
        musicDao = session.getMusicDao();
        searchDao = session.getSearchDao();
        playListDao = session.getPlayListDao();
    }

    public MusicDao getMusicDao(){
        return musicDao;
    }

    public SearchDao getSearchDao(){return searchDao; }

    public PlayListDao getPlayListDao(){return playListDao;}
}
