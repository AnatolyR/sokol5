import store from '../store';
import { router } from '../router';

export const userService = {
    login,
    logout
};

function login(username, password) {
    if (username === "admin" && password === "admin") {
        store.dispatch("setUser", {name: username}).then(() => {
            router.push({ path: '/' });
        });
    }
}

function logout() {
    store.dispatch("setUser", null).then(() => {
        location.href = "/";
    });
}