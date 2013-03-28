require 'rubygems'
require 'sinatra'

# Sinatra defines #set at the top level as a way to set application configuration
set :run, false
set :env, (ENV['RACK_ENV'] ? ENV['RACK_ENV'].to_sym : :development)

require './main'
run Sinatra::Application
