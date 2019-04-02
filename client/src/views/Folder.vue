<template>
    <div>
        <div v-if="errorMessage" class="s-folder-container">
            <b-alert show variant="danger">{{errorMessage}}</b-alert>
        </div>
        <div v-if="loading" class="s-folder-container s-folder-loader">
            <b-spinner variant="secondary" label="Loading..." />
        </div>
        <div v-if="!loading" class="s-folder-container">
            <b-table responsive bordered hover small :items="items" :fields="fields">
                <template slot="title1" slot-scope="data">
                    <!--<a :href="'/document/'+ data.item.id">{{ data.item.title }}</a>-->
                    <router-link :to="'/document/'+ data.item.id">{{ data.item.title }}</router-link>
                </template>
            </b-table>
        </div>
    </div>
</template>

<style>
    .s-folder-container {
        margin-top: 1em;
    }
    .s-folder-loader {
        text-align: center;
    }
</style>

<script>
    import { folderService } from '../services/folder.service';

    export default {
        mounted() {
            this.fields = this.folder.fields;
            folderService.getFolderData(this.folder.id).then((response) => {
                console.log("response", response);
                this.items = response.data;
                this.loading = false;
            }).catch((error) => {
                this.loading = false;
                console.log(error);
                this.errorMessage = 'Не удается загрузить данные';
            });
        },
        props: [
            'folder'
        ],
        data() {
            return {
                loading: true,
                items: null,
                errorMessage: null,
                fields: null
            }
        }
    }
</script>
