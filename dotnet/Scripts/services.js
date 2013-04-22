'use strict';

var module = angular.module('app.services', []);

var ONE_HOUR_IN_MS = 1000 * 60 * 60;

// Shared model for current document
module.factory('doc',
    function ($rootScope) {
        var service = $rootScope.$new(true);
        service.dirty = false;
        service.lastSave = 0;
        service.timeSinceLastSave = function () {
            return new Date().getTime() - this.lastSave;
        };
        service.$watch('info',
            function (newValue, oldValue) {
                service.dirty = true;
            },
            true);
        service.$watch('info',
            function() {
                service.dirty = false;
            });
        return service;
    });

module.factory('editor',
    function (doc, backend, $q, $rootScope, $log) {
        var editor = null;
        var EditSession = require("ace/edit_session").EditSession;
        var service = {
            loading:false,
            saving:false,
            rebind:function (element) {
                editor = ace.edit(element);
            },
            snapshot:function () {
                doc.dirty = false;
                var data = angular.extend({}, doc.info);
                data.resource_id = doc.resource_id;
                if (doc.info.editable) {
                    data.content = editor.getSession().getValue();
                }
                return data;
            },
            create:function () {
                $log.info("Creating new doc");
                this.updateEditor({
                    content:'',
                    labels:{
                        starred:false
                    },
                    editable:true,
                    title:'Untitled document',
                    description:'',
                    mimeType:'text/plain',
                    resource_id:null
                });
            },
            load:function (id, reload) {
                $log.info("Loading resource", id, doc.resource_id);
                if (!reload && doc.info && id == doc.resource_id) {
                    return $q.when(doc.info);
                }
                this.loading = true;
                return backend.load(id).then(angular.bind(this,
                    function (result) {
                        this.loading = false;
                        this.updateEditor(result.data);
                        $rootScope.$broadcast('loaded', doc.info);
                        return result;
                    }), angular.bind(this,
                    function (result) {
                        $log.warn("Error loading", result);
                        this.loading = false;
                        $rootScope.$broadcast('error', {
                            action:'load',
                            message:"An error occured while loading the file"
                        });
                        return result;
                    }));
            },
            save:function (newRevision) {
                $log.info("Saving file", newRevision);
                if (this.saving || this.loading) {
                    throw 'Save called from incorrect state';
                }
                this.saving = true;
                var file = this.snapshot();

                // Force revision if first save of the session
                newRevision = newRevision || doc.timeSinceLastSave() > ONE_HOUR_IN_MS;
                var promise = backend.save(file, newRevision);
                promise.then(angular.bind(this,
                    function (result) {
                        $log.info("Saved file", result);
                        this.saving = false;
                        doc.resource_id = result.data;
                        doc.lastSave = new Date().getTime();
                        $rootScope.$broadcast('saved', doc.info);
                        return doc.info;
                    }), angular.bind(this,
                    function (result) {
                        this.saving = false;
                        doc.dirty = true;
                        $rootScope.$broadcast('error', {
                            action:'save',
                            message:"An error occured while saving the file"
                        });
                        return result;
                    }));
                return promise;
            },
            updateEditor:function (fileInfo) {
                $log.info("Updating editor", fileInfo);
                var session = new EditSession(fileInfo.content);
                session.on('change', function () {
                    doc.dirty = true;
                    $rootScope.$apply();
                });
                fileInfo.content = null;
                doc.lastSave = 0;
                doc.info = fileInfo;
                doc.resource_id = fileInfo.id;
                editor.setSession(session);
                editor.setReadOnly(!doc.info.editable);
                editor.focus();
            },
            state:function () {
                if (this.loading) {
                    return EditorState.LOAD;
                } else if (this.saving) {
                    return EditorState.SAVE;
                } else if (doc.dirty) {
                    return EditorState.DIRTY;
                } else if (!doc.info.editable) {
                    return EditorState.READONLY;
                }
                return EditorState.CLEAN;
            }
        };
        return service;
    });

module.factory('backend',
    function ($http, $log) {
        var jsonTransform = function (data, headers) {
            return angular.fromJson(data);
        };
        var service = {
            user:function () {
                return $http.get('/user', {transformResponse:jsonTransform});
            },
            about:function () {
                return $http.get('/about', {transformResponse:jsonTransform});
            },
            load:function (id) {
                return $http.get('/svc', {
                    transformResponse:jsonTransform,
                    params:{
                        'file_id':id
                    }
                });
            },
            save:function (fileInfo, newRevision) {
                $log.info('Saving', fileInfo);
                return $http({
                    url:'/svc',
                    method:fileInfo.resource_id ? 'PUT' : 'POST',
                    headers:{
                        'Content-Type':'application/json'
                    },
                    params:{
                        'newRevision':newRevision
                    },
                    transformResponse:jsonTransform,
                    data:JSON.stringify(fileInfo)
                });
            }
        };
        return service;
    });

module.factory('autosaver',
    function (editor, saveInterval, $timeout) {
        var saveFn = function () {
            if (editor.state() == EditorState.DIRTY) {
                editor.save(false);
            }
        };
        var createTimeout = function () {
            return $timeout(saveFn, saveInterval).then(createTimeout);
        }
        return createTimeout();
    });
