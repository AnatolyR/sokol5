import axios from 'axios';
import store from '../store';
import { router } from '../router';

export const userService = {
    login,
    logout
};

function login(username, password, rememberMe) {
    // if (username === "admin" && password === "admin") {
    //     store.dispatch("setUser", {name: username}).then(() => {
    //         router.push({ path: '/' });
    //     });
    // }

    var bodyFormData = new FormData();
    bodyFormData.set('j_username', username);
    bodyFormData.set('j_password', password);
    bodyFormData.set('remember-me', rememberMe);
        // '&remember-me=' +
        // credentials.rememberMe +
        bodyFormData.set('submit', 'Login');
    const headers = {};

    // return new Promise((resolve, reject) => {
    return axios.post('/api/authentication', bodyFormData, {
            maxRedirects: 0
        });
            // .then((res) => {
            //     console.log("login res", res);
            //     resolve();
            // }).catch((err) => {
            //     console.log("login err", err);
            //     reject();
            // });
    // });
}

function logout() {
    axios.get('/api/logout', {
        maxRedirects: 0
    }).then((res) => {
        store.dispatch("setUser", null).then(() => {
            location.href = "/";
        });
    }).catch((err) => {
        store.dispatch("setUser", null).then(() => {
            location.href = "/";
        });
    });
}