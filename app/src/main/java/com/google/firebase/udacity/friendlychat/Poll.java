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

import java.util.List;

public class Poll {

    public static final int DBKEY_TAG = 2543;
    private String author;
    private String title;
    private String description;
    private String option1;
    private String option2;
    private String dbKey;
    public Poll() {
    }

    public Poll(String author, String title, String description,  String option1, String option2) {
        this.setAuthor(author);
        this.setDescription(description);
        this.setTitle(title);
        this.setOption1(option1);
        this.setOption2(option2);
    }

    public String getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    public void setAuthor(String author){
        this.author = author;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOption1() {
        return option1;
    }

    public void setOption1(String option1) {
        this.option1 = option1;
    }

    public String getOption2() {
        return option2;
    }

    public void setOption2(String option2) {
        this.option2 = option2;
    }

    public String getDbKey() {
        return dbKey;
    }

    public void setDbKey(String dbKey) {
        this.dbKey = dbKey;
    }

    public String toString() {
        String str = "";
        str += "Title: " + this.getTitle() + "\n";
        str += "Author: " + this.getAuthor() + "\n";
        str += "Description: " + this.getDescription() + "\n";
        str += "DbKEY: " + this.getDbKey() + "\n";
        return str;
    }
}
