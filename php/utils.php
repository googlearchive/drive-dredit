<?php
/**
 * Utility functions for the DrEdit PHP application.
 *
 * @author Burcu Dogan <jbd@google.com>
 *
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Returns the current user in the session or NULL.
 */
function get_user() {
  if (isset($_SESSION["user"])) {
    return json_decode($_SESSION["user"]);
  }
  return NULL;
}

/**
 * Sets the current user.
 */
function set_user($tokens) {
  $_SESSION["user"] = json_encode(array(
    tokens => $tokens
  ));
}

/**
 * Deletes the user in the session.
 */
function delete_user() {
  $_SESSION["user"] = NULL;
}

/**
 * Checks whether or not there is an authenticated
 * user in the session. If not, responds with error message.
 */
function checkUserAuthentication($app) {
  $user = get_user();
  if (!$user) {
    $app->renderErrJson($app, 401, 'User is not authenticated.');
  }
}

/**
 * Checks whether or not all given params are represented in the
 * request's query parameters. If not, responds with error message.
 */
function checkRequiredQueryParams($app, $params = array()) {
  foreach ($params as &$param) {
    if (!$app->request()->get($param)) {
      renderErrJson($app, 400, 'Required parameter missing.');
    }
  }
};

/**
 * Renders the given object as JSON.
 */
function renderJson($app, $obj) {
  echo json_encode($obj);
}

/**
 * Renders the given message as JSON and responds with the
 * given HTTP status code.
 */
function renderErrJson($app, $statusCode, $message) {
  echo json_encode(array( message => $message ));
  $app->halt($statusCode);
}

/**
 * Renders the given Exception object as JSON.
 */
function renderEx($app, $ex) {
  echo json_encode($ex);
}
