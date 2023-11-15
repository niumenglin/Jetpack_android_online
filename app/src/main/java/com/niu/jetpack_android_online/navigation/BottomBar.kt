package com.niu.jetpack_android_online.navigation

class BottomBar {
    /**
     * activeColor : #333333
     * inActiveColor : #666666
     * tabs : [{"size":24,"enable":true,"index":0,"pageUrl":"main/tabs/home","title":"首页"},{"size":24,"enable":true,"index":1,"pageUrl":"main/tabs/sofa","title":"沙发"},{"size":40,"enable":true,"index":2,"tintColor":"#ff678f","pageUrl":"main/tabs/publish","title":""},{"size":24,"enable":true,"index":3,"pageUrl":"main/tabs/find","title":"发现"},{"size":24,"enable":true,"index":4,"pageUrl":"main/tabs/my","title":"我的"}]
     */
    var activeColor: String? = null
    var inActiveColor: String? = null
    var tabs: List<Tab>? = null
    var selectTab = 0 //底部导航栏默认选中项

    class Tab {
        /**
         * size : 24
         * enable : true
         * index : 0
         * route : "home_fragment"
         * title : 首页
         */
        var size = 0
        var enable = false
        var index = 0
        var route: String? = null
        var title: String? = null
    }
}