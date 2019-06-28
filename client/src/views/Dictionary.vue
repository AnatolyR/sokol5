<template>
    <div>
        <div v-if="errorMessage" class="s-folder-container">
            <b-alert show variant="danger">{{errorMessage}}</b-alert>
        </div>
        <div v-if="loading" class="s-folder-container s-folder-loader">
            <b-spinner variant="secondary" label="Загрузка..." />
        </div>
        <div v-if="!loading" class="s-folder-container">
            <s-table :buttons="{add: true, del: true}" :loadData="loadData"
                     :columns="tableColumns"
                     :delete-url="`delete/${url}`"
                     :add-url="`${url}`"
            ></s-table>
        </div>
    </div>
</template>

<style>
    .s-folder-container {
        margin: 0.5em;
    }
    .s-folder-loader {
        text-align: center;
    }
</style>

<script>
    import STable from '../components/Table';
    import axios from 'axios';

    export default {
        components: {STable},
        mounted() {
            this.fields = this.dictionary.fields;
            this.url = this.dictionary.url;
            this.loading = false;
            this.tableColumns = this.dictionary.fields;
        },
        props: [
            'dictionary'
        ],
        methods: {
            loadData(spec) {
                let url = `/api/${this.url}?size=${spec.size}&page=${spec.page}`;
                if (spec.sortProperty && spec.sortDirection) {
                    url += `&sortProperty=${spec.sortProperty}`;

                    url += `&sort=${spec.sortProperty},${spec.sortDirection}`;
                }
                if (spec.sortDirection) {
                    url += `&sortDirection=${spec.sortDirection}`;
                }
          
                let res = new Promise((resolve, reject) => {
                    axios.get(url, {
                        params: {
                            conditions: spec.conditions ? JSON.stringify(spec.conditions).slice(1, -1) : null
                        }
                    })
                        .then((res) => {
                            res.data.content = res.data._embedded[this.url];
                            res.data.totalPages = res.data.page.totalPages;
                            resolve(res);
                        })
                        .catch((error) => {
                            this.loading = false;
                            console.log(error);
                            this.errorMessage = 'Не удается загрузить данные';
                        });
                });
                return res;
            }
        },
        data() {
            return {
                loading: true,
                items: null,
                errorMessage: null,
                fields: null,
                tableColumns: []
            }
        }
    }
</script>
