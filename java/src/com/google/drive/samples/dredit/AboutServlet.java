/*
 * Copyright (c) 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.drive.samples.dredit;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.About;

/**
 * Servlet that simply return the JSON of Drive's user About feed.
 *
 * @author nivco@google.com (Nicolas Garnier)
 * @author jbd@google.com (Burcu Dogan)
 */
@SuppressWarnings("serial")
public class AboutServlet extends DrEditServlet {
	
  /**
   * Returns a JSON representation of Drive's user's About feed.
   */
  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    Drive service = getDriveService(getCredential(req, resp));
    try {
    	About about = service.about().get().execute();
    	sendJson(resp, about);
    } catch (GoogleJsonResponseException e) {
      if (e.getStatusCode() == 401) {
        // The user has revoked our token or it is otherwise bad.
        // Delete the local copy so that their next page load will recover.
        deleteCredential(req, resp);
        sendGoogleJsonResponseError(resp, e);
      }
    }
  }
}
