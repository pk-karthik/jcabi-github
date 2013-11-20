/**
 * Copyright (c) 2012-2013, JCabi.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the jcabi.com nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import javax.json.Json;
import javax.json.JsonObject;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Github issue.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.1
 * @see <a href="http://developer.github.com/v3/issues/">Issues API</a>
 * @checkstyle MultipleStringLiterals (500 lines)
 */
@Immutable
@SuppressWarnings("PMD.TooManyMethods")
public interface Issue extends Comparable<Issue>, JsonReadable, JsonPatchable {

    /**
     * Repository we're in.
     * @return Repo
     */
    @NotNull(message = "repository is never NULL")
    Repo repo();

    /**
     * Get its number.
     * @return Issue number
     */
    int number();

    /**
     * Get all comments of the issue.
     * @return Comments
     * @see <a href="http://developer.github.com/v3/issues/comments/">Issue Comments API</a>
     */
    @NotNull(message = "comments are never NULL")
    Comments comments();

    /**
     * Get all labels of the issue.
     * @return Labels
     * @see <a href="http://developer.github.com/v3/issues/labels/">Labels API</a>
     */
    @NotNull(message = "labels are never NULL")
    Labels labels();

    /**
     * Get all events of the issue.
     * @return Events
     * @see <a href="http://developer.github.com/v3/issues/events/#list-events-for-an-issue">List Events for an Issue</a>
     */
    @NotNull(message = "iterable of events is never NULL")
    Iterable<Event> events();

    /**
     * Smart Issue with extra features.
     */
    @Immutable
    @ToString
    @Loggable(Loggable.DEBUG)
    @EqualsAndHashCode(of = "issue")
    final class Smart implements Issue {
        /**
         * Encapsulated issue.
         */
        private final transient Issue issue;
        /**
         * Public ctor.
         * @param iss Issue
         */
        public Smart(final Issue iss) {
            this.issue = iss;
        }
        /**
         * Get its author.
         * @return Author of issue (who submitted it)
         * @throws IOException If fails
         */
        public User author() throws IOException {
            return this.issue.repo().github().users().get(
                this.issue.json().getJsonObject("user").getString("login")
            );
        }
        /**
         * Is it open?
         * @return TRUE if it's open
         * @throws IOException If fails
         */
        public boolean isOpen() throws IOException {
            // @checkstyle MultipleStringLiterals (1 line)
            return "open".equals(this.state());
        }
        /**
         * Open it (make sure it's open).
         * @throws IOException If fails
         */
        public void open() throws IOException {
            this.state("open");
        }
        /**
         * Close it (make sure it's closed).
         * @throws IOException If fails
         */
        public void close() throws IOException {
            this.state("closed");
        }
        /**
         * Get its state.
         * @return State of issue
         * @throws IOException If fails
         */
        public String state() throws IOException {
            final String state = this.issue.json().getString("state");
            if (state == null) {
                throw new IllegalStateException(
                    String.format(
                        "state is NULL is issue #%d", this.issue.number()
                    )
                );
            }
            return state;
        }
        /**
         * Change its state.
         * @param state State of issue
         * @throws IOException If fails
         */
        public void state(final String state) throws IOException {
            this.issue.patch(
                Json.createObjectBuilder().add("state", state).build()
            );
        }
        /**
         * Get its body.
         * @return Body of issue
         * @throws IOException If fails
         */
        public String title() throws IOException {
            final String title = this.issue.json().getString("title");
            if (title == null) {
                throw new IllegalStateException(
                    String.format(
                        "title is NULL is issue #%d", this.issue.number()
                    )
                );
            }
            return title;
        }
        /**
         * Change its state.
         * @param text Text of issue
         * @throws IOException If fails
         */
        public void title(final String text) throws IOException {
            this.issue.patch(
                Json.createObjectBuilder().add("title", text).build()
            );
        }
        /**
         * Get its title.
         * @return Title of issue
         * @throws IOException If fails
         */
        public String body() throws IOException {
            final String body = this.issue.json().getString("body");
            if (body == null) {
                throw new IllegalStateException(
                    String.format(
                        "body is NULL is issue #%d", this.issue.number()
                    )
                );
            }
            return body;
        }
        /**
         * Change its body.
         * @param text Body of issue
         * @throws IOException If fails
         */
        public void body(final String text) throws IOException {
            this.issue.patch(
                Json.createObjectBuilder().add("body", text).build()
            );
        }
        /**
         * Assign this issue to another user.
         * @param login Login of the user to assign to
         * @throws IOException If fails
         */
        public void assign(final String login) throws IOException {
            this.issue.patch(
                Json.createObjectBuilder().add("assignee", login).build()
            );
        }
        /**
         * Get its URL.
         * @return URL of issue
         * @throws IOException If fails
         */
        public URL url() throws IOException {
            final String url = this.issue.json().getString("url");
            if (url == null) {
                throw new IllegalStateException(
                    String.format(
                        "url is NULL is issue #%d", this.issue.number()
                    )
                );
            }
            try {
                return new URL(url);
            } catch (MalformedURLException ex) {
                throw new IllegalStateException(ex);
            }
        }
        /**
         * Get its HTML URL.
         * @return URL of issue
         * @throws IOException If fails
         */
        public URL htmlUrl() throws IOException {
            final String url = this.issue.json().getString("html_url");
            if (url == null) {
                throw new IllegalStateException(
                    String.format(
                        "html_url is NULL is issue #%d", this.issue.number()
                    )
                );
            }
            try {
                return new URL(url);
            } catch (MalformedURLException ex) {
                throw new IllegalStateException(ex);
            }
        }
        /**
         * When this issue was created.
         * @return Date of creation
         * @throws IOException If fails
         */
        public Date createdAt() throws IOException {
            final String date = this.issue.json().getString("created_at");
            if (date == null) {
                throw new IllegalStateException(
                    String.format(
                        "created_at is NULL is issue #%d", this.issue.number()
                    )
                );
            }
            return new Time(date).date();
        }
        /**
         * Is it a pull requests?
         * @return TRUE if it is a pull request
         * @throws IOException If fails
         */
        public boolean isPull() throws IOException {
            return this.issue.json()
                .getJsonObject("pull_request")
                .containsKey("url");
        }
        /**
         * Get pull request.
         * @return Pull request
         * @throws IOException If fails
         */
        public Pull pull() throws IOException {
            final String url = this.issue.json()
                .getJsonObject("pull_request")
                .getString("url");
            return this.issue.repo().pulls().get(
                Integer.parseInt(url.substring(url.lastIndexOf('/') + 1))
            );
        }
        @Override
        public Repo repo() {
            return this.issue.repo();
        }
        @Override
        public int number() {
            return this.issue.number();
        }
        @Override
        public Comments comments() {
            return this.issue.comments();
        }
        @Override
        public Labels labels() {
            return this.issue.labels();
        }
        @Override
        public Iterable<Event> events() {
            return this.issue.events();
        }
        @Override
        public JsonObject json() throws IOException {
            return this.issue.json();
        }
        @Override
        public void patch(final JsonObject json) throws IOException {
            this.issue.patch(json);
        }
        @Override
        public int compareTo(final Issue obj) {
            return this.issue.compareTo(obj);
        }
    }

}