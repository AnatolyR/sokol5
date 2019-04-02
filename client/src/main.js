import Vue from 'vue'
import App from './App.vue'
import { router } from './router'
import store from './store'
import BootstrapVue from 'bootstrap-vue'
import 'bootstrap/dist/css/bootstrap.css'
import 'bootstrap-vue/dist/bootstrap-vue.css'
import 'selectize/dist/css/selectize.bootstrap3.css'

import { library } from '@fortawesome/fontawesome-svg-core'
import { faAngleLeft } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'

library.add(faAngleLeft);
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
