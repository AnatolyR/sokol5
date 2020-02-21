<template>
    <div>
        <b-navbar fixed="top" v-if="loggedIn" toggleable="lg" type="dark" variant="dark" class="navbar-fixed-top">
            <b-navbar-brand href="/">
                <img src="../assets/icon.png" class="d-inline-block align-top" alt="SOKOL" />
                СОКОЛ
            </b-navbar-brand>

            <b-navbar-toggle target="nav_collapse" />

            <b-collapse is-nav id="nav_collapse">
                <b-navbar-nav>
                    <b-nav-item exact-active-class="active" to="/folders">Папки</b-nav-item>

                    <b-nav-item-dropdown text="Создать" right>
                        <b-dropdown-item @click="createIncoming" href="#">Входящий</b-dropdown-item>
                        <b-dropdown-item href="#">Исходящий</b-dropdown-item>
                        <b-dropdown-item @click="createInner" href="#">Внутренний</b-dropdown-item>
                        <b-dropdown-item href="#">Договор</b-dropdown-item>
                    </b-nav-item-dropdown>

<!--                    <b-nav-item exact-active-class="active" to="/search">Поиск</b-nav-item>-->
                    <b-nav-item exact-active-class="active" to="/dictionaries">Справочники</b-nav-item>
<!--                    <b-nav-item exact-active-class="active" to="/reports">Отчеты</b-nav-item>-->
                </b-navbar-nav>

                <b-navbar-nav class="ml-auto">
                    <!--<b-nav-form>-->
                        <!--<b-form-input size="sm" class="mr-sm-2" type="text" placeholder="Search" />-->
                        <!--<b-button size="sm" class="my-2 my-sm-0" type="submit">Search</b-button>-->
                    <!--</b-nav-form>-->


                    <b-nav-item exact-active-class="active" to="/admin">Администрирование</b-nav-item>

                    <b-nav-item-dropdown right>
                        <template slot="button-content">{{user.name}}</template>
                        <b-dropdown-item href="#">Профиль</b-dropdown-item>
                        <b-dropdown-item v-on:click="logout()">Выход</b-dropdown-item>
                    </b-nav-item-dropdown>
                </b-navbar-nav>
            </b-collapse>
        </b-navbar>
    </div>
</template>

<script>
    import { mapState } from 'vuex';
    import { userService } from "../services/user.service";
    import axios from "axios";

    export default {
        name: "s-header",
        computed: {
            ...mapState({
                loggedIn: 'user',
                user: 'user'
            })
        },
        data() {
            return {
                // loggedIn: true,
                // user: {name: "admin"}
            }
        },
        methods: {
            logout() {
                userService.logout();
            },
            createIncoming() {
                // axios.post('/api/createDocument/Входящий', {})
                //     .then((res) => this.$router.push({path: `/document/${res.data}`,
                //         query: {isNew: true}}));

                axios.post('/api/createDocument/Входящий', {})
                    .then((res) => this.$router.push({name: 'document',
                        params: {isNew: true, documentId: res.data}}));
            },
            createInner() {
                axios.post('/api/createDocument/Внутренний', {})
                    .then((res) => this.$router.push({name: 'document',
                        params: {isNew: true, documentId: res.data}}));
            }
        }
    }
</script>