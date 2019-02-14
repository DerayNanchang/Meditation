package com.deray.meditation.http;


import com.deray.meditation.module.music.NeteaseSearch;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface APIS {

    /**
     *  根据歌名找到歌曲/歌单
     * @param key       请用作者QQ：523077333
     * @param id        歌曲/歌单的id，当type=so(搜索音乐)时，id的值为关键词，必须对中文进行URL编码
     * @param type      解析类型：song 单曲，songlist 歌单，so 搜索，url 链接，pic 专辑图，lrc 歌词
     * @param cache     默认=0不缓存，当cache=1时开启缓存，提高解析速度70%，数据不更新时，可把参数值改成cache=0访问一次即可清除缓存。
     * @param nu        当type=so(搜索音乐)时有效，定义搜索结果数量，默认100
     * @return          Observable
     */
    @GET("/music/api/wy/")
    Observable<NeteaseSearch> getSearchNeteaseMusic(@Query("key") String key,@Query("id") String id,@Query("type") String type,@Query("cache") int cache,@Query("nu") int nu);

}
