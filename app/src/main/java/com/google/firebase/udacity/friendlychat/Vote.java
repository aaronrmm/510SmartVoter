/**
 * Copyright Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.firebase.udacity.friendlychat;

import java.util.HashMap;
import java.util.Map;

public class Vote {

    public static final int DBKEY_TAG = 2543;
    private String pollKey;
    private String uID;
    private String option;

    public Vote() {
    }

    public Vote(String pollKey, String uID, String option) {
        this.setUser(uID);
        this.setPollID(pollKey);
        this.setVote(option);
    }

    public void setPollID(String pollKey) {
        this.pollKey = pollKey;
    }

    public void setUser(String uID) {
        this.uID = uID;
    }

    public void setVote(String option) {
        this.option = option;
    }
    public String getPollID() {
        return this.pollKey;
    }

    public String getUser() {
        return this.uID;
    }

    public String getVote() {
        return this.option;
    }
    /*Returns the vote count for this option
    public int getVotes(String pollKey, String option) {
        int count = 0;
        for (Map.Entry entry : votes.entrySet()) {
            if (entry.getValue().equals(optionName)) {
                count++;
            }
        }
        return count;
    }*/

    /*/Returns the vote for this user
    public String getUsersVote(String user) {
        if (votes.containsKey(user)) {
            return votes.get(user);
        }
        return null;
    }

    public void setVote(String user, String option) {
        votes.put(user, option);
    }*/
}
