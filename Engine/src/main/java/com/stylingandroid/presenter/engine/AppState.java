package com.stylingandroid.presenter.engine;

/**
 * Created by markallison on 25/05/2013.
 */
public final class AppState {
    private static AppState instance = new AppState();
    private CharSequence presentationName = null;

    private CharSequence eventLongName;
    private CharSequence eventShortName;
    private CharSequence eventHashtag;

    public static AppState getInstance() {
        return instance;
    }

    private AppState() {
        super();
    }

    public CharSequence getPresentationName() {
        if (presentationName == null) {
            return "move";
        }
        return presentationName;
    }

    public void setPresentationName(CharSequence presentationName) {
        this.presentationName = presentationName;
    }

    public void setEvent(CharSequence longName, CharSequence shortName, CharSequence hashtag) {
        this.eventLongName = longName;
        this.eventShortName = shortName;
        this.eventHashtag = hashtag;
    }

    public CharSequence getEventLongName() {
        return eventLongName;
    }

    public CharSequence getEventShortName() {
        return eventShortName;
    }

    public CharSequence getEventHashtag() {
        return eventHashtag;
    }
}
