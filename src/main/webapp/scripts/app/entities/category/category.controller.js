'use strict';

angular.module('jhipsterApp')
    .controller('CategoryController', function ($scope, Category, ParseLinks) {
        $scope.categorys = [];
        $scope.page = 1;
        $scope.loadAll = function() {
            Category.query({page: $scope.page, per_page: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                for (var i = 0; i < result.length; i++) {
                    $scope.categorys.push(result[i]);
                }
            });
        };
        $scope.reset = function() {
            $scope.page = 1;
            $scope.categorys = [];
            $scope.loadAll();
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.create = function () {
            Category.update($scope.category,
                function () {
                    $scope.reset();
                    $('#saveCategoryModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.update = function (id) {
            Category.get({id: id}, function(result) {
                $scope.category = result;
                $('#saveCategoryModal').modal('show');
            });
        };

        $scope.delete = function (id) {
            Category.get({id: id}, function(result) {
                $scope.category = result;
                $('#deleteCategoryConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Category.delete({id: id},
                function () {
                    $scope.reset();
                    $('#deleteCategoryConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.clear = function () {
            $scope.category = {name: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
