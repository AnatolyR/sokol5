<template>
    <div>
        <div v-if="errorMessage" class="s-dictionary-error-message">
            <b-alert show variant="danger">{{errorMessage}}</b-alert>
        </div>
        <div v-if="loading" class="s-dictionary-loading s-dictionary-loader">
            <b-spinner variant="secondary" label="Загрузка..." />
        </div>
        <div v-if="!loading" class="s-dictionary-container">
            <s-table :buttons="buttons" :loadData="loadData"
                     :columns="tableColumns"
                     :delete-url="`delete/${url}`"
                     :add-url="`${url}`"
                     :add-type="`${addType}`"
            ></s-table>
        </div>
    </div>
</template>

<style>
    .s-dictionary-container {
        margin: 0.5em;
    }
    .s-dictionary-error-message {
        margin: 0.5em;
    }
    .s-dictionary-loading {
        margin: 0.5em;
    }
    .s-dictionary-loader {
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
            this.addType = this.dictionary.addType;
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
        computed: {
            buttons() {
                let buttons = {};
                if (this.dictionary && this.dictionary.actions) {
                    this.dictionary.actions.forEach((a) => buttons[a] = true);
                }
                return buttons;
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
