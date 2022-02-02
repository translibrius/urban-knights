package com.ogentertainment.urbanknights;

import android.app.Application;

public class UrbanKnights extends Application  {

    private String playerID, playerUsername, playerPassword, playerSector, playerGold, playerColor, playerPlace1, playerPlace2, playerPlace3, playerLastRefresh;
    private String returnas;

    public String getUserVariable(String type){
        switch (type) {
            case "id":
                returnas = playerID;
            case "username":
                returnas = playerUsername;
            case "password":
                returnas = playerPassword;
            case "sector":
                returnas = playerSector;
            case "gold":
                returnas = playerGold;
            case "color":
                returnas = playerColor;
            case "place1":
                returnas = playerPlace1;
            case "place2":
                returnas = playerPlace2;
            case "place3":
                returnas = playerPlace3;
            case "lastrefresh":
                returnas = playerLastRefresh;
        }
        return returnas;
    }


    public void setUserVariable(String type, String value){
        switch (type) {
            case "id":
                playerID = value;
            case "username":
                playerUsername = value;
            case "password":
                playerPassword = value;
            case "sector":
                playerSector = value;
            case "gold":
                playerGold = value;
            case "color":
                playerColor = value;
            case "place1":
                playerPlace1 = value;
            case "place2":
                playerPlace2 = value;
            case "place3":
                playerPlace3 = value;
            case "lastrefresh":
                playerLastRefresh = value;
        }
    }

    public static boolean isActivityVisible() {
        return activityVisible;
    }

    public static void activityResumed() {
        activityVisible = true;
    }

    public static void activityPaused() {
        activityVisible = false;
    }

    private static boolean activityVisible;
}
