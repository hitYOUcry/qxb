package com.qixingbang.qxb.server;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.qixingbang.qxb.common.utils.LogUtil;

import org.apache.http.HttpEntity;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

/**
 * Created by cr30 on 2015/11/8.
 */
public class MultipartRequest extends JsonObjectRequest {
    private HttpEntity mHttpEntity;

    /**
     * Constructor: MultipartRequest
     */
    public MultipartRequest(String url, HttpEntity entity, Response.Listener<JSONObject> listener,
                            Response.ErrorListener errorListener) {
        super(url, listener, errorListener);
        mHttpEntity = entity;
    }

    /**
     * Constructor: MultipartRequest
     */
    public MultipartRequest(int method, String url, HttpEntity entity,
                            Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
        mHttpEntity = entity;
    }

    @Override
    public String getBodyContentType() {
        return mHttpEntity.getContentType().getValue();
    }

    @Override
    public byte[] getBody() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            // 将mHttpEntity写入到bos中
            mHttpEntity.writeTo(bos);
        } catch (Exception e) {
            LogUtil.e("", "IOException writing to ByteArrayOutputStream");
        }
        return bos.toByteArray();
    }
}
