package com.deray.meditation.db.manager.search;

import com.deray.meditation.db.bean.Music;
import com.deray.meditation.db.bean.Search;
import com.deray.meditation.db.dao.SearchDao;
import com.deray.meditation.db.manager.base.DBManager;
import com.deray.meditation.utils.HanyuPinyinHelper;

import org.greenrobot.greendao.query.QueryBuilder;

import java.io.IOException;
import java.util.List;

import taobe.tec.jcc.JChineseConvertor;

public class SearchManager {

    public interface TYPES{
        int MUSIC = 1;
        int VIDEO = 2;
    }
    private String search1;
    private String search2;
    private String search3;

    public interface TAGS{
        String SEARCH_1 = "search1";
        String SEARCH_2 = "search2";
        String SEARCH_3 = "search3";

        String MUSIC_NAME = "music_name";       // 歌名
        String SINGER = "singer";   // 歌手
        String ALBUM = "album";     // 专辑
    }
    SearchDao dao = DBManager.get().getSearchDao();
    JChineseConvertor jChineseConvertor;

    {
        try {
            jChineseConvertor = JChineseConvertor.getInstance();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private SearchManager() {
    }

    private static class GetInstance{
        private static SearchManager searchManager = new SearchManager();
    }

    public static SearchManager get(){
        return GetInstance.searchManager;
    }

    public String setID(String type,String search1,String search2){
        return "_"+type + "_" + search1+"_" + search2+"_";
    }
    public String getID(int type,String search1,String search2){
        return "_"+type + "_" + search1+"_" + search2+"_";
    }

    /**
     *  怕以后忘记定义的关键字代表什么意思
     * @return
     */
/*    public String findFieldByTypeAndTag(int type,String tag){
        switch (type){
            case TYPES.MUSIC:
                switch (tag){
                    case TAGS.SEARCH_1 : return TAGS.MUSIC_NAME;
                    case TAGS.SEARCH_2 : return TAGS.SINGER;
                    case TAGS.SEARCH_3 : return TAGS.ALBUM;
                }
                break;
        }
    }*/

    public void setMusicSearch(List<Music> musics){
        for (Music music : musics){
            insertMusicSearch(music);
        }

    }

    private void insertMusicSearch(Music music) {
        Search search = new Search();
        // 主键
        search.setID(getID(TYPES.MUSIC,music.getName(),music.getSinger()));
        search.setMusicKey(music.getKey());
        // 类型
        search.setType(TYPES.MUSIC+"");
        // 简繁
        search.setSearch1(jChineseConvertor.t2s(music.getName()));
        search.setSearch2(jChineseConvertor.t2s(music.getSinger()));
        search.setSearch3(jChineseConvertor.t2s(music.getAlbum()));
        search.setTraditionalSearch1(jChineseConvertor.s2t(music.getName()));
        search.setTraditionalSearch2(jChineseConvertor.s2t(music.getSinger()));
        search.setTraditionalSearch3(jChineseConvertor.s2t(music.getAlbum()));
        // 全拼首字母拼音
        search.setCompleteSearch1(HanyuPinyinHelper.getPinyinString(music.getName()));
        search.setCompleteSearch2(HanyuPinyinHelper.getPinyinString(music.getSinger()));
        search.setCompleteSearch3(HanyuPinyinHelper.getPinyinString(music.getAlbum()));
        search.setInitialSearch1(HanyuPinyinHelper.getFirstLettersLo(music.getName()));
        search.setInitialSearch2(HanyuPinyinHelper.getFirstLettersLo(music.getSinger()));
        search.setInitialSearch3(HanyuPinyinHelper.getFirstLettersLo(music.getAlbum()));
        dao.insertOrReplace(search);
    }


    public List<Search> findSearchByEditable(String editable){
        String keyword = "%" + editable + "%";
        QueryBuilder<Search> builder = DBManager.get().getSearchDao().queryBuilder();
        return builder.where(builder.or(SearchDao.Properties.Search1.like(keyword),
                SearchDao.Properties.Search2.like(keyword),
                SearchDao.Properties.Search3.like(keyword),
                SearchDao.Properties.TraditionalSearch1.like(keyword),
                SearchDao.Properties.TraditionalSearch2.like(keyword),
                SearchDao.Properties.TraditionalSearch3.like(keyword),
                SearchDao.Properties.CompleteSearch1.like(keyword),
                SearchDao.Properties.CompleteSearch2.like(keyword),
                SearchDao.Properties.CompleteSearch3.like(keyword),
                SearchDao.Properties.InitialSearch1.like(keyword),
                SearchDao.Properties.InitialSearch2.like(keyword),
                SearchDao.Properties.InitialSearch3.like(keyword))).build().list();
    }

    public List<Search> findAll(){
        return DBManager.get().getSearchDao().queryBuilder().list();
    }
}
