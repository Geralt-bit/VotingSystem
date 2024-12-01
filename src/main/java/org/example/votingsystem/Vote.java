package org.example.votingsystem;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Vote {
    private User user;
    private VoteOption voteOption;
    private int voteCount;

    public Vote(User user, VoteOption voteOption, int voteCount) {
        this.user = user;
        this.voteOption = voteOption;
        this.voteCount = voteCount;
    }

    public User getUser() {
        return user;
    }

    public VoteOption getVoteOption() {
        return voteOption;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public String getUserName() {
        return user != null ? user.getUsername() : "";
    }

    public String getOptionName() {
        return voteOption.getOptionName();
    }

    public IntegerProperty voteCountProperty() {
        return new SimpleIntegerProperty(voteCount);
    }
}
