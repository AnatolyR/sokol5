<template>
    <div>
        <div v-if="errorMessage" class="s-folder-container">
            <b-alert show variant="danger">{{errorMessage}}</b-alert>
        </div>
        <div v-if="loading" class="s-folder-container s-folder-loader">
            <b-spinner variant="secondary" label="Loading..." />
        </div>
        <div v-if="!loading" class="s-folder-container">
            <s-table :loadData="loadData" :columns="tableColumns"></s-table>
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
            this.fields = this.folder.fields;
            this.loading = false;
            this.tableColumns = this.folder.fields;
        },
        props: [
            'folder'
        ],
        methods: {
            loadData(spec) {
                let url = `/api/folders/${this.folder.id}/data?size=${spec.size}&page=${spec.page}`;
                if (spec.sortProperty && spec.sortDirection) {
                    url += `&sortProperty=${spec.sortProperty}`;
                }
                if (spec.sortDirection) {
                    url += `&sortDirection=${spec.sortDirection}`;
                }
                let res = new Promise((resolve, reject) => {
                    axios.get(url)
                        .then(resolve)
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
                tableColumns: [],
                tableColumns1: [
                    {
                        id: 'id',
                        title: 'ИД',
                        visible: true
                    },
                    {
                        id: 'title',
                        title: 'Заголовок',
                        visible: true
                    },
                    {
                        id: 'type',
                        title: 'Тип',
                        visible: true
                    },
                    {
                        id: 'status',
                        title: 'Статус',
                        visible: true
                    }
                ]
            }
        }
    }
</script>
