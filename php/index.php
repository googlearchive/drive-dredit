<?php
/**
 * Main entry point for web requests to the DrEdit PHP application.
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

require 'lib/Slim/Slim.php';
require 'lib/apiclient/Google_Client.php';
require 'lib/apiclient/contrib/Google_DriveService.php';
require 'lib/apiclient/contrib/Google_Oauth2Service.php';
require 'utils.php';
require 'credentials.php';

\Slim\Slim::registerAutoloader();
session_start();

// create a new Slim app.
$app = new \Slim\Slim(array(
  'templates.path' => './public',
));

// initialize a client with application credentials and required scopes.
$client = new Google_Client();
$client->setClientId(CLIENT_ID);
$client->setClientSecret(CLIENT_SECRET);
$client->setRedirectUri(REDIRECT_URI);
$client->setScopes(array(
  'https://www.googleapis.com/auth/drive',
  'https://www.googleapis.com/auth/userinfo.email',
  'https://www.googleapis.com/auth/userinfo.profile'));
$client->setUseObjects(true);

// if there is an existing session, set the access token
if ($user = get_user()) {
  $client->setAccessToken($user->tokens);
}

// initialize the drive service with the client.
$service = new Google_DriveService($client);

/**
 * Gets the metadata and contents for the given file_id.
 */
$app->get('/svc', function() use ($app, $client, $service) {
  checkUserAuthentication($app);
  checkRequiredQueryParams($app, array('file_id'));
  $fileId = $app->request()->get('file_id');
  try {
    // Retrieve metadata for the file specified by $fileId.
    $file = $service->files->get($fileId);

    // Get the contents of the file.
    $request = new Google_HttpRequest($file->downloadUrl);
    $response = $client->getIo()->authenticatedRequest($request);
    $file->content = $response->getResponseBody();

    renderJson($app, $file);
  } catch (Exception $ex) {
    renderEx($app, $ex);
  }
});

/**
 * Creates a new file with the metadata and contents
 * in the request body. Requires login.
 */
$app->post('/svc', function() use ($app, $client, $service) {
  checkUserAuthentication($app);
  $inputFile = json_decode($app->request()->getBody());
  try {
    $file = new Google_DriveFile();
    $file->setTitle($inputFile->title);
    $file->setDescription($inputFile->description);
    $file->setMimeType($mimeType);
    // Set the parent folder.
    if ($inputFile->parentId != null) {
      $parentsCollectionData = new Google_DriveFileParentsCollection();
      $parentsCollectionData->setId($inputFile->parentId);
      $file->setParentsCollection(array($parentsCollectionData));
    }
    $createdFile = $service->files->insert($file, array(
      'data' => $inputFile->content,
      'mimeType' => $mimeType,
    ));
    renderJson($app, $createdFile->id);
  } catch (Exception $ex) {
    renderEx($app, $ex);
  }
});

/**
 * Modifies an existing file given in the request body and responds
 * with the file id. Requires login.
 */
$app->put('/svc', function() use ($app, $client, $service) {
  checkUserAuthentication($app);
  $inputFile = json_decode($app->request()->getBody());
  $fileId = $inputFile->id;
  try {
    // Retrieve metadata for the file specified by $fileId and modify it with
    // the new changes.
    $file = $service->files->get($fileId);
    $file->setTitle($inputFile->title);
    $file->setDescription($inputFile->description);
    $file->getLabels()->setStarred($inputFile->labels->starred);

    // Update the existing file.
    $output = $service->files->update(
      $fileId, $file, array('data' => $inputFile->content)
    );

    renderJson($app, $output->id);
  } catch (Exception $ex) {
    renderEx($app, $ex);
  }
});

/**
 * Gets user profile. Requires login.
 */
$app->get('/user', function() use ($app, $client, $service) {
  checkUserAuthentication($app);
  $userinfoService = new Google_Oauth2Service($client);
  try {
    $user = $userinfoService->userinfo->get();
    renderJson($app, $user);
  } catch (Exception $ex) {
    renderEx($app, $ex);
  }
});

/**
 * Gets the information about the current user along with Drive API settings.
 * Requires login.
 */
$app->get('/about', function() use ($app, $client, $service) {
  checkUserAuthentication($app);
  try {
    $about = $service->about->get();
    renderJson($app, $about);
  } catch (Exception $ex) {
    renderEx($app, $ex);
  }
});

/**
 * The start page, also handles the OAuth2 callback.
 */
$app->get('/', function() use ($app, $client, $user) {
  // handle OAuth2 callback if code is set.
  if ($code = $app->request()->get('code')) {
    // handle code, retrieve credentials.
    $client->authenticate();
    $tokens = $client->getAccessToken();
    set_user($tokens);
    $app->redirect('/');
  }
  if ($user) { // if there is a user in the session
    $app->render('index.html');
  } else {
    // redirect to the auth page
    $app->redirect($client->createAuthUrl());
  }
});

$app->run();
