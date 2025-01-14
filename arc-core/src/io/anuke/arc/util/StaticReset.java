package io.anuke.arc.util;

import io.anuke.arc.*;

/** An extremely simply utility class used for checking whether the application has re-initialized.*/
public class StaticReset{
    private Files files;

    /** Returns true if the application has re-initialized since the last call.*/
    public boolean check(){
        if(files == null || files != Core.files){
            files = Core.files;
            return true;
        }
        return false;
    }
}
