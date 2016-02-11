package com.example.user.malendar;

import java.io.Serializable;

/**
 * Created by user on 16. 2. 11.
 */
public class ImageList  implements Serializable {

    public String path;
    public String name;

    public ImageList(String p, String n){
        path = p;
        name = n;
    }
}
