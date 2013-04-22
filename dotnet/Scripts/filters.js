'use strict';

/* Filters */

angular.module('app.filters', [])
    .filter('saveStateFormatter',
    function () {
        return function (state) {
            if (state == EditorState.DIRTY) {
                return 'You have unsaved changes';
            } else if (state == EditorState.SAVE) {
                return 'Saving...';
            } else if (state == EditorState.LOAD) {
                return 'Loading...';
            } else if (state == EditorState.READONLY) {
                return "Read only"
            } else {
                return 'All changes saved';
            }
        };
    })
    .filter('bytes',
    function () {
        return function (bytes) {
            bytes = parseInt(bytes);
            if (isNaN(bytes)) {
                return "...";
            }
            var units = [' bytes', ' KB', ' MB', ' GB', ' TB', ' PB', ' EB', ' ZB', ' YB'];
            var p = bytes > 0 ? Math.floor(Math.log(bytes) / Math.LN2 / 10) : 0;
            var u = Math.round(bytes / Math.pow(2, 10 * p));
            return u + units[p];
        }
    });

