package com.deray.meditation.module.music;

import java.util.List;

public class NeteaseSearch {


    /**
     * Code : OK
     * Body : [{"id":108478,"title":"醉赤壁","author":"林俊杰","url":"https://api.mlwei.com/music/api/wy/?key=523077333&cache=0&type=url&id=108478","pic":"https://api.mlwei.com/music/api/wy/?key=523077333&cache=0&type=pic&id=108478","lrc":"https://api.mlwei.com/music/api/wy/?key=523077333&cache=0&type=lrc&id=108478"},{"id":531295576,"title":"最美的期待","author":"周笔畅","url":"https://api.mlwei.com/music/api/wy/?key=523077333&cache=0&type=url&id=531295576","pic":"https://api.mlwei.com/music/api/wy/?key=523077333&cache=0&type=pic&id=531295576","lrc":"https://api.mlwei.com/music/api/wy/?key=523077333&cache=0&type=lrc&id=531295576"},{"id":526464293,"title":"空空如也 ","author":"任然","url":"https://api.mlwei.com/music/api/wy/?key=523077333&cache=0&type=url&id=526464293","pic":"https://api.mlwei.com/music/api/wy/?key=523077333&cache=0&type=pic&id=526464293","lrc":"https://api.mlwei.com/music/api/wy/?key=523077333&cache=0&type=lrc&id=526464293"},{"id":516076896,"title":"纸短情长（完整版）","author":"烟把儿","url":"https://api.mlwei.com/music/api/wy/?key=523077333&cache=0&type=url&id=516076896","pic":"https://api.mlwei.com/music/api/wy/?key=523077333&cache=0&type=pic&id=516076896","lrc":"https://api.mlwei.com/music/api/wy/?key=523077333&cache=0&type=lrc&id=516076896"},{"id":479408220,"title":"凉凉","author":"张碧晨","url":"https://api.mlwei.com/music/api/wy/?key=523077333&cache=0&type=url&id=479408220","pic":"https://api.mlwei.com/music/api/wy/?key=523077333&cache=0&type=pic&id=479408220","lrc":"https://api.mlwei.com/music/api/wy/?key=523077333&cache=0&type=lrc&id=479408220"},{"id":539941039,"title":"佛系少女","author":"冯提莫","url":"https://api.mlwei.com/music/api/wy/?key=523077333&cache=0&type=url&id=539941039","pic":"https://api.mlwei.com/music/api/wy/?key=523077333&cache=0&type=pic&id=539941039","lrc":"https://api.mlwei.com/music/api/wy/?key=523077333&cache=0&type=lrc&id=539941039"},{"id":30987293,"title":"讲真的","author":"曾惜","url":"https://api.mlwei.com/music/api/wy/?key=523077333&cache=0&type=url&id=30987293","pic":"https://api.mlwei.com/music/api/wy/?key=523077333&cache=0&type=pic&id=30987293","lrc":"https://api.mlwei.com/music/api/wy/?key=523077333&cache=0&type=lrc&id=30987293"},{"id":452986458,"title":"红昭愿","author":"音阙诗听","url":"https://api.mlwei.com/music/api/wy/?key=523077333&cache=0&type=url&id=452986458","pic":"https://api.mlwei.com/music/api/wy/?key=523077333&cache=0&type=pic&id=452986458","lrc":"https://api.mlwei.com/music/api/wy/?key=523077333&cache=0&type=lrc&id=452986458"},{"id":536502758,"title":"离人愁","author":"李袁杰","url":"https://api.mlwei.com/music/api/wy/?key=523077333&cache=0&type=url&id=536502758","pic":"https://api.mlwei.com/music/api/wy/?key=523077333&cache=0&type=pic&id=536502758","lrc":"https://api.mlwei.com/music/api/wy/?key=523077333&cache=0&type=lrc&id=536502758"},{"id":557584888,"title":"往后余生","author":"马良","url":"https://api.mlwei.com/music/api/wy/?key=523077333&cache=0&type=url&id=557584888","pic":"https://api.mlwei.com/music/api/wy/?key=523077333&cache=0&type=pic&id=557584888","lrc":"https://api.mlwei.com/music/api/wy/?key=523077333&cache=0&type=lrc&id=557584888"}]
     */

    private String Code;
    private List<BodyBean> Body;

    public String getCode() {
        return Code;
    }

    public void setCode(String Code) {
        this.Code = Code;
    }

    public List<BodyBean> getBody() {
        return Body;
    }

    public void setBody(List<BodyBean> Body) {
        this.Body = Body;
    }

    public static class BodyBean {
        /**
         * id : 108478
         * title : 醉赤壁
         * author : 林俊杰
         * url : https://api.mlwei.com/music/api/wy/?key=523077333&cache=0&type=url&id=108478
         * pic : https://api.mlwei.com/music/api/wy/?key=523077333&cache=0&type=pic&id=108478
         * lrc : https://api.mlwei.com/music/api/wy/?key=523077333&cache=0&type=lrc&id=108478
         */

        private int id;
        private String title;
        private String author;
        private String url;
        private String pic;
        private String lrc;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getPic() {
            return pic;
        }

        public void setPic(String pic) {
            this.pic = pic;
        }

        public String getLrc() {
            return lrc;
        }

        public void setLrc(String lrc) {
            this.lrc = lrc;
        }
    }
}
