import Vue from 'vue'
import App from './App.vue'
import { router } from './router'
import store from './store'
import BootstrapVue from 'bootstrap-vue'
import 'bootstrap/dist/css/bootstrap.css'
import 'bootstrap-vue/dist/bootstrap-vue.css'
import 'selectize/dist/css/selectize.bootstrap3.css'
import 'bootstrap-select/dist/css/bootstrap-select.css'

import 'pc-bootstrap4-datetimepicker/build/css/bootstrap-datetimepicker.css'

import { library } from '@fortawesome/fontawesome-svg-core'
import { faAngleLeft, faAngleRight, faAngleUp, faAngleDown } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import axios from 'axios'

library.add(faAngleLeft, faAngleRight, faAngleUp, faAngleDown);
Vue.component('font-awesome-icon', FontAwesomeIcon);

Vue.use(BootstrapVue);

Vue.config.productionTip = false;

import { mockServer } from "./mockServer";
mockServer();

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app');

axios.interceptors.response.use(function (response) {
    return response;
}, function (error) {
    if (401 === error.response.status && error.config.url !== '/api/authentication') {
        store.dispatch("setUser", null);
    } else {
        return Promise.reject(error);
    }
});
