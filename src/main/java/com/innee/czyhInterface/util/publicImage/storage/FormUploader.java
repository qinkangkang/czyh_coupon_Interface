package com.innee.czyhInterface.util.publicImage.storage;


import java.io.File;
import java.io.IOException;

import com.innee.czyhInterface.util.publicImage.common.Config;
import com.innee.czyhInterface.util.publicImage.common.QiniuException;
import com.innee.czyhInterface.util.publicImage.http.AsyncCallback;
import com.innee.czyhInterface.util.publicImage.http.Client;
import com.innee.czyhInterface.util.publicImage.http.Response;
import com.innee.czyhInterface.util.publicImage.util.Crc32;
import com.innee.czyhInterface.util.publicImage.util.StringMap;


public final class FormUploader {

    private final String token;
    private final String key;
    private final File file;
    private final byte[] data;
    private final String mime;
    private final boolean checkCrc;
    private StringMap params;
    private Client client;
    private String fileName;

    FormUploader(Client client, String upToken, String key, byte[] data, StringMap params,
                 String mime, boolean checkCrc) {
        this(client, upToken, key, data, null, params, mime, checkCrc);
    }

    FormUploader(Client client, String upToken, String key, File file, StringMap params,
                 String mime, boolean checkCrc) {
        this(client, upToken, key, null, file, params, mime, checkCrc);
    }

    private FormUploader(Client client, String upToken, String key, byte[] data, File file, StringMap params,
                         String mime, boolean checkCrc) {
        this.client = client;
        token = upToken;
        this.key = key;
        this.file = file;
        this.data = data;
        this.params = params;
        this.mime = mime;
        this.checkCrc = checkCrc;
    }


    Response upload() throws QiniuException {
        buildParams();
        if (data != null) {
            return client.multipartPost(Config.zone.upHost, params, "file", fileName, data, mime, new StringMap());
        }
        return client.multipartPost(Config.zone.upHost, params, "file", fileName, file, mime, new StringMap());
    }

    void asyncUpload(final UpCompletionHandler handler) throws IOException {
        buildParams();
        if (data != null) {
            client.asyncMultipartPost(Config.zone.upHost, params, "file", fileName,
                    data, mime, new StringMap(), new AsyncCallback() {
                        @Override
                        public void complete(Response r) {
                            handler.complete(key, r);
                        }
                    });
            return;
        }
        client.asyncMultipartPost(Config.zone.upHost, params, "file", fileName,
                file, mime, new StringMap(), new AsyncCallback() {
                    @Override
                    public void complete(Response r) {
                        handler.complete(key, r);
                    }
                });
    }

    private void buildParams() throws QiniuException {
        params.put("token", token);
        if (key != null) {
            params.put("key", key);
        }
        if (file != null) {
            fileName = file.getName();
        }
        if (fileName == null || fileName.trim().length() == 0) {
            fileName = "fileName";
        }
        if (checkCrc) {
            long crc32 = 0;
            if (file != null) {
                try {
                    crc32 = Crc32.file(file);
                } catch (IOException e) {
                    throw new QiniuException(e);
                }
            } else {
                crc32 = Crc32.bytes(data);
            }
            params.put("crc32", "" + crc32);
        }
    }
}
