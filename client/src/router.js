import Vue from 'vue'
import Router from 'vue-router'

import Folders from './views/Folders.vue'
import Document from './views/Document.vue'
import Dictionaries from './views/Dictionaries.vue'
import Reports from './views/Reports.vue'
import Search from './views/Search.vue'
import Error from './views/Error.vue'

import Login from './views/Login.vue'
import store from './store'

import axios from 'axios'

Vue.use(Router);

export const router = new Router({
    mode: 'history',
    base: process.env.BASE_URL,
    routes: [
        {
            path: '/',
            name: 'folders',
            component: Folders,
            alias: '/folders/*'
        },
        {
            path: '/document/:documentId',
            name: 'document',
            component: Document,
            props: true
        },
        {
            path: '/dictionaries',
            name: 'dictionaries',
            component: Dictionaries
        },
        {
            path: '/reports',
            name: 'reports',
            component: Reports
        },
        {
            path: '/search',
            name: 'search',
            component: Search
        },
        {
            path: '/error',
            name: 'error',
            component: Error
        },
        // {
        //     path: '/about',
        //     name: 'about',
        //     // route level code-splitting
        //     // this generates a separate chunk (about.[hash].js) for this route
        //     // which is lazy-loaded when the route is visited.
        //     component: () => import(/* webpackChunkName: "about" */ './views/About.vue')
        // },
        {
            path: '/login',
            name: 'login',
            component: Login,
            props: true
        }
    ]
});

router.beforeEach((to, from, next) => {
    // redirect to login page if not logged in and trying to access a restricted page
    const publicPages = ['/login'];
    const authRequired = !publicPages.includes(to.path);
    const loggedIn = store.state.user;

    if (authRequired && !loggedIn) {
        axios.get('/api/currentUser').then((res) => {
            const user = res.data;
            store.dispatch("setUser", user);
        }).catch((err) => {
            
        });
    }
    next();
});
