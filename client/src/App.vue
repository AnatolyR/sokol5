<template>
  <div id="app">
    <s-header></s-header>
    <b-modal v-if="!loggedIn" :visible="true" id="modal-1" title="Вход">
      <!--<p class="my-4">Hello from modal!</p>-->

      <form @submit.prevent="handleSubmit" class="form-signin">
        <div v-if="errorMessage" class="">
          <b-alert show variant="danger">{{errorMessage}}</b-alert>
        </div>
        <!--<h1 class="h3 mb-3 font-weight-normal">Вход</h1>-->
        <label for="inputUser" class="sr-only">Имя пользователя</label>
        <input v-model="user" id="inputUser" class="form-control" placeholder="Имя пользователя" required autofocus>
        <label for="inputPassword" class="sr-only">Пароль</label>
        <input v-model="password" type="password" id="inputPassword" class="form-control" placeholder="Пароль" required>
        <!--<div class="checkbox mb-3">-->
        <!--<label>-->
        <!--<input type="checkbox" value="remember-me"> Запомнить-->
        <!--</label>-->
        <!--</div>-->
        <button class="btn btn-lg btn-primary btn-block" type="submit">Войти</button>
      </form>
      <div slot="modal-footer" class="w-100"></div>
    </b-modal>
    <router-view v-if="loggedIn"/>
  </div>
</template>

<style>
#app {
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
  padding-top: 58px;
}
#nav {
  padding: 30px;
}

#nav a {
  font-weight: bold;
  color: #2c3e50;
}

#nav a.router-link-exact-active {
  color: #42b983;
}

a .nav-link.router-link-exact-active {
  color: #fff;
}
  .modal-dialog .close {
    display: none;
  }
  .modal-dialog .modal-footer {
    border: 0;
  }
</style>

<script>
  import Header from "./components/Header";
  import store from './store';
  import axios from 'axios';
  import { userService } from "./services/user.service";

  export default {
      mounted() {

      },
      data() {
          return {
              user: null,
              password: null,
              errorMessage: null
          }
      },
      components: {
          "s-header": Header
      },
      computed: {
        loggedIn() {
            return store.state.user != null;
        }
      },
      methods: {
          handleSubmit () {
              userService.login(this.user, this.password).then(() => {
                  axios.get('/api/currentUser').then((res) => {
                      const user = res.data;
                      store.dispatch("setUser", user);
                  }).catch((err) => {
                      console.log(err);
                  });
              }).catch((err) => {
                  if (err.response.status === 401) {
                      this.errorMessage = "Не верное имя пользователя или пароль";
                  } else {
                      this.$router.push('/error');
                  }
              });
          }
      }
  }
</script>
