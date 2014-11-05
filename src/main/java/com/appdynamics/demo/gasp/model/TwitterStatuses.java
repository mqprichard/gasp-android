package com.appdynamics.demo.gasp.model;

/**
 * Copyright (c) 2013 Mark Prichard
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

class TwitterStatuses {
    private TwitterStatus[] statuses;

    public TwitterStatus[] getStatuses() {
        return statuses;
    }

    public void setStatuses(TwitterStatus[] statuses) {
        this.statuses = statuses;
    }
}
