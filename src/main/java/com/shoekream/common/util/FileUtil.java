package com.shoekream.common.util;

import com.shoekream.common.exception.ErrorCode;
import com.shoekream.common.exception.ShoeKreamException;

import java.util.UUID;

public class FileUtil {

    public static void checkFileFormat(String originalFileName) {

        int index;
        try {
            index = originalFileName.lastIndexOf(".");
        } catch(StringIndexOutOfBoundsException e) {
            throw new ShoeKreamException(ErrorCode.WRONG_FILE_FORMAT);
        }

        String ext = originalFileName.substring(index + 1);
        if(!(ext.equals("jpg") || ext.equals("jpeg") || ext.equals("png") || ext.equals("gif"))) {
            throw new ShoeKreamException(ErrorCode.WRONG_FILE_FORMAT);
        }
    }

    public static String makeFileName(String originalFileName, String folder) {

        int index = originalFileName.lastIndexOf(".");
        String ext = originalFileName.substring(index + 1);

        // 저장할 파일 이름
        String storedFileName = UUID.randomUUID() + "." + ext;

        // 저장할 디렉토리 경로 + 파일 이름
        return folder + "/" + storedFileName;
    }

}
