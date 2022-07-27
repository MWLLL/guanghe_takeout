package com.guanghe;

import org.junit.jupiter.api.Test;

import java.util.UUID;

public class UploadFileTest {
    @Test
    public void test1(){
        String fileName = "fdadfdfa.jpg";
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        String filename  = UUID.randomUUID().toString()+suffix;//xxx.jpg
        System.out.println(filename);
    }
}
