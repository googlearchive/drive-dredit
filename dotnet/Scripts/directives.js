'use strict';

var module = angular.module('app.directives', []);

module.directive('aceEditor',
    function (editor) {
        return {
            retrict:'A',
            link:function (scope, element) {
                editor.rebind(element[0]);
            }
        };
    });

module.directive('star',
    function () {
        return {
            restrict:'E',
            replace:true,
            scope:{
                val:'=value',
                // Value bound to
                eventFn:'&click'
                // Optional expression evaluated on click
            },
            link:function (scope, element) {
                element.bind('click',
                    function () {
                        scope.$apply(function () {
                            scope.val = !scope.val;
                        });
                        scope.$eval(scope.eventFn, scope.val);
                    });
            },
            template:'<i class="star" ng-class="{\'icon-star\' : val, \'icon-star-empty\' : !val}" ng-click="toggle()"></i>'
        }
    });

module.directive('alert',
    function ($rootScope) {
        return {
            restrict:'E',
            replace:true,
            link:function (scope, element) {
                $rootScope.$on('error',
                    function (event, data) {
                        scope.message = data.message;
                        element.show();
                    });
                scope.close = function () {
                    element.hide();
                };
            },
            template:'<div class="hide alert alert-error">' +
                '  <span class="close" ng-click="close()">×</span>' +
                '  {{message}}' +
                '</div>'
        }
    })