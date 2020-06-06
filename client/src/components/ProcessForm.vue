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
                     :delete-url="`delete/tasks`"
                     add-type="task"
                     :object-id="objectId"
                     :object-type="objectType"
                     delete-info-title-field="executorTitle"
            ></s-table>
        </div>
    </div>
</template>

<style>

</style>

<script>
    import STable from '../components/Table';
    import axios from 'axios';
    import Vue from 'vue';

    export default {
        name: 's-process-form',
        components: {STable},
        props: {
            objectId: {},
            objectType: {}
        },
        mounted() {
            this.loading = false;
            this.loadActions();
        },
        methods: {
            loadActions() {
                axios.get(`/api/tasks/availableActions?objectId=${this.objectId}&objectType=${this.objectType}`)
                    .then((res) => {
                        res.data.forEach((a) => Vue.set(this.buttons, a, true));

                    });
            },
            loadData(spec) {
                let url = `/api/document/${this.objectId}/tasks?size=${spec.size}&page=${spec.page}`;
                let sortProperty = spec.sortProperty === 'sizeStr' ? 'size' : spec.sortProperty;
                if (sortProperty && spec.sortDirection) {
                    url += `&sortProperty=${sortProperty}`;

                    url += `&sort=${sortProperty},${spec.sortDirection.toLowerCase()}`;
                } else if (spec.sortDirection) {
                    url += `&sortDirection=${spec.sortDirection}`;
                } else {
                    url += `&sort=createDate,desc`;
                }

                let res = new Promise((resolve, reject) => {
                    axios.get(url, {
                        params: {
                            conditions: spec.conditions ? JSON.stringify(spec.conditions).slice(1, -1) : null
                        }
                    })
                        .then((res) => {
                            // this.$emit("updateAttaches");
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
                buttons: {
                    refresh: false,
                    columns: false
                },
                tableColumns: [
                    {
                        "id": "select",
                        "title": "Выделение",
                        "type": "checkbox",
                        "ref": "id",
                        "visible": true
                    },
                    {
                        id: "id",
                        title: "ИД",
                        visible: false
                    },
                    {
                        id: "createDate",
                        title: "Дата создания",
                        visible: true,
                        type: 'datetime'
                    },
                    {
                        id: "authorTitle",
                        title: "Автор",
                        visible: true
                    },
                    {
                        id: "controllerTitle",
                        title: "Контроллер",
                        visible: true
                    },
                    {
                        id: "executorTitle",
                        title: "Исполнитель",
                        visible: true
                    },
                    {
                        id: "dueDate",
                        title: "Дата исполнения",
                        visible: true,
                        type: 'datetime'
                    },
                    {
                        id: "description",
                        title: "Текст",
                        visible: true
                    },
                    {
                        id: "statusTitle",
                        title: "Статус",
                        visible: true
                    }
                ]
            }
        }
    }
</script>