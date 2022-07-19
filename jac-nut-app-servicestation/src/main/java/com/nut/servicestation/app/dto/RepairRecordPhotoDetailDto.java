package com.nut.servicestation.app.dto;

import lombok.Data;

import java.util.List;

@Data
public class RepairRecordPhotoDetailDto {
    /**
     * 图片种类
     */
    private String photoType;
    /**
     * 图片Url
     */
    private List<Photo> photoList;

    public static class Photo{
        /**
         * 图片Url
         */
        private String url;

        public Photo(String url) {
            this.url = url;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

}
