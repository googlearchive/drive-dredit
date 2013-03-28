# Copyright (C) 2012-2013 Google Inc.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

require 'rubygems'
require 'sinatra'
require 'google/api_client'
require 'google/api_client/client_secrets'

enable :sessions

SCOPES = [
  'https://www.googleapis.com/auth/drive.file',
  'https://www.googleapis.com/auth/userinfo.email',
  'https://www.googleapis.com/auth/userinfo.profile'
]

configure do
  set :port, 8000
  set :public_folder, 'assets'
  set :credentials, Google::APIClient::ClientSecrets.load

  # Preload API definitions
  client = Google::APIClient.new
  set :drive, client.discovered_api('drive', 'v2')
  set :oauth2, client.discovered_api('oauth2', 'v2')
end

##
# Main entry point for the app. Ensures the user is authorized
# & inits the editor for either edit of the opened files or creating
# a new file.
get '/' do
  # handle possible callback from OAuth2 consent page.
  if params[:code]
    authorize_code(params[:code])
    redirect '/'
  elsif params[:error] # User denied the oauth grant
    halt 403
  end

  # If not authorized, redirect to OAuth2 consent page.
  redirect api_client.authorization.authorization_uri.to_s unless authorized?

  if params[:state]
    state = MultiJson.decode(params[:state] || '{}')
    if state['parentId']
      redirect to("/#/create/#{state['parentId']}")
    else
      doc_id = state['ids'] ? state['ids'].first : ''
      redirect to("/#/edit/#{doc_id}")
    end
  end

  # render index.html
  return File.read(File.join(settings.public_folder, 'public/index.html'))

end

###
# Gets the current user profile.
#
get '/user' do
  result = api_client.execute!(:api_method => settings.oauth2.userinfo.get)
  json result.data
end

###
# Gets Drive metadata.
#
get '/about' do
  result = api_client.execute!(:api_method => settings.drive.about.get)
  json result.data
end

###
# Gets file metadata and contents.
#
get '/svc' do
  result = api_client.execute!(
    :api_method => settings.drive.files.get,
    :parameters => { :fileId => params['file_id'] })
  file = result.data.to_hash
  result = api_client.execute(:uri => result.data.downloadUrl)
  file['content'] = result.body
  json file
end

##
# Creates a new file.
post '/svc' do
  _, file, content = prepare_data(request.body)
  result = api_client.execute!(
    :api_method => settings.drive.files.insert,
    :body_object => file,
    :media => content,
    :parameters => {
      'uploadType' => 'multipart',
      'alt' => 'json'})
  json result.data.id
end

##
# Updates existing file metadata and contents.
put '/svc' do
  resource_id, file, content = prepare_data(request.body)
  result = api_client.execute(
    :api_method => settings.drive.files.update,
    :body_object => file,
    :media => content,
    :parameters => {
      'fileId' => resource_id,
      'newRevision' => params['newRevision'] || 'false',
      'uploadType' => 'multipart',
      'alt' => 'json' }
  )
  json result.data.id
end

helpers do
  ##
  # Render json data in a response
  def json(data, status_code = 200)
    content_type :json
    [status_code, data.to_json]
  end

  ##
  # Get an API client instance
  def api_client
    @client ||= (begin
      client = Google::APIClient.new

      client.authorization.client_id = settings.credentials.client_id
      client.authorization.client_secret = settings.credentials.client_secret
      client.authorization.redirect_uri = settings.credentials.redirect_uris.first
      client.authorization.scope = SCOPES
      client
    end)
  end

  def authorized?
    return api_client.authorization.access_token
  end
end

before do
  # Make sure access token is up to date for each request
  api_client.authorization.update_token!(session)
  puts api_client.authorization.inspect

  # if existing access token is expired and refresh token is set,
  # ask for a new access token.
  if api_client.authorization.refresh_token &&
    api_client.authorization.expired?
    api_client.authorization.fetch_access_token!
  end
end

after do
  # Serialize the access/refresh token to the session
  session[:access_token] = api_client.authorization.access_token
  session[:refresh_token] = api_client.authorization.refresh_token
  session[:expires_in] = api_client.authorization.expires_in
  session[:issued_at] = api_client.authorization.issued_at
end

##
# Upgrade our authorization code when a user launches the app from Drive &
# ensures saved refresh token is up to date
def authorize_code(code)
  api_client.authorization.code = code
  api_client.authorization.fetch_access_token!
  # put the tokens to the sesion
  session[:access_token] = api_client.authorization.access_token
  session[:refresh_token] = api_client.authorization.refresh_token
  session[:expires_in] = api_client.authorization.expires_in
  session[:issued_at] = api_client.authorization.issued_at
end


##
# Prepare request data for upload
def prepare_data(body)
  data = MultiJson.decode(body)
  resource_id = data['resource_id']
  [resource_id, data, data['content']]
end
