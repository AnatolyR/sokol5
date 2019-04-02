<template>
  <div id="app">
    <s-header></s-header>
    <router-view/>
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
</style>

<script>
  import Header from "./components/Header";

  export default {
      data() {
          return {

          }
      },
      components: {
          "s-header": Header
      },
      computed: {

      },
      methods: {

      }
  }
</script>
