package com.stylingandroid.presenter.engine;

/**
 * Created by markallison on 25/05/2013.
 */
public final class AppState {
    private static AppState instance = new AppState();
    private String presentationName = null;

    private String eventLongName;
    private String eventShortName;
    private String eventHashtag;

    public static AppState getInstance() {
        return instance;
    }

    private AppState() {
        super();
    }

    public String getPresentationName() {
        if (presentationName == null) {
            return "move";
        }
        return presentationName;
    }

    public void setPresentationName(String presentationName) {
        this.presentationName = presentationName;
    }

    public void setEvent(String longName, String shortName, String hashtag) {
        this.eventLongName = longName;
        this.eventShortName = shortName;
        this.eventHashtag = hashtag;
    }

    public String getEventLongName() {
        return eventLongName;
    }

    public String getEventShortName() {
        return eventShortName;
    }

    public String getEventHashtag() {
        return eventHashtag;
    }
}
