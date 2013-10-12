package com.cloudbees.gasp.model;

/**
 * Copyright (c) 2013 Mark Prichard, CloudBees
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * User: Mark Prichard (mprichard@cloudbees.com)
 * Date: 8/27/13
 */
public class Places {
    private Place results[];
    private String status;
    private String next_page_token;

    public String getNext_page_token() {
        return next_page_token;
    }

    public Place[] getResults() {
        return results;
    }

    public String getStatus() {
        return status;
    }
}