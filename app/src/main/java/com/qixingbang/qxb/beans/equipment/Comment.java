package com.qixingbang.qxb.beans.equipment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zqj on 2015/9/6 19:36.
 */
public class Comment {
    private Commenter mCommenter = new Commenter();
    private String mContent;
    private String mCommentTime;
    private int mId;

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getCommentTime() {
        return mCommentTime;
    }

    public void setCommentTime(String time) {
        this.mCommentTime = time;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String commentContent) {
        mContent = commentContent;
    }

    public String getUserName() {
        return mCommenter.getName();
    }

    public void setUserName(String name) {
        mCommenter.setName(name);
    }

    public String getUserIconUrl() {
        return mCommenter.getPortrait();
    }

    public void setUserIconUrl(String iconUrl) {
        mCommenter.setPortrait(iconUrl);
    }


    private class Commenter {
        private String mPortrait;
        private String mName;
        private int mId;

        public int getId() {
            return mId;
        }

        public void setId(int id) {
            mId = id;
        }

        public String getName() {
            return mName;
        }

        public void setName(String name) {
            mName = name;
        }

        public String getPortrait() {
            return mPortrait;
        }

        public void setPortrait(String portrait) {
            mPortrait = portrait;
        }
    }


    private final static String KEY_TIME = "commentTime";
    private final static String KEY_CONTENT = "content";
    private final static String KEY_COMMENTER = "commenter";
    private final static String KEY_ID = "commentId";

    private final static String KEY_NICKNAME = "nickname";
    private final static String KEY_USER_ID = "userId";
    private final static String KEY_ICON = "icon";

    public static Comment fromJsonObject(JSONObject jsonObject) {
        Comment comment = new Comment();
        comment.mCommentTime = jsonObject.optString(KEY_TIME);
        comment.mContent = jsonObject.optString(KEY_CONTENT);
        comment.mId = jsonObject.optInt(KEY_ID);

        JSONObject commenterJson = jsonObject.optJSONObject(KEY_COMMENTER);
        comment.mCommenter.mName = commenterJson.optString(KEY_NICKNAME);
        comment.mCommenter.mId = commenterJson.optInt(KEY_USER_ID);
        comment.mCommenter.mPortrait = commenterJson.optString(KEY_ICON);
        return comment;
    }

    public static List<Comment> fromJsonArray(JSONArray jsonArray) {
        List<Comment> list = new ArrayList<>();
        int length = jsonArray.length();
        if (length > 0) {
            for (int i = 0; i < length; i++) {
                list.add(fromJsonObject(jsonArray.optJSONObject(i)));
            }
        }
        return list;
    }

    public static void fromJsonArray(List<Comment> list, JSONArray jsonArray) {
        list.clear();
        int length = jsonArray.length();
        if (length > 0) {
            for (int i = 0; i < length; i++) {
                list.add(fromJsonObject(jsonArray.optJSONObject(i)));
            }
        }
    }

    public static JSONObject getSubmitCommentJSON(int itemId, String content, String type) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("parentId", itemId);
            jsonObject.put("content", content);
            jsonObject.put("type", type);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
