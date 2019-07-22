<template>
    <div>
        <div v-if="errorMessage" class="s-folder-container">
            <b-alert show variant="danger">{{errorMessage}}</b-alert>
        </div>
        <div v-if="loading" class="s-folder-container s-folder-loader">
            <b-spinner variant="secondary" label="Загрузка..." />
        </div>
        <div v-if="adminSections" class="s-admin-list">
            <b-container fluid>
                <b-row>
                    <b-col cols="2" style="padding: 0; position: fixed; height: 100%; overflow: auto;">

                        <div style="padding-top: 0.5em;">
                            <b-nav vertical pills style="text-align: left;">
                                <template v-for="section in adminSections">
                                    <b-nav-item v-if="section.type !== '--'" class="s-folder-nav" exact-active-class="active"
                                            :key="section.id"
                                            :to="'/admin/' + section.id">{{section.name}}</b-nav-item>
                                    <div v-if="section.type === '--'" style="height: 1em;"></div>
                                </template>
                            </b-nav>
                        </div>

                    </b-col>

                    <b-col offset="2" cols="10">
                        <keep-alive>
                            <router-view :key="$route.fullPath"></router-view>
                        </keep-alive>
                    </b-col>
                </b-row>
            </b-container>


        </div>
    </div>
</template>

<style>
    button {
        margin-right: 0.5em;
    }
    .s-folder-nav > a {
        /*border-radius: 0 !important;*/
    }

</style>

<script>
    import VueRouter from "vue-router";
    import Dictionary from './Dictionary'
    import User from './User'
    import Contragent from './Contragent'
    import axios from "axios";

    export default {
        mounted() {
            // if(this.$router.resolve({ name: '/folders/incoming'})){
            //     return;
            // }
            this.loading = true;
            axios.get(`/api/config?configName=admin/index`).then((response) => {
                this.adminSections = response.data;

                this.adminSections.forEach((section) => this.$router.addRoutes([{
                    name: '/admin/' + section.id,
                    path: '/admin/' + section.id,
                    props: {dictionary: section},
                    component: Dictionary
                }]));

                this.loading = false;
            }).catch((error) => {
                this.loading = false;
                console.log(error);
                this.errorMessage = 'Не удается загрузить данные';
            });

        },
        router: new VueRouter({
            mode: 'history',
            routes: [
                {
                    path: '/user/:userId',
                    name: 'user',
                    component: User,
                    meta: {openedFromDictionary: true},
                    props: true
                },
                {
                    path: '/contragent/:contragentId',
                    name: 'contragent',
                    component: Contragent,
                    meta: {openedFromDictionary: true},
                    props: true
                }
            ]
        }),
        data() {
            return {
                loading: true,
                errorMessage: null,
                adminSections: null
            }
        }
    }
</script>