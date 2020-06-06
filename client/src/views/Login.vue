<template>
    <div>
        <form @submit.prevent="handleSubmit" class="form-signin">
            <div v-if="errorMessage" class="">
                <b-alert show variant="danger">{{errorMessage}}</b-alert>
            </div>
            <h1 class="h3 mb-3 font-weight-normal">Вход</h1>
            <label for="inputUser" class="sr-only">Имя пользователя</label>
            <input v-model="user" id="inputUser" class="form-control" placeholder="Имя пользователя" required autofocus>
            <label for="inputPassword" class="sr-only">Пароль</label>
            <input v-model="password" type="password" id="inputPassword" class="form-control" placeholder="Пароль" required>
            <div class="checkbox mb-3">
                <label>
                    <input type="checkbox" value="remember-me" v-model="rememberMe"> Запомнить
                </label>
            </div>
            <button class="btn btn-lg btn-primary btn-block" type="submit">Войти</button>
        </form>
    </div>
</template>

<style>
    .form-signin {
        width: 100%;
        max-width: 330px;
        padding: 15px;
        margin: auto;
    }
    .form-signin .checkbox {
        font-weight: 400;
    }
    .form-signin .form-control {
        position: relative;
        box-sizing: border-box;
        height: auto;
        padding: 10px;
        font-size: 16px;
    }
    .form-signin .form-control:focus {
        z-index: 2;
    }
    #inputUser {
        margin-bottom: -1px;
        border-bottom-right-radius: 0;
        border-bottom-left-radius: 0;
    }
    .form-signin input[type="password"] {
        margin-bottom: 10px;
        border-top-left-radius: 0;
        border-top-right-radius: 0;
    }

</style>

<script>
    import { userService } from "../services/user.service";

    export default {
        props: [
            'backUrl'
        ],
        data() {
            return {
                user: null,
                password: null,
                rememberMe: null,
                errorMessage: null
            }
        },
        methods: {
            handleSubmit () {
                userService.login(this.user, this.password, this.rememberMe).then(() => {
                    const url = this.$route.query['url'];
                    if (url) {
                        this.$router.push(url);
                    } else {
                        this.$router.push("/");
                    }
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
