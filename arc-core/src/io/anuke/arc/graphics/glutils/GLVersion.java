package io.anuke.arc.graphics.glutils;

import io.anuke.arc.Application;
import io.anuke.arc.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GLVersion{

    private final String vendorString;
    private final String rendererString;
    private final Type type;
    private final String TAG = "GLVersion";
    private int majorVersion;
    private int minorVersion;
    private int releaseVersion;

    public GLVersion(Application.ApplicationType appType, String versionString, String vendorString, String rendererString){
        if(appType == Application.ApplicationType.Android) this.type = Type.GLES;
        else if(appType == Application.ApplicationType.iOS) this.type = Type.GLES;
        else if(appType == Application.ApplicationType.Desktop) this.type = Type.OpenGL;
        else if(appType == Application.ApplicationType.WebGL) this.type = Type.WebGL;
        else this.type = Type.NONE;

        if(type == Type.GLES){
            //OpenGL<space>ES<space><version number><space><vendor-specific information>.
            extractVersion("OpenGL ES (\\d(\\.\\d){0,2})", versionString);
        }else if(type == Type.WebGL){
            //WebGL<space><version number><space><vendor-specific information>
            extractVersion("WebGL (\\d(\\.\\d){0,2})", versionString);
        }else if(type == Type.OpenGL){
            //<version number><space><vendor-specific information>
            extractVersion("(\\d(\\.\\d){0,2})", versionString);
        }else{
            majorVersion = -1;
            minorVersion = -1;
            releaseVersion = -1;
            vendorString = "";
            rendererString = "";
        }

        this.vendorString = vendorString;
        this.rendererString = rendererString;
    }

    private void extractVersion(String patternString, String versionString){
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(versionString);
        boolean found = matcher.find();
        if(found){
            String result = matcher.group(1);
            String[] resultSplit = result.split("\\.");
            majorVersion = parseInt(resultSplit[0], 2);
            minorVersion = resultSplit.length < 2 ? 0 : parseInt(resultSplit[1], 0);
            releaseVersion = resultSplit.length < 3 ? 0 : parseInt(resultSplit[2], 0);
        }else{
            Log.err("[Arc GL] Invalid version string: " + versionString);
            majorVersion = 2;
            minorVersion = 0;
            releaseVersion = 0;
        }
    }

    /** Forgiving parsing of gl major, minor and release versions as some manufacturers don't adhere to spec **/
    private int parseInt(String v, int defaultValue){
        try{
            return Integer.parseInt(v);
        }catch(NumberFormatException nfe){
            Log.err("[Arc GL] Error parsing number: " + v + ", assuming: " + defaultValue);
            return defaultValue;
        }
    }

    /** @return what {@link Type} of GL implementation this application has access to, e.g. {@link Type#OpenGL} or {@link Type#GLES} */
    public Type getType(){
        return type;
    }

    /** @return the major version of current GL connection. -1 if running headless */
    public int getMajorVersion(){
        return majorVersion;
    }

    /** @return the minor version of the current GL connection. -1 if running headless */
    public int getMinorVersion(){
        return minorVersion;
    }

    /** @return the release version of the current GL connection. -1 if running headless */
    public int getReleaseVersion(){
        return releaseVersion;
    }

    /** @return the vendor string associated with the current GL connection */
    public String getVendorString(){
        return vendorString;
    }

    /**
     * @return the name of the renderer associated with the current GL connection.
     * This name is typically specific to a particular configuration of a hardware platform.
     */
    public String getRendererString(){
        return rendererString;
    }

    /**
     * Checks to see if the current GL connection version is higher, or equal to the provided test versions.
     * @param testMajorVersion the major version to test against
     * @param testMinorVersion the minor version to test against
     * @return true if the current version is higher or equal to the test version
     */
    public boolean isVersionEqualToOrHigher(int testMajorVersion, int testMinorVersion){
        return majorVersion > testMajorVersion || (majorVersion == testMajorVersion && minorVersion >= testMinorVersion);
    }

    /** @return a string with the current GL connection data */
    public String getDebugVersionString(){
        return "Type: " + type + "\n" +
        "Version: " + majorVersion + ":" + minorVersion + ":" + releaseVersion + "\n" +
        "Vendor: " + vendorString + "\n" +
        "Renderer: " + rendererString;
    }

    public enum Type{
        OpenGL,
        GLES,
        WebGL,
        NONE
    }
}
