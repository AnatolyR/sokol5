<template>
    <div>
        <div v-if="errorMessage" class="s-folder-container">
            <b-alert show variant="danger">{{errorMessage}}</b-alert>
        </div>
        <div v-if="loading" class="s-folder-container s-folder-loader">
            <b-spinner variant="secondary" label="Loading..." />
        </div>
        <div v-if="folders">
            <b-container fluid>
                <b-row>
                    <b-col cols="2" style="padding: 0; position: fixed; height: 100%; overflow: auto;
                        z-index: 1000;background-color: white;">

                        <div style="padding-top: 0.5em;">
                            <b-nav vertical pills style="text-align: left;">
                                <template v-for="folder in folders">
                                    <b-nav-item v-if="!folder.type || folder.type !== '--'"
                                                class="s-folder-nav"
                                                exact-active-class="active"
                                                :key="folder.id"
                                                :to="'/folders/' + folder.id">{{folder.name}}</b-nav-item>
                                    <div v-if="folder.type && folder.type === '--'" style="height: 1em;"></div>
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
    import Folder from './Folder.vue'
    import axios from "axios";
    import Document from "./Document.vue";

    export default {
        mounted() {
            // if(this.$router.resolve({ name: '/folders/incoming'})){
            //     return;
            // }
            this.loading = true;
            axios.get(`/api/config?configName=folderLists/user`).then((response) => {
                this.folders = response.data;

                this.folders.forEach((folder) => this.$router.addRoutes([{
                    name: '/folders/' + folder.id,
                    path: '/folders/' + folder.id,
                    props: {folder: folder},
                    component: Folder
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
                    path: '/document/:documentId',
                    name: 'document',
                    component: Document,
                    meta: {openedFromFolder: true},
                    props: true
                }
            ]
            // linkActiveClass: "bg-blue-dark text-white"
        }),
        data() {
            return {
                loading: true,
                errorMessage: null,
                folders: null
            }
        }
    }
</script>