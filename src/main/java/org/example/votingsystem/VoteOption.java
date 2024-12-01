package org.example.votingsystem;

public class VoteOption {
    private int id;
    private String optionName;

    public VoteOption(int id, String optionName) {
        this.id = id;
        this.optionName = optionName;
    }

    public VoteOption(String optionName) {
        this.optionName = optionName;
    }

    public int getId() {
        return id;
    }

    public String getOptionName() {
        return optionName;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setOptionName(String optionName) {
        this.optionName = optionName;
    }

    @Override
    public String toString() {
        return optionName;
    }
}
