package com.innee.czyhInterface.util.publicImage.storage.model;

import com.innee.czyhInterface.util.publicImage.util.StringUtils;

/**
 * Created by bailong on 15/2/20.
 */
public class FileListing {
    public FileInfo[] items;
    public String marker;

    public boolean isEOF() {
        return StringUtils.isNullOrEmpty(marker);
    }
}
