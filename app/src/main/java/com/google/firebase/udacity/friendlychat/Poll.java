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

import java.util.ArrayList;

public class Poll {

    private String author;
    private String name;
    private String description;
    private ArrayList<String> options;

    public Poll() {
    }

    public Poll(String author, String name, String description, ArrayList<String> options) {
        this.setAuthor(author);
        this.setDescription(description);
        this.setName(name);
        this.setOptions(options);
    }

    public String getAuthor() {
        return author;
    }

    public String getName() {
        return name;
    }

    public void setAuthor(String author){
        this.author = author;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<String> getOptions() {
        return options;
    }

    public void setOptions(ArrayList<String> options) {
        this.options = options;
    }
}
